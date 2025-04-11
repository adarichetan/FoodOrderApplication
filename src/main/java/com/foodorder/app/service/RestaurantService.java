package com.foodorder.app.service;

import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.Restaurant;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.utility.Response;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Response addRestaurant(Restaurant restaurant);

    Response getRestaurantById(int restaurantId);

    Response getAllRestaurants();

    Response addFood(FoodItem foodItem);

    Response removeFood(String foodItem);

    Response updateFood(FoodItem foodItem);

    Response getAllFood();

    Response getFoodByName(String foodItem);

    Response getFoodByCategory(FoodCategory category);
}