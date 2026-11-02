package com.dtc.lms.controller;

import com.dtc.lms.model.Book;
import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.service.AuthService;
import com.dtc.lms.service.BookService;
import com.dtc.lms.service.CirculationService;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Member related controller.
 *
 * <ul>
 *   <li>{@code GET  /admin/members}     - the librarian sees all members</li>
 *   <li>{@code POST /admin/members}     - the librarian registers a member (Requirement 5)</li>
 *   <li>{@code GET  /member/register}   - self-registration form</li>
 *   <li>{@code POST /member/register}   - self-registration (Requirement 5)</li>
 *   <li>{@code GET  /member/dashboard}  - member home: browse/search books + "My Loans" (Requirements 8)</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/admin/members", "/member/dashboard", "/member/register"})
public class MemberServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();
    private final BookService bookService = new BookService();
    private final CirculationService circulationService = new CirculationService();
    private final ClockService clockService = new ClockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/admin/members" -> showMembersPage(request, response);
            case "/member/dashboard" -> showMemberDashboard(request, response);
            case "/member/register" -> {
                if (SessionUtil.isMember(request)) {
                    response.sendRedirect(request.getContextPath() + "/member/dashboard");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/member/register.jsp")
                        .forward(request, response);
            }
            default -> response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String path = request.getServletPath();
        String action = Validators.trim(request.getParameter("action"));

        try {
            if ("/admin/members".equals(path) && "add".equals(action)) {
                Member member = authService.registerMember(
                        request.getParameter("memberId"),
                        request.getParameter("name"),
                        request.getParameter("password"),
                        null);
                Flash.success(request, "Member registered: " + member.getMemberId()
                        + " - " + member.getName() + " (status ACTIVE).");
                response.sendRedirect(request.getContextPath() + "/admin/members");

            } else if ("/member/register".equals(path)) {
                Member member = authService.registerMember(
                        request.getParameter("memberId"),
                        request.getParameter("name"),
                        request.getParameter("password"),
                        request.getParameter("confirmPassword"));
                Flash.success(request, "Registration successful for " + member.getMemberId()
                        + ". Please log in with your password.");
                response.sendRedirect(request.getContextPath() + "/member/login");

            } else {
                Flash.error(request, "Unknown action.");
                response.sendRedirect(request.getContextPath() + "/admin/members");
            }
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
            // Send the user back to the form that failed.
            response.sendRedirect(request.getContextPath()
                    + ("/member/register".equals(path) ? "/member/register" : "/admin/members"));
        }
    }

    /* ------------------------------ admin: all members ------------------------------ */
    private void showMembersPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("members", authService.listMembers());
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
            request.setAttribute("members", List.of());
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/members.jsp").forward(request, response);
    }

    /* ------------------------- member: dashboard + my loans ------------------------- */
    private void showMemberDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String memberId = SessionUtil.memberId(request);
        if (memberId == null) {
            response.sendRedirect(request.getContextPath() + "/member/login");
            return;
        }

        LocalDate today = clockService.today();
        String keyword = Validators.trim(request.getParameter("q"));

        try {
            // Requirement 12: re-check the suspension rule whenever the dashboard loads.
            circulationService.refreshSuspension(memberId, today);

            Member member = authService.requireMember(memberId);
            List<Loan> myLoans = circulationService.listLoansForMember(memberId, today);
            List<Book> books = keyword.isEmpty() ? bookService.listAll() : bookService.search(keyword);

            long openLoans = myLoans.stream().filter(Loan::isActive).count();
            long overdueLoans = myLoans.stream().filter(loan -> loan.isActive() && loan.getDaysOverdue() > 0).count();

            request.setAttribute("member", member);
            request.setAttribute("myLoans", myLoans);
            request.setAttribute("books", books);
            request.setAttribute("keyword", keyword);
            request.setAttribute("openLoans", openLoans);
            request.setAttribute("overdueLoans", overdueLoans);
            request.setAttribute("today", today);

        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/views/member/dashboard.jsp").forward(request, response);
    }
}
