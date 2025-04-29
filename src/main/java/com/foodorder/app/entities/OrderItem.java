package com.foodorder.app.entities;

import com.foodorder.app.utility.CurrencyFormatter;
import com.foodorder.app.utility.Formattable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem implements Formattable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private FoodItem foodItem;
    private Integer quantity;

    @ManyToOne
    private Order order;

    private double price;

//    public OrderItem(Order order, FoodItem foodItem, Integer quantity) {
//        this.id = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 5000);
//        this.order = order;
//        this.foodItem = foodItem;
//        this.quantity = quantity;
//        this.price = foodItem.getPrice() * quantity;
//    }

    @Override
    public List<String> getColumns() {
        return List.of("NAME", "PRICE", "CATEGORY", "QUANTITY");
    }

    @Override
    public List<String> getValues() {
        return List.of(this.foodItem.getName(),
                CurrencyFormatter.format(this.getPrice()),
                String.valueOf(this.foodItem.getCategory()),
                String.valueOf(this.quantity));
    }

    @Override
    public String getTitle() {
        return "ORDERED ITEM";
    }
}

