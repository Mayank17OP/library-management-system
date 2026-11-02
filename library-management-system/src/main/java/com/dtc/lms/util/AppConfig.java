package com.dtc.lms.util;

import java.math.BigDecimal;

/**
 * One single place for every constant of the application.
 *
 * <p>Keeping the business numbers (loan period, fine per day, suspension limit)
 * in a single class means the examiner only has to look here to change a rule,
 * and every service automatically follows the new rule.</p>
 */
public final class AppConfig {

    /** Utility class - must never be instantiated. */
    private AppConfig() {
    }

    /* =====================================================================
       1. APPLICATION META DATA (used on the About page and the footer)
       ===================================================================== */
    public static final String APP_NAME = "Library Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String COLLEGE = "Delhi Technical Campus, Greater Noida";
    public static final String SUBJECT = "Software Engineering (CIC-309)";
    public static final String SESSION = "2026-27 | B.Tech CSE | 3rd Year | 5th Sem | Section A";
    public static final String GUIDE = "Dr. Eirtyy Telang";
    public static final String TEAM = "Anushka Bhatnagar (04118002724) and Bhawana Joshi (04218002724)";

    /* =====================================================================
       2. BUSINESS RULES  (Requirement 11 and 12 of the problem statement)
       ===================================================================== */

    /** A book is issued for 14 days. */
    public static final int LOAN_PERIOD_DAYS = 14;

    /** A successful renewal extends the due date by 7 days. */
    public static final int RENEWAL_PERIOD_DAYS = 7;

    /** A loan can be renewed only once (Requirement 10). */
    public static final int MAX_RENEWALS = 1;

    /** Fine = Rs 2 per day per overdue book (Requirement 11). */
    public static final BigDecimal FINE_PER_DAY = new BigDecimal("2.00");

    /**
     * A member becomes SUSPENDED when an unreturned book is more than
     * this many days overdue (Requirement 12). 101+ days overdue => suspended.
     */
    public static final int SUSPEND_AFTER_DAYS = 100;

    /* =====================================================================
       3. SECURITY - demo accounts
       ---------------------------------------------------------------------
       The admin account is a configuration-level account (Requirement 1).
       The password is NEVER stored in plain text: only its BCrypt hash is
       kept here. BCrypt hashes are one-way, so reading this file does not
       reveal the password. "admin123" is a demo password only.
       ===================================================================== */
    public static final String ADMIN_USERNAME = "admin";

    /** BCrypt hash of "admin123" (cost factor 10). Verified by PasswordUtilTest. */
    public static final String ADMIN_PASSWORD_HASH =
            "$2a$10$F11kkqUg0O0YgRIeWn2mBe5fI1rkCNCOSBxwmkiu6RTp3iIhAo8De";

    /** Demo password used by seed.sql / demo data reset for every member. */
    public static final String DEMO_MEMBER_PASSWORD = "member123";

    /** BCrypt hash of "member123" - used by DemoDataService. */
    public static final String DEMO_MEMBER_PASSWORD_HASH =
            "$2a$10$85/zqDlV9EKoJOwg9NlAU.MQXphreIrDswmIZ660pIIP879HWNDl2";

    /* =====================================================================
       4. DATABASE CONFIGURATION  (read from environment variables)
       ---------------------------------------------------------------------
       Docker Compose sets these automatically. For a manual local install
       the defaults below are used, or you can export the variables first.
       ===================================================================== */
    public static final String DB_URL = env("DB_URL",
            "jdbc:mysql://localhost:3306/library_db"
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC&characterEncoding=UTF-8");

    public static final String DB_USER = env("DB_USER", "root");

    public static final String DB_PASSWORD = env("DB_PASSWORD", "root");

    /** Maximum number of pooled database connections. */
    public static final int DB_POOL_SIZE = Integer.parseInt(env("DB_POOL_SIZE", "10"));

    /* =====================================================================
       5. KEY NAMES used by the app_settings table (simulated date feature)
       ===================================================================== */
    public static final String SETTING_SIMULATED_TODAY = "simulated_today";

    /* =====================================================================
       Helper: read an environment variable with a fallback default.
       ===================================================================== */
    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
