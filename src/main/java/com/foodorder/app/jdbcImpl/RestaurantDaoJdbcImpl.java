package com.foodorder.app.jdbcImpl;

import com.foodorder.app.sqlqueries.RestaurantSqlQueries;
import com.foodorder.app.dao.RestaurantDao;
import com.foodorder.app.entities.Restaurant;
import com.foodorder.app.utility.ConnectionUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantDaoJdbcImpl implements RestaurantDao {
    private static final RestaurantDaoJdbcImpl restaurantDaoJdbc = new RestaurantDaoJdbcImpl();
    private Connection con;

    private RestaurantDaoJdbcImpl() {
        initSqlDataConnection();
    }

    public static RestaurantDaoJdbcImpl getRestaurantDaoJdbcImpl() {
        return restaurantDaoJdbc;
    }

    void initSqlDataConnection() {
        this.con = ConnectionUtility.getConnection();
    }

    @Override
    public boolean addRestaurant(Restaurant restaurant) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(RestaurantSqlQueries.INSERT_RESTAURANT)) {
            ps.setInt(1, restaurant.getId());
            ps.setString(2, restaurant.getName());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Restaurant> getRestaurantById(int restaurantId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(RestaurantSqlQueries.SELECT_RESTAURANT_BY_ID)) {
            ps.setInt(1, restaurantId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Restaurant restaurant = Restaurant.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name")).build();

                return Optional.ofNullable(restaurant);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Restaurant> getAllRestaurants() throws SQLException {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(RestaurantSqlQueries.SELECT_ALL_RESTAURANTS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Restaurant restaurant = Restaurant.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name")).build();
                restaurantList.add(restaurant);
            }
            return restaurantList;
        }
    }
}
