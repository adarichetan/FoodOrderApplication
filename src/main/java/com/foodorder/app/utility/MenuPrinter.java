package com.foodorder.app.utility;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuPrinter {

    private static final String DOTTED_LINE = "--------------------------------------------";

    public static void displayMainMenu() {
        displayMenu("༻❁ WELCOME TO FOOD ORDERING APPLICATION ❁༺",
                List.of("Login", "Customer Registration", "Exit"));
    }

    public static void displayLoginMenu() {
        displaySection("User Login Portal");
    }

    public static void displayRegistrationMenu() {
        displaySection("Customer Registration");
    }

    public static void displaySection(String title) {
        System.out.println();
        System.out.println(ColourCodes.BLUE + title.toUpperCase() + ColourCodes.RESET);
        System.out.println(ColourCodes.BLUE + DOTTED_LINE + ColourCodes.RESET);
    }

    public static void displayMenu(String title, List<String> options) {
        displaySection(title);
        AtomicInteger index = new AtomicInteger(1);

        for (String option : options) {
            if (option.equalsIgnoreCase("[Enter Food Name] ➕ Add Item to Cart: ")) {
                System.out.println(option);
            } else {
                System.out.println(ColourCodes.YELLOW + "[" + index.getAndIncrement() + "] "
                        + ColourCodes.RESET + option);
            }
        }

        System.out.println(ColourCodes.BLUE + DOTTED_LINE + ColourCodes.RESET);
        System.out.print(ColourCodes.GREEN + "👉 Enter your choice:\n" + ColourCodes.RESET);
    }
}
