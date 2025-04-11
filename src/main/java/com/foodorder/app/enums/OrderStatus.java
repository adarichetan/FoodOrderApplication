package com.foodorder.app.enums;
public enum OrderStatus {
    COMPLETED("COMPLETED"), PROCESSING("PROCESSING"), ORDERED("ORDERED"), CANCELED("CANCELED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
