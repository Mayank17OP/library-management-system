package com.dtc.lms.service;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.dao.LoanDAO;
import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.dao.jdbc.JdbcBookDAO;
import com.dtc.lms.dao.jdbc.JdbcLoanDAO;
import com.dtc.lms.dao.jdbc.JdbcMemberDAO;
import com.dtc.lms.model.Book;
import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;
import com.dtc.lms.util.AppConfig;
import com.dtc.lms.util.Validators;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * The heart of the system: issue, return and renew a book, plus the automatic
 * suspension rule.
 *
 * <h2>The three rules the examiner will ask about</h2>
 * <ol>
 *   <li><b>Issue</b> (Requirement 7): allowed only when the member is ACTIVE and
 *       {@code available_copies > 0}. Afterwards availability drops by one.</li>
 *   <li><b>Return</b> (Requirements 9 and 11): fine = Rs 2 x days overdue, the
 *       loan is closed and availability goes back up by one.</li>
 *   <li><b>Renew</b> (Requirement 10): only for a loan that is not overdue and
 *       has never been renewed. The due date moves 7 days ahead.</li>
 * </ol>
 *
 * <p>Every method receives {@code today} as a parameter. Production code passes
 * the clock from {@link ClockService} (so the demo date works), and the tests
 * pass any date they like - no waiting, no mocking of system time.</p>
 */
public class CirculationService {

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final LoanDAO loanDAO;
    private final FineService fineService;

    public CirculationService() {
        this(new JdbcBookDAO(), new JdbcMemberDAO(), new JdbcLoanDAO(), new FineService());
    }

    public CirculationService(BookDAO bookDAO, MemberDAO memberDAO, LoanDAO loanDAO,
                             FineService fineService) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.loanDAO = loanDAO;
        this.fineService = fineService;
    }

    /* ============================================================
       1. ISSUE A BOOK  (Requirement 7)
       ============================================================ */
    public Loan issueBook(String memberId, String bookId, LocalDate today) throws ServiceException {
        try {
            final String cleanMemberId = Validators.requireId(memberId, "Member ID");
            final String cleanBookId = Validators.requireId(bookId, "Book ID");

            Member member = memberDAO.findById(cleanMemberId)
                    .orElseThrow(() -> new ServiceException("No member found with ID " + cleanMemberId + "."));

            // Requirement 12: a suspended member can never issue a book.
            boolean suspended = refreshSuspension(cleanMemberId, today);
            if (suspended) {
                throw new ServiceException("Member " + cleanMemberId + " is SUSPENDED because a book is "
                        + "more than " + AppConfig.SUSPEND_AFTER_DAYS + " days overdue. "
                        + "The book must be returned to the library counter before new issues.");
            }

            Book book = bookDAO.findById(cleanBookId)
                    .orElseThrow(() -> new ServiceException("No book found with ID " + cleanBookId + "."));

            // Requirement 7: only when available > 0.
            if (!book.isAvailable()) {
                throw new ServiceException("All copies of \"" + book.getTitle()
                        + "\" are currently issued. Please try another title.");
            }

            // Extra rule (keeps the register clean): one open copy per member per title.
            if (loanDAO.findActiveLoan(cleanMemberId, cleanBookId).isPresent()) {
                throw new ServiceException("Member " + cleanMemberId
                        + " already has a copy of \"" + book.getTitle() + "\" that is not returned yet.");
            }

            // Atomic "reserve a copy" - returns false when the shelf just went empty.
            if (!bookDAO.decrementAvailable(cleanBookId)) {
                throw new ServiceException("All copies of \"" + book.getTitle()
                        + "\" were just issued to someone else. Please try again.");
            }

            LocalDate dueDate = fineService.calculateDueDate(today);
            Loan loan = new Loan(cleanMemberId, cleanBookId, today, dueDate);
            try {
                int loanId = loanDAO.insert(loan);
                loan.setLoanId(loanId);
            } catch (SQLException insertFailed) {
                // Compensating action: the copy we reserved goes back on the shelf.
                bookDAO.incrementAvailable(cleanBookId);
                throw insertFailed;
            }
            loan.setBookTitle(book.getTitle());
            loan.setMemberName(member.getName());
            return decorate(loan, today);

        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException invalidInput) {
            throw new ServiceException(invalidInput.getMessage(), invalidInput);
        } catch (SQLException ex) {
            throw new ServiceException("Could not issue the book. Please try again.", ex);
        }
    }

    /* ============================================================
       2. RETURN A BOOK  (Requirements 9 and 11)
       ============================================================ */
    public Loan returnBook(int loanId, String actingMemberId, boolean actingAsAdmin, LocalDate today)
            throws ServiceException {

        try {
            Loan loan = requireLoan(loanId);
            assertNotReturned(loan);
            assertOwnership(loan, actingMemberId, actingAsAdmin);

            // Requirement 11: Rs 2 for every day after the due date.
            BigDecimal fine = fineService.calculateFine(loan.getDueDate(), today);

            loanDAO.markReturned(loanId, today, fine);
            bookDAO.incrementAvailable(loan.getBookId());

            // Requirement 12 (reverse direction): once the very late book is back,
            // a previously suspended member becomes ACTIVE again.
            refreshSuspension(loan.getMemberId(), today);

            loan.setReturned(true);
            loan.setReturnDate(today);
            loan.setFine(fine);
            return decorate(loan, today);

        } catch (ServiceException businessError) {
            throw businessError;
        } catch (SQLException ex) {
            throw new ServiceException("Could not return the book. Please try again.", ex);
        }
    }

    /* ============================================================
       3. RENEW A BOOK  (Requirement 10)
       ============================================================ */
    public Loan renewBook(int loanId, String actingMemberId, boolean actingAsAdmin, LocalDate today)
            throws ServiceException {

        try {
            Loan loan = requireLoan(loanId);
            assertNotReturned(loan);
            assertOwnership(loan, actingMemberId, actingAsAdmin);

            // Rule 1: only one renewal per loan.
            if (loan.isRenewed()) {
                throw new ServiceException("This loan has already been renewed once. "
                        + "Please return the book and issue it again.");
            }

            // Rule 2: an overdue book cannot be renewed.
            long overdueDays = fineService.daysOverdue(loan.getDueDate(), today);
            if (overdueDays > 0) {
                throw new ServiceException("Renewal is not allowed because the book is overdue by "
                        + overdueDays + " day(s) (fine Rs " + fineService.fineForDays(overdueDays)
                        + "). Please return it first.");
            }

            LocalDate newDueDate = fineService.calculateRenewedDueDate(loan.getDueDate());
            loanDAO.renew(loanId, newDueDate);
            loan.setDueDate(newDueDate);
            loan.setRenewed(true);
            return decorate(loan, today);

        } catch (ServiceException businessError) {
            throw businessError;
        } catch (SQLException ex) {
            throw new ServiceException("Could not renew the book. Please try again.", ex);
        }
    }

    /* ============================================================
       4. AUTOMATIC SUSPENSION  (Requirement 12)
       ============================================================ */

    /**
     * Re-evaluates the member's status against the 100-day rule.
     *
     * <p>Called when a member logs in, when a dashboard is loaded, and after
     * every issue/return - so the rule can never be dodged.</p>
     *
     * @return true when the member is suspended
     */
    public boolean refreshSuspension(String memberId, LocalDate today) throws ServiceException {
        try {
            Member member = memberDAO.findById(memberId).orElse(null);
            if (member == null) {
                return false;
            }

            long worstOverdueDays = 0;
            for (Loan loan : loanDAO.findByMember(memberId)) {
                if (loan.isActive()) {
                    worstOverdueDays = Math.max(worstOverdueDays,
                            fineService.daysOverdue(loan.getDueDate(), today));
                }
            }

            boolean mustSuspend = fineService.shouldSuspend(worstOverdueDays);

            if (mustSuspend && !member.isSuspended()) {
                memberDAO.updateStatus(memberId, MemberStatus.SUSPENDED);
                return true;
            }
            if (!mustSuspend && member.isSuspended()) {
                // No more books beyond the limit -> lift the suspension.
                memberDAO.updateStatus(memberId, MemberStatus.ACTIVE);
                return false;
            }
            return member.isSuspended();
        } catch (SQLException ex) {
            throw new ServiceException("Could not update the member status.", ex);
        }
    }

    /* ============================================================
       5. READ HELPERS (used by the dashboards)
       ============================================================ */

    /** Loans of one member, decorated with status text and running fine. */
    public List<Loan> listLoansForMember(String memberId, LocalDate today) throws ServiceException {
        try {
            List<Loan> loans = loanDAO.findByMember(memberId);
            loans.forEach(loan -> decorate(loan, today));
            return loans;
        } catch (SQLException ex) {
            throw new ServiceException("Could not load your loans.", ex);
        }
    }

    /** Every loan of the library (admin screens). */
    public List<Loan> listAllLoans(LocalDate today) throws ServiceException {
        try {
            List<Loan> loans = loanDAO.findAll();
            loans.forEach(loan -> decorate(loan, today));
            return loans;
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the loan register.", ex);
        }
    }

    /** Only the loans that are still out (admin dashboard). */
    public List<Loan> listActiveLoans(LocalDate today) throws ServiceException {
        try {
            List<Loan> loans = loanDAO.findActive();
            loans.forEach(loan -> decorate(loan, today));
            return loans;
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the active loans.", ex);
        }
    }

    /**
     * Fills the display fields of a loan: status badge text, days overdue and
     * the fine the member would pay if the book came back today.
     */
    public Loan decorate(Loan loan, LocalDate today) {
        if (loan.isReturned()) {
            BigDecimal fine = loan.getFine() == null ? BigDecimal.ZERO : loan.getFine();
            loan.setStatusText(fine.signum() > 0 ? "Returned (Late)" : "Returned");
            loan.setCurrentFine(fine);
            loan.setDaysOverdue(fineService.daysOverdue(loan.getDueDate(), loan.getReturnDate()));
        } else {
            long overdueDays = fineService.daysOverdue(loan.getDueDate(), today);
            loan.setDaysOverdue(overdueDays);
            loan.setStatusText(overdueDays > 0 ? "Overdue" : "Issued");
            loan.setCurrentFine(fineService.fineForDays(overdueDays));
        }
        return loan;
    }

    /* ============================ private helpers ============================ */

    private Loan requireLoan(int loanId) throws ServiceException {
        try {
            return loanDAO.findById(loanId)
                    .orElseThrow(() -> new ServiceException("No loan found with number " + loanId + "."));
        } catch (SQLException ex) {
            throw new ServiceException("Could not load loan " + loanId + ".", ex);
        }
    }

    private void assertNotReturned(Loan loan) throws ServiceException {
        if (loan.isReturned()) {
            throw new ServiceException("This book was already returned on " + loan.getReturnDate() + ".");
        }
    }

    /** A member may only touch his/her own loans; the librarian may touch all. */
    private void assertOwnership(Loan loan, String actingMemberId, boolean actingAsAdmin)
            throws ServiceException {
        if (!actingAsAdmin && !loan.getMemberId().equalsIgnoreCase(Validators.trim(actingMemberId))) {
            throw new ServiceException("You can only manage your own loans.");
        }
    }
}
