package com.foodorder.app;

import com.foodorder.app.factory.DaoFactory;
import com.foodorder.app.utility.ConfigReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            String daoType = ConfigReader.getKey("dao.type");
            DaoFactory.setDaoType(daoType != null ? DaoFactory.DaoType.valueOf(daoType.toUpperCase()): DaoFactory.DaoType.JDBC);
        } catch (IllegalArgumentException e) {
            log.error("Invalid dao.type in config.properties, Using default: JDBC.", e);
        }

        new FoodOrderApp().run();
    }
}