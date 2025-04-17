package com.foodorder.app.entities;

import com.foodorder.app.enums.FoodCategory;
import com.foodorder.app.utility.CurrencyFormatter;
import com.foodorder.app.utility.Formattable;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FoodItem implements Formattable {
    private Integer id;
    private String name;
    private double price;
    private FoodCategory category;
    private Integer restaurantId;

    @Override
    public List<String> getColumns() {
        return List.of("FOOD NAME", "ITEM PRICE", "FOOD CATEGORY");
    }

    @Override
    public List<String> getValues() {
        return List.of(String.valueOf(this.name), CurrencyFormatter.format(price), String.valueOf(this.category));
    }

    @Override
    public String getTitle() {
        return "Food Menu";
    }

}