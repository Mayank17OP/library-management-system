package com.dtc.lms.service;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.dao.LoanDAO;
import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.dao.jdbc.JdbcBookDAO;
import com.dtc.lms.dao.jdbc.JdbcLoanDAO;
import com.dtc.lms.dao.jdbc.JdbcMemberDAO;
import com.dtc.lms.model.Loan;
import com.dtc.lms.model.MemberStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Builds the statistics of Requirement 13:
 * total books, total members, active loans and suspended members
 * (plus a few extra numbers that make the report useful).
 */
public class ReportService {

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final LoanDAO loanDAO;
    private final CirculationService circulationService;

    public ReportService() {
        this(new JdbcBookDAO(), new JdbcMemberDAO(), new JdbcLoanDAO(), new CirculationService());
    }

    public ReportService(BookDAO bookDAO, MemberDAO memberDAO, LoanDAO loanDAO,
                         CirculationService circulationService) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.loanDAO = loanDAO;
        this.circulationService = circulationService;
    }

    /**
     * Collects every number for the dashboard in one go.
     *
     * @param today simulated or real date - decides which loans count as overdue
     */
    public DashboardStats buildStats(LocalDate today) throws ServiceException {
        try {
            int totalTitles = bookDAO.countTitles();
            int totalCopies = bookDAO.sumTotalCopies();
            int availableCopies = bookDAO.sumAvailableCopies();

            int totalMembers = memberDAO.countAll();
            int suspendedMembers = memberDAO.countByStatus(MemberStatus.SUSPENDED);
            int activeMembers = totalMembers - suspendedMembers;

            int activeLoans = loanDAO.countActive();
            int overdueLoans = loanDAO.countOverdue(today);
            BigDecimal finesCollected = loanDAO.sumFinesCollected();

            // Money that would be collected if every overdue book came back today.
            BigDecimal finesOutstanding = BigDecimal.ZERO;
            for (Loan loan : circulationService.listActiveLoans(today)) {
                finesOutstanding = finesOutstanding.add(loan.getCurrentFine());
            }

            return new DashboardStats(totalTitles, totalCopies, availableCopies,
                    totalMembers, activeMembers, suspendedMembers,
                    activeLoans, overdueLoans, finesCollected, finesOutstanding);

        } catch (SQLException ex) {
            throw new ServiceException("Could not build the report. Please try again.", ex);
        }
    }
}
