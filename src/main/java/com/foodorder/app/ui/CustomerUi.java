package com.foodorder.app.ui;

import com.foodorder.app.entities.*;

import com.foodorder.app.service.*;
import com.foodorder.app.utility.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class CustomerUi extends Ui {
    private final Scanner scanner;
    private final RestaurantService restaurantService;
    private final CustomerService customerService;
    private final CartService cartService;
    private final OrderService orderService;
    private final Validators validators;
    private final AuthenticationService authService;
    private User loggedInCustomer;

    @Override
    public void initCustomerScreen(User userData) {
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

    private void viewMenu() {
        Response allFoodResponse = restaurantService.getAllFood();
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();
        DataFormatter.printTable(foodItems);

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
                    Response responseCart = cartService.getCartItems(loggedInCustomer);
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
        int quantity = validators.checkNumericInput("Enter Quantity: ").intValue();
        if (quantity == -2) return;
        if (quantity > 500) {
            System.out.println("That quantity seems too high. it should be less than 500.");
            return;
        }

        Response response = cartService.addToCart(loggedInCustomer, item, quantity);
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

    private boolean isSimilar(String menuItem, String userInput) {
        String normalizedMenuItem = menuItem.trim().toLowerCase().replaceAll("\\s+", "");
        return normalizedMenuItem.equalsIgnoreCase(userInput);
    }

    private Optional<FoodItem> foodItemIsPresent(String foodName) {
        Response allFoodResponse = restaurantService.getAllFood();
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();

        String normalizedFoodName = foodName.trim().toLowerCase().replaceAll("\\s+", "");

        return foodItems.stream()
                .filter(f -> isSimilar(f.getName(), normalizedFoodName))
                .findFirst();
    }

    private List<CartItem> getCart() {
        Response responseCart = cartService.getCartItems(loggedInCustomer);

        if (Boolean.FALSE.equals(responseCart.isSuccess())) {
            System.out.println(responseCart.getMessage());
            return List.of();
        }
        System.out.println(ColourCodes.CYAN + "\nCART" + ColourCodes.RESET);
        List<CartItem> cart = (List<CartItem>) responseCart.getData();
        DataFormatter.printTable(cart);

        double totalBill = cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
        System.out.println("Total Bill: " + CurrencyFormatter.format(totalBill));
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
        String foodItem = validators.checkStringInput("❌ Enter the food name to remove from cart: ");
        if (foodItem == null) return;
        String normalizedFoodName = foodItem.trim().toLowerCase().replaceAll("\\s+", "");

        cart.stream()
                .filter(c -> isSimilar(c.getFoodItem().getName(), normalizedFoodName))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    Response response = cartService.deleteFromCart(loggedInCustomer.getUserId(), cartItem.getFoodItem().getName());
                    System.out.println(response.getMessage());
                }, () -> System.out.println("❌ Food item not found."));
    }

    private void updateCartQuantity(List<CartItem> cart) {
        String foodItem = validators.checkStringInput("✏️ Enter the food name to update: ");
        if (foodItem == null) return;
        String normalizedFoodName = foodItem.trim().toLowerCase().replaceAll("\\s+", "");

        cart.stream()
                .filter(c -> isSimilar(c.getFoodItem().getName(), normalizedFoodName))
                .findFirst().ifPresentOrElse(cartItem -> {

                    int newQuantity = validators.checkNumericInput("Enter quantity: ").intValue();
                    if (newQuantity == -2) return;
                    if (newQuantity > 500) {
                        System.out.println("That quantity seems too high. it should be less than 500.");
                        return;
                    }
                    Response response = cartService.updateQuantityFromCart(loggedInCustomer.getUserId(), foodItem, newQuantity);
                    System.out.println(response.getMessage());
                }, () -> System.out.println("❌ Food item not found."));
    }

    private boolean proceedWithCheckout() {
        System.out.println("Do you want to place the order? (y/n)");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            Response placeOrderResponse = orderService.placeOrder(loggedInCustomer);
            if (Boolean.FALSE.equals(placeOrderResponse.isSuccess())) {
                System.out.println(placeOrderResponse.getMessage());
                return false;
            }
            Order order = (Order) placeOrderResponse.getData();

            System.out.println(ColourCodes.BLUE + "Order placed successfully.\uD83C\uDF89 \nOrder ID: " + order.getId()
                    + " Total Bill: " +CurrencyFormatter.format(order.getTotalBill()) + ColourCodes.RESET);
            return true;
        } else {
            System.out.println("Order not placed.");
        }
        return false;
    }

    private void trackOrderStatus() {
        int orderId = validators.checkNumericInput("Enter order Id: ").intValue();
        if (orderId == -2) return;

        Response responseOrder = orderService.getOrderById(orderId);
        if (Boolean.FALSE.equals(responseOrder.isSuccess())) {
            System.out.println(responseOrder.getMessage());
            return;
        }
        Order order = (Order) responseOrder.getData();
        System.out.println("Order status: " + order.getOrderStatus());
    }

    private List<Order> getCustomerOrders() {
        Response ordersResponse = orderService.getAllOrders();
        if (Boolean.FALSE.equals(ordersResponse.isSuccess())) {
            System.out.println(ordersResponse.getMessage());
            return List.of();
        }
        List<Order> allOrders = (List<Order>) ordersResponse.getData();
        return allOrders.stream()
                .filter(order -> Objects.equals(order.getUser().getUserId(), loggedInCustomer.getUserId())).toList();
    }

    private void viewOrderHistory() {
        List<Order> orders = getCustomerOrders();
        if (orders.isEmpty()) return;

        DataFormatter.printTable(orders.stream().toList());

        int finalOrderId = validators.checkNumericInput("🔍 Enter the Order ID to view order details: \n\uD83D\uDEAA ").intValue();
        if (finalOrderId == -2) return;

        boolean exists = orders.stream().anyMatch(order -> order.getId() == finalOrderId);
        if (!exists) {
            System.err.println("❗ No order found with ID: " + finalOrderId);
            return;
        }

        Response responseOrder = orderService.getOrderById(finalOrderId);
        if (Boolean.TRUE.equals(responseOrder.isSuccess()) || responseOrder.getData() != null) {
            Order order = (Order) responseOrder.getData();

            List<OrderItem> orderItems = order.getOrderItems();
            DataFormatter.printTable(orderItems);
        } else {
            System.out.println(responseOrder.getMessage());
        }
    }

    private void logoutCustomer() {
        Response res = authService.logoutUser(loggedInCustomer);
        if (Boolean.FALSE.equals(res.isSuccess())) {
            System.out.println(res.getMessage());
            return;
        }
        System.out.println(res.getMessage());
        loggedInCustomer = null;
    }

    @Override
    public void initAdminScreen(User user) {
        //not being used here
    }
}