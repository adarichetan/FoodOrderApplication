package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.CartDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CartDaoHibernateImpl implements CartDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    @Override
    public boolean addToCart(User user, FoodItem foodItem, int quantity) {
        try {
            tx.begin();
            String qString = """
                    SELECT c FROM CartItem c
                    WHERE c.user.userId = :userId AND c.foodItem.id = :foodId
                    """;
            TypedQuery<CartItem> query = manager.createQuery(qString, CartItem.class);
            query.setParameter("userId", user.getUserId());
            query.setParameter("foodId", foodItem.getId());

            CartItem existingItem = null;
            try {
                existingItem = query.getSingleResult();
            } catch (NoResultException ignored) {
            }
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                manager.merge(existingItem);
            } else {
                CartItem cartItem = CartItem.builder()
                        .foodItem(foodItem)
                        .user(user)
                        .quantity(quantity)
                        .build();

                manager.persist(cartItem);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CartItem> getCartByUserId(User user) {
        TypedQuery<CartItem> getCart = manager.createNamedQuery("getCartByUserId", CartItem.class);
        getCart.setParameter("userId", user.getUserId());
        return getCart.getResultList();
    }

    @Override
    public boolean clearCart(User user) {
        try {
            tx.begin();
            int deletedCount = manager.createQuery("""
                                DELETE FROM CartItem c WHERE c.user.userId = :userId
                            """).
                    setParameter("userId", user.getUserId())
                    .executeUpdate();
            tx.commit();
            return deletedCount > 0;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Failed to clear cart", e);
        }
    }

    @Override
    public boolean updateQuantityFromCart(int id, String foodName, int newQuantity) {
        try {
            tx.begin();
//            TypedQuery<CartItem> query = manager.createQuery("SELECT c FROM CartItem c WHERE c.foodItem.name LIKE :foodName", CartItem.class)
//                    .setParameter("foodName", foodName);
//            CartItem item = query.getSingleResult();

            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<CartItem> cq = builder.createQuery(CartItem.class);
            Root<CartItem> root = cq.from(CartItem.class);

            cq.where(builder.equal(root.get("foodItem").get("name"), foodName));
            TypedQuery<CartItem> query = manager.createQuery(cq);
            CartItem item = query.getSingleResult();

            item.setQuantity(newQuantity);
            manager.merge(item);
            tx.commit();
            return true;

        } catch (NoResultException e) {
            tx.rollback();
            return false;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteFromCart(int userId, String name) {
        try {
            tx.begin();
            int deleteItemsFromCart =
                    manager.createNamedQuery("deleteItemsFromCart")
                    .setParameter("userId", userId).setParameter("name", name).executeUpdate();
            tx.commit();
            return deleteItemsFromCart > 0;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
