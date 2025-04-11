package com.foodorder.app.sqlqueries;

public class FoodSqlQueries {

    private FoodSqlQueries() {
    }

    public static final String INSERT_FOOD = "INSERT INTO food (name, price,category,restaurant_id) VALUES (?,?,?,?)";

    public static final String SELECT_ALL_FOOD = "SELECT * FROM food";

    public static final String UPDATE_FOOD = "UPDATE food SET name =?, price=?, category=? WHERE id = ?";

    public static final String DELETE_FOOD_BY_ID = "DELETE FROM food WHERE id =?";

    public static final String SELECT_FOOD_BY_CATEGORY = """
            SELECT id, name, price, category, restaurant_id
            FROM food
            WHERE category = ?
            """;

    public static final String SELECT_FOOD_BY_ID = """
            SELECT id, name, price, category, restaurant_id
            FROM food
            WHERE id = ?
            """;

}
