package com.foodorder.app.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigReader {
    private static final Properties properties = new Properties();

    private ConfigReader() {

    }

    static {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/config.properties");
            properties.load(input);
        } catch (IOException e) {
            log.error("failed to load config file..");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
