package com.dtc.lms.service;

/**
 * A checked exception carrying a message that is safe to show to the user
 * (for example "Book ID B003 already exists", "No copies available").
 *
 * <p>Servlets catch it, put the message in a flash message and redirect - so
 * validation failures are normal control flow, not crashes. Technical failures
 * (like a broken database connection) are wrapped inside it too, and the user
 * sees a friendly sentence instead of a stack trace.</p>
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
