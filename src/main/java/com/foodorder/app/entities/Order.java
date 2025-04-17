
package com.foodorder.app.entities;

import com.foodorder.app.enums.OrderStatus;
import com.foodorder.app.utility.CurrencyFormatter;
import com.foodorder.app.utility.Formattable;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@ToString
public class Order implements Formattable {
    private int id;
    private User user;
    private Timestamp orderOn;
    private OrderStatus orderStatus;
    private List<OrderItem> orderItems;
    private double totalBill;

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        calculateTotalBill();
    }

    public void calculateTotalBill() {
        this.totalBill = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
    }

    @Override
    public List<String> getColumns() {
        return List.of("ORDER ID", "ORDER DATE", "STATUS", "TOTAL BILL");
    }

    @Override
    public List<String> getValues() {
        double totalbill= this.totalBill;
        return List.of(String.valueOf(this.id), String.valueOf(this.orderOn),
                String.valueOf(this.orderStatus), CurrencyFormatter.format(totalbill));
    }

    @Override
    public String getTitle() {
        return "Order History";
    }
}