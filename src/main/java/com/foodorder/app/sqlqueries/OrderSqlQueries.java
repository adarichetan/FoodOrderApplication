package com.foodorder.app.sqlqueries;

public class OrderSqlQueries {
    private OrderSqlQueries() {
    }

    public static final String INSERT_ORDER = "INSERT INTO orders (user_id, order_status, order_on,total_bill) VALUES (?,?,?,?)";

    public static final String INSERT_ORDER_ITEM = "INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (?,?,?,?)";

    public static final String UPDATE_TOTAL_BILL_BY_ORDER_ID = "UPDATE orders SET total_bill =? WHERE id =?";

    public static final String DELETE_ALL_CART_ITEM_BY_USER_ID = "DELETE FROM cart_items WHERE user_id =?";

    public static final String UPDATE_ORDER_STATUS_BY_ID = """
            UPDATE orders SET order_status = ?
            WHERE id = ?
            """;

    public static final String SELECT_ORDER_BY_ID = """
            SELECT id, user_id, order_on, order_status,total_bill
            FROM orders
            WHERE id = ?
            """;

    public static final String SELECT_ORDER_ITEMS_BY_ORDER_ID = """
                SELECT oi.id, oi.food_id, oi.quantity, oi.price, f.name AS food_name, f.category, f.restaurant_id
                FROM order_items oi
                JOIN food f ON oi.food_id = f.id
                WHERE oi.order_id = ?
            """;

    public static final String SELECT_CART_ITEMS_BY_USER_ID = """
                SELECT c.id, c.quantity, f.id AS food_id, f.name, f.price, f.category, f.restaurant_id
                FROM cart_items c
                JOIN food f ON c.food_id = f.id
                WHERE c.user_id = ?
            """;

    public static final String SELECT_ALL_ORDERS = """
                SELECT o.id, o.user_id, o.order_on, o.order_status, o.total_bill,
                       oi.food_id, oi.quantity, oi.price,
                       f.name AS food_name, f.category, f.restaurant_id
                FROM orders o
                INNER JOIN order_items oi ON o.id = oi.order_id
                INNER JOIN food f ON oi.food_id = f.id
                ORDER BY o.id;
            """;
}