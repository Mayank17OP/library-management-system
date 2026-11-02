package com.dtc.lms.filter;

import com.dtc.lms.model.Role;
import com.dtc.lms.service.ClockService;
import com.dtc.lms.util.Flash;
import com.dtc.lms.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

/**
 * Role-based access control (a dedicated requirement of the problem statement).
 *
 * <p>Rules:</p>
 * <ul>
 *   <li>{@code /admin/**} may only be opened by a logged-in ADMIN,</li>
 *   <li>{@code /member/**} (dashboard, circulation) only by a logged-in MEMBER,</li>
 *   <li>everything else is public: home, about, both login pages, registration,
 *       CSS, JavaScript and images.</li>
 * </ul>
 *
 * <p>The filter also publishes the "today" date to every page, so the layout can
 * show the orange "Simulated date active" banner.</p>
 *
 * <p>Note: in this project every rule is enforced here <i>and</i> checked again in
 * the service layer (a member cannot return somebody else's loan even with a
 * hand-crafted URL) - defence in depth.</p>
 */
public class AuthFilter implements Filter {

    /** Pages that never need a login. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/home", "/about",
            "/login", "/admin/login", "/member/login", "/member/register",
            "/logout", "/favicon.ico");

    /** Static resources: served without touching the database. */
    private static final Set<String> STATIC_PREFIXES = Set.of("/css/", "/js/", "/images/");

    private final ClockService clockService = new ClockService();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());

        // Static files: no session logic, no database call.
        if (isStatic(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Publish the effective "today" for JSP views (and the demo banner).
        LocalDate today = LocalDate.now();
        LocalDate simulated = null;
        try {
            simulated = clockService.simulatedDate().orElse(null);
            today = simulated != null ? simulated : today;
        } catch (RuntimeException ignored) {
            // Never let a database hiccup break the layout.
        }
        request.setAttribute("today", today);
        request.setAttribute("simulatedDate", simulated);

        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        Role role = SessionUtil.role(request);

        if (path.startsWith("/admin")) {
            if (role == Role.ADMIN) {
                chain.doFilter(request, response);
            } else if (role == Role.MEMBER) {
                Flash.error(request, "That page is only for the librarian.");
                response.sendRedirect(contextPath + "/member/dashboard");
            } else {
                Flash.error(request, "Please log in as the librarian to continue.");
                response.sendRedirect(contextPath + "/admin/login");
            }
            return;
        }

        if (path.startsWith("/member")) {
            if (role == Role.MEMBER) {
                chain.doFilter(request, response);
            } else if (role == Role.ADMIN) {
                Flash.error(request, "You are logged in as the librarian, not as a member.");
                response.sendRedirect(contextPath + "/admin/dashboard");
            } else {
                Flash.error(request, "Please log in as a member to continue.");
                response.sendRedirect(contextPath + "/member/login");
            }
            return;
        }

        // Anything else (should not happen) is allowed and left to the servlet.
        chain.doFilter(request, response);
    }

    private boolean isStatic(String path) {
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".svg")
                || path.endsWith(".ico") || path.endsWith(".png") || path.endsWith(".jpg")) {
            return true;
        }
        return STATIC_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
