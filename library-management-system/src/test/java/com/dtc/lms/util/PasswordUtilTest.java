package com.dtc.lms.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for BCrypt hashing.
 *
 * <p>One of these checks is especially useful: it proves that the hash stored in
 * {@link AppConfig} really belongs to the demo password "admin123", so the login
 * page hint can never drift away from the code.</p>
 */
class PasswordUtilTest {

    @Test
    @DisplayName("hash() then verify() round-trips, and two hashes of one password differ (salt)")
    void hashAndVerify() {
        String first = PasswordUtil.hash("member123");
        String second = PasswordUtil.hash("member123");

        assertNotEquals(first, second, "BCrypt adds a random salt to every hash");
        assertTrue(PasswordUtil.verify("member123", first));
        assertTrue(PasswordUtil.verify("member123", second));
        assertFalse(PasswordUtil.verify("member124", first));
    }

    @Test
    @DisplayName("The demo admin hash in AppConfig matches admin123")
    void adminHashMatchesDemoPassword() {
        assertTrue(PasswordUtil.verify("admin123", AppConfig.ADMIN_PASSWORD_HASH));
        assertFalse(PasswordUtil.verify("admin", AppConfig.ADMIN_PASSWORD_HASH));
    }

    @Test
    @DisplayName("The seeded member hash in sql/seed.sql matches member123")
    void memberHashMatchesDemoPassword() {
        assertTrue(PasswordUtil.verify("member123", AppConfig.DEMO_MEMBER_PASSWORD_HASH));
    }

    @Test
    @DisplayName("Null, blank and malformed hashes are handled without an exception")
    void defensiveVerification() {
        assertFalse(PasswordUtil.verify(null, AppConfig.ADMIN_PASSWORD_HASH));
        assertFalse(PasswordUtil.verify("admin123", null));
        assertFalse(PasswordUtil.verify("admin123", "not-a-bcrypt-hash"));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(""));
    }
}
