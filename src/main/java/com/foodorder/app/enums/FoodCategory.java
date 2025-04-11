package com.foodorder.app.enums;

public enum FoodCategory {
    VEG("VEG"), NONVEG("NONVEG"), BEVERAGES("BEVERAGES"), DESSERT("DESSERT");

    private final String category;

    FoodCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }
}
