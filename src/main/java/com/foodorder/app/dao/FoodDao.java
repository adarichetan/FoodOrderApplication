package com.foodorder.app.dao;

import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.enums.FoodCategory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FoodDao {
    boolean saveFood(FoodItem foodItem) throws SQLException;

    Optional<FoodItem> getFoodById(int id) throws SQLException;

    List<FoodItem> getAllFood() throws SQLException;

    List<FoodItem> getFoodByCategory(FoodCategory category) throws SQLException;

    boolean updateFood(FoodItem foodItem) throws SQLException;

    boolean deleteFood(FoodItem foodItem) throws  SQLException;
}