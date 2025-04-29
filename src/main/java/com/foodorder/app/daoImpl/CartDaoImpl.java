package com.foodorder.app.daoImpl;

import com.foodorder.app.dao.CartDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CartDaoImpl implements CartDao {
    private static final List<CartItem> cartData = Collections.synchronizedList(new ArrayList<>());
    private static CartDaoImpl cartDao;
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private CartDaoImpl() {
    }

    public static CartDaoImpl getCartDaoImpl() {
        if (cartDao == null) {
            cartDao = new CartDaoImpl();
        }
        return cartDao;
    }

    @Override
    public boolean addToCart(User user, FoodItem foodItem, int quantity) {
        Optional<CartItem> existingItem = cartData.stream()
                .filter(cartItem -> cartItem.getUser().getName().equalsIgnoreCase(user.getName()) &&
                        cartItem.getFoodItem().getName().equalsIgnoreCase(foodItem.getName()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);

        } else {
            CartItem cartItem = CartItem.builder()
                    .id(idCounter.getAndIncrement())
                    .user(user)
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .build();
            cartData.add(cartItem);
        }
        return true;
    }

    @Override
    public List<CartItem> getCartByUserId(User user) {
        return cartData.stream()
                .filter(cartItem -> cartItem.getUser().getEmail().equalsIgnoreCase(user.getEmail()))
                .toList();
    }

    @Override
    public boolean clearCart(User user) {
        return cartData.removeIf(cartItem -> cartItem.getUser().getName().equalsIgnoreCase(user.getName()));
    }

    @Override
    public boolean updateQuantityFromCart(int id, String foodName, int newQuantity) {
        Optional<CartItem> itemToUpdate = cartData.stream()
                .filter(cartItem ->
                        cartItem.getUser().getUserId().equals(id) && cartItem.getFoodItem().getName().equalsIgnoreCase(foodName))
                .findFirst();

        if (itemToUpdate.isPresent()) {

            itemToUpdate.get().setQuantity(newQuantity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteFromCart(int id, String name) {
        return cartData.removeIf(cartItem -> cartItem.getUser().getUserId() == id &&
                cartItem.getFoodItem().getName().equalsIgnoreCase(name));
    }

}
