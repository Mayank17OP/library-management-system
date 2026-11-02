package com.dtc.lms.model;

/**
 * The two roles of the system (Requirement: Librarian/Admin and Member).
 * The role is stored in the HTTP session and checked by
 * {@code com.dtc.lms.filter.AuthFilter}.
 */
public enum Role {
    ADMIN("Librarian"),
    MEMBER("Member");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
