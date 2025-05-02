package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.FoodDao;

import com.foodorder.app.entities.FoodItem;

import com.foodorder.app.enums.FoodCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public  class FoodDaoHibernateImpl implements FoodDao {
    private final EntityManager manager;
    private final EntityTransaction tx;


    @Override
    public boolean saveFood(FoodItem foodItem) throws RuntimeException {
        try {
            tx.begin();
            manager.persist(foodItem);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            log.error("Failed to persist FoodItem: {}", foodItem, e);
            throw new RuntimeException("Error saving food item", e);
        }
    }


    @Override
    public Optional<FoodItem> getFoodById(int id) {
        try {
            tx.begin();
            FoodItem foodItem = manager.find(FoodItem.class, id);
            tx.commit();
            return Optional.ofNullable(foodItem);
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            log.error("Error fetching FoodItem with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<FoodItem> getAllFood() throws RuntimeException {
        try {
            return manager.createQuery("FROM FoodItem", FoodItem.class).getResultList();
        } catch (PersistenceException e) {
            log.error("Failed to fetch food items", e);
            throw new RuntimeException();
        }
    }

    @Override
    public List<FoodItem> getFoodByCategory(FoodCategory category) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<FoodItem> cq = cb.createQuery(FoodItem.class);
        Root<FoodItem> root = cq.from(FoodItem.class);
        cq.select(root).where(cb.equal(root.get("category"), category));
        return manager.createQuery(cq).getResultList();
    }


    @Override
    public boolean updateFood(FoodItem foodItem) {
        try {
            tx.begin();
            manager.merge(foodItem);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to update food item", e);
        }
    }

//TODO : delete item should recevice only id and

    @Override
    public boolean deleteFood(FoodItem foodItem) {
        try {
            tx.begin();
            FoodItem existing = manager.find(FoodItem.class, foodItem.getId());
            if (existing != null) {
                manager.remove(existing);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to delete food item", e);
        }
    }
}