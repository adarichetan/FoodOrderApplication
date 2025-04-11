package com.foodorder.app.ui;

import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.Restaurant;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.RestaurantService;
import com.foodorder.app.utility.ColourCodes;
import com.foodorder.app.utility.MenuPrinter;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ManageFoodItemsUi {
    private final Scanner scanner;
    private final RestaurantService restaurantService;

    public ManageFoodItemsUi(Scanner scanner, RestaurantService restaurantService) {
        this.scanner = scanner;
        this.restaurantService = restaurantService;
    }

    public void initManageFoods() {
        boolean isExit = false;
        while (!isExit) {
            try {
                MenuPrinter.displayMenu("MANAGE FOOD ITEMS", List.of(
                        "➕ Add Food Item", "❌ Remove Food Item", "🛠️ Update Food Item", "📋 Display All Food Items",
                        "📂 Display Food by Category", "🔙 Back to Admin Menu"));

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addFoodItem(restaurantService);
                    case 2 -> removeFoodItem(restaurantService);
                    case 3 -> updateFoodItem(restaurantService);
                    case 4 -> displayAllFoodItems(restaurantService);
                    case 5 -> displayFoodByCategory(restaurantService);
                    case 6 -> isExit = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException | InputMismatchException e) {
                log.error("Error from restaurant food Ui: ", e);
                System.out.println("invalid input");
                scanner.nextLine();
            }
        }
    }

    private int getRestaurant(RestaurantService restaurantService) {
        System.out.println(ColourCodes.CYAN + "Enter the restaurant ID to add food item from the restaurants listed below:" + ColourCodes.RESET);

        Response allRestaurants = restaurantService.getAllRestaurants();
        if (ResponseStatus.FAILURE.equals(allRestaurants.getResponseStatus())) {
            System.out.println(allRestaurants.getMessage());
            return -1;
        }

        List<Restaurant> restaurantList = (List<Restaurant>) allRestaurants.getData();
        System.out.println(ColourCodes.GREEN + "Available Restaurants:" + ColourCodes.RESET);
        restaurantList.forEach(restaurant ->
                System.out.println(restaurant.getId() + " - " + restaurant.getName()));

        int choice;
        while (true) {
            choice = getIntegerInput(ColourCodes.GREEN + "Enter your choice: " + ColourCodes.RESET);
            if (choice == -2) return -1;
            if (choice <= 0) {
                continue;
            }
            break;
        }
        return choice;
    }

    public final String getValidInput(String prompt) {
        System.out.println(prompt + ColourCodes.BLUE + "Press '0' to exit!" + ColourCodes.RESET);
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.err.println("Returning to previous menu..");
            return null;
        }
        return input;
    }

    private int getIntegerInput(String prompt) {
        while (true) {
            System.out.println(prompt + ColourCodes.BLUE + "Press '-1' to exit!" + ColourCodes.RESET);
            try {
                int number = scanner.nextInt();
                scanner.nextLine();
                if (number == -1) {
                    return -2;
                }
                if (number == 0) {
                    System.out.println(ColourCodes.RED + "⚠ Input must be greater than 0!" + ColourCodes.RESET);
                    return -1;
                }
                return number;

            } catch (InputMismatchException e) {
                System.out.println(ColourCodes.RED + "⚠ Please enter a valid integer!" + ColourCodes.RESET);
                scanner.nextLine();
                return -1;
            }
        }
    }

    private void addFoodItem(RestaurantService restaurantService) {
        while (true) {
            String foodName = getValidInput("Enter food name: ");
            if (foodName == null) return;

            int price;
            do {
                price = getIntegerInput("Enter food price: ");
                if (price == -1) continue;
                if (price == -2) return;

            } while (price <= 0);

            FoodCategory category = null;
            while (category == null) {
                String categoryStr = getValidInput("Enter food category (VEG/NONVEG/BEVERAGES/DESSERT):");
                if (categoryStr == null) return;

                try {
                    category = FoodCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println(ColourCodes.RED + "⚠ Invalid category. Please try again!" + ColourCodes.RESET);
                }
            }

            int restaurantId = getRestaurant(restaurantService);
            if (restaurantId == -1) return;

            Response restaurantById = restaurantService.getRestaurantById(restaurantId);
            if (Boolean.FALSE.equals(restaurantById.isSuccess())) {
                System.out.println(restaurantById.getMessage());
                return;
            }

            FoodItem foodItem = FoodItem.builder()
                    .name(foodName)
                    .price(price)
                    .category(category)
                    .restaurantId(restaurantId)
                    .build();

            Response response = restaurantService.addFood(foodItem);
            System.out.println(response.getMessage());

            break;
        }
    }

    private void removeFoodItem(RestaurantService restaurantService) {
        String foodName = getValidInput("Enter food name to remove: ");
        if (foodName == null) return;

        Response response = restaurantService.removeFood(foodName);
        System.out.println(response.getMessage());
    }

    private void updateFoodItem(RestaurantService restaurantService) {
        String foodName = getValidInput("Enter food name to update: ");
        if (foodName == null) return;

        Response foodResponse = restaurantService.getFoodByName(foodName);
        if (Boolean.FALSE.equals(foodResponse.isSuccess())) {
            System.err.println(foodResponse.getMessage());
            return;
        }
        System.out.println(foodResponse.getMessage());

        FoodItem foodItem = (FoodItem) foodResponse.getData();

        String newName = getValidInput("Enter new food name: ");
        if (newName == null) return;

        int newPrice;
        do {
            newPrice = getIntegerInput("Enter new food price: ");
            if (newPrice == -1) continue;
            if (newPrice == -2) return;
        } while (newPrice <= 0);

        FoodCategory newCategory = null;
        while (newCategory == null) {
            String newCategoryStr = getValidInput("Enter new category (VEG/NONVEG/BEVERAGES/DESSERT): ");
            if (newCategoryStr == null) return;
            try {
                newCategory = FoodCategory.valueOf(newCategoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(ColourCodes.RED + "⚠ Invalid category. Please try again!" + ColourCodes.RESET);
            }
        }

        foodItem.setName(newName);
        foodItem.setPrice(newPrice);
        foodItem.setCategory(newCategory);

        Response response = restaurantService.updateFood(foodItem);
        System.out.println(response.getMessage());
    }

    private void displayAllFoodItems(RestaurantService restaurantService) {
        System.out.println(ColourCodes.CYAN + "\nMENU" + ColourCodes.RESET);
        System.out.printf(ColourCodes.PURPLE + "| %-15s | %-10s | %-10s |" + ColourCodes.RESET + "%n", "Food Name", "Item Price", "Food Category");
        Response allFoodResponse = restaurantService.getAllFood();
        if (Boolean.FALSE.equals(allFoodResponse.isSuccess())) {
            System.out.println(allFoodResponse.getMessage());
        }
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();
        foodItems.forEach(System.out::println);
    }

    public void displayFoodByCategory(RestaurantService restaurantService) {

        String categoryStr = getValidInput("Choose Food Category: \nVEG/NONVEG/BEVERAGES/DESSERT: ");
        if (categoryStr == null) return;

        FoodCategory category;
        try {
            category = FoodCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(ColourCodes.RED + "⚠ Invalid category!" + ColourCodes.RESET);
            return;
        }

        Response foodByCategoryResponse = restaurantService.getFoodByCategory(category);
        if (Boolean.FALSE.equals(foodByCategoryResponse.isSuccess())) {
            System.out.println(foodByCategoryResponse.getMessage());
            return;
        }
        System.out.println(ColourCodes.CYAN + "\nMENU" + ColourCodes.RESET);
        System.out.printf(ColourCodes.PURPLE + "| %-15s | %-10s | %-10s |" + ColourCodes.RESET + "%n", "Food Name", "Item Price", "Food Category");
        List<FoodItem> foodItems = (List<FoodItem>) foodByCategoryResponse.getData();
        foodItems.forEach(System.out::println);
    }
}