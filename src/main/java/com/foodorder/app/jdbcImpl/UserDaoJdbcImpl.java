package com.foodorder.app.jdbcImpl;

import com.foodorder.app.sqlqueries.UserSqlQueries;
import com.foodorder.app.dao.UserDao;
import com.foodorder.app.entities.User;
import com.foodorder.app.enums.UserRole;
import com.foodorder.app.utility.ConnectionUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoJdbcImpl implements UserDao {
    private static final UserDaoJdbcImpl userDaoJdbc = new UserDaoJdbcImpl();
    private Connection con;

    private UserDaoJdbcImpl() {
        initSqlDataConnection();
    }

    public static UserDaoJdbcImpl getUserDaoJdbcImpl() {
        return userDaoJdbc;
    }

    void initSqlDataConnection() {
        this.con = ConnectionUtility.getConnection();
    }

    @Override
    public boolean grantAccessAsAdmin(String name) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UserSqlQueries.GRANT_ADMIN)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UserSqlQueries.INSERT_USER)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getRole().name());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UserSqlQueries.SELECT_USER_BY_EMAIL)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .userId(rs.getInt("user_id"))
                            .name(rs.getString("name"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .address(rs.getString("address"))
                            .role(UserRole.valueOf(rs.getString("role_type")))
                            .isLoggedIn(rs.getBoolean("is_logged_in"))
                            .build();

                    return Optional.ofNullable(user);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> fetchAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(UserSqlQueries.SELECT_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = User.builder().userId(rs.getInt("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .address(rs.getString("address"))
                        .role(UserRole.valueOf(rs.getString("role_type")))
                        .isLoggedIn(rs.getBoolean("is_logged_in"))
                        .build();
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        Optional<User> userByEmail = findUserByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            try (PreparedStatement ps = con.prepareStatement(UserSqlQueries.UPDATE_USER)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getAddress());
                ps.setString(5, user.getRole().toString());
                ps.setBoolean(6, user.isLoggedIn());
                ps.setInt(7, user.getUserId());

                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }
}