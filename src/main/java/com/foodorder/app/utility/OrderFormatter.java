package com.foodorder.app.utility;

import com.foodorder.app.entities.Order;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderFormatter {
    public static void printTable(List<Order> orders) {
        String format = "| %-10s    | %-10s | %-22s | %-15s | %-10s |%n";
        String separator =
                ColourCodes.PURPLE + "+------------+------------+------------------------+-----------------+------------+" + ColourCodes.RESET;

        System.out.println(separator);
        System.out.format(format, ColourCodes.PURPLE + "Order ID", "User ID", "Order Date", "  Status", "  Total Bill" + ColourCodes.RESET);
        System.out.println(separator);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Order order : orders) {
            String formattedDate = sdf.format(order.getOrderOn());
            System.out.format(format,
                    order.getId(),
                    order.getUser().getUserId(),
                    formattedDate,
                    order.getOrderStatus(),
                    order.getTotalBill());
        }
        System.out.println(separator);
    }
}
