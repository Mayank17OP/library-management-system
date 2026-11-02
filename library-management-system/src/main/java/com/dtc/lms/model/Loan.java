package com.dtc.lms.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One issue/return transaction: "member X borrowed book Y on date Z".
 *
 * <p>Maps to the {@code loans} table:</p>
 * <pre>
 * loans(loan_id, member_id, book_id, issue_date, due_date,
 *       return_date, returned, renewed, fine)
 * </pre>
 *
 * <p>The fields {@code bookTitle}, {@code memberName}, {@code statusText},
 * {@code daysOverdue} and {@code currentFine} are display-only values that the
 * service layer fills in after a JOIN, so JSP pages stay free of business
 * logic.</p>
 */
public class Loan {

    private int loanId;
    private String memberId;
    private String bookId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private boolean renewed;
    private BigDecimal fine = BigDecimal.ZERO;

    /* ---------- display-only fields (not stored in the loans table) ---------- */
    private String bookTitle;
    private String memberName;
    private String statusText = "Issued";
    private long daysOverdue;
    private BigDecimal currentFine = BigDecimal.ZERO;

    public Loan() {
    }

    public Loan(String memberId, String bookId, LocalDate issueDate, LocalDate dueDate) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    /* ------------------------------ helpers ------------------------------ */

    /** A loan that has not been returned yet. */
    public boolean isActive() {
        return !returned;
    }

    /** True when the book was returned after the due date. */
    public boolean isReturnedLate() {
        return returned && fine != null && fine.signum() > 0;
    }

    /** Badge CSS class used by the JSP (<code>badge-issued</code>, etc.). */
    public String getStatusCssClass() {
        return switch (statusText) {
            case "Overdue" -> "badge-danger";
            case "Returned" -> "badge-success";
            case "Returned (Late)" -> "badge-warning";
            default -> "badge-info";
        };
    }

    /* ------------------------------ getters ------------------------------ */

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public boolean isRenewed() {
        return renewed;
    }

    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine == null ? BigDecimal.ZERO : fine;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public BigDecimal getCurrentFine() {
        return currentFine;
    }

    public void setCurrentFine(BigDecimal currentFine) {
        this.currentFine = currentFine == null ? BigDecimal.ZERO : currentFine;
    }

    @Override
    public String toString() {
        return "Loan{" + loanId + ", " + memberId + " -> " + bookId
                + ", due=" + dueDate + ", returned=" + returned + '}';
    }
}
