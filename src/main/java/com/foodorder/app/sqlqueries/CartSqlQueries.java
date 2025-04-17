package com.foodorder.app.sqlqueries;

public class CartSqlQueries {

    private CartSqlQueries() {
    }

    public static final String UPDATE_CART_QUANTITY_INCREMENT = "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";

    public static final String INSERT_CART_ITEM = "INSERT INTO cart_items (user_id, food_id, quantity) VALUES (?,?,?)";

    public static final String DELETE_CART_ITEMS_BY_USER_ID = "DELETE FROM cart_items WHERE user_id = ?";

    public static final String SELECT_FOOD_ID_BY_NAME = " SELECT id FROM food WHERE name ILIKE ?";

    public static final String SELECT_CART_ITEMS_BY_USER_ID = """
                SELECT c.id, c.quantity, f.id AS food_id, f.name, f.price, f.category, f.restaurant_id
                FROM cart_items c
                JOIN food f ON c.food_id = f.id
                WHERE c.user_id = ?
            """;

    public static final String SELECT_CART_ITEM_BY_USER_AND_FOOD_ID = """
            SELECT id,quantity from cart_items
            WHERE user_id = ? AND food_id = ?
            """;

    public static final String UPDATE_CART_QUANTITY = """
            UPDATE cart_items SET quantity =?
            WHERE user_id =? AND food_id =?
            """;

    public static final String DELETE_CART_ITEM_BY_USER_ID_AND_FOOD_ID = """
            DELETE FROM cart_items
            WHERE user_id = ? AND food_id = ?
            """;
}
