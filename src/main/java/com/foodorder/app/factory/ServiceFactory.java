package com.foodorder.app.factory;

import com.foodorder.app.dao.*;
import com.foodorder.app.service.*;
import com.foodorder.app.serviceImpl.*;
import com.foodorder.app.utility.Validators;
import lombok.Getter;

import java.util.Scanner;

@Getter
public class ServiceFactory {
    @Getter
    private static final ServiceFactory serviceFactory = new ServiceFactory();

    private final AuthenticationService authenticationService;
    private final AdminService adminService;
    private final CartService cartService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final Scanner scanner;
    private final Validators validators;

    private final UserDao userDao= DaoFactory.getUserDao();
    private final CartDao cartDao =DaoFactory.getCartDao();
    private final OrderDao orderDao= DaoFactory.getOrderDao();
    private final RestaurantDao restaurantDao= DaoFactory.getRestaurantDao();
    private final FoodDao foodDao= DaoFactory.getRestaurantFoodDao();

    private ServiceFactory() {
        scanner = new Scanner(System.in);
        validators = new Validators(scanner);

        adminService = new AdminServiceImpl(userDao);
        orderService = new OrderServiceImpl(orderDao, cartDao);
        cartService = new CartServiceImpl(cartDao);
        customerService = new CustomerServiceImpl(userDao);
        restaurantService = new RestaurantServiceImpl(restaurantDao, foodDao);
        authenticationService = new AuthenticationServiceImpl(userDao);
    }

}