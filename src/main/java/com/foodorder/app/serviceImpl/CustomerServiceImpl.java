package com.foodorder.app.serviceImpl;

import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;

import com.foodorder.app.enums.ResponseStatus;

import com.foodorder.app.service.CartService;
import com.foodorder.app.service.CustomerService;
import com.foodorder.app.service.OrderService;
import com.foodorder.app.utility.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
@Getter
public class CustomerServiceImpl implements CustomerService {
    private final OrderService orderService;
    private final UserDao userDao;
    private final CartService cartService;
    Response response = null;

    public CustomerServiceImpl(OrderService orderService, CartService cartService, UserDao userDao) {
        this.userDao = userDao;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    public Response addToCart(User user, FoodItem item, int quantity) {
        return cartService.addToCart(user, item, quantity);
    }

    @Override
    public Response getCart(User user) {
        return cartService.getCartItems(user);
    }

    public Response placeOrder(User customer) {
        return orderService.placeOrder(customer);
    }

    @Override
    public Response getAllOrders(User customer) {
        return orderService.getAllOrders();
    }

    @Override
    public Response getOrderById(int orderId) {
        return orderService.getOrder(orderId);
    }

    @Override
    public Response setLoginStatus(String email) {
        try {
            boolean setLoginStatus = userDao.setLoginStatus(email);
            if (setLoginStatus) {
                return new Response(ResponseStatus.SUCCESS, "Set login success");
            }
        } catch (SQLException e) {
            log.error("Error from user service while attempting to change the login status: ", e);
        }
        return new Response(ResponseStatus.FAILURE, "Unable to set login status");
    }

    @Override
    public Response logoutUser(String email) {
        try {
            boolean logoutUser = userDao.logoutUser(email);
            if (logoutUser) {
                return new Response(ResponseStatus.SUCCESS, "Logout success!");
            }
        } catch (SQLException e) {
            log.error("Error from user service while attempting to logout: ", e);
        }
        return new Response(ResponseStatus.FAILURE, "Unable to logout customer");
    }

    @Override
    public Response updateCartQuantity(int id, String foodName, int newQuantity) {
        return cartService.updateQuantityFromCart(id, foodName, newQuantity);
    }

    @Override
    public Response deleteFromCart(int id, String name) {
        return cartService.deleteFromCart(id, name);
    }
}