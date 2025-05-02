package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.UserRole;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDaoHibernateImpl implements UserDao {
    private final EntityManager manager;
    private final EntityTransaction tx;

    @Override
    public boolean grantAccessAsAdmin(int userId) {

        try {
            tx.begin();
            User user = manager.find(User.class, userId);
            if (user == null) {
                tx.rollback();
                return false;
            }
            user.setRole(UserRole.ADMIN);
            manager.merge(user);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to grant admin access to user with ID: " + userId, e);
        }
    }

    @Override
    public Optional<User> saveUser(User user) {
        try {
            tx.begin();
            manager.persist(user);
            tx.commit();
            return Optional.of(user);
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = manager.createQuery(jpql, User.class);
        query.setParameter("email", email);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
    }

    @Override
    public List<User> fetchAllUsers() {
        try {
            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<User> cq = builder.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            cq.select(root);
            return manager.createQuery(cq).getResultList();
        } catch (PersistenceException e) {
            throw new RuntimeException("Failed to fetch all users..", e);
        }
    }

    @Override
    public Optional<User> updateUser(User user) {
        try {
            tx.begin();
            User updatedUser = manager.merge(user);
            tx.commit();
            return Optional.of(updatedUser);
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to update user..", e);
        }
    }
}
