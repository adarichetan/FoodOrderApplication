package com.foodorder.app.ui;

import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.OrderItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.AuthenticationService;
import com.foodorder.app.service.OrderService;
import com.foodorder.app.service.RestaurantService;
import com.foodorder.app.service.AdminService;
import com.foodorder.app.utility.*;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class AdminUi extends Ui {
    private final AdminService adminService;
    private User loggedInAdmin;
    private final OrderService orderService;
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final ManageFoodItemsUi manageFoodItemsUi;
    private final Validators validators;

    public AdminUi(Scanner scanner, Validators validators, AdminService adminService, RestaurantService restaurantService, OrderService orderService, AuthenticationService authService) {
        this.validators = validators;
        this.adminService = adminService;
        this.orderService = orderService;
        this.scanner = scanner;
        this.authService = authService;
        this.manageFoodItemsUi = new ManageFoodItemsUi(scanner, restaurantService, validators);
    }

    @Override
    public void initAdminScreen(User userData) {
        loggedInAdmin = userData;
        System.out.println(ColourCodes.GREEN + "Welcome, Admin " + loggedInAdmin.getName() + "!" + ColourCodes.RESET);

        boolean isExit = false;
        while (!isExit) {
            try {
                MenuPrinter.displayMenu("ADMIN MENU", List.of(
                        "🔐 Grant Role As Admin", "👥 Display All Users", "📦 Display All Orders",
                        "🍽️ Manage Food Items", "🚪 Logout"));

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> grantRoleAsAdmin();
                    case 2 -> displayAllUsers();
                    case 3 -> displayAllOrders();
                    case 4 -> manageFoodItemsUi.initManageFoods();
                    case 5 -> {
                        logOutAdmin(loggedInAdmin);
                        isExit = true;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException | InputMismatchException e) {
                log.error("Error from initAdminScreen method: ", e);
                scanner.nextLine();
            }
        }
    }

    @Override
    public void initCustomerScreen(User user) {
        //not being used here
    }

    private void grantRoleAsAdmin() {
        Response allUsers = adminService.fetchAllCustomers();
        if (Boolean.FALSE.equals(allUsers.isSuccess())) {
            System.out.println(allUsers.getMessage());
        }
        List<User> usersData = (List<User>) allUsers.getData();
        DataFormatter.printTable(usersData);

        String userName = validators.checkStringInput("\uD83D\uDC64 Enter the name of the user you'd like to authorize as Admin: \n↩\uFE0F ");
        if (userName == null) return;

//        if (usersData.stream().noneMatch(user -> user.getName().equalsIgnoreCase(userName))) {
//            System.out.println("❗ No user found with specified name." + userName);
//            return;
//        }

        Optional<User> first = usersData.stream().filter(user -> user.getName().equalsIgnoreCase(userName)).findFirst();
        if (first.isEmpty()) {
            System.out.println("❗ No user found with specified name." + userName);
            return;
        }
        Response response = adminService.grantAccess(first.get().getUserId());
        if (Boolean.FALSE.equals(response.isSuccess())) {
            System.out.println(response.getMessage());
            return;
        }
        System.out.println(response.getMessage());
    }

    private void logOutAdmin(User loggedInAdmin) {
        Response response = authService.logoutUser(loggedInAdmin);

        if (response.getResponseStatus() != ResponseStatus.SUCCESS) {
            System.out.println(response.getMessage());
            return;
        }

        loggedInAdmin = null;
        System.out.println(response.getMessage());
    }

    private void displayAllUsers() {
        Response allUsersResponse = adminService.fetchAllUsers();
        if (allUsersResponse.getResponseStatus() != ResponseStatus.SUCCESS) {
            System.out.println(allUsersResponse.getMessage());
            return;
        }

        List<User> users = (List<User>) allUsersResponse.getData();
        if (users == null || users.isEmpty()) {
            System.out.println("NO users available to display");
            return;
        }
        DataFormatter.printTable(users);
    }

    private void displayAllOrders() {
        Response response = orderService.getAllOrders();

        if (Boolean.FALSE.equals(response.isSuccess())) {
            System.out.println(response.getMessage());
            return;
        }

        List<Order> orders = (List<Order>) response.getData();
        if (orders.isEmpty()) {
            System.err.println("No orders found.");
            return;
        }

        OrderFormatter.printTable(orders);
        int finalOrderId = validators.checkNumericInput("🔍 Enter the Order ID to view order details: \n\uD83D\uDEAA ").intValue();
        if (finalOrderId == -2) return;

        boolean exists = orders.stream().anyMatch(order -> order.getId() == finalOrderId);
        if (!exists) {
            System.err.println("❗ No order found with ID: " + finalOrderId);
            return;
        }
        showOrderDetails(finalOrderId);
    }

    private void showOrderDetails(int orderId) {
        Response orderResponse = orderService.getOrderById(orderId);

        if (Boolean.FALSE.equals(orderResponse.isSuccess())) {
            System.err.println(orderResponse.getMessage());
            return;
        }

        Order order = (Order) orderResponse.getData();
        List<OrderItem> orderItems = order.getOrderItems();
        DataFormatter.printTable(orderItems);
    }
}
