package com.foodorder.app.serviceImpl;


import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;

import com.foodorder.app.service.AuthenticationService;

import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;


import java.sql.SQLException;

import java.util.Optional;

@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDao userDao;


    public AuthenticationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public Response registerUser(User user) {
        if (user == null) {
            return new Response(ResponseStatus.FAILURE, "Invalid user credentials.");
        }

        try {
            Optional<User> userByEmail = userDao.findUserByEmail(user.getEmail());
            if (userByEmail.isPresent()) {
                return new Response(ResponseStatus.FAILURE, "User already exists. Please try again.");
            }

            if (userDao.addUser(user)) {
                return new Response(user, ResponseStatus.SUCCESS, "Registration successful. Please log in.");
            }
        } catch (Exception e) {
            log.error("Error during registration and authentication process: ", e);
        }

        return new Response(ResponseStatus.FAILURE, "An error occurred during registration. Please contact admin.");
    }

    @Override
    public Response loginUser(String email, String password) {
        if (email == null || password == null) {
            return new Response(ResponseStatus.FAILURE, "Incorrect username or password.\nPlease try again..");
        }

        try {
            Optional<User> optionalUser = userDao.findUserByEmail(email.toLowerCase());

            if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
                User user = optionalUser.get();

                Response response = setLoginStatus(user);
                if (Boolean.FALSE.equals(response.isSuccess())) {
                    return response;
                }
                return response;

            } else {
                return new Response(ResponseStatus.FAILURE, "Incorrect username or password.\nPlease try again..");
            }
        } catch (SQLException e) {
            log.error("Error in loginUser while accessing database: ", e);
            return new Response(ResponseStatus.FAILURE, "An error occurred during login. Please contact admin.");
        }
    }

    public Response setLoginStatus(User user) {
        if (user == null) {
            return new Response(ResponseStatus.FAILURE, "Unable to logout customer..");
        }

        try {
            user.setLoggedIn(true);
            Optional<User> loginUser = userDao.updateUser(user);
            if (loginUser.isPresent()) {
                return new Response(loginUser.get(), ResponseStatus.SUCCESS, "User successfully logged in.");
            }

        } catch (SQLException e) {
            log.error("Error from user service while attempting to logout: ", e);
        }

        return new Response(ResponseStatus.FAILURE, "Error while login. Please try again..");
    }

    @Override
    public Response logoutUser(User user) {
        if (user == null) {
            return new Response(ResponseStatus.FAILURE, "Unable to logout customer..");
        }
        try {
            user.setLoggedIn(false);
            Optional<User> logoutUser = userDao.updateUser(user);
            if (logoutUser.isPresent())
           {
                return new Response(ResponseStatus.SUCCESS, "Logout success!");
            }
        } catch (SQLException e) {
            log.error("Error from user service while attempting to logout: ", e);
        }
        return new Response(ResponseStatus.FAILURE, "Unable to logout customer");
    }
}