package com.foodorder.app.jdbcImpl;

import com.foodorder.app.sqlqueries.OrderSqlQueries;
import com.foodorder.app.dao.OrderDao;
import com.foodorder.app.entities.*;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.enums.OrderStatus;
import com.foodorder.app.utility.ConnectionUtility;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrderDaoJdbcImpl implements OrderDao {
    private static final OrderDaoJdbcImpl orderDao = new OrderDaoJdbcImpl();
    private Connection con;

    private OrderDaoJdbcImpl() {
        initSqlDataConnection();
    }

    public static OrderDaoJdbcImpl getOrderDaoJdbcImpl() {
        return orderDao;
    }

    void initSqlDataConnection() {
        this.con = ConnectionUtility.getConnection();
    }

    @Override
    public Optional<Order> placeOrder(User user) throws SQLException {


        try (PreparedStatement ps = con.prepareStatement(OrderSqlQueries.INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, user.getUserId());
            ps.setString(2, OrderStatus.ORDERED.name());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

                    ps.setDouble(4, 0.0);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return Optional.empty();

            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) return Optional.empty();

            int orderId = rs.getInt(1);
            List<CartItem> cartItems = getCartItemsByUserId(user);
            if (cartItems.isEmpty()) {
                return Optional.empty();
            }

            double totalBill = 0.0;

            try (PreparedStatement orderItemStmt = con.prepareStatement(OrderSqlQueries.INSERT_ORDER_ITEM)) {
                orderItemStmt.setInt(1, orderId);
                for (CartItem cartItem : cartItems) {
                    double itemTotal = cartItem.getQuantity() * cartItem.getFoodItem().getPrice();
                    totalBill += itemTotal;

                    orderItemStmt.setInt(2, cartItem.getFoodItem().getId());
                    orderItemStmt.setInt(3, cartItem.getQuantity());
                    orderItemStmt.setDouble(4, itemTotal);
                    orderItemStmt.addBatch();
                }
                orderItemStmt.executeBatch();
            }
            try (PreparedStatement ps2 = con.prepareStatement(OrderSqlQueries.UPDATE_TOTAL_BILL_BY_ORDER_ID)) {
                ps2.setDouble(1, totalBill);
                ps2.setInt(2, orderId);
                ps2.executeUpdate();
            }

            try (PreparedStatement clearCart = con.prepareStatement(OrderSqlQueries.DELETE_ALL_CART_ITEM_BY_USER_ID)) {
                clearCart.setInt(1, user.getUserId());
                clearCart.executeUpdate();
            }

            Order order = Order.builder()
                    .id(orderId)
                    .user(user)
                    .orderOn(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                    .orderStatus(OrderStatus.ORDERED)
                    .orderItems(cartItems.stream()
                            .map(cartItem -> OrderItem.builder()
                                    .foodItem(cartItem.getFoodItem())
                                    .quantity(cartItem.getQuantity())
                                    .build())
                            .toList())
                    .totalBill(totalBill)
                    .build();

            return Optional.of(order);
        }
    }

    private List<CartItem> getCartItemsByUserId(User user) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(OrderSqlQueries.SELECT_CART_ITEMS_BY_USER_ID)) {
            stmt.setInt(1, user.getUserId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = FoodItem.builder()
                        .id(rs.getInt("food_id"))
                        .name(rs.getString("name"))
                        .price(rs.getDouble("price"))
                        .category(FoodCategory.valueOf(rs.getString("category")))
                        .restaurantId(rs.getInt("restaurant_id"))
                        .build();

                CartItem cartItem = CartItem.builder().
                        id(rs.getInt("id")).
                        foodItem(foodItem)
                        .quantity(rs.getInt("quantity")).
                        build();
                cartItems.add(cartItem);
            }
        }
        return cartItems;
    }

    private List<OrderItem> getOrderedItems(int orderId) throws SQLException {

        List<OrderItem> orderedItems = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(OrderSqlQueries.SELECT_ORDER_ITEMS_BY_ORDER_ID)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FoodItem foodItem = FoodItem.builder()
                        .id(rs.getInt("food_id"))
                        .name(rs.getString("food_name"))
                        .price(rs.getDouble("price"))
                        .category(FoodCategory.valueOf(rs.getString("category")))
                        .restaurantId(rs.getInt("restaurant_id"))
                        .build();

                OrderItem orderItem = OrderItem.builder()
                        .id(rs.getInt("id"))
                        .foodItem(foodItem)
                        .quantity(rs.getInt("quantity"))
                        .price(rs.getDouble("price"))
                        .build();

                orderedItems.add(orderItem);
            }
        }
        return orderedItems;
    }

    @Override
    public Optional<Order> getOrderById(int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(OrderSqlQueries.SELECT_ORDER_BY_ID)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                int orderId = resultSet.getInt("id");
                List<OrderItem> orderedItems = getOrderedItems(orderId);

                Order order = Order.builder()
                        .id(resultSet.getInt("id"))
                        .user(User.builder().userId(resultSet.getInt("user_id")).build())
                        .orderItems(orderedItems)
                        .orderStatus(OrderStatus.valueOf(resultSet.getString("order_status")))
                        .orderOn(Timestamp.valueOf(resultSet.getTimestamp("order_on").toLocalDateTime().truncatedTo(ChronoUnit.SECONDS)))
                        .totalBill(resultSet.getDouble("total_bill"))
                        .build();
                return Optional.ofNullable(order);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(OrderSqlQueries.SELECT_ALL_ORDERS)) {
            ResultSet rs = ps.executeQuery();

            int lastOrderId = -1;
            Order.OrderBuilder orderBuilder = null;
            List<OrderItem> orderItems = null;

            while (rs.next()) {
                int orderId = rs.getInt("id");
                if (orderId != lastOrderId) {
                    if (orderBuilder != null) {
                        orderList.add(orderBuilder.orderItems(orderItems).build());
                    }

                    orderBuilder = Order.builder()
                            .id(orderId)
                            .user(User.builder().userId(rs.getInt("user_id"))
                                    .build())
                            .orderOn(rs.getTimestamp("order_on"))
                            .orderStatus(OrderStatus.valueOf(rs.getString("order_status")))
                            .totalBill(rs.getDouble("total_bill"));

                    orderItems = new ArrayList<>();
                    lastOrderId = orderId;
                }

                FoodItem foodItem = FoodItem.builder()
                        .id(rs.getInt("food_id"))
                        .name(rs.getString("food_name"))
                        .price(rs.getDouble("price"))
                        .category(FoodCategory.valueOf(rs.getString("category")))
                        .restaurantId(rs.getInt("restaurant_id"))
                        .build();

                OrderItem orderItem = OrderItem.builder()
                        .foodItem(foodItem)
                        .price(rs.getDouble("price"))
                        .quantity(rs.getInt("quantity"))
                        .build();

                orderItems.add(orderItem);
            }
            if (orderBuilder != null) {
                orderList.add(orderBuilder.orderItems(orderItems).build());
            }
        }
        return orderList;
    }

    @Override
    public boolean updateOrderStatus(Order order) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(OrderSqlQueries.UPDATE_ORDER_STATUS_BY_ID)) {
            ps.setString(1, order.getOrderStatus().name());
            ps.setInt(2, order.getId());
            return ps.executeUpdate() > 0;
        }
    }
}