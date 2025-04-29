package com.foodorder.app.serviceImpl;


import com.foodorder.app.dao.CartDao;
import com.foodorder.app.dao.OrderDao;
import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.OrderStatus;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.OrderService;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final CartDao cartDao;

    public OrderServiceImpl(OrderDao orderDao, CartDao cartDao) {
        this.orderDao = orderDao;
        this.cartDao = cartDao;
    }

    @Override
    public Response placeOrder(User user) {
        try {
            List<CartItem> cartItems = cartDao.getCartByUserId(user);
            if (cartItems.isEmpty()) {
                return new Response(ResponseStatus.FAILURE, "❗ Your cart is empty.");
            }

            Optional<Order> order = orderDao.placeOrder(user, cartItems);
            if (order.isPresent()) {
                boolean b = cartDao.clearCart(user);
                if (! b){
                    return new Response(ResponseStatus.FAILURE, "Unable to clear the existing cart.. please contact admin");
                }

                simulateOrderProcessing(order.get());
                return new Response(order.get(), ResponseStatus.SUCCESS, "Order placed successfully.\uD83C\uDF89");
            }
        } catch (Exception e) {
            log.error("Error in placeOrder method from orderServiceImpl", e);
        }
        return new Response(ResponseStatus.FAILURE, "Unable to save the order.");
    }

    @Override
    public Response getOrderById(int orderId) {
        try {
            Optional<Order> orderById = orderDao.getOrderById(orderId);
            return orderById.map(order -> new Response(order, ResponseStatus.SUCCESS, "Order details fetched successfully."))
                    .orElseGet(() -> new Response(ResponseStatus.FAILURE, "Invalid order ID or order not found."));
        } catch (Exception e) {
            log.error("Error in getOrder method: ", e);
            return new Response(ResponseStatus.FAILURE, "An unexpected error occurred while retrieving the order.");
        }
    }

    @Override
    public Response getAllOrders() {
        try {
            List<Order> allOrders = orderDao.getAllOrders();

            if (!allOrders.isEmpty()) {
                return new Response(allOrders, ResponseStatus.SUCCESS, "Successfully fetched orders.");
            }
        } catch (SQLException e) {
            log.error("Error while fetching all orders", e);
        }
        return new Response(ResponseStatus.FAILURE, "You have no past orders.");
    }

    @Override
    public void simulateOrderProcessing(Order order) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                order.setOrderStatus(OrderStatus.PROCESSING);
                orderDao.updateOrderStatus(order);

                Thread.sleep(15000);
                order.setOrderStatus(OrderStatus.COMPLETED);
                orderDao.updateOrderStatus(order);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while simulatingOrderProcessing method", e);
                handleOrderFailure(order);
            } catch (Exception e) {
                handleOrderFailure(order);
            }
        });
        thread.start();
    }

    void handleOrderFailure(Order order) {
        order.setOrderStatus(OrderStatus.CANCELED);
        try {
            orderDao.updateOrderStatus(order);
        } catch (Exception e) {
            log.error("Failed to set orderStatus", e);
        }
    }
}
