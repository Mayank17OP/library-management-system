package com.dtc.lms.dao.jdbc;

import com.dtc.lms.dao.SettingsDAO;
import com.dtc.lms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * JDBC implementation of {@link SettingsDAO} for the {@code app_settings} table.
 *
 * <p>It stores the "Simulated Today" date used by the demo. MySQL supports
 * {@code INSERT ... ON DUPLICATE KEY UPDATE}, so saving a setting is a single
 * statement ("upsert").</p>
 */
public class JdbcSettingsDAO implements SettingsDAO {

    @Override
    public Optional<String> get(String key) throws SQLException {
        String sql = "SELECT setting_value FROM app_settings WHERE setting_key = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString(1);
                    return (value == null || value.isBlank()) ? Optional.empty() : Optional.of(value);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void put(String key, String value) throws SQLException {
        String sql = "INSERT INTO app_settings (setting_key, setting_value) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String key) throws SQLException {
        String sql = "DELETE FROM app_settings WHERE setting_key = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.executeUpdate();
        }
    }
}
