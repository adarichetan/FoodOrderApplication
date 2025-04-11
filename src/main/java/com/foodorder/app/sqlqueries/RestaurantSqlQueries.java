package com.foodorder.app.sqlqueries;

public class RestaurantSqlQueries {
    private RestaurantSqlQueries() {
    }

    public static final String INSERT_RESTAURANT = "INSERT INTO restaurants (id, name) VALUES (?,?)";

    public static final String SELECT_ALL_RESTAURANTS = "SELECT * FROM restaurants";

    public static final String SELECT_RESTAURANT_BY_ID = """
            SELECT id, name FROM restaurants
            WHERE id =?
            """;
}
