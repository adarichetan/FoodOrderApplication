package com.foodorder.app.serviceImpl;

import com.foodorder.app.dao.FoodDao;
import com.foodorder.app.dao.RestaurantDao;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.Restaurant;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.RestaurantService;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantDao restaurantDao;
    private final FoodDao foodDaoDao;
    Response response;

    public RestaurantServiceImpl(RestaurantDao restaurantDao, FoodDao foodDao) {
        this.restaurantDao = restaurantDao;
        this.foodDaoDao = foodDao;
    }

    @Override
    public Response getRestaurantById(int restaurantId) {
        try {
            Optional<Restaurant> restaurantById = restaurantDao.getRestaurantById(restaurantId);
            if (restaurantById.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "❗ No restaurant found with ID: " + restaurantId);
            }
            Restaurant restaurant = restaurantById.get();
            response = new Response(restaurant, ResponseStatus.SUCCESS, "Restaurant retrieved successfully");
        } catch (Exception e) {
            log.error("Error from get restaurant by category method in restaurant service: ", e);
            response = new Response(ResponseStatus.FAILURE, "An error occurred..Please contact admin");
        }
        return response;
    }

    @Override
    public Response getAllRestaurants() {
        try {
            List<Restaurant> allRestaurants = restaurantDao.getAllRestaurants();
            if (allRestaurants.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "No restaurant found");
            }
            response = new Response(allRestaurants, ResponseStatus.SUCCESS, "successfully retrieved the restaurants");
        } catch (Exception e) {
            log.error("Error from getAllRestaurants method in restaurant service: ", e);
            response = new Response(ResponseStatus.FAILURE, "An error occurred..Please contact admin");
        }
        return response;
    }

    @Override
    public Response addRestaurant(Restaurant restaurant) {
        try {
            boolean b = restaurantDao.addRestaurant(restaurant);
            if (b) {
                response = new Response(ResponseStatus.SUCCESS, "✅ Restaurant added successfully");
            } else {
                response = new Response(ResponseStatus.FAILURE, "! Unable to add restaurant.");
            }
        } catch (Exception e) {
            log.error("An error occurred while adding the restaurant ", e);
            response = new Response(ResponseStatus.FAILURE, "An error occurred adding restaurant. Please contact admin.");
        }
        return response;
    }

    @Override
    public Response addFood(FoodItem foodItem) {
        Response allFood = getAllFood();
        List<FoodItem> data = (List<FoodItem>) allFood.getData();
        if (data == null || data.isEmpty()){
            return new Response(ResponseStatus.FAILURE, "Cannot add food item.. Please contact admin. ");
        }
        Optional<FoodItem> foodItemPresent = data.stream().filter(foodItem1 -> foodItem1.getName().equalsIgnoreCase(foodItem.getName())).findFirst();
        if (foodItemPresent.isPresent()) {
            return new Response(ResponseStatus.FAILURE, "Food item is already present.");

        }
        try {
            if (foodDaoDao.saveFood(foodItem)) {
                response = new Response(ResponseStatus.SUCCESS, "✅ Food successfully added.");
            } else {
                response = new Response(ResponseStatus.FAILURE, "❗ Food not added.");
            }

        } catch (Exception e) {
            log.error("Error add food item method : ", e);
            response = new Response(ResponseStatus.FAILURE, "An error occurred in adding food.. Please contact admin.");
        }
        return response;
    }

    @Override
    public Response removeFood(String foodItem) {
        if (foodItem == null || foodItem.trim().isEmpty()) {
            return new Response(ResponseStatus.FAILURE, "Invalid attempt to add food item.");
        }

        Response allFood = getAllFood();
        List<FoodItem> data = (List<FoodItem>) allFood.getData();
        Optional<FoodItem> foodItemPresent = data.stream().filter(foodItem1 -> foodItem1.getName().equalsIgnoreCase(foodItem)).findFirst();
        if (foodItemPresent.isEmpty()) {
            return new Response(ResponseStatus.FAILURE, "❗ Food item not found.");
        }

        try {
            FoodItem foodItemToDelete = foodItemPresent.get();
            boolean itemDeleted = foodDaoDao.deleteFood(foodItemToDelete);
            if (itemDeleted) {
                return new Response(ResponseStatus.SUCCESS, "✅ Item deleted successfully");
            }
        } catch (Exception e) {
            log.error("Error removing food items : Something went wrong ", e);
            response = new Response(ResponseStatus.FAILURE, "⚠ An error occurred");
        }
        return response;
    }

    @Override
    public Response updateFood(FoodItem foodItem) {
        try {
            boolean isUpdated = foodDaoDao.updateFood(foodItem);

            if (isUpdated) {
                return new Response(ResponseStatus.SUCCESS, "✅ Updated food successfully.");
            } else {
                return new Response(Boolean.FALSE, ResponseStatus.FAILURE, "❌ Food update failed.");
            }
        } catch (SQLException e) {
            log.error("Error from update food item method", e);
            return new Response(ResponseStatus.FAILURE, "⚠ Error: food not updated due to an exception");
        }
    }

    @Override
    public Response getAllFood() {
        try {
            List<FoodItem> allFood = foodDaoDao.getAllFood();
            if (allFood.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "Error : No food found.");
            } else {
                response = new Response(allFood, ResponseStatus.SUCCESS, "Food fetched successfully");
            }
        } catch (Exception e) {
            log.error("Error from get all food method in restaurant service: ", e);
            response = new Response(ResponseStatus.FAILURE, "⚠ An error occurred.. Please contact admin.");
        }
        return response;
    }

    @Override
    public Response getFoodByName(String foodItem) {
        if (foodItem == null || foodItem.trim().isEmpty()) {
            return new Response(ResponseStatus.FAILURE, "Food item cannot be empty or null");
        }
        try {
            List<FoodItem> allFood = foodDaoDao.getAllFood();
            if (allFood.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "❗ Error : No food found");
            }
            Optional<FoodItem> foodItemOptional = allFood.stream()
                    .filter(f -> f
                            .getName()
                            .equalsIgnoreCase(foodItem))
                    .findFirst();
            if (foodItemOptional.isPresent()) {
                FoodItem foodItem1 = foodItemOptional.get();
                response = new Response(foodItem1, ResponseStatus.SUCCESS, "✅ Food found!");
            } else {
                response = new Response(ResponseStatus.FAILURE, "❗ Error : No food found");
            }
        } catch (Exception e) {
            log.error("Error from get food by name method in restaurant service: ", e);
            response = new Response(ResponseStatus.FAILURE, "⚠ An error occurred.. Please contact admin.");
        }
        return response;
    }

    @Override
    public Response getFoodByCategory(FoodCategory category) {
        try {
            List<FoodItem> foodByCategory = foodDaoDao.getFoodByCategory(category);
            if (foodByCategory.isEmpty()) {
                response = new Response(ResponseStatus.FAILURE, "❗ Error : No food found.");
            } else {
                response = new Response(foodByCategory, ResponseStatus.SUCCESS, "✅ Food found.");
            }
        } catch (Exception e) {
            log.error("Error from get food by category method in restaurant service: ", e);
            response = new Response(ResponseStatus.FAILURE, "⚠ An error occurred... Please contact admin.");
        }
        return response;
    }
}