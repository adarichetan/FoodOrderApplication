package com.foodorder.app.enums;
public enum ResponseStatus {
    SUCCESS(200), FAILURE(404);

    private final int statusCode;

    ResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
