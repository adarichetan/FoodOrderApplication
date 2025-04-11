package com.foodorder.app.dao;

import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface CartDao {
    boolean addToCart(User user, FoodItem foodItem, int quantity) throws SQLException;

    List<CartItem> getCartItems(User user) throws SQLException;

    boolean clearCart(User userId) throws SQLException;

    boolean updateQuantityFromCart(int id, String foodName, int newQuantity) throws SQLException;

    boolean deleteFromCart(int id,String name) throws SQLException;
}
