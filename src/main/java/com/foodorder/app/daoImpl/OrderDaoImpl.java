package com.foodorder.app.daoImpl;

import com.foodorder.app.dao.OrderDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.OrderItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.OrderStatus;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderDaoImpl implements OrderDao {
    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idCounter = new AtomicInteger(1);
    @Getter
    private static final OrderDao orderDao = new OrderDaoImpl();

    private OrderDaoImpl() {
    }

    @Override
    public Optional<Order> placeOrder(User user, List<CartItem> userCart ) {
        Order order = Order.builder().id(idCounter.getAndIncrement())
                .user(user)
                .orderStatus(OrderStatus.ORDERED)
                .orderOn(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .orderItems(new ArrayList<>())
                .build();
        orders.add(order);

        for (CartItem cartItem : userCart) {
            OrderItem orderItem = OrderItem.builder().
                    order(order).foodItem(cartItem.getFoodItem()).quantity(cartItem.getQuantity()).
                    build();
            order.addOrderItem(orderItem);
        }

        order.calculateTotalBill();
        CartDaoImpl.getCartDaoImpl().clearCart(user);
        return Optional.of(order);
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        return orders.stream().filter(o -> o.getId() == id).findFirst();
    }

    @Override
    public List<Order> getAllOrders() {
        return orders.isEmpty() ? Collections.emptyList() : List.copyOf(orders);
    }

    @Override
    public boolean updateOrderStatus(Order order) {
        return orders.stream()
                .filter(o -> o.getId() == order.getId())
                .findFirst()
                .map(o -> {
                    o.setOrderStatus(order.getOrderStatus());
                    return true;
                }).orElse(false);
    }
}

