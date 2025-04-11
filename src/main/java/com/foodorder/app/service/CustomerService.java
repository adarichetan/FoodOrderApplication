package com.foodorder.app.service;

import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.utility.Response;

public interface CustomerService {
    Response addToCart(User user, FoodItem foodItem, int quantity);

    Response getCart(User user);

    Response placeOrder(User user);

    Response getAllOrders(User customer);

    Response getOrderById(int orderId);

    Response setLoginStatus(String email);

    Response logoutUser(String email);

    Response updateCartQuantity(int id, String foodName, int newQuantity);

    Response deleteFromCart(int id, String foodName);
}
