package com.foodorder.app.factory;

import com.foodorder.app.dao.*;
import com.foodorder.app.daoImpl.*;
import com.foodorder.app.jdbcImpl.*;

public class DaoFactory {
    public enum DaoType {COLLECTION, JDBC, HIBERNATE}

    private static DaoType currentType = DaoType.JDBC;

    public static void setDaoType(DaoType type) {
        currentType = type;
    }

    public static UserDao getUserDao() {
        return switch (currentType) {
            case JDBC -> UserDaoJdbcImpl.getUserDaoJdbcImpl();
            default -> UserDaoImpl.getUserDaoImpl();
        };
    }

    public static OrderDao getOrderDao(){
        return switch (currentType){
            case JDBC -> OrderDaoJdbcImpl.getOrderDaoJdbcImpl();
            default -> OrderDaoImpl.getOrderDao();
        };
    }
    public static RestaurantDao getRestaurantDao(){
        return switch (currentType){
            case JDBC -> RestaurantDaoJdbcImpl.getRestaurantDaoJdbcImpl();
            default -> RestaurantDaoImpl.getRestaurantDao();
        };
    }

    public static FoodDao getRestaurantFoodDao(){
        return switch (currentType){
            case JDBC -> FoodDaoJdbcImpl.getFoodDaoJdbc();
            default -> FoodDaoImpl.getFoodDaoImpl();
        };
    }
    public static CartDao getCartDao(){
        return switch (currentType){
            case JDBC -> CartDaoJdbcImpl.getCartDaoJdbcImpl();
            default -> CartDaoImpl.getCartDaoImpl();
        };
    }
}