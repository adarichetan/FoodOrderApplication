package com.foodorder.app.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
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
        if (connection == null) {
            try {
                Properties properties = new Properties();
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
                properties.load(fileInputStream);
                String url = properties.getProperty("url");
                connection = DriverManager.getConnection(url, properties);

            } catch (SQLException | IOException e) {
                log.error("properties file not found");
            }
        }
        return connection;
    }

    public static void connectionClose() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Error occurred while terminating connection", e);
        }
    }
}
