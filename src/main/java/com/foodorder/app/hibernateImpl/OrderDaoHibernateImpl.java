package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.OrderDao;
import com.foodorder.app.daoImpl.CartDaoImpl;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.Order;
import com.foodorder.app.entities.OrderItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderDaoHibernateImpl implements OrderDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    @Override
    public Optional<Order> placeOrder(User user, List<CartItem> userCart) {

        try {
            tx.begin();

            Order newOrder = Order.builder()
                    .user(user)
                    .orderStatus(OrderStatus.ORDERED)
                    .orderItems(new ArrayList<>())
                    .totalBill(0.0)
                    .build();

            double totalBill = 0.0;

            for (CartItem cartItem : userCart) {
                OrderItem orderItem = OrderItem.builder()
                        .order(newOrder)
                        .foodItem(cartItem.getFoodItem())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getFoodItem().getPrice() * cartItem.getQuantity())
                        .build();

                totalBill += orderItem.getPrice();
                newOrder.addOrderItem(orderItem);
            }

            newOrder.setTotalBill(totalBill);

            manager.persist(newOrder);
            tx.commit();
            return Optional.of(newOrder);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        try {
            tx.begin();
            Order order = manager.find(Order.class, id);

            tx.commit();
            return Optional.of(order);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        String query = "SELECT o FROM Order o";
        TypedQuery<Order> query1 = manager.createQuery(query, Order.class);
        return query1.getResultList();
    }

    @Override
    public boolean updateOrderStatus(Order order) {
        try {
            tx.begin();
            manager.merge(order);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
