package com.dtc.lms.controller;

import com.dtc.lms.service.BookService;
import com.dtc.lms.service.ReportService;
import com.dtc.lms.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller for the public pages: Home and About (the navigation entry points
 * required by Requirement 15).
 */
@WebServlet(urlPatterns = {"/home", "/about"})
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService bookService = new BookService();
    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Small live numbers on the home page. If the database is not reachable
        // yet, the page must still render - so the error is ignored on purpose.
        try {
            request.setAttribute("stats", reportService.buildStats(LocalDate.now()));
            request.setAttribute("bookCount", bookService.countTitles());
        } catch (ServiceException | RuntimeException ignored) {
            // Database not ready yet: the home page still renders, without live numbers.
            request.setAttribute("bookCount", 0);
        }

        String path = request.getServletPath();
        String view = "/about".equals(path) ? "/WEB-INF/views/about.jsp" : "/WEB-INF/views/home.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }
}
