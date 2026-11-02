package com.dtc.lms.service;

import java.math.BigDecimal;

/**
 * The numbers shown on the admin dashboard and on the reports page
 * (Requirement 13). A simple immutable view object with getters, so the JSP can
 * read it with {@code ${stats.totalTitles}}.
 */
public class DashboardStats {

    private final int totalTitles;
    private final int totalCopies;
    private final int availableCopies;
    private final int totalMembers;
    private final int activeMembers;
    private final int suspendedMembers;
    private final int activeLoans;
    private final int overdueLoans;
    private final BigDecimal finesCollected;
    private final BigDecimal finesOutstanding;

    public DashboardStats(int totalTitles, int totalCopies, int availableCopies,
                          int totalMembers, int activeMembers, int suspendedMembers,
                          int activeLoans, int overdueLoans,
                          BigDecimal finesCollected, BigDecimal finesOutstanding) {
        this.totalTitles = totalTitles;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.totalMembers = totalMembers;
        this.activeMembers = activeMembers;
        this.suspendedMembers = suspendedMembers;
        this.activeLoans = activeLoans;
        this.overdueLoans = overdueLoans;
        this.finesCollected = finesCollected == null ? BigDecimal.ZERO : finesCollected;
        this.finesOutstanding = finesOutstanding == null ? BigDecimal.ZERO : finesOutstanding;
    }

    public int getTotalTitles() {
        return totalTitles;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getIssuedCopies() {
        return Math.max(totalCopies - availableCopies, 0);
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public int getActiveMembers() {
        return activeMembers;
    }

    public int getSuspendedMembers() {
        return suspendedMembers;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public int getOverdueLoans() {
        return overdueLoans;
    }

    public BigDecimal getFinesCollected() {
        return finesCollected;
    }

    public BigDecimal getFinesOutstanding() {
        return finesOutstanding;
    }

    /* ---- percentages for the CSS/SVG bar chart on the reports page ---- */

    /** Share of the copies that are currently on the shelf. */
    public int getAvailablePercent() {
        return percent(availableCopies, totalCopies);
    }

    /** Share of the copies that are with members. */
    public int getIssuedPercent() {
        return percent(getIssuedCopies(), totalCopies);
    }

    /** Share of the loans that are overdue. */
    public int getOverduePercent() {
        return percent(overdueLoans, Math.max(activeLoans, 1));
    }

    /** Share of members that are suspended. */
    public int getSuspendedPercent() {
        return percent(suspendedMembers, Math.max(totalMembers, 1));
    }

    private int percent(int part, int total) {
        if (total <= 0) {
            return 0;
        }
        int value = Math.round(part * 100f / total);
        return Math.max(0, Math.min(100, value));
    }
}
