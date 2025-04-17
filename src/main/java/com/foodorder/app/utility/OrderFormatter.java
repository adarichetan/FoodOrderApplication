package com.foodorder.app.utility;

import com.foodorder.app.entities.Order;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderFormatter {

    private static final String FORMAT = "| %-10s | %-8s | %-19s | %-12s | %15s |%n";
    private static final String SEPARATOR =
            ColourCodes.PURPLE + "+------------+----------+---------------------+--------------+-----------------+" + ColourCodes.RESET;

    public static void printTable(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders to display.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(SEPARATOR);
        System.out.printf(FORMAT,
                ColourCodes.PURPLE + "Order ID  " + ColourCodes.RESET,
                ColourCodes.PURPLE + "User ID " + ColourCodes.RESET,
                ColourCodes.PURPLE + "Order Date         " + ColourCodes.RESET,
                ColourCodes.PURPLE + "Status      " + ColourCodes.RESET,
                ColourCodes.PURPLE + "Total Bill     " + ColourCodes.RESET
        );
        System.out.println(SEPARATOR);

        for (Order order : orders) {
            String formattedDate = order.getOrderOn() != null
                    ? sdf.format(order.getOrderOn())
                    : "N/A";

            System.out.printf(FORMAT,
                    order.getId(),
                    order.getUser() != null ? order.getUser().getUserId() : "N/A",
                    formattedDate,
                    order.getOrderStatus(),
                    CurrencyFormatter.format(order.getTotalBill())
            );
        }

        System.out.println(SEPARATOR);
    }
}
