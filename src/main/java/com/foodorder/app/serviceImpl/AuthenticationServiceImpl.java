package com.foodorder.app.serviceImpl;


import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.factory.ServiceFactory;

import com.foodorder.app.service.AuthenticationService;

import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDao userDao;


    public AuthenticationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public Response handleRegisterAuth(User user) {
        if (user == null) {
            return new Response(ResponseStatus.FAILURE, "Invalid user credentials.");
        }

        try {
            List<User> existingUsers = userDao.fetchAllUsers();

            if (existingUsers.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "");
            }
            boolean emailExists = existingUsers.stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equalsIgnoreCase(user.getEmail()));

            if (emailExists) {
                return new Response(ResponseStatus.FAILURE, "User already exists. Please try again..");
            }

            Response registrationResponse = registerUser(user);
            if (Boolean.FALSE.equals(registrationResponse.isSuccess())) {
                return registrationResponse;
            }

        } catch (Exception e) {
            log.error("Error while fetching the users from authentication service", e);
        }

        return new Response(ResponseStatus.SUCCESS, "Registration successful. Please log in.");
    }


    @Override
    public Response registerUser(User user) {
        try {
            if (userDao.addUser(user)) {
                return new Response(user, ResponseStatus.SUCCESS, "User added successfully.");
            }
        } catch (Exception e) {
            log.error("Error from registerUser method: ", e);
        }

        return new Response(ResponseStatus.FAILURE, "An error occurred during registration. Please contact admin.");
    }

    @Override
    public Response loginUser(String email, String password) {
        try {
            Optional<User> optionalUser = userDao.findUserByEmail(email.toLowerCase());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if (user.getPassword().equals(password)) {
                    return new Response(user, ResponseStatus.SUCCESS, "User successfully logged in.");
                } else {
                    return new Response(ResponseStatus.FAILURE, "Incorrect username or password.\nPlease try again..");
                }
            } else {
                return new Response(ResponseStatus.FAILURE, "Incorrect username or password.\nPlease try again..");
            }
        } catch (SQLException e) {
            log.error("Error in loginUser while accessing database: ", e);
            return new Response(ResponseStatus.FAILURE, "An error occurred during login. Please contact admin.");
        }
    }
}