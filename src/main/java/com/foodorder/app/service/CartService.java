package com.foodorder.app.service;

import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.utility.Response;

public interface CartService {

    Response addToCart(User user, FoodItem foodItem, int quantity);

    Response getCartItems(User user);

    Response clearCart(User userId);

    Response updateQuantityFromCart(int id, String foodName, int newQuantity);

    Response deleteFromCart(int id,String name);
}
