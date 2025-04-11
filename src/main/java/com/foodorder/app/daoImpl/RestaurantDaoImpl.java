package com.foodorder.app.daoImpl;


import com.foodorder.app.dao.RestaurantDao;
import com.foodorder.app.entities.Restaurant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RestaurantDaoImpl implements RestaurantDao {
    private final List<Restaurant> restaurants = Collections.synchronizedList (new ArrayList<>());
    @Getter
    private static final RestaurantDaoImpl restaurantDao = new RestaurantDaoImpl();

    private void initRestaurants() {
        Restaurant restaurant1 = Restaurant.builder().id(1001).name("FastFoodCorner").build();
        restaurants.add(restaurant1);
    }

    private RestaurantDaoImpl() {
        initRestaurants();
    }

    @Override
    public boolean addRestaurant(Restaurant restaurant) {
        return restaurants.add(restaurant);
    }

    @Override
    public Optional<Restaurant> getRestaurantById(int restaurantId) {
        return restaurants.stream()
                .filter(r -> r.getId() == restaurantId)
                .findFirst();
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants);
    }
}
