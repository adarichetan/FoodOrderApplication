package com.foodorder.app.utility;

import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Validators {
    Scanner scanner = new Scanner(System.in);
    public final Predicate<String> isEmailValid = email -> Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.[A-Za-z]*$", email);

    public final Predicate<String> isPasswordValid = password -> Pattern.matches("^(?=.*[0-9])" +
            "(?=.*[a-z])" +
            "(?=.*[A-Z])" +
            "(?=.*[@#$%^&+=!~])" +
            "(?!.*[\\s]$)(?!^[\\s]).{8,20}$", password);


    public final String getValidEmailInput() {
        while (true) {
            String email = getValidInput("Enter Email: ");
            if (email == null) return null;

            if (Boolean.FALSE.equals(isEmailValid.test(email))) {
                System.err.println("""
                        ⚠️ Invalid email format. Make sure:
                        - It contains '@' and a domain (e.g., .com, .org)
                        - No spaces or invalid characters
                        - Like: name@example.com
                        """);

                continue;
            }
            return email;
        }
    }

    public final String getValidPasswordInput() {
        while (true) {
            String password = getValidInput("Enter Password: ");
            if (password == null) return null;

            if (Boolean.FALSE.equals(isPasswordValid.test(password))) {

                System.err.println("""
                        ⚠️ Invalid password format. Make sure:
                        - It contains 8–20 characters
                        - 1 uppercase, 1 lowercase
                        - 1 number and 1 special character like (@, #, !, etc.)
                        """);
                continue;
            }
            return password;
        }
    }

    public final String getValidInput(String prompt) {
        System.out.println(prompt +
                ColourCodes.BLUE + "Press '0' to exit!" + ColourCodes.RESET);
        String input = scanner.nextLine();

        if (input.equals("0")) {
            System.err.println("Returning to main menu..");
            return null;
        }
        return input;
    }
}
