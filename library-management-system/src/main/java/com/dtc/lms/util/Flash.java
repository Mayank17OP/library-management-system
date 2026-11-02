package com.dtc.lms.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Tiny helper for "flash messages": a message that survives one redirect and is
 * then displayed once (and removed) by the JSP header.
 *
 * <p>This is how the application follows the Post/Redirect/Get pattern: the
 * servlet saves a message in the session and redirects, so refreshing the page
 * does not repeat the last action.</p>
 */
public final class Flash {

    public static final String SUCCESS = "flashSuccess";
    public static final String ERROR = "flashError";
    public static final String INFO = "flashInfo";

    private Flash() {
    }

    public static void success(HttpServletRequest request, String message) {
        put(request, SUCCESS, message);
    }

    public static void error(HttpServletRequest request, String message) {
        put(request, ERROR, message);
    }

    public static void info(HttpServletRequest request, String message) {
        put(request, INFO, message);
    }

    private static void put(HttpServletRequest request, String key, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(key, message);
    }
}
