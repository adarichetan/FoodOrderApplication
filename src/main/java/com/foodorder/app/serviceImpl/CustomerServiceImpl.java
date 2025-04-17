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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
@Getter

public class CustomerServiceImpl implements CustomerService {
    private final UserDao userDao;

    public CustomerServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Response getAllCustomers() {
        return null;
    }
//    private final OrderService orderService;
//    private final UserDao userDao;
//    private final CartService cartService;
//
//    @Override
//    public Response addToCart(User user, FoodItem item, int quantity) {
//        return cartService.addToCart(user, item, quantity);
//    }
//    @Override
//    public Response getCart(User user) {
//        return cartService.getCartItems(user);
//    }
//
//    public Response placeOrder(User customer) {
//        return orderService.placeOrder(customer);
//    }
//
//    @Override
//    public Response getAllOrders(User customer) {
//        return orderService.getAllOrders();
//    }
//
//    @Override
//    public Response getOrderById(int orderId) {
//        return orderService.getOrderById(orderId);
//    }
//
////    @Override
////    public Response setLoginStatus(User user) {
////        try {
////            user.setLoggedIn(true);
////            boolean setLoginStatus = userDao.updateUser(user);
////            if (setLoginStatus) {
////                return new Response(ResponseStatus.SUCCESS, "Set login success");
////            }
////        } catch (SQLException e) {
////            log.error("Error from user service while attempting to change the login status: ", e);
////        }
////        return new Response(ResponseStatus.FAILURE, "Unable to set login status");
////    }
//
////    @Override
////    public Response logoutUser(User user) {
////        try {
////            user.setLoggedIn(false);
////            boolean logoutUser = userDao.updateUser(user);
////            if (logoutUser) {
////                return new Response(ResponseStatus.SUCCESS, "Logout success!");
////            }
////        } catch (SQLException e) {
////            log.error("Error from user service while attempting to logout: ", e);
////        }
////        return new Response(ResponseStatus.FAILURE, "Unable to logout customer");
////    }
//
//    @Override
//    public Response updateCartQuantity(int id, String foodName, int newQuantity) {
//        return cartService.updateQuantityFromCart(id, foodName, newQuantity);
//    }
//
//    @Override
//    public Response deleteFromCart(int id, String name) {
//        return cartService.deleteFromCart(id, name);
//    }
}