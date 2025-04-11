package com.foodorder.app.dao;

import com.foodorder.app.entities.Restaurant;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RestaurantDao {
    boolean addRestaurant(Restaurant restaurant) throws SQLException;

   Optional<Restaurant> getRestaurantById(int restaurantId) throws SQLException;

   List<Restaurant> getAllRestaurants() throws SQLException;

}
