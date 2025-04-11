package com.foodorder.app.enums;

public enum UserRole {
    CUSTOMER("CUSTOMER"), ADMIN("ADMIN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
