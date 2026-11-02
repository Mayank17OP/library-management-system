package com.dtc.lms.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the money and suspension rules (Requirements 10, 11 and 12).
 *
 * <p>{@code FineService} has no database dependency, so these tests are pure
 * arithmetic - they run in milliseconds and need no setup at all.</p>
 */
class FineServiceTest {

    private final FineService fineService = new FineService();
    private final LocalDate due = LocalDate.of(2026, 3, 10);

    /* ------------------------------- fines ------------------------------- */

    @Test
    @DisplayName("A book returned on the due date costs nothing")
    void noFineWhenReturnedOnTime() {
        assertEquals(new BigDecimal("0.00"), fineService.calculateFine(due, due));
        assertEquals(0L, fineService.daysOverdue(due, due));
    }

    @Test
    @DisplayName("A book returned early costs nothing (negative days are clamped to 0)")
    void noFineWhenReturnedEarly() {
        assertEquals(new BigDecimal("0.00"), fineService.calculateFine(due, due.minusDays(4)));
        assertEquals(0L, fineService.daysOverdue(due, due.minusDays(4)));
    }

    @Test
    @DisplayName("5 days overdue = Rs 10 (Rs 2 per day)")
    void fiveDaysOverdueIsTenRupees() {
        LocalDate returned = due.plusDays(5);
        assertEquals(5L, fineService.daysOverdue(due, returned));
        assertEquals(new BigDecimal("10.00"), fineService.calculateFine(due, returned));
    }

    @Test
    @DisplayName("1 day overdue = Rs 2 and 30 days overdue = Rs 60")
    void fineGrowsPerDay() {
        assertEquals(new BigDecimal("2.00"), fineService.calculateFine(due, due.plusDays(1)));
        assertEquals(new BigDecimal("60.00"), fineService.calculateFine(due, due.plusDays(30)));
    }

    @Test
    @DisplayName("isOverdue is true only after the due date")
    void isOverdueBoundary() {
        assertFalse(fineService.isOverdue(due, due));
        assertFalse(fineService.isOverdue(due, due.minusDays(1)));
        assertTrue(fineService.isOverdue(due, due.plusDays(1)));
    }

    /* ---------------------------- suspension ---------------------------- */

    @Test
    @DisplayName("Exactly 100 days overdue is NOT enough for a suspension")
    void oneHundredDaysDoesNotSuspend() {
        assertFalse(fineService.shouldSuspend(100));
    }

    @Test
    @DisplayName("101 days overdue suspends the member (more than 100)")
    void oneHundredAndOneDaysSuspends() {
        assertTrue(fineService.shouldSuspend(101));
        assertTrue(fineService.shouldSuspend(240));
    }

    /* ------------------------------ periods ------------------------------ */

    @Test
    @DisplayName("A loan runs for 14 days and a renewal adds 7 more")
    void loanAndRenewalPeriods() {
        assertEquals(due.plusDays(14), fineService.calculateDueDate(due));
        assertEquals(due.plusDays(7), fineService.calculateRenewedDueDate(due));
    }
}
