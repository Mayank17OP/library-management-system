package com.dtc.lms.support;

import com.dtc.lms.dao.LoanDAO;
import com.dtc.lms.model.Loan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** In-memory test double for {@link LoanDAO}. */
public class InMemoryLoanDAO implements LoanDAO {

    private final List<Loan> table = new ArrayList<>();
    private int nextId = 1;

    @Override
    public int insert(Loan loan) throws SQLException {
        loan.setLoanId(nextId++);
        loan.setReturned(false);
        table.add(loan);
        return loan.getLoanId();
    }

    @Override
    public Optional<Loan> findById(int loanId) {
        return table.stream().filter(l -> l.getLoanId() == loanId).findFirst();
    }

    @Override
    public List<Loan> findByMember(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : table) {
            if (loan.getMemberId().equals(memberId)) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(table);
    }

    @Override
    public List<Loan> findActive() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : table) {
            if (!loan.isReturned()) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public Optional<Loan> findActiveLoan(String memberId, String bookId) {
        return table.stream()
                .filter(l -> l.getMemberId().equals(memberId)
                        && l.getBookId().equals(bookId)
                        && !l.isReturned())
                .findFirst();
    }

    @Override
    public void markReturned(int loanId, LocalDate returnDate, BigDecimal fine) throws SQLException {
        Optional<Loan> found = findById(loanId);
        if (found.isEmpty()) {
            throw new SQLException("Unknown loan " + loanId);
        }
        if (found.get().isReturned()) {
            return;                      // the SQL WHERE returned = 0 would update nothing
        }
        found.get().setReturned(true);
        found.get().setReturnDate(returnDate);
        found.get().setFine(fine);
    }

    @Override
    public void renew(int loanId, LocalDate newDueDate) {
        findById(loanId).ifPresent(loan -> {
            loan.setDueDate(newDueDate);
            loan.setRenewed(true);
        });
    }

    @Override
    public int countActive() {
        return findActive().size();
    }

    @Override
    public int countOverdue(LocalDate today) {
        return (int) table.stream()
                .filter(l -> !l.isReturned() && l.getDueDate().isBefore(today))
                .count();
    }

    @Override
    public BigDecimal sumFinesCollected() {
        return table.stream()
                .filter(Loan::isReturned)
                .map(Loan::getFine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deleteAll() {
        table.clear();
        nextId = 1;
    }
}
