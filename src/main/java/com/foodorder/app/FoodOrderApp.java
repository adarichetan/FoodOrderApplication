package com.foodorder.app;

import com.foodorder.app.entities.User;
import com.foodorder.app.enums.UserRole;
import com.foodorder.app.factory.ServiceFactory;
import com.foodorder.app.factory.UiFactory;
import com.foodorder.app.service.AuthenticationService;
import com.foodorder.app.ui.Ui;
import com.foodorder.app.utility.*;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
public class FoodOrderApp {
    private final ServiceFactory serviceFactory = ServiceFactory.getServiceFactory();
    private final AuthenticationService authService = serviceFactory.getAuthenticationService();
    private final Scanner scanner = serviceFactory.getScanner();
    private final Validators validators = serviceFactory.getValidators();

    public void run() {
        log.info("Application started...");

        int choice = -1;
        while (choice != 3) {
            try {
                MenuPrinter.displayMainMenu();
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> loginFlow();
                    case 2 -> registrationFlow();
                    case 3 -> {
                        ConnectionUtility.connectionClose();
                        System.out.println("Thank you for using the Food Ordering App! 🍽️");
                    }
                    default -> System.err.println("❌ Invalid choice. Try again.");
                }

            } catch (NoSuchElementException e) {
                log.error("Input error:", e);
                System.err.println("⚠️ Invalid input. Enter a valid number.");
                scanner.nextLine();
            } catch (Exception e) {
                log.error("Unexpected error:", e);
                System.err.println("⚠️ Something went wrong. Please contact admin.");
                scanner.nextLine();
            }
        }
    }

    private void loginFlow() {
        MenuPrinter.displayLoginMenu();

        String email = validators.checkStringInput("Enter your email: ");
        if (email == null) return;

        String password = validators.checkStringInput("Enter your password: ");
        if (password == null) return;

        Response loginResponse = authService.loginUser(email, password);

        if (Boolean.FALSE.equals(loginResponse.isSuccess())) {
            System.err.println(loginResponse.getMessage());
            return;
        }

        User user = (User) loginResponse.getData();
        Ui ui = UiFactory.getUiByRole(user.getRole(), serviceFactory);
        ui.initUserUi(user);
    }

    private void registrationFlow() {
        MenuPrinter.displayRegistrationMenu();

        while (true) {
            String name = validators.getValidName();
            if (name == null) return;

            String address = validators.getValidAddress();
            if (address == null) return;

            String email = validators.getValidEmail();
            if (email == null) return;

            String password = validators.getValidPassword();
            if (password == null) return;

            User newUser = User.builder()
                    .name(name)
                    .address(address)
                    .email(email)
                    .password(password)
                    .role(UserRole.CUSTOMER)
                    .build();

            Response response = authService.registerUser(newUser);

            if (Boolean.TRUE.equals(response.isSuccess())) {
                System.out.println(response.getMessage());
                loginFlow();
                break;
            } else {
                System.err.println(response.getMessage());
            }
        }
    }
}
