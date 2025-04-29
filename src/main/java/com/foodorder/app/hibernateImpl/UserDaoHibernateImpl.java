package com.foodorder.app.hibernateImpl;

import com.foodorder.app.dao.UserDao;
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
            user.setRole(UserRole.ADMIN);
            tx.commit();
            return true;

        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> saveUser(User user) {
        try {
            tx.begin();
            manager.persist(user);
            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
        return Optional.of(user);
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
        }
    }

    @Override
    public List<User> fetchAllUsers() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> cq = builder.createQuery(User.class);
        Root<User> from = cq.from(User.class);
        cq.select(from);

        TypedQuery<User> query = manager.createQuery(cq);

        return query.getResultList();
    }

    @Override
    public Optional<User> updateUser(User user) {
        try {
            tx.begin();
            User udpatedUser = manager.merge(user);
            tx.commit();
            return Optional.of(udpatedUser);

        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
