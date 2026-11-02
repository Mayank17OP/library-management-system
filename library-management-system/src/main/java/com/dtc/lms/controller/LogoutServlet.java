package com.dtc.lms.controller;

import com.dtc.lms.util.Flash;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Ends the session (Requirement 15: logout from every page).
 * Both GET and POST work, because the navigation bar uses a simple link.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logout(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();          // removes role, member id and every flash message
        }
        // A brand-new session only carries the goodbye message.
        Flash.info(request, "You have been logged out successfully.");
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
