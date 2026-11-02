package com.dtc.lms.dao;

import com.dtc.lms.model.Loan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for the {@code loans} table (issue / return / renew).
 */
public interface LoanDAO {

    /**
     * Inserts a loan and returns the generated primary key.
     * Throws SQLException when the member or book does not exist (foreign keys).
     */
    int insert(Loan loan) throws SQLException;

    Optional<Loan> findById(int loanId) throws SQLException;

    /** All loans of one member (with book title filled in), newest first. */
    List<Loan> findByMember(String memberId) throws SQLException;

    /** Every loan in the library (with book title + member name), newest first. */
    List<Loan> findAll() throws SQLException;

    /** Only the loans that are still out (returned = 0). */
    List<Loan> findActive() throws SQLException;

    /** The open loan of a given book for a given member, if any. */
    Optional<Loan> findActiveLoan(String memberId, String bookId) throws SQLException;

    /** Marks a loan as returned and stores the final fine (Requirement 9). */
    void markReturned(int loanId, LocalDate returnDate, BigDecimal fine) throws SQLException;

    /** Extends the due date and flags the loan as renewed (Requirement 10). */
    void renew(int loanId, LocalDate newDueDate) throws SQLException;

    int countActive() throws SQLException;

    /** Unreturned loans whose due date is before today. */
    int countOverdue(LocalDate today) throws SQLException;

    /** Fine money collected from returned books (used in the report). */
    BigDecimal sumFinesCollected() throws SQLException;

    /** Used by the "Reset Demo Data" button. */
    void deleteAll() throws SQLException;
}
