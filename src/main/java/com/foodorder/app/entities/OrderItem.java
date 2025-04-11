package com.foodorder.app.entities;

import com.foodorder.app.utility.Formattable;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderItem implements Formattable {
    private Integer id;
    private FoodItem foodItem;
    private int quantity;
    private Order order;
    private double price;

    public OrderItem(Order order, FoodItem foodItem, int quantity) {
        this.id = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 5000);
        this.order = order;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.price = foodItem.getPrice() * quantity;
    }

    @Override
    public List<String> getColumns() {
        return List.of("Name", "Price", "Category", "Quantity");
    }

    @Override
    public List<String> getValues() {
        return List.of(this.foodItem.getName(), String.valueOf(this.foodItem.getPrice()), String.valueOf(this.foodItem.getCategory()), String.valueOf(this.quantity));
    }

    @Override
    public String getTitle() {
        return "Ordered item";
    }
}

