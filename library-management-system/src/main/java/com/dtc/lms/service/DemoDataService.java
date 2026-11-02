package com.dtc.lms.service;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.dao.LoanDAO;
import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.dao.SettingsDAO;
import com.dtc.lms.dao.jdbc.JdbcBookDAO;
import com.dtc.lms.dao.jdbc.JdbcLoanDAO;
import com.dtc.lms.dao.jdbc.JdbcMemberDAO;
import com.dtc.lms.dao.jdbc.JdbcSettingsDAO;
import com.dtc.lms.model.Book;
import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;
import com.dtc.lms.util.AppConfig;
import com.dtc.lms.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Fills the database with a realistic demo set and implements the
 * "Reset Demo Data" button of the admin panel.
 *
 * <p>{@code sql/seed.sql} does exactly the same thing at database level
 * (for Docker / manual import); this class exists so the button works from the
 * web UI without restarting anything.</p>
 *
 * <h2>What the demo shows</h2>
 * <ul>
 *   <li>one loan that is on time,</li>
 *   <li>one loan that is 5 days overdue (fine = Rs 10),</li>
 *   <li>one loan that is 101 days overdue, so member M004 is auto-suspended,</li>
 *   <li>two historical loans that were already returned (one late, fine Rs 6).</li>
 * </ul>
 */
public class DemoDataService {

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final LoanDAO loanDAO;
    private final SettingsDAO settingsDAO;
    private final CirculationService circulationService;
    private final FineService fineService;

    public DemoDataService() {
        this(new JdbcBookDAO(), new JdbcMemberDAO(), new JdbcLoanDAO(),
                new JdbcSettingsDAO(), new CirculationService(), new FineService());
    }

    public DemoDataService(BookDAO bookDAO, MemberDAO memberDAO, LoanDAO loanDAO,
                           SettingsDAO settingsDAO, CirculationService circulationService,
                           FineService fineService) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.loanDAO = loanDAO;
        this.settingsDAO = settingsDAO;
        this.circulationService = circulationService;
        this.fineService = fineService;
    }

    /**
     * Deletes every loan, book and member and inserts the demo set again.
     * The simulated date is cleared, so the demo starts from the real today.
     */
    public void resetDemoData() throws ServiceException {
        LocalDate today = LocalDate.now();
        try {
            // Delete children first, otherwise the foreign keys block us.
            loanDAO.deleteAll();
            bookDAO.deleteAll();
            memberDAO.deleteAll();
            settingsDAO.delete(AppConfig.SETTING_SIMULATED_TODAY);

            insertBooks();
            insertMembers();
            insertLoans(today);

            // Let the real rule decide who is suspended (M004 falls into it).
            for (Member member : memberDAO.findAll()) {
                circulationService.refreshSuspension(member.getMemberId(), today);
            }
        } catch (SQLException ex) {
            throw new ServiceException("Could not reset the demo data: " + ex.getMessage(), ex);
        }
    }

    /* ---------------------------- books ---------------------------- */
    private void insertBooks() throws SQLException, ServiceException {
        String[][] books = {
                {"B001", "Let Us Java", "Yashavant Kanetkar", "5"},
                {"B002", "Clean Code", "Robert C. Martin", "3"},
                {"B003", "Introduction to Algorithms", "Cormen, Leiserson, Rivest and Stein", "4"},
                {"B004", "Database System Concepts", "Abraham Silberschatz", "3"},
                {"B005", "Operating System Concepts", "Peter B. Galvin", "3"},
                {"B006", "Data Communications and Networking", "Behrouz A. Forouzan", "2"},
                {"B007", "Software Engineering: A Practitioner's Approach", "Roger S. Pressman", "4"},
                {"B008", "Head First Design Patterns", "Eric Freeman", "2"},
                {"B009", "Effective Java", "Joshua Bloch", "3"},
                {"B010", "The Pragmatic Programmer", "Andrew Hunt", "2"},
                {"B011", "Data Structures Using C", "Reema Thareja", "5"},
                {"B012", "Discrete Mathematics and Its Applications", "Kenneth H. Rosen", "3"}
        };
        for (String[] row : books) {
            int copies = Integer.parseInt(row[3]);
            // All copies start on the shelf; issuing a book reduces this number.
            bookDAO.insert(new Book(row[0], row[1], row[2], copies, copies));
        }
    }

    /* ---------------------------- members ---------------------------- */
    private void insertMembers() throws SQLException, ServiceException {
        String[][] members = {
                {"M001", "Aarav Mehta"},
                {"M002", "Diya Kapoor"},
                {"M003", "Rohan Sinha"},
                {"M004", "Sneha Nair"}
        };
        String hash = PasswordUtil.hash(AppConfig.DEMO_MEMBER_PASSWORD);
        for (String[] row : members) {
            memberDAO.insert(new Member(row[0], row[1], hash, MemberStatus.ACTIVE));
        }
    }

    /* ---------------------------- loans ---------------------------- */
    private void insertLoans(LocalDate today) throws SQLException, ServiceException {
        // 1) Active, on time: issued 3 days ago, due in 11 days.
        insertActiveLoan("M001", "B003", today.minusDays(3), today.plusDays(11));

        // 2) Active and 5 days overdue -> fine Rs 10.
        insertActiveLoan("M002", "B007", today.minusDays(19), today.minusDays(5));

        // 3) Active and 101 days overdue -> member M004 gets suspended.
        insertActiveLoan("M004", "B011", today.minusDays(115), today.minusDays(101));

        // 4) History: returned 2 days before the due date, so no fine.
        insertReturnedLoan("M003", "B001", today.minusDays(30), today.minusDays(16), today.minusDays(18));

        // 5) History: returned 3 days late -> fine Rs 6.
        insertReturnedLoan("M001", "B005", today.minusDays(40), today.minusDays(26), today.minusDays(23));
    }

    private void insertActiveLoan(String memberId, String bookId, LocalDate issueDate, LocalDate dueDate)
            throws SQLException {
        Loan loan = new Loan(memberId, bookId, issueDate, dueDate);
        loanDAO.insert(loan);
        // Keep books.available_copies consistent with the loan register.
        bookDAO.decrementAvailable(bookId);
    }

    private void insertReturnedLoan(String memberId, String bookId, LocalDate issueDate,
                                    LocalDate dueDate, LocalDate returnDate)
            throws SQLException, ServiceException {
        Loan loan = new Loan(memberId, bookId, issueDate, dueDate);
        int loanId = loanDAO.insert(loan);
        BigDecimal fine = fineService.calculateFine(dueDate, returnDate);
        loanDAO.markReturned(loanId, returnDate, fine);
    }

    /** Helper used by the admin dashboard to show what the reset created. */
    public List<String> describeDemoSet() {
        return List.of(
                "12 books, 4 members",
                "1 loan on time (M001 - Introduction to Algorithms)",
                "1 loan 5 days overdue, fine Rs 10 (M002 - Software Engineering)",
                "1 loan 101 days overdue, M004 auto-suspended",
                "2 returned loans in the history (one with Rs 6 fine)");
    }
}
