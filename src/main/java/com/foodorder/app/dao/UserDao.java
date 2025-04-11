package com.foodorder.app.dao;

import com.foodorder.app.entities.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    boolean grantAccessAsAdmin(String name) throws SQLException;

    boolean addUser(User user) throws  SQLException;

    Optional<User> findUserByEmail(String email) throws SQLException;

    List<User> fetchAllUsers() throws SQLException;

    boolean setLoginStatus(String email) throws SQLException;

    boolean logoutUser(String email) throws SQLException;

}