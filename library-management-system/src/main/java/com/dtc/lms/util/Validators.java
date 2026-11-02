package com.dtc.lms.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Server-side input validation.
 *
 * <p>The browser also validates (HTML5 attributes + a little JavaScript), but
 * client-side checks can be bypassed, so every rule is repeated here. Any
 * violation throws {@link IllegalArgumentException} with a message that is
 * shown to the user as a flash message.</p>
 */
public final class Validators {

    /** Book IDs and member IDs: 2-12 letters/digits, e.g. B001, M004. */
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{2,12}$");

    /** Person / book names: letters, digits, spaces and common punctuation. */
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[A-Za-z0-9 .,'&:;()\\-/]{2,120}$");

    private Validators() {
    }

    /** Trims a value, returning "" for null. */
    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /** Requires a non-blank field and returns it trimmed. */
    public static String requireText(String value, String fieldName) {
        String cleaned = trim(value);
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return cleaned;
    }

    /** Requires a value that looks like an ID (e.g. B001 / M004). */
    public static String requireId(String value, String fieldName) {
        String cleaned = requireText(value, fieldName).toUpperCase();
        if (!ID_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                    fieldName + " must be 2-12 letters or digits (for example B001 or M004).");
        }
        return cleaned;
    }

    /** Requires a title/author/name field. */
    public static String requireName(String value, String fieldName) {
        String cleaned = requireText(value, fieldName);
        if (!TEXT_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                    fieldName + " must be 2-120 characters and may not contain special symbols.");
        }
        return cleaned;
    }

    /** Requires a whole number greater than zero (e.g. number of copies). */
    public static int requirePositiveInt(String value, String fieldName) {
        String cleaned = requireText(value, fieldName);
        int number;
        try {
            number = Integer.parseInt(cleaned);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
        if (number <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
        if (number > 999) {
            throw new IllegalArgumentException(fieldName + " looks too large (maximum 999).");
        }
        return number;
    }

    /** Requires an integer id such as a loan id. */
    public static int requirePositiveIntValue(String value, String fieldName) {
        return requirePositiveInt(value, fieldName);
    }

    /** Requires a password of at least 6 characters. */
    public static String requirePassword(String value) {
        String cleaned = trim(value);
        if (cleaned.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (cleaned.length() > 72) {
            throw new IllegalArgumentException("Password must be at most 72 characters long.");
        }
        return cleaned;
    }

    /** Parses a money value safely (used for statistics only). */
    public static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
