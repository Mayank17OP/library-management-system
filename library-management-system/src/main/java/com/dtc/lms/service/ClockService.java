package com.dtc.lms.service;

import com.dtc.lms.dao.SettingsDAO;
import com.dtc.lms.dao.jdbc.JdbcSettingsDAO;
import com.dtc.lms.util.AppConfig;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Decides what "today" means for the application.
 *
 * <p>Normally that is the real date. For the demo the admin can store a
 * "Simulated Today" date in the {@code app_settings} table; every screen then
 * uses that date, so overdue fines, renewals and automatic suspension can be
 * demonstrated instantly instead of waiting 100 real days.</p>
 *
 * <p>Because the date comes from one place, no business rule has to know about
 * the demo feature - services simply receive the date as a parameter.</p>
 */
public class ClockService {

    private final SettingsDAO settingsDAO;

    public ClockService() {
        this(new JdbcSettingsDAO());
    }

    public ClockService(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }

    /** The date the application should behave as "today". Never null. */
    public LocalDate today() {
        return simulatedDate().orElseGet(LocalDate::now);
    }

    /** Empty when the real system date is used. */
    public Optional<LocalDate> simulatedDate() {
        try {
            Optional<String> stored = settingsDAO.get(AppConfig.SETTING_SIMULATED_TODAY);
            if (stored.isPresent()) {
                return Optional.of(LocalDate.parse(stored.get()));
            }
        } catch (SQLException | DateTimeParseException ex) {
            // A missing settings table or a corrupted value must never break
            // the whole application - fall back to the real date.
            return Optional.empty();
        }
        return Optional.empty();
    }

    public boolean isSimulated() {
        return simulatedDate().isPresent();
    }

    /** Activates the demo date. */
    public void setSimulatedDate(LocalDate date) throws ServiceException {
        try {
            settingsDAO.put(AppConfig.SETTING_SIMULATED_TODAY, date.toString());
        } catch (SQLException ex) {
            throw new ServiceException("Could not save the simulated date: " + ex.getMessage(), ex);
        }
    }

    /** Returns to the real system date. */
    public void clearSimulatedDate() throws ServiceException {
        try {
            settingsDAO.delete(AppConfig.SETTING_SIMULATED_TODAY);
        } catch (SQLException ex) {
            throw new ServiceException("Could not clear the simulated date: " + ex.getMessage(), ex);
        }
    }
}
