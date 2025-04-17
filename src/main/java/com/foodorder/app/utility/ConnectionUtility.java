package com.foodorder.app.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionUtility {
    private static Connection connection;

    private ConnectionUtility() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                Properties properties = new Properties();
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
                properties.load(fileInputStream);
                String url = properties.getProperty("url");
                connection = DriverManager.getConnection(url, properties);

            }
        }catch (SQLException | IOException e) {
                log.error("Failed to establish database connection: ", e);
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
