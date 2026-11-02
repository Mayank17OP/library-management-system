package com.dtc.lms.support;

import com.dtc.lms.dao.SettingsDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** In-memory test double for {@link SettingsDAO} (the app_settings table). */
public class InMemorySettingsDAO implements SettingsDAO {

    private final Map<String, String> table = new HashMap<>();

    @Override
    public Optional<String> get(String key) {
        String value = table.get(key);
        return (value == null || value.isBlank()) ? Optional.empty() : Optional.of(value);
    }

    @Override
    public void put(String key, String value) {
        table.put(key, value);
    }

    @Override
    public void delete(String key) {
        table.remove(key);
    }
}
