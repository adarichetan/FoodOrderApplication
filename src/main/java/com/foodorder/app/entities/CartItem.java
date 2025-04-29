package com.foodorder.app.entities;

import com.foodorder.app.utility.CurrencyFormatter;
import com.foodorder.app.utility.Formattable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem implements Formattable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private FoodItem foodItem;

    private Integer quantity;

    @OneToOne
    private User user;

    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }

    @Override
    public List<String> getColumns() {
        return List.of("NAME", "CATEGORY","PRICE", "QUANTITY");
    }

    @Override
    public List<String> getValues() {
        return List.of(this.foodItem.getName(), String.valueOf(this.foodItem.getCategory()),  CurrencyFormatter.format(foodItem.getPrice()), String.valueOf(this.quantity));
    }

    @Override
    public String getTitle() {
        return "CART ITEMS";
    }
}