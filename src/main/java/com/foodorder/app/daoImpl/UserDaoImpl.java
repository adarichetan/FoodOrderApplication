package com.foodorder.app.daoImpl;


import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.UserRole;

import java.util.*;

public class UserDaoImpl implements UserDao {
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private static final UserDaoImpl userDao = new UserDaoImpl();
    private int idCounter = 10;

    private void initializeUsers() {
//        User user3 = User.builder().userId(5).name("omi").email("omi@gmail.com").password("Omi@12345").role(UserRole.CUSTOMER).build();
        User user1 = User.builder().userId(2).name("Chetan").email("chetan@gmail.com").password("Chetan@123").role(UserRole.CUSTOMER).build();
        User adminUser = User.builder().userId(4).name("Admin").email("admin@gmail.com").password("Admin@123").role(UserRole.ADMIN).build();
        users.add(user1);
//        users.add(user3);
        users.add(adminUser);
    }

    private UserDaoImpl() {
        initializeUsers();
    }

    public static UserDaoImpl getUserDaoImpl() {
        return userDao;
    }

    @Override
    public boolean grantAccessAsAdmin(String name) {
        Optional<User> first = users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst();

        if (first.isPresent()) {
            first.get().setRole(UserRole.ADMIN);
            return true;
        }
        return false;
    }

    @Override
    public boolean addUser(User user) {
        user.setUserId(getIdGenerator());
        return users.add(user);
    }

    private int getIdGenerator() {
        return idCounter++;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<User> fetchAllUsers() {
        return List.copyOf(users);
    }

    @Override
    public Optional<User> updateUser(User user) {
        Optional<User> first = users.stream().filter(u -> Objects.equals(u.getUserId(), user.getUserId())).findFirst();
        if (first.isPresent()) {
            User existingUser = first.get();
            existingUser.setName(user.getName());
            existingUser.setRole(user.getRole());
            existingUser.setEmail(user.getEmail());
            existingUser.setName(user.getName());
            existingUser.setPassword(user.getPassword());
            existingUser.setLoggedIn(user.isLoggedIn());

            return Optional.of(user);
        }
        return Optional.empty();
    }
}
