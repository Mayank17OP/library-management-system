package com.dtc.lms.service;

import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.support.InMemoryBookDAO;
import com.dtc.lms.support.InMemoryLoanDAO;
import com.dtc.lms.support.InMemoryMemberDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for login and registration (Requirements 1, 5 and 6):
 * admin credentials, BCrypt verification, duplicate member IDs and the
 * suspension check performed at login.
 */
class AuthServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 5, 20);

    private InMemoryMemberDAO memberDAO;
    private InMemoryLoanDAO loanDAO;
    private CirculationService circulation;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        InMemoryBookDAO bookDAO = new InMemoryBookDAO();
        memberDAO = new InMemoryMemberDAO();
        loanDAO = new InMemoryLoanDAO();
        circulation = new CirculationService(bookDAO, memberDAO, loanDAO, new FineService());
        authService = new AuthService(memberDAO, circulation);
    }

    /* ------------------------------ admin ------------------------------ */

    @Test
    @DisplayName("admin / admin123 is accepted, a wrong password is not")
    void adminCredentials() {
        assertTrue(authService.authenticateAdmin("admin", "admin123"));
        assertTrue(authService.authenticateAdmin("ADMIN", "admin123"), "username is case-insensitive");
        assertFalse(authService.authenticateAdmin("admin", "wrong"));
        assertFalse(authService.authenticateAdmin("root", "admin123"));
        assertFalse(authService.authenticateAdmin(null, null));
    }

    /* --------------------------- registration -------------------------- */

    @Test
    @DisplayName("Registration stores a BCrypt hash, never the plain password")
    void registrationHashesThePassword() throws Exception {
        Member member = authService.registerMember("M100", "Test Student", "secret123", "secret123");

        assertTrue(member.getPasswordHash().startsWith("$2"), "BCrypt hashes start with $2");
        assertFalse(member.getPasswordHash().contains("secret123"));
    }

    @Test
    @DisplayName("A duplicate member ID is rejected")
    void duplicateMemberIdIsRejected() throws Exception {
        authService.registerMember("M100", "Test Student", "secret123", "secret123");

        ServiceException error = assertThrows(ServiceException.class,
                () -> authService.registerMember("M100", "Another Student", "secret123", "secret123"));
        assertTrue(error.getMessage().contains("already registered"), error.getMessage());
    }

    @Test
    @DisplayName("Mismatched passwords and short passwords are rejected")
    void passwordValidation() {
        assertThrows(ServiceException.class,
                () -> authService.registerMember("M101", "Test Student", "secret123", "secret999"));
        assertThrows(ServiceException.class,
                () -> authService.registerMember("M101", "Test Student", "123", "123"));
        assertThrows(ServiceException.class,
                () -> authService.registerMember("", "Test Student", "secret123", null));
    }

    /* ------------------------------- login ----------------------------- */

    @Test
    @DisplayName("A member can log in with the correct password")
    void memberLoginWorks() throws Exception {
        authService.registerMember("M100", "Test Student", "secret123", "secret123");

        Member member = authService.loginMember("M100", "secret123", TODAY);

        assertEquals("M100", member.getMemberId());
        assertTrue(member.isActive());
    }

    @Test
    @DisplayName("A wrong password or an unknown ID fails with the same generic message")
    void failedLoginIsGeneric() throws Exception {
        authService.registerMember("M100", "Test Student", "secret123", "secret123");

        ServiceException wrongPassword = assertThrows(ServiceException.class,
                () -> authService.loginMember("M100", "nope", TODAY));
        ServiceException unknownId = assertThrows(ServiceException.class,
                () -> authService.loginMember("M999", "secret123", TODAY));

        assertEquals(wrongPassword.getMessage(), unknownId.getMessage(),
                "the message must not reveal which member IDs exist");
    }

    @Test
    @DisplayName("Logging in with a 101-day-overdue book marks the member suspended")
    void loginAppliesSuspension() throws Exception {
        authService.registerMember("M100", "Test Student", "secret123", "secret123");
        loanDAO.insert(new Loan("M100", "B001", TODAY.minusDays(120), TODAY.minusDays(101)));

        Member member = authService.loginMember("M100", "secret123", TODAY);

        assertTrue(member.isSuspended(), "Requirement 12 must be applied at login");
        assertEquals(1, memberDAO.countByStatus(com.dtc.lms.model.MemberStatus.SUSPENDED));
    }

    @Test
    @DisplayName("Login does not throw for a member with no loans at all")
    void loginWithoutLoans() throws Exception {
        authService.registerMember("M100", "Test Student", "secret123", "secret123");
        assertDoesNotThrow(() -> authService.loginMember("M100", "secret123", TODAY));
    }
}
