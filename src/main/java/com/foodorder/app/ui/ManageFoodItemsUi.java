package com.foodorder.app.ui;

import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.service.RestaurantService;
import com.foodorder.app.utility.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
public class ManageFoodItemsUi {
    private final Scanner scanner;
    private final RestaurantService restaurantService;
    private final Validators validators;

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

    private void addFoodItem(RestaurantService restaurantService) {
        while (true) {
            String foodName = validators.checkStringInput("Enter food name: ");
            if (foodName == null) return;

            double price = validators.checkNumericInput("Enter Price: ").doubleValue();
            if (price == -2) return;

            FoodCategory category = null;
            while (category == null) {
                String categoryStr = validators.checkStringInput("Enter food category (VEG/NONVEG/BEVERAGES/DESSERT):");
                if (categoryStr == null) return;

                try {
                    category = FoodCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println(ColourCodes.RED + "⚠ Invalid category. Please try again!" + ColourCodes.RESET);
                }
            }
            int restaurantId = 1001;

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
        String foodName = validators.checkStringInput("Enter food name to remove: ");
        if (foodName == null) return;

        Response response = restaurantService.removeFood(foodName);
        System.out.println(response.getMessage());
    }

    private void updateFoodItem(RestaurantService restaurantService) {
        String foodName = validators.checkStringInput("Enter food name to update: ");
        if (foodName == null) return;

        Response foodResponse = restaurantService.getFoodByName(foodName);
        if (Boolean.FALSE.equals(foodResponse.isSuccess())) {
            System.err.println(foodResponse.getMessage());
            return;
        }
        System.out.println(foodResponse.getMessage());

        FoodItem foodItem = (FoodItem) foodResponse.getData();

        String newName = validators.checkStringInput("Enter new food name: ");
        if (newName == null) return;

        double newPrice = validators.checkNumericInput("Enter new food price: ").doubleValue();
        if (newPrice == -2) return;

        FoodCategory newCategory = null;
        while (newCategory == null) {
            String newCategoryStr = validators.checkStringInput("Enter new category (VEG/NONVEG/BEVERAGES/DESSERT): ");
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

        Response allFoodResponse = restaurantService.getAllFood();
        if (Boolean.FALSE.equals(allFoodResponse.isSuccess())) {
            System.out.println(allFoodResponse.getMessage());
        }
        List<FoodItem> foodItems = (List<FoodItem>) allFoodResponse.getData();
        DataFormatter.printTable(foodItems);
    }

    private void displayFoodByCategory(RestaurantService restaurantService) {
        String categoryStr = validators.checkStringInput("Choose Food Category: \nVEG/NONVEG/BEVERAGES/DESSERT: ");
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

        List<FoodItem> foodItems = (List<FoodItem>) foodByCategoryResponse.getData();
        DataFormatter.printTable(foodItems);
    }
}
