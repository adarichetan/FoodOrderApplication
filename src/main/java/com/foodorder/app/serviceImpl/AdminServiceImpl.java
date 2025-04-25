package com.foodorder.app.serviceImpl;

import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.enums.UserRole;
import com.foodorder.app.service.AdminService;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserDao userDao;
    Response response;

    public AdminServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Response grantAccess(String name) {
        try {

            if (userDao.grantAccessAsAdmin(name)) {
                return new Response(ResponseStatus.SUCCESS, "✅ User granted Admin access.");
            }

        } catch (SQLException e) {
            log.error("Error from grant access as admin method", e);
        }
        return new Response(ResponseStatus.FAILURE, "User not found");
    }

    @Override
    public Response fetchAllCustomers() {
        Response response1 = fetchAllUsers();
        if (Boolean.FALSE.equals(response1.isSuccess())) {
            return new Response(ResponseStatus.FAILURE, "No users found.");
        }
        List<User> data = (List<User>) response1.getData();
        List<User> custData = data.stream().filter(user -> user.getRole().equals(UserRole.CUSTOMER)).toList();
        return new Response(custData, ResponseStatus.SUCCESS, "Customers fetched successfully");
    }

    @Override
    public Response fetchAllUsers() {
        try {
            List<User> allUsers = userDao.fetchAllUsers();

            if (allUsers == null || allUsers.isEmpty()) {
                response = new Response(ResponseStatus.FAILURE, "No users found.");
            } else {
                response = new Response(allUsers, ResponseStatus.SUCCESS, "Users fetched successfully");
            }
        } catch (SQLException e) {
            log.error("Error fetching users", e);
            response = new Response(ResponseStatus.FAILURE, "An error occurred while fetching users.");
        }
        return response;
    }
}