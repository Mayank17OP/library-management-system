package com.dtc.lms.dao.jdbc;

import com.dtc.lms.dao.LoanDAO;
import com.dtc.lms.model.Loan;
import com.dtc.lms.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link LoanDAO} (PreparedStatement only).
 *
 * <p>The "find" queries JOIN books and members so the UI can show the book title
 * and the member name without a second round-trip to the database (avoids the
 * N+1 query problem).</p>
 */
public class JdbcLoanDAO implements LoanDAO {

    private static final String SELECT_WITH_NAMES =
            "SELECT l.loan_id, l.member_id, l.book_id, l.issue_date, l.due_date, "
                    + "       l.return_date, l.returned, l.renewed, l.fine, "
                    + "       b.title AS book_title, m.name AS member_name "
                    + "FROM loans l "
                    + "JOIN books b   ON b.book_id   = l.book_id "
                    + "JOIN members m ON m.member_id = l.member_id ";

    @Override
    public int insert(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (member_id, book_id, issue_date, due_date, "
                + "return_date, returned, renewed, fine) VALUES (?, ?, ?, ?, NULL, 0, 0, 0)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, loan.getMemberId());
            ps.setString(2, loan.getBookId());
            ps.setDate(3, Date.valueOf(loan.getIssueDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public Optional<Loan> findById(int loanId) throws SQLException {
        String sql = SELECT_WITH_NAMES + "WHERE l.loan_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Loan> findByMember(String memberId) throws SQLException {
        String sql = SELECT_WITH_NAMES + "WHERE l.member_id = ? ORDER BY l.loan_id DESC";
        return query(sql, memberId);
    }

    @Override
    public List<Loan> findAll() throws SQLException {
        String sql = SELECT_WITH_NAMES + "ORDER BY l.loan_id DESC";
        return query(sql);
    }

    @Override
    public List<Loan> findActive() throws SQLException {
        String sql = SELECT_WITH_NAMES + "WHERE l.returned = 0 ORDER BY l.due_date";
        return query(sql);
    }

    @Override
    public Optional<Loan> findActiveLoan(String memberId, String bookId) throws SQLException {
        String sql = SELECT_WITH_NAMES + "WHERE l.member_id = ? AND l.book_id = ? AND l.returned = 0";
        List<Loan> loans = query(sql, memberId, bookId);
        return loans.isEmpty() ? Optional.empty() : Optional.of(loans.get(0));
    }

    @Override
    public void markReturned(int loanId, LocalDate returnDate, BigDecimal fine) throws SQLException {
        String sql = "UPDATE loans SET returned = 1, return_date = ?, fine = ? "
                + "WHERE loan_id = ? AND returned = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setBigDecimal(2, fine == null ? BigDecimal.ZERO : fine);
            ps.setInt(3, loanId);
            ps.executeUpdate();
        }
    }

    @Override
    public void renew(int loanId, LocalDate newDueDate) throws SQLException {
        String sql = "UPDATE loans SET due_date = ?, renewed = 1 WHERE loan_id = ? AND returned = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(newDueDate));
            ps.setInt(2, loanId);
            ps.executeUpdate();
        }
    }

    @Override
    public int countActive() throws SQLException {
        return singleInt("SELECT COUNT(*) FROM loans WHERE returned = 0");
    }

    @Override
    public int countOverdue(LocalDate today) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE returned = 0 AND due_date < ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public BigDecimal sumFinesCollected() throws SQLException {
        String sql = "SELECT COALESCE(SUM(fine), 0) FROM loans WHERE returned = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    @Override
    public void deleteAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM loans")) {
            ps.executeUpdate();
        }
    }

    /* ----------------------------- helpers ----------------------------- */

    private List<Loan> query(String sql, String... parameters) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                ps.setString(i + 1, parameters[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
        }
        return loans;
    }

    private int singleInt(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setMemberId(rs.getString("member_id"));
        loan.setBookId(rs.getString("book_id"));
        loan.setIssueDate(toLocalDate(rs.getDate("issue_date")));
        loan.setDueDate(toLocalDate(rs.getDate("due_date")));
        loan.setReturnDate(toLocalDate(rs.getDate("return_date")));
        loan.setReturned(rs.getBoolean("returned"));
        loan.setRenewed(rs.getBoolean("renewed"));
        loan.setFine(rs.getBigDecimal("fine"));
        loan.setBookTitle(rs.getString("book_title"));
        loan.setMemberName(rs.getString("member_name"));
        return loan;
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
