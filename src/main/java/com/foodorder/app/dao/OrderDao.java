package com.foodorder.app.dao;

import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Optional<Order> placeOrder(User order,List<CartItem> cartItems ) throws SQLException;

    Optional<Order> getOrderById(int id) throws SQLException;

    List<Order> getAllOrders() throws SQLException;

    boolean updateOrderStatus(Order order) throws SQLException;

}