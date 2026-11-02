package com.dtc.lms.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Web-application life-cycle hook.
 *
 * <p>On start-up we log the active database target; on shut-down we close the
 * connection pool so MySQL does not keep half-open connections around.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().log(
                AppConfig.APP_NAME + " " + AppConfig.APP_VERSION
                        + " starting. Database = " + AppConfig.DB_URL
                        + " (user " + AppConfig.DB_USER + ")");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DBConnection.shutdown();
        event.getServletContext().log(AppConfig.APP_NAME + " stopped, connection pool closed.");
    }
}
