package com.dtc.lms.dao;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Key/value access to the {@code app_settings} table.
 *
 * <p>It is used for the demo feature "Simulated Today": the admin can store a
 * fake current date in the database, and every screen then behaves as if that
 * date were today (to show fines, renewals and suspensions instantly).</p>
 */
public interface SettingsDAO {

    Optional<String> get(String key) throws SQLException;

    /** Inserts the key or replaces its value. */
    void put(String key, String value) throws SQLException;

    void delete(String key) throws SQLException;
}
