package com.dtc.lms.controller;

import com.dtc.lms.model.Loan;
import com.dtc.lms.model.Member;
import com.dtc.lms.service.AuthService;
import com.dtc.lms.service.BookService;
import com.dtc.lms.service.CirculationService;
import com.dtc.lms.service.ClockService;
import com.dtc.lms.service.ReportService;
import com.dtc.lms.service.ServiceException;
import com.dtc.lms.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Admin side of the application:
 * the dashboard (stat cards + books + active loans + simulated date control),
 * the loan register and the reports page (Requirement 13).
 *
 * <p>Loading the dashboard also re-evaluates every member's suspension status,
 * which is the "check on dashboard load" part of Requirement 12.</p>
 */
@WebServlet(urlPatterns = {"/admin/dashboard", "/admin/loans", "/admin/reports"})
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReportService reportService = new ReportService();
    private final CirculationService circulationService = new CirculationService();
    private final BookService bookService = new BookService();
    private final AuthService authService = new AuthService();
    private final ClockService clockService = new ClockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LocalDate today = clockService.today();
        String path = request.getServletPath();

        try {
            request.setAttribute("today", today);
            request.setAttribute("stats", reportService.buildStats(today));

            switch (path) {
                case "/admin/loans" -> {
                    request.setAttribute("loans", circulationService.listAllLoans(today));
                    request.getRequestDispatcher("/WEB-INF/views/admin/loans.jsp")
                            .forward(request, response);
                }
                case "/admin/reports" -> {
                    request.setAttribute("loans", circulationService.listAllLoans(today));
                    request.setAttribute("members", authService.listMembers());
                    request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp")
                            .forward(request, response);
                }
                default -> {
                    refreshAllSuspensions(today);
                    request.setAttribute("stats", reportService.buildStats(today));
                    request.setAttribute("loans", circulationService.listActiveLoans(today));
                    request.setAttribute("books", bookService.listAll());
                    request.setAttribute("members", authService.listMembers());
                    request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                            .forward(request, response);
                }
            }
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        }
    }

    /** Requirement 12: applies the 100-day rule to every member. */
    private void refreshAllSuspensions(LocalDate today) {
        try {
            List<Member> members = authService.listMembers();
            for (Member member : members) {
                circulationService.refreshSuspension(member.getMemberId(), today);
            }
        } catch (ServiceException ignored) {
            // A status refresh must never stop the dashboard from loading.
        }
    }

    /** Kept for readability: the dashboard also shows how many loans are out. */
    @SuppressWarnings("unused")
    private long countActive(List<Loan> loans) {
        return loans.stream().filter(Loan::isActive).count();
    }
}
