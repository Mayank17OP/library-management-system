package com.dtc.lms.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gives out JDBC connections from a HikariCP pool.
 *
 * <p>Why a pool? Opening a physical MySQL connection is expensive (network
 * handshake + authentication). A pool keeps a small number of connections open
 * and recycles them, which is what makes a web application fast.</p>
 *
 * <p>The pool is created lazily on the first request and closed when the web
 * application stops (see {@link AppContextListener}).</p>
 */
public final class DBConnection {

    private static volatile HikariDataSource dataSource;

    /**
     * The driver class is loaded explicitly.
     *
     * <p>Reason: under Tomcat, {@code DriverManager} lives in the container's
     * class loader and does not see the JDBC driver packaged inside the WAR,
     * which normally produces the confusing error "No suitable driver". Naming
     * the class here (and in HikariConfig) removes that problem and also gives a
     * clear error message when the JAR is really missing.</p>
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException missingDriver) {
            throw new IllegalStateException(
                    "MySQL JDBC driver not found on the classpath: " + missingDriver.getMessage(),
                    missingDriver);
        }
    }

    private DBConnection() {
    }

    /** @return the shared pool (created on first use). */
    public static HikariDataSource getDataSource() {
        HikariDataSource local = dataSource;
        if (local == null) {
            synchronized (DBConnection.class) {
                local = dataSource;
                if (local == null) {
                    local = createDataSource();
                    dataSource = local;
                }
            }
        }
        return local;
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(AppConfig.DB_URL);
        config.setUsername(AppConfig.DB_USER);
        config.setPassword(AppConfig.DB_PASSWORD);
        config.setMaximumPoolSize(AppConfig.DB_POOL_SIZE);
        config.setPoolName("lms-pool");
        // Do not stop Tomcat from starting when MySQL is temporarily down:
        // the error is reported per request instead, with a friendly page.
        config.setInitializationFailTimeout(-1);
        return new HikariDataSource(config);
    }

    /**
     * Borrows a connection from the pool.
     * Callers must close it (try-with-resources) so it returns to the pool.
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Closes the pool - called when the application stops. */
    public static void shutdown() {
        HikariDataSource local = dataSource;
        if (local != null) {
            local.close();
            dataSource = null;
        }
    }
}
