package com.foodorder.app.utility;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.function.Predicate;

@Slf4j
public class Validators {
    private final Scanner scanner;
    private static final String EXIT_MESSAGE = ColourCodes.BLUE+ "Enter 'E' to return!" + ColourCodes.RESET;

    public Validators(Scanner scanner) {
        this.scanner = scanner;
    }

    public final String getValidInput(String prompt, Predicate<String> validationRule, String errorMessage) {
        while (true) {
            System.out.println(prompt + EXIT_MESSAGE);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("E")) {
                System.err.println("Returning to main menu...");
                return null;
            }

            if (validationRule.test(input)) {
                return input;
            } else {
                System.err.println(errorMessage);
            }
        }
    }

    public String getValidName() {
        return getValidInput(
                "Enter your name: ",
                input -> !input.isBlank() && input.length() >= 2 && input.length() <= 50,
                "Name cannot be empty and must be between 2 and 50 characters long."
        );
    }

    public String getValidAddress() {
        return getValidInput(
                "Enter your address: ",
                input -> !input.isBlank() && input.length() >= 5 && input.length() <= 100,
                "Address cannot be empty and must be between 5 and 100 characters long."
        );
    }

    public String getValidEmail() {
        return getValidInput("Enter your email: ",
                input -> input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.[A-Za-z]*$"),
                """
                        ⚠️ Invalid email format. Make sure:
                        - It contains '@' and a domain (e.g., .com, .org)
                        - No spaces or invalid characters
                        - Like: name@example.com
                        """
        );
    }

    public String getValidPassword() {
        return getValidInput("Enter your password: ",
                input -> input.matches("(?=.*[a-z])" +
                        "(?=.*[A-Z])(?=.*[@#$%^&+=!~])(?!.*[\\s]$)(?!^[\\s]).{8,20}$"),
                """
                        ⚠️ Invalid password format. Make sure:
                        - It contains 8–20 characters
                        - 1 uppercase, 1 lowercase
                        - 1 number and 1 special character like (@, #, !, etc.)
                        """
        );
    }

    public Number checkNumericInput(String prompt) {
        while (true) {
            System.out.println(prompt + EXIT_MESSAGE);
            try {

                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("E")) {
                    System.err.println("Returning to previous menu...");
                    return -2;
                }

                double number = Double.parseDouble(input);

                if (number <= 0) {
                    System.out.println(ColourCodes.RED + "⚠ Input must be greater than 0!" + ColourCodes.RESET);
                    continue;
                }

                if (number != Math.floor(number)) {
                    System.out.println(ColourCodes.RED + "⚠ Please enter a valid integer!" + ColourCodes.RESET);
                    continue;
                }

                return number;

            } catch (NumberFormatException e) {
                System.out.println(ColourCodes.RED + "⚠ Please enter a valid number!" + ColourCodes.RESET);
            }
        }
    }

    public final String checkStringInput(String prompt) {
        while (true) {
            System.out.println(prompt + EXIT_MESSAGE);
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.err.println("⚠ input cannot be empty!");
                continue;
            }

            if (input.equalsIgnoreCase("E")) {
                System.err.println("Returning to previous menu..");
                return null;
            }
            return input;
        }
    }
}
