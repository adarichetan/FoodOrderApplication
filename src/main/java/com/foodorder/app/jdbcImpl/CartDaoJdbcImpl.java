package com.foodorder.app.jdbcImpl;

import com.foodorder.app.sqlqueries.CartSqlQueries;
import com.foodorder.app.dao.CartDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.utility.ConnectionUtility;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CartDaoJdbcImpl implements CartDao {
    private static final CartDaoJdbcImpl cartDaoJdbc = new CartDaoJdbcImpl();
    private Connection con;

    private CartDaoJdbcImpl() {
        initSqlDataConnection();
    }

    public static CartDaoJdbcImpl getCartDaoJdbcImpl() {
        return cartDaoJdbc;
    }

    void initSqlDataConnection() {
        this.con = ConnectionUtility.getConnection();
    }

    @Override
    public boolean addToCart(User user, FoodItem foodItem, int quantity) throws SQLException {
        try (PreparedStatement preparedStatement = con.prepareStatement(CartSqlQueries.SELECT_CART_ITEM_BY_USER_AND_FOOD_ID)) {
            preparedStatement.setInt(1, user.getUserId());
            preparedStatement.setInt(2, foodItem.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int cartItemId = resultSet.getInt("id");

                try (PreparedStatement updateStmt = con.prepareStatement(CartSqlQueries.UPDATE_CART_QUANTITY_INCREMENT)) {
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, cartItemId);

                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertStmt = con.prepareStatement(CartSqlQueries.INSERT_CART_ITEM)) {
                    insertStmt.setInt(1, user.getUserId());
                    insertStmt.setInt(2, foodItem.getId());
                    insertStmt.setInt(3, quantity);

                    return insertStmt.executeUpdate() > 0;
                }
            }
        }
    }

    @Override
    public List<CartItem> getCartByUserId(User user) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();

        try (PreparedStatement getStmt = con.prepareStatement(CartSqlQueries.SELECT_CART_ITEMS_BY_USER_ID)) {
            getStmt.setInt(1, user.getUserId());
            ResultSet rs = getStmt.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = FoodItem.builder()
                        .id(rs.getInt("food_id"))
                        .name(rs.getString("name"))
                        .price(rs.getDouble("price"))
                        .category(FoodCategory.valueOf(rs.getString("category")))
                        .restaurantId(rs.getInt("restaurant_id"))
                        .build();

                CartItem cartItem = CartItem.builder()
                        .id(rs.getInt("id"))
                        .foodItem(foodItem)
                        .quantity(rs.getInt("quantity"))
                        .user(user).build();

                cartItems.add(cartItem);
            }
        }
        return cartItems;
    }

    @Override
    public boolean clearCart(User user) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(CartSqlQueries.DELETE_CART_ITEMS_BY_USER_ID)) {
            stmt.setInt(1, user.getUserId());
            return stmt.executeUpdate() > 0;    
        }
    }

    @Override
    public boolean updateQuantityFromCart(int id, String foodName, int newQuantity) throws SQLException {
        try (PreparedStatement foodStmt = con.prepareStatement(CartSqlQueries.SELECT_FOOD_ID_BY_NAME)) {
            foodStmt.setString(1, foodName);
            ResultSet foodRs = foodStmt.executeQuery();
            if (foodRs.next()) {
                int foodId = foodRs.getInt("id");

                try (PreparedStatement ps = con.prepareStatement(CartSqlQueries.UPDATE_CART_QUANTITY)) {
                    ps.setInt(1, newQuantity);
                    ps.setInt(2, id);
                    ps.setInt(3, foodId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean deleteFromCart(int userId, String foodName) throws SQLException {
        try (PreparedStatement foodStmt = con.prepareStatement(CartSqlQueries.SELECT_FOOD_ID_BY_NAME)) {
            foodStmt.setString(1, foodName);
            ResultSet foodRs = foodStmt.executeQuery();

            if (foodRs.next()) {
                int foodId = foodRs.getInt("id");

                try (PreparedStatement deleteStmt = con.prepareStatement(CartSqlQueries.DELETE_CART_ITEM_BY_USER_ID_AND_FOOD_ID)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setInt(2, foodId);
                    return deleteStmt.executeUpdate() > 0;
                }
            } else {
                return false;
            }
        }
    }
}