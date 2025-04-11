package com.foodorder.app.ui;

import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.OrderItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.OrderService;
import com.foodorder.app.service.RestaurantService;
import com.foodorder.app.service.AdminService;
import com.foodorder.app.utility.*;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class AdminUi extends Ui {

    private final AdminService adminService;
    private User loggedInAdmin;
    private final OrderService orderService;
    private final Scanner scanner;
    private final ManageFoodItemsUi manageFoodItemsUi;

    public AdminUi(Scanner scanner, AdminService adminService, RestaurantService restaurantService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.scanner = scanner;
        this.manageFoodItemsUi = new ManageFoodItemsUi(scanner, restaurantService);
    }

    @Override
    public void initAdminScreen(User userData) {

        Response loginUserAccessResponse = provideLoginUserAccess(userData.getEmail());
        if (Boolean.FALSE.equals(loginUserAccessResponse.isSuccess())) {
            System.out.println(loginUserAccessResponse.getMessage());
            return;
        }
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
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void initCustomerScreen(User user) {
        //not being used here
    }

    private Response provideLoginUserAccess(String email) {
        return adminService.setLoginStatus(email);
    }

    private void grantRoleAsAdmin() {
        Response allUsers = adminService.fetchAllUsers();
        if (!allUsers.isSuccess()) {
            System.out.println(allUsers.getMessage());
        }
        List<User> usersData = (List<User>) allUsers.getData();
        DataFormatter.printTable(usersData);

        System.out.println("\uD83D\uDC64  Enter the name of the person you'd like to authorize as Admin: \n"
                + ColourCodes.RED + "↩️ Or enter 'exit' to back to menu: " + ColourCodes.RESET);
        String name = scanner.nextLine();
        if (name == null || name.isBlank()) {
            System.out.println("Enter the proper details and  try again..");
            return;
        }

        if (name.equalsIgnoreCase("exit")) {
            return;
        }

        Response response = adminService.grantAccess(name);
        if (Boolean.FALSE.equals(response.isSuccess())) {
            System.out.println(response.getMessage());
            return;
        }
        System.out.println(response.getMessage());
    }

    private void logOutAdmin(User admin) {
        Response response = adminService.logoutUser(admin.getEmail());

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

    private int promptOrderIdSelection(List<Order> orders) {
        while (true) {
            System.out.println("🔍 Enter the Order ID to view order details: \n" +
                    ColourCodes.RED + "🚪 Or type 'exit' to return to the previous menu: " + ColourCodes.RESET);

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) return -1;

            if (!input.matches("\\d+")) {
                System.out.println("Invalid input, Please enter a valid numeric ID.");
                return -1;
            }

            int orderId = Integer.parseInt(input);
            boolean exists = orders.stream().anyMatch(order -> order.getId() == orderId);
            if (!exists) {
                System.err.println("❗ No order found with ID: " + orderId);
                return -1;
            }
            return orderId;
        }
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

        int orderId = promptOrderIdSelection(orders);
        if (orderId == -1) return;
        showOrderDetails(orderId);
    }

    private void showOrderDetails(int orderId) {
        Response orderResponse = orderService.getOrder(orderId);

        if (Boolean.FALSE.equals(orderResponse.isSuccess())) {
            System.err.println(orderResponse.getMessage());
            return;
        }

        Order order = (Order) orderResponse.getData();
        List<OrderItem> orderItems = order.getOrderItems();
        DataFormatter.printTable(orderItems);
    }
}
