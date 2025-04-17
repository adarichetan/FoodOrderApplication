package com.foodorder.app;


import com.foodorder.app.factory.DaoFactory;

public class Main {
    public static void main(String[] args) {
        DaoFactory.setDaoType(DaoFactory.DaoType.JDBC);
        new FoodOrderApp().run();
    }
}