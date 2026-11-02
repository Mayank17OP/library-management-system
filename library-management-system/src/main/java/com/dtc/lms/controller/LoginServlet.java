package com.dtc.lms.controller;

import com.dtc.lms.model.Member;
import com.dtc.lms.model.Role;
import com.dtc.lms.service.AuthService;
import com.dtc.lms.service.ClockService;
import com.dtc.lms.service.ServiceException;
import com.dtc.lms.util.Flash;
import com.dtc.lms.util.SessionUtil;
import com.dtc.lms.util.Validators;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Login controller for both roles.
 *
 * <ul>
 *   <li>{@code GET /admin/login} and {@code GET /member/login} show the forms.</li>
 *   <li>{@code POST /login} with {@code role=ADMIN|MEMBER} authenticates.</li>
 * </ul>
 *
 * <p>After a successful login the session id is changed
 * ({@code request.changeSessionId()}), which prevents "session fixation"
 * attacks - a standard item in the security checklist of a web project.</p>
 */
@WebServlet(urlPatterns = {"/login", "/admin/login", "/member/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();
    private final ClockService clockService = new ClockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/login".equals(path)) {
            if (SessionUtil.isAdmin(request)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(request, response);
            return;
        }

        if ("/member/login".equals(path)) {
            if (SessionUtil.isMember(request)) {
                response.sendRedirect(request.getContextPath() + "/member/dashboard");
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/member-login.jsp").forward(request, response);
            return;
        }

        // Plain /login has no form of its own - send the visitor home.
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = Validators.trim(request.getParameter("role")).toUpperCase();

        if ("ADMIN".equals(role)) {
            handleAdminLogin(request, response);
        } else if ("MEMBER".equals(role)) {
            handleMemberLogin(request, response);
        } else {
            Flash.error(request, "Please choose a valid login type.");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    /* ---------------------- librarian login (Requirement 1) ---------------------- */
    private void handleAdminLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!authService.authenticateAdmin(username, password)) {
            Flash.error(request, "Invalid librarian username or password.");
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        startSession(request, Role.ADMIN, null, "Librarian");
        Flash.success(request, "Welcome back, Librarian. You are logged in.");
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    /* ------------------------ member login (Requirement 6) ------------------------ */
    private void handleMemberLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Member member = authService.loginMember(
                    request.getParameter("memberId"),
                    request.getParameter("password"),
                    clockService.today());   // Requirement 12: suspension checked at login

            startSession(request, Role.MEMBER, member.getMemberId(), member.getName());

            if (member.isSuspended()) {
                Flash.error(request, "Your membership is SUSPENDED: a book is more than 100 days "
                        + "overdue. Please return it at the library counter.");
            } else {
                Flash.success(request, "Welcome, " + member.getName() + "!");
            }
            response.sendRedirect(request.getContextPath() + "/member/dashboard");

        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/member/login");
        }
    }

    /** Creates a fresh session for the logged-in user (prevents session fixation). */
    private void startSession(HttpServletRequest request, Role role, String memberId, String displayName) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();          // new id, old data is kept
        session.setAttribute(SessionUtil.ROLE, role.name());
        session.setAttribute(SessionUtil.DISPLAY_NAME, displayName);
        if (memberId != null) {
            session.setAttribute(SessionUtil.MEMBER_ID, memberId);
        }
        session.setMaxInactiveInterval(30 * 60);   // 30 minutes
    }
}
