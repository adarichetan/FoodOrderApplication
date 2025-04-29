package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.FoodDao;

import com.foodorder.app.entities.FoodItem;

import com.foodorder.app.enums.FoodCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FoodDaoHibernateImpl implements FoodDao {
    private final EntityManager manager;
    private final EntityTransaction tx;


    @Override
    public boolean saveFood(FoodItem foodItem) {
        try {
            tx.begin();
            manager.persist(foodItem);
            tx.commit();
            return true;

        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FoodItem> getFoodById(int id) {
        try {
            tx.begin();
            FoodItem foodItem = manager.find(FoodItem.class, id);
            tx.commit();
            return Optional.of(foodItem);
        } catch (Exception e) {
            tx.rollback();
        }
        return Optional.empty();
    }

    @Override
    public List<FoodItem> getAllFood() {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<FoodItem> cq = criteriaBuilder.createQuery(FoodItem.class);
        Root<FoodItem> from = cq.from(FoodItem.class);
        cq.select(from);
        TypedQuery<FoodItem> query = manager.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public List<FoodItem> getFoodByCategory(FoodCategory category) {
        String query = "SELECT f FROM FoodItem f WHERE f.category = :category";
        TypedQuery<FoodItem> query1 = manager.createQuery(query, FoodItem.class);
        query1.setParameter("category", category);

        return query1.getResultList();
    }

    @Override
    public boolean updateFood(FoodItem foodItem) {
        try {
            tx.begin();
            manager.merge(foodItem);
            tx.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteFood(FoodItem foodItem) {
        try {
            tx.begin();
            FoodItem foodItem1 = manager.find(FoodItem.class, foodItem.getId());
            manager.remove(foodItem1);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
