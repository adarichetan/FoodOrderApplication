package com.foodorder.app.dao;

import com.foodorder.app.entities.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    boolean grantAccessAsAdmin(int userId) throws SQLException;

    Optional<User> saveUser(User user) throws  SQLException;

    Optional<User> findUserByEmail(String email) throws SQLException;

    List<User> fetchAllUsers() throws SQLException;

    Optional<User> updateUser(User user) throws SQLException;
}