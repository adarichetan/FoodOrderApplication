package com.foodorder.app.utility;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionUtility {
    private static Connection connection;

    private ConnectionUtility() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = ConfigReader.getKey("url");
                String user = ConfigReader.getKey("user");
                String password = ConfigReader.getKey("password");

                connection = DriverManager.getConnection(url, user, password);
                log.info("Database connection established successfully.");

            } catch (SQLException e) {
                log.error("Failed to establish database connection: ", e);
            }
        }
        return connection;
    }

    public static void connectionClose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            log.error("Error occurred while closing the database connection", e);
        }
    }
}
