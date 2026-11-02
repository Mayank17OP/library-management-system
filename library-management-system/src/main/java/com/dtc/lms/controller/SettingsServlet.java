package com.dtc.lms.controller;

import com.dtc.lms.service.ClockService;
import com.dtc.lms.service.DemoDataService;
import com.dtc.lms.service.ServiceException;
import com.dtc.lms.util.Flash;
import com.dtc.lms.util.Validators;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Admin-only demo controls (Requirement: "Simulated Today" + "Reset Demo Data").
 *
 * <ul>
 *   <li>{@code action=set-date}      with a {@code date=yyyy-MM-dd} parameter</li>
 *   <li>{@code action=shift}         with {@code days=5} (a shortcut: today + 5 days)</li>
 *   <li>{@code action=clear-date}    back to the real date</li>
 *   <li>{@code action=reset-demo}    reload the whole demo data set</li>
 * </ul>
 */
@WebServlet("/admin/settings")
public class SettingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ClockService clockService = new ClockService();
    private final DemoDataService demoDataService = new DemoDataService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = Validators.trim(request.getParameter("action")).toLowerCase();

        try {
            switch (action) {
                case "set-date" -> {
                    LocalDate date = parseDate(request.getParameter("date"));
                    clockService.setSimulatedDate(date);
                    Flash.info(request, "Simulated date is now " + date
                            + ". Fines, renewals and suspensions use this date.");
                }
                case "shift" -> {
                    int days = Integer.parseInt(Validators.requireText(
                            request.getParameter("days"), "Days"));
                    LocalDate date = LocalDate.now().plusDays(days);
                    clockService.setSimulatedDate(date);
                    Flash.info(request, "Simulated date moved " + days + " day(s) ahead: " + date + ".");
                }
                case "clear-date" -> {
                    clockService.clearSimulatedDate();
                    Flash.success(request, "Simulated date cleared. The application uses the real date again.");
                }
                case "reset-demo" -> {
                    demoDataService.resetDemoData();
                    Flash.success(request, "Demo data reset: 12 books, 4 members and 5 loans restored "
                            + "(1 on time, 1 five days overdue, 1 more than 100 days overdue).");
                }
                default -> Flash.error(request, "Unknown settings action.");
            }
        } catch (DateTimeParseException | NumberFormatException invalidNumber) {
            Flash.error(request, "Please enter a valid date (yyyy-MM-dd) or number of days.");
        } catch (IllegalArgumentException invalid) {
            Flash.error(request, invalid.getMessage());
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private LocalDate parseDate(String value) {
        String clean = Validators.requireText(value, "Date");
        return LocalDate.parse(clean);   // yyyy-MM-dd because of <input type="date">
    }
}
