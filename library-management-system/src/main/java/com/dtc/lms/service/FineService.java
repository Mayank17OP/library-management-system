package com.dtc.lms.service;

import com.dtc.lms.util.AppConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * All money / overdue / suspension arithmetic in one place.
 *
 * <p>Rules (from {@link AppConfig}):</p>
 * <ul>
 *   <li>A book may be kept for 14 days ({@code LOAN_PERIOD_DAYS}).</li>
 *   <li>Fine = Rs 2 per day for every day after the due date ({@code FINE_PER_DAY}).</li>
 *   <li>A member is suspended when a book is more than 100 days overdue
 *       ({@code SUSPEND_AFTER_DAYS}).</li>
 * </ul>
 *
 * <p>The class is deliberately free of database code, which is why it can be
 * unit-tested completely (see {@code FineServiceTest}).</p>
 */
public class FineService {

    /**
     * How many days late is the book?
     *
     * @param dueDate    date on which the book had to be back
     * @param returnDate the day we compare with (real today, simulated today,
     *                   or the actual return date)
     * @return 0 when the book is on time, otherwise the number of late days
     */
    public long daysOverdue(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate == null || returnDate == null) {
            return 0L;
        }
        long days = ChronoUnit.DAYS.between(dueDate, returnDate);
        return Math.max(days, 0L);
    }

    /** True when {@code today} is strictly after the due date. */
    public boolean isOverdue(LocalDate dueDate, LocalDate today) {
        return daysOverdue(dueDate, today) > 0;
    }

    /** Converts a number of late days into rupees (Rs 2 x days). */
    public BigDecimal fineForDays(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return AppConfig.FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays)).setScale(2);
    }

    /**
     * Fine for a book returned on {@code returnDate}.
     * 5 days late with FINE_PER_DAY = 2 gives Rs 10.00.
     */
    public BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
        return fineForDays(daysOverdue(dueDate, returnDate));
    }

    /** Requirement 12: more than 100 days overdue means suspended. */
    public boolean shouldSuspend(long overdueDays) {
        return overdueDays > AppConfig.SUSPEND_AFTER_DAYS;
    }

    /** Date on which a book issued today has to be returned. */
    public LocalDate calculateDueDate(LocalDate issueDate) {
        return issueDate.plusDays(AppConfig.LOAN_PERIOD_DAYS);
    }

    /** Due date after one renewal (Requirement 10). */
    public LocalDate calculateRenewedDueDate(LocalDate currentDueDate) {
        return currentDueDate.plusDays(AppConfig.RENEWAL_PERIOD_DAYS);
    }

    /* ----- small getters so the JSP can print the rules on screen ----- */
    public BigDecimal getFinePerDay() {
        return AppConfig.FINE_PER_DAY;
    }

    public int getLoanPeriodDays() {
        return AppConfig.LOAN_PERIOD_DAYS;
    }

    public int getSuspensionLimitDays() {
        return AppConfig.SUSPEND_AFTER_DAYS;
    }
}
