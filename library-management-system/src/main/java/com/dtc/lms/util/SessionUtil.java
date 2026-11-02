package com.dtc.lms.util;

import com.dtc.lms.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * One place that knows the names of the session attributes.
 *
 * <p>Using constants (instead of typing "role" everywhere) removes a whole class
 * of bugs: a typo becomes a compile error instead of a user who appears logged
 * out.</p>
 */
public final class SessionUtil {

    public static final String ROLE = "role";
    public static final String MEMBER_ID = "memberId";
    public static final String DISPLAY_NAME = "displayName";

    private SessionUtil() {
    }

    /** @return the role of the logged-in user, or null for a guest. */
    public static Role role(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(ROLE);
        return value instanceof String name ? safeRole(name) : null;
    }

    public static String memberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(MEMBER_ID);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return role(request) == Role.ADMIN;
    }

    public static boolean isMember(HttpServletRequest request) {
        return role(request) == Role.MEMBER;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return role(request) != null;
    }

    private static Role safeRole(String name) {
        try {
            return Role.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
