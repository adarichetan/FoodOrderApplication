package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.RestaurantDao;
import com.foodorder.app.entities.Restaurant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantDaoHibernateImpl implements RestaurantDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    public boolean addRestaurant(Restaurant restaurant) {
        try {
            tx.begin();
            manager.persist(restaurant);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to add restaurant", e);
        }
    }

    @Override
    public Optional<Restaurant> getRestaurantById(int restaurantId) {
        try {
            tx.begin();
            Restaurant restaurant = manager.find(Restaurant.class, restaurantId);
            tx.commit();
            return Optional.ofNullable(restaurant);
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to fetch restaurant with ID: " + restaurantId, e);
        }
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        try {
            String query = "SELECT r FROM Restaurant r";
            TypedQuery<Restaurant> typedQuery = manager.createQuery(query, Restaurant.class);
            return typedQuery.getResultList();
        } catch (PersistenceException e) {
            throw new RuntimeException("Failed to fetch all restaurants", e);
        }
    }
}
