package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.CartDao;
import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CartDaoHibernateImpl implements CartDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    @Override
    public boolean addToCart(User user, FoodItem foodItem, int quantity) {
        return false;
    }

    @Override
    public List<CartItem> getCartItems(User user) {

        return List.of();
    }

    @Override
    public boolean clearCart(User userId) {
        return false;
    }

    @Override
    public boolean updateQuantityFromCart(int id, String foodName, int newQuantity)  {
        return false;
    }

    @Override
    public boolean deleteFromCart(int id, String name) {
        return false;
    }
}
