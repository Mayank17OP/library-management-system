package com.dtc.lms.service;

import com.dtc.lms.model.Book;
import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;
import com.dtc.lms.support.InMemoryBookDAO;
import com.dtc.lms.support.InMemoryLoanDAO;
import com.dtc.lms.support.InMemoryMemberDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the circulation rules:
 * issue (Requirement 7), return (Requirement 9), renew (Requirement 10) and
 * automatic suspension (Requirement 12).
 *
 * <p>The three DAOs are replaced by in-memory fakes, so the test exercises the
 * real service logic without needing MySQL.</p>
 */
class CirculationServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 1);

    private InMemoryBookDAO bookDAO;
    private InMemoryMemberDAO memberDAO;
    private InMemoryLoanDAO loanDAO;
    private CirculationService circulation;

    @BeforeEach
    void setUp() throws Exception {
        bookDAO = new InMemoryBookDAO();
        memberDAO = new InMemoryMemberDAO();
        loanDAO = new InMemoryLoanDAO();
        circulation = new CirculationService(bookDAO, memberDAO, loanDAO, new FineService());

        // B001 has 2 copies, B002 has 1 copy (so it can run out).
        bookDAO.insert(new Book("B001", "Effective Java", "Joshua Bloch", 2, 2));
        bookDAO.insert(new Book("B002", "Clean Code", "Robert C. Martin", 1, 1));
        memberDAO.insert(new Member("M001", "Aarav Mehta", MemberStatus.ACTIVE));
    }

    /* ------------------------------- ISSUE ------------------------------- */

    @Test
    @DisplayName("Issuing a book reduces the available copies by one and sets a 14-day due date")
    void issueReducesAvailability() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);

        assertNotNull(loan);
        assertEquals(TODAY, loan.getIssueDate());
        assertEquals(TODAY.plusDays(14), loan.getDueDate(), "loan period must be 14 days");
        assertEquals("Issued", loan.getStatusText());

        Book book = bookDAO.findById("B001").orElseThrow();
        assertEquals(1, book.getAvailableCopies(), "one copy must be reserved");
        assertEquals(2, book.getTotalCopies(), "the total never changes");
    }

    @Test
    @DisplayName("A book with no available copy cannot be issued")
    void issueFailsWhenNoCopiesLeft() throws Exception {
        circulation.issueBook("M001", "B002", TODAY);          // takes the only copy

        InMemoryMemberDAO other = memberDAO;
        other.insert(new Member("M002", "Diya Kapoor", MemberStatus.ACTIVE));

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.issueBook("M002", "B002", TODAY));
        assertTrue(error.getMessage().contains("currently issued"), error.getMessage());
    }

    @Test
    @DisplayName("An unknown member or book is rejected with a clear message")
    void issueFailsForUnknownIds() {
        assertThrows(ServiceException.class, () -> circulation.issueBook("M999", "B001", TODAY));
        assertThrows(ServiceException.class, () -> circulation.issueBook("M001", "B999", TODAY));
        assertThrows(ServiceException.class, () -> circulation.issueBook("M001", "", TODAY));
    }

    @Test
    @DisplayName("The same member cannot hold two copies of the same title")
    void issueFailsForDuplicateActiveLoan() throws Exception {
        circulation.issueBook("M001", "B001", TODAY);

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.issueBook("M001", "B001", TODAY));
        assertTrue(error.getMessage().contains("already has a copy"), error.getMessage());
    }

    @Test
    @DisplayName("A suspended member cannot issue a book (Requirement 12)")
    void suspendedMemberCannotIssue() throws Exception {
        // A 101-day-overdue loan is what makes the member suspended.
        loanDAO.insert(new Loan("M001", "B001", TODAY.minusDays(115), TODAY.minusDays(101)));
        assertTrue(circulation.refreshSuspension("M001", TODAY));
        assertEquals(MemberStatus.SUSPENDED, memberDAO.findById("M001").orElseThrow().getStatus());

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.issueBook("M001", "B002", TODAY));
        assertTrue(error.getMessage().contains("SUSPENDED"), error.getMessage());
    }

    /* ------------------------------- RETURN ------------------------------- */

    @Test
    @DisplayName("Returning on time restores availability and charges no fine")
    void returnOnTime() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);
        assertEquals(1, bookDAO.findById("B001").orElseThrow().getAvailableCopies());

        Loan returned = circulation.returnBook(loan.getLoanId(), "M001", false, TODAY.plusDays(10));

        assertTrue(returned.isReturned());
        assertEquals(new BigDecimal("0.00"), returned.getFine());
        assertEquals("Returned", returned.getStatusText());
        assertEquals(2, bookDAO.findById("B001").orElseThrow().getAvailableCopies(),
                "the copy must be back on the shelf");
    }

    @Test
    @DisplayName("Returning 5 days late charges exactly Rs 10 and still restores the copy")
    void returnFiveDaysLate() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);

        Loan returned = circulation.returnBook(loan.getLoanId(), "M001", false, TODAY.plusDays(19));

        assertEquals(new BigDecimal("10.00"), returned.getFine());
        assertEquals(5, returned.getDaysOverdue());
        assertEquals("Returned (Late)", returned.getStatusText());
        assertEquals(2, bookDAO.findById("B001").orElseThrow().getAvailableCopies());
    }

    @Test
    @DisplayName("A loan can only be returned once")
    void returnTwiceIsRejected() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);
        circulation.returnBook(loan.getLoanId(), "M001", false, TODAY);

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.returnBook(loan.getLoanId(), "M001", false, TODAY));
        assertTrue(error.getMessage().contains("already returned"), error.getMessage());
        assertEquals(2, bookDAO.findById("B001").orElseThrow().getAvailableCopies(),
                "availability must not be increased twice");
    }

    @Test
    @DisplayName("A member cannot return somebody else's loan")
    void memberCannotTouchForeignLoan() throws Exception {
        memberDAO.insert(new Member("M002", "Diya Kapoor", MemberStatus.ACTIVE));
        Loan loan = circulation.issueBook("M001", "B001", TODAY);

        assertThrows(ServiceException.class,
                () -> circulation.returnBook(loan.getLoanId(), "M002", false, TODAY));
        // the librarian, however, may handle any loan
        assertNotNull(circulation.returnBook(loan.getLoanId(), null, true, TODAY));
    }

    /* ------------------------------- RENEW ------------------------------- */

    @Test
    @DisplayName("Renewing extends the due date by 7 days")
    void renewExtendsBySevenDays() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);

        Loan renewed = circulation.renewBook(loan.getLoanId(), "M001", false, TODAY.plusDays(3));

        assertEquals(TODAY.plusDays(21), renewed.getDueDate());
        assertTrue(renewed.isRenewed());
        assertEquals(1, bookDAO.findById("B001").orElseThrow().getIssuedCopies(),
                "renewing does not change availability");
    }

    @Test
    @DisplayName("A loan can be renewed only once")
    void secondRenewalIsRejected() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);
        circulation.renewBook(loan.getLoanId(), "M001", false, TODAY);

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.renewBook(loan.getLoanId(), "M001", false, TODAY));
        assertTrue(error.getMessage().contains("already been renewed"), error.getMessage());
    }

    @Test
    @DisplayName("An overdue book cannot be renewed and the reason is shown")
    void overdueBookCannotBeRenewed() throws Exception {
        Loan loan = circulation.issueBook("M001", "B001", TODAY);

        ServiceException error = assertThrows(ServiceException.class,
                () -> circulation.renewBook(loan.getLoanId(), "M001", false, TODAY.plusDays(20)));
        assertTrue(error.getMessage().contains("overdue by 6 day"), error.getMessage());
        assertTrue(error.getMessage().contains("Rs 12.00"), error.getMessage());
    }

    /* ---------------------------- SUSPENSION ---------------------------- */

    @Test
    @DisplayName("A member becomes suspended when a book passes 100 days overdue")
    void automaticSuspensionAfterLimit() throws Exception {
        loanDAO.insert(new Loan("M001", "B001", TODAY.minusDays(114), TODAY.minusDays(100)));
        assertFalse(circulation.refreshSuspension("M001", TODAY), "exactly 100 days is still allowed");

        // one more day pushes the loan to 101 days overdue
        assertTrue(circulation.refreshSuspension("M001", TODAY.plusDays(1)));
        assertEquals(MemberStatus.SUSPENDED, memberDAO.findById("M001").orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Returning the very late book lifts the suspension automatically")
    void suspensionIsLiftedAfterReturn() throws Exception {
        Loan late = new Loan("M001", "B001", TODAY.minusDays(120), TODAY.minusDays(106));
        int loanId = loanDAO.insert(late);
        bookDAO.decrementAvailable("B001");
        assertTrue(circulation.refreshSuspension("M001", TODAY));

        // the librarian registers the return of the 106-day-overdue book
        Loan returned = circulation.returnBook(loanId, null, true, TODAY);

        assertEquals(new BigDecimal("212.00"), returned.getFine(), "106 days x Rs 2");
        assertFalse(memberDAO.findById("M001").orElseThrow().isSuspended(),
                "no loan is beyond the limit any more, so the member is active again");
    }
}
