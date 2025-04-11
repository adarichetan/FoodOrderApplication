package com.foodorder.app.service;


import com.foodorder.app.entities.User;
import com.foodorder.app.utility.Response;

import java.sql.SQLException;

public interface AuthenticationService {
    Response loginUser(String email, String password);

    Response handleRegisterAuth(User user);

    Response registerUser(User user) throws SQLException;
}