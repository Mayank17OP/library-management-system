package com.dtc.lms.model;

/**
 * Status of a member account.
 * A member becomes SUSPENDED automatically when a book is more than
 * {@code AppConfig.SUSPEND_AFTER_DAYS} days overdue (Requirement 12).
 */
public enum MemberStatus {
    ACTIVE,
    SUSPENDED
}
