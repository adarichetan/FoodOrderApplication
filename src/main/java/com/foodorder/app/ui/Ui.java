package com.foodorder.app.ui;

import com.foodorder.app.entities.User;

public abstract class Ui {
    public abstract void initAdminScreen(User user);

    public abstract void initCustomerScreen(User user);

    public void initUserUi(User user) {
        switch (user.getRole()) {
            case ADMIN -> initAdminScreen(user);
            case CUSTOMER -> initCustomerScreen(user);
        }
    }
}
