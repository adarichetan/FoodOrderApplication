package com.foodorder.app.entities;

import com.foodorder.app.enums.FoodCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodItem {
    private Integer id;
    private String name;
    private double price;
    private FoodCategory category;
    private Integer restaurantId;

    @Override
    public String toString() {
        return String.format("| %-15s | %-10.2f | %-13s |", name, price, category);
    }

}