package com.foodorder.app.serviceImpl;

import com.foodorder.app.dao.CartDao;
import com.foodorder.app.entities.CartItem;
import com.foodorder.app.entities.FoodItem;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.ResponseStatus;
import com.foodorder.app.service.CartService;
import com.foodorder.app.utility.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class CartServiceImpl implements CartService {
    private final CartDao cartDao;

    public CartServiceImpl(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Response addToCart(User user, FoodItem foodItem, int quantity) {
        try {
            if (cartDao.addToCart(user, foodItem, quantity))
                return new Response(ResponseStatus.SUCCESS, ">> Successfully added to the cart ✅ ");

        } catch (Exception e) {
            log.error("Error from cart service method while adding item to cart", e);
            return new Response(ResponseStatus.FAILURE, "Error from cartService method while adding item to cart.. please contact admin.");
        }
        return new Response(ResponseStatus.FAILURE, "Unable to add to cart.");
    }

    @Override
    public Response getCartItems(User user) {
        try {
            List<CartItem> cartItems = cartDao.getCartItems(user);
            if (!cartItems.isEmpty())
                return new Response(cartItems, ResponseStatus.SUCCESS, "Successfully fetched the data");

        } catch (Exception e) {
            log.error("Error from cartService method.. please contact admin.", e);
        }
        return new Response(ResponseStatus.FAILURE, "Your cart is empty.");
    }

    @Override
    public Response clearCart(User userId) {
        try {
            if (cartDao.clearCart(userId)) return new Response(ResponseStatus.SUCCESS, "Cart cleared successfully.");
        } catch (Exception e) {
            log.error("Error from cartService method", e);
        }
        return new Response(ResponseStatus.FAILURE, "Cart is already empty.");
    }

    @Override
    public Response updateQuantityFromCart(int id, String foodName, int newQuantity) {
        try {
            if (cartDao.updateQuantityFromCart(id,foodName,newQuantity)) {
                return new Response(ResponseStatus.SUCCESS, "Quantity updated.");
            }
        } catch (SQLException e) {
            log.error("Error from update quantity method", e);
        }
        return new Response(ResponseStatus.FAILURE, "Not able to update the quantity.");
    }

    @Override
    public Response deleteFromCart(int id,String name) {
        try {
            if(cartDao.deleteFromCart(id, name)){
                return  new Response(ResponseStatus.SUCCESS, "Food item removed from cart.");
            }
        } catch (SQLException e) {
            log.error("Error from delete quantity method", e);
        }
        return new Response(ResponseStatus.FAILURE, "Not able to delete the food item.");
    }

}
