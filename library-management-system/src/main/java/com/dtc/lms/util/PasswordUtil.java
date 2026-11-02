package com.dtc.lms.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing with BCrypt.
 *
 * <p>Passwords are never stored or compared in plain text:</p>
 * <ul>
 *   <li>When a member registers we store {@code BCrypt.hashpw(password, salt)}.</li>
 *   <li>When the member logs in we call {@code BCrypt.checkpw(typed, storedHash)}.</li>
 * </ul>
 *
 * <p>BCrypt is deliberately slow and includes a random salt inside the hash
 * string, so two members with the same password get different hashes and
 * brute-force attacks are expensive.</p>
 */
public final class PasswordUtil {

    /** Work factor: 10 is a good balance between safety and login speed. */
    private static final int COST = 10;

    private PasswordUtil() {
    }

    /** Hashes a plain-text password. The returned string already contains the salt. */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
    }

    /**
     * Compares a typed password with a stored hash.
     *
     * @return true only when they match; false for null/blank/malformed input
     *         (never throws, so a bad row in the database cannot crash a login).
     */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || plainPassword.isEmpty()
                || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException malformedHash) {
            return false;
        }
    }
}
