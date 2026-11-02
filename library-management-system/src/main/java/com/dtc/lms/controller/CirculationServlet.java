package com.dtc.lms.controller;

import com.dtc.lms.model.Loan;
import com.dtc.lms.service.CirculationService;
import com.dtc.lms.service.ClockService;
import com.dtc.lms.service.ServiceException;
import com.dtc.lms.util.Flash;
import com.dtc.lms.util.SessionUtil;
import com.dtc.lms.util.Validators;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

/**
 * Issue / return / renew (Requirements 7, 9 and 10) for both roles.
 *
 * <p>The URL decides the role: {@code /admin/circulation} acts as the librarian
 * (may handle any loan), {@code /member/circulation} acts as the logged-in
 * member (only his/her own loans). The servlet itself contains no rules - it
 * validates nothing about fines or limits, it simply calls
 * {@link CirculationService} and turns the result into a flash message.</p>
 */
@WebServlet(urlPatterns = {"/admin/circulation", "/member/circulation"})
public class CirculationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Pages the admin may be sent back to after an action. */
    private static final Set<String> ALLOWED_BACK_PAGES = Set.of("loans", "books", "dashboard", "members");

    private final CirculationService circulationService = new CirculationService();
    private final ClockService clockService = new ClockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = Validators.trim(request.getParameter("action")).toLowerCase();
        boolean isAdmin = request.getServletPath().startsWith("/admin");
        String sessionMemberId = SessionUtil.memberId(request);
        LocalDate today = clockService.today();   // honors the demo date

        try {
            switch (action) {
                case "issue" -> {
                    // The librarian types the member ID; a member can only issue for himself.
                    String memberId = isAdmin
                            ? Validators.trim(request.getParameter("memberId"))
                            : sessionMemberId;
                    Loan loan = circulationService.issueBook(memberId,
                            request.getParameter("bookId"), today);
                    Flash.success(request, "Issued: \"" + loan.getBookTitle() + "\" to "
                            + loan.getMemberId() + ". Due on " + loan.getDueDate() + ".");
                }
                case "return" -> {
                    Loan loan = circulationService.returnBook(parseLoanId(request),
                            sessionMemberId, isAdmin, today);
                    String fineText = loan.getFine().signum() > 0
                            ? " Fine collected: Rs " + loan.getFine() + "."
                            : " No fine.";
                    Flash.success(request, "Returned: \"" + loan.getBookTitle() + "\"."
                            + fineText);
                }
                case "renew" -> {
                    Loan loan = circulationService.renewBook(parseLoanId(request),
                            sessionMemberId, isAdmin, today);
                    Flash.success(request, "Renewed: \"" + loan.getBookTitle()
                            + "\". New due date " + loan.getDueDate() + ".");
                }
                default -> Flash.error(request, "Unknown circulation action.");
            }
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
        }

        response.sendRedirect(resolveRedirect(request, isAdmin));
    }

    /** Reads and validates the loan number coming from the form. */
    private int parseLoanId(HttpServletRequest request) throws ServiceException {
        try {
            return Validators.requirePositiveIntValue(request.getParameter("loanId"), "Loan number");
        } catch (IllegalArgumentException invalid) {
            throw new ServiceException(invalid.getMessage(), invalid);
        }
    }

    /** Post/Redirect/Get back to the page the action started from. */
    private String resolveRedirect(HttpServletRequest request, boolean isAdmin) {
        String context = request.getContextPath();
        if (!isAdmin) {
            return context + "/member/dashboard";
        }
        String back = Validators.trim(request.getParameter("back"));
        if (ALLOWED_BACK_PAGES.contains(back)) {
            return context + "/admin/" + back;
        }
        return context + "/admin/loans";
    }
}
