package com.foodorder.app.factory;

import com.foodorder.app.dao.*;
import com.foodorder.app.daoImpl.*;
import com.foodorder.app.hibernateImpl.*;
import com.foodorder.app.jdbcImpl.*;
import com.foodorder.app.utility.ConnectionUtility;
import jakarta.persistence.EntityManager;

public class DaoFactory {
    public enum DaoType {COLLECTION, JDBC, HIBERNATE}

    private static DaoType currentType = DaoType.JDBC;

    public static void setDaoType(DaoType type) {
        currentType = type;
    }

    final static EntityManager manager = ConnectionUtility.getEntityManager();

    public static UserDao getUserDao() {
        return switch (currentType) {
            case JDBC -> UserDaoJdbcImpl.getUserDaoJdbcImpl();
            case HIBERNATE -> new UserDaoHibernateImpl(manager, manager.getTransaction());
            default -> UserDaoImpl.getUserDaoImpl();
        };
    }

    public static OrderDao getOrderDao(){
        return switch (currentType){
            case JDBC -> OrderDaoJdbcImpl.getOrderDaoJdbcImpl();
            case HIBERNATE -> new OrderDaoHibernateImpl(manager, manager.getTransaction());
            default -> OrderDaoImpl.getOrderDao();
        };
    }
    public static RestaurantDao getRestaurantDao(){
        return switch (currentType){
            case JDBC -> RestaurantDaoJdbcImpl.getRestaurantDaoJdbcImpl();
            case HIBERNATE -> new RestaurantDaoHibernateImpl(manager, manager.getTransaction());
            default -> RestaurantDaoImpl.getRestaurantDao();
        };
    }

    public static FoodDao getRestaurantFoodDao(){
        return switch (currentType){
            case JDBC -> FoodDaoJdbcImpl.getFoodDaoJdbc();
            case HIBERNATE -> new FoodDaoHibernateImpl(manager, manager.getTransaction());
            default -> FoodDaoImpl.getFoodDaoImpl();
        };
    }
    public static CartDao getCartDao(){
        return switch (currentType){
            case JDBC -> CartDaoJdbcImpl.getCartDaoJdbcImpl();
            case HIBERNATE -> new CartDaoHibernateImpl(manager, manager.getTransaction());
            default -> CartDaoImpl.getCartDaoImpl();
        };
    }
}