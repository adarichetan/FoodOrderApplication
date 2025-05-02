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
@NamedQueries({
        @NamedQuery(
                name = "getCartByUserId",
                query = "SELECT c FROM CartItem c WHERE c.user.userId = :userId"),
        @NamedQuery(name = "deleteItemsFromCart",
                query = "DELETE FROM CartItem c WHERE c.user.userId = :userId AND c.foodItem.name = :name")
}
)

@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_userId", "foodItem_id"})
})
public class CartItem implements Formattable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private FoodItem foodItem;

    private Integer quantity;

    @ManyToOne
    private User user;

    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }

    @Override
    public List<String> getColumns() {
        return List.of("NAME", "CATEGORY", "PRICE", "QUANTITY");
    }

    @Override
    public List<String> getValues() {
        return List.of(this.foodItem.getName(), String.valueOf(this.foodItem.getCategory()), CurrencyFormatter.format(foodItem.getPrice()), String.valueOf(this.quantity));
    }

    @Override
    public String getTitle() {
        return "CART ITEMS";
    }
}