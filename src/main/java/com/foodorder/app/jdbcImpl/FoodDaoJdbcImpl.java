package com.foodorder.app.jdbcImpl;

import com.foodorder.app.sqlqueries.FoodSqlQueries;
import com.foodorder.app.dao.FoodDao;
import com.foodorder.app.entities.FoodItem;

import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.utility.ConnectionUtility;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FoodDaoJdbcImpl implements FoodDao {

    @Getter
    private static final FoodDaoJdbcImpl foodDaoJdbc = new FoodDaoJdbcImpl();
    private Connection con;

    private FoodDaoJdbcImpl() {
        initSqlDataConnection();
    }

    void initSqlDataConnection() {
        this.con = ConnectionUtility.getConnection();
    }

    @Override
    public boolean saveFood(FoodItem item) throws SQLException {
        try (PreparedStatement addStmt = con.prepareStatement(FoodSqlQueries.INSERT_FOOD)) {
            addStmt.setString(1, item.getName());
            addStmt.setDouble(2, item.getPrice());
            addStmt.setString(3, item.getCategory().name());
            addStmt.setInt(4, item.getRestaurantId());

            return addStmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<FoodItem> getFoodByCategory(FoodCategory category) throws SQLException {
        List<FoodItem> foodItemList = new ArrayList<>();
        try (PreparedStatement getStmt = con.prepareStatement(FoodSqlQueries.SELECT_FOOD_BY_CATEGORY)) {
            getStmt.setString(1, category.name());

            ResultSet rs = getStmt.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = mapResultSetToFoodItem(rs);
                foodItemList.add(foodItem);
            }
            return foodItemList;
        }
    }

    @Override
    public Optional<FoodItem> getFoodById(int id) throws SQLException {
        try (PreparedStatement getStmt = con.prepareStatement(FoodSqlQueries.SELECT_FOOD_BY_ID)) {
            getStmt.setInt(1, id);
            ResultSet rs = getStmt.executeQuery();
            if (rs.next()) {
                FoodItem foodItem = mapResultSetToFoodItem(rs);
                return Optional.ofNullable(foodItem);
            }
            return Optional.empty();
        }
    }

    @Override
    public List<FoodItem> getAllFood() throws SQLException {
        List<FoodItem> foodItemList = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(FoodSqlQueries.SELECT_ALL_FOOD)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = mapResultSetToFoodItem(rs);
                foodItemList.add(foodItem);
            }
            return foodItemList.stream().sorted(Comparator.comparing(FoodItem::getCategory)).toList();
        }
    }

    private FoodItem mapResultSetToFoodItem(ResultSet rs) throws SQLException {
        return FoodItem.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getDouble("price"))
                .category(FoodCategory.valueOf(rs.getString("category")))
                .restaurantId(rs.getInt("restaurant_id"))
                .build();
    }

    @Override
    public boolean updateFood(FoodItem foodItem) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(FoodSqlQueries.UPDATE_FOOD)) {
            ps.setString(1, foodItem.getName());
            ps.setDouble(2, foodItem.getPrice());
            ps.setString(3, foodItem.getCategory().name());
            ps.setInt(4, foodItem.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteFood(FoodItem foodItem) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(FoodSqlQueries.DELETE_FOOD_BY_ID)) {
            ps.setInt(1, foodItem.getId());

            return ps.executeUpdate() > 0;
        }
    }
}