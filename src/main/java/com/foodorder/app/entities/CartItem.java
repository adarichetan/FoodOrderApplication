package com.foodorder.app.entities;

import com.foodorder.app.utility.CurrencyFormatter;
import com.foodorder.app.utility.Formattable;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartItem implements Formattable {
    private Integer id;
    private FoodItem foodItem;
    private int quantity;
    private User user;

    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }

    @Override
    public List<String> getColumns() {
        return List.of("NAME", "CATEGORY", "QUANTITY", "PRICE");
    }

    @Override
    public List<String> getValues() {
        return List.of(this.foodItem.getName(), String.valueOf(this.foodItem.getCategory()), String.valueOf(this.quantity),
                CurrencyFormatter.format(foodItem.getPrice()));
    }

    @Override
    public String getTitle() {
        return "Ordered item";
    }
}