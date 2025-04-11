package com.foodorder.app.factory;

import com.foodorder.app.enums.UserRole;
import com.foodorder.app.ui.AdminUi;
import com.foodorder.app.ui.CustomerUi;
import com.foodorder.app.ui.Ui;

public class UiFactory {

    public static Ui getUiByRole(UserRole role, ServiceFactory serviceFactory) {
        return switch (role) {
            case ADMIN -> new AdminUi(
                    serviceFactory.getScanner(),
                    serviceFactory.getAdminService(),
                    serviceFactory.getRestaurantService(),
                    serviceFactory.getOrderService());

            case CUSTOMER -> new CustomerUi(
                    serviceFactory.getScanner(),
                    serviceFactory.getRestaurantService(),
                    serviceFactory.getCustomerService());
        };
    }
}
