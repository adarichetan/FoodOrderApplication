package com.foodorder.app.ui;

import com.foodorder.app.entities.*;
import com.foodorder.app.enums.ResponseStatus;

import com.foodorder.app.service.*;
import com.foodorder.app.utility.ColourCodes;
import com.foodorder.app.utility.DataFormatter;
import com.foodorder.app.utility.MenuPrinter;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CustomerUi extends Ui {
    private final RestaurantService restaurantService;
    private User loggedInCustomer;
    private CustomerService customerService;
    private final Scanner scanner;

    public CustomerUi(Scanner scanner, RestaurantService restaurantService, CustomerService customerService) {
        this.scanner = scanner;
        this.restaurantService = restaurantService;
        this.customerService = customerService;
    }

    @Override
    public void initCustomerScreen(User userData) {
        Response response = provideLoginUserAccess(userData.getEmail());
        if (response.getResponseStatus() != ResponseStatus.SUCCESS) {
            System.out.println(response.getMessage());
            return;
        }
        loggedInCustomer = userData;

        System.out.println(ColourCodes.GREEN + "Welcome, " + loggedInCustomer.getName() + "!" + ColourCodes.RESET);

        boolean isExit = false;
        while (!isExit) {
            try {
                List<String> customerOptions = List.of(
                        "📋 View Menu",
                        "➕ Add to Cart",
                        "🛒 View Cart",
                        "✅ Place Order",
                        "🚚 Track Order Status",
                        "📜 View Order History",
                        "🔓 Logout"
                );
                MenuPrinter.displayMenu("\uD83C\uDF74 Customer Menu Options", customerOptions);

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> viewMenu();
                    case 2 -> addToCart();
                    case 3 -> viewCart();
                    case 4 -> placeOrder();
                    case 5 -> trackOrderStatus();
                    case 6 -> viewOrderHistory();
                    case 7 -> {
                        logoutCustomer();
                        isExit = true;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException | IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                log.error("Error from init customer ui:", e);
                System.err.println("An error occurred: Please contact admin.");
            }
        }
    }

    private Response provideLoginUserAccess(String email) {
        return customerService.setLoginStatus(email);
    }

    private void viewMenu() {
        System.out.println(ColourCodes.CYAN + "\nMENU" + ColourCodes.RESET);
        System.out.printf(ColourCodes.PURPLE + "| %-15s | %-10s | %-10s |" + ColourCodes.RESET + "%n", "Food Name", "Item Price", "Food Category");
        Response allFoodResponse = restaurantService.getAllFood();
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();
        foodItems.forEach(System.out::println);
    }

    private void addToCart() {
        while (true) {
            viewMenu();
            MenuPrinter.displayMenu("", List.of("\uD83D\uDD19 Return to Customer Menu", "\uD83D\uDED2 View Cart ",
                    "✅ Proceed to Checkout ", "[Enter Food Name] ➕ Add Item to Cart: ")
            );

            String foodName = scanner.nextLine().trim();

            switch (foodName.toLowerCase()) {
                case "1" -> {
                    return;
                }
                case "2" -> viewCart();
                case "3" -> {
                    Response responseCart = customerService.getCart(loggedInCustomer);
                    if (Boolean.FALSE.equals(responseCart.isSuccess())) {
                        System.out.println(responseCart.getMessage());
                    } else {
                        proceedWithCheckout();
                    }
                }
                default -> {
                    Optional<FoodItem> foodItem = foodItemIsPresent(foodName);
                    foodItem.ifPresentOrElse(
                            this::proceedWithCart,
                            () -> System.out.println("❌ Food item not found. Please try again.")
                    );
                }
            }
        }
    }

    private void proceedWithCart(FoodItem item) {
        System.out.println("Enter quantity:");
        int quantity = getQuantityInput();
        if (quantity <= 0) {
            System.out.println(" quantity must be greater than 0");
            return;
        }

        Response response = customerService.addToCart(loggedInCustomer, item, quantity);
        if (Boolean.FALSE.equals(response.isSuccess())) {
            System.out.println(response.getMessage());
            return;
        }
        System.out.println(response.getMessage());
        getCart();

    }

    private void placeOrder() {
        List<CartItem> cart = getCart();
        if (cart.isEmpty()) return;

        boolean isExit = true;
        while (isExit) {
            MenuPrinter.displayMenu("", List.of("\uD83D\uDD19 Return to the previous menu", "✅ Proceed to Checkout"));
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    return;
                }
                case "2" -> {
                    if (proceedWithCheckout()) {
                        isExit = false;
                    }
                }

                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private Optional<FoodItem> foodItemIsPresent(String foodName) {
        Response allFoodResponse = restaurantService.getAllFood();
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();
        return foodItems.stream()
                .filter(f -> f.getName().equalsIgnoreCase(foodName))
                .findFirst();
    }

    private List<CartItem> getCart() {
        Response responseCart = customerService.getCart(loggedInCustomer);

        if (Boolean.FALSE.equals(responseCart.isSuccess())) {
            System.out.println(responseCart.getMessage());
            return List.of();
        }
        System.out.println(ColourCodes.CYAN + "\nCART" + ColourCodes.RESET);
        List<CartItem> cart = (List<CartItem>) responseCart.getData();
        DataFormatter.printTable(cart);

        System.out.println("Total Bill: " + cart.stream().mapToDouble(CartItem::getTotalPrice).sum() + "₹");
        return cart;
    }

    private void viewCart() {
        while (true) {
            List<CartItem> cart = getCart();
            if (cart.isEmpty()) return;

            MenuPrinter.displayMenu("", List.of("\uD83D\uDD19 Return to the previous menu", "✅ Proceed to Checkout",
                    "✏️ Update Cart Quantity", "❌ Delete Item from Cart"));
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    return;
                }
                case "2" -> {
                    proceedWithCheckout();
                    return;
                }
                case "3" -> updateCartQuantity(cart);

                case "4" -> deleteCartItem(cart);

                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private void deleteCartItem(List<CartItem> cart) {
        System.out.println("❌ Enter the food name to remove from cart: ");
        String foodNameToDelete = scanner.nextLine().trim();

        cart.stream()
                .filter(c -> c.getFoodItem().getName().equalsIgnoreCase(foodNameToDelete))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    Response response = customerService.deleteFromCart(loggedInCustomer.getUserId(), cartItem.getFoodItem().getName());
                    System.out.println(response.getMessage());
                }, () -> System.out.println("❌ Food item not found."));
    }


    private int getQuantityInput() {
        int quantity = 0;
        try {
            quantity = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid integer for quantity.");
            scanner.nextLine();
        }
        return quantity;

    }

    private void updateCartQuantity(List<CartItem> cart) {
        System.out.println("✏️ Enter the food name to update:");
        String foodNameToUpdate = scanner.nextLine().trim();

        cart.stream()
                .filter(c -> c.getFoodItem().getName().equalsIgnoreCase(foodNameToUpdate))
                .findFirst()
                .ifPresentOrElse(cartItem -> {

                    System.out.println("Enter new quantity:");
                    int newQuantity = getQuantityInput();

                    if (newQuantity <= 0) {
                        System.out.println("Quantity should be greater than 0.");
                        return;
                    }
                    if (newQuantity > 999) {
                        System.out.println("That quantity seems too high. it should be less than 999.");
                        return;
                    }
                    Response response = customerService.updateCartQuantity(loggedInCustomer.getUserId(), foodNameToUpdate, newQuantity);
                    System.out.println(response.getMessage());
                }, () -> System.out.println("❌ Food item not found."));
    }

    private boolean proceedWithCheckout() {
        System.out.println("Do you want to place the order? (y/n)");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            Response placeOrderResponse = customerService.placeOrder(loggedInCustomer);
            if (Boolean.FALSE.equals(placeOrderResponse.isSuccess())) {
                System.out.println(placeOrderResponse.getMessage());
                return false;
            }
            Order order = (Order) placeOrderResponse.getData();

            System.out.println(ColourCodes.BLUE + "Order placed successfully.\uD83C\uDF89 \nOrder ID: " + order.getId()
                    + " Total Bill: " + order.getTotalBill() + "₹" + ColourCodes.RESET);
            return true;
        } else {
            System.out.println("Order not placed.");
        }
        return false;
    }

    private void trackOrderStatus() {
        Response allOrdersResponse = customerService.getAllOrders(loggedInCustomer);
        if (Boolean.FALSE.equals(allOrdersResponse.isSuccess())) {
            System.out.println(allOrdersResponse.getMessage());
            return;
        }

        System.out.println("Enter order ID:");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        Response responseOrder = customerService.getOrderById(orderId);
        if (Boolean.FALSE.equals(responseOrder.isSuccess())) {
            System.out.println(responseOrder.getMessage());
            return;
        }
        Order order = (Order) responseOrder.getData();
        System.out.println("Order status: " + order.getOrderStatus());
    }

    private List<Order> getCustomerOrders() {
        Response ordersResponse = customerService.getAllOrders(loggedInCustomer);
        if (Boolean.FALSE.equals(ordersResponse.isSuccess())) {
            System.out.println(ordersResponse.getMessage());
            return List.of();
        }
        List<Order> allOrders = (List<Order>) ordersResponse.getData();
        return allOrders.stream()
                .filter(order -> Objects.equals(order.getUser().getUserId(), loggedInCustomer.getUserId())).toList();
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

    private void viewOrderHistory() {
        List<Order> customerOrders = getCustomerOrders();
        if (customerOrders.isEmpty()) return;

        DataFormatter.printTable(customerOrders.stream().toList());

        int orderId = promptOrderIdSelection(customerOrders);
        if (orderId == -1) return;

        Response responseOrder = customerService.getOrderById(orderId);
        if (Boolean.TRUE.equals(responseOrder.isSuccess()) || responseOrder.getData() != null) {
            Order order = (Order) responseOrder.getData();

            List<OrderItem> orderItems = order.getOrderItems();
            DataFormatter.printTable(orderItems);
        } else {
            System.out.println(responseOrder.getMessage());
        }
    }

    private void logoutCustomer() {
        Response response = customerService.logoutUser(loggedInCustomer.getEmail());
        if (Boolean.FALSE.equals(response.isSuccess())) {
            System.out.println(response.getMessage());
            return;
        }
        System.out.println(response.getMessage());
        loggedInCustomer = null;
        customerService = null;
    }

    @Override
    public void initAdminScreen(User user) {
        //not being used here
    }
}