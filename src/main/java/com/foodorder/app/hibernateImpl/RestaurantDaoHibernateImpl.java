package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.RestaurantDao;
import com.foodorder.app.entities.Restaurant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantDaoHibernateImpl implements RestaurantDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    @Override
    public boolean addRestaurant(Restaurant restaurant) {
        try {
            tx.begin();
            manager.persist(restaurant);
            tx.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Restaurant> getRestaurantById(int restaurantId) {
        try {
            tx.begin();
            Restaurant restaurant = manager.find(Restaurant.class, restaurantId);

            tx.commit();
            return Optional.of(restaurant);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        String query = "SELECT r FROM Restaurant r";
        TypedQuery<Restaurant> query1 = manager.createQuery(query, Restaurant.class);
        return query1.getResultList();
    }
}
