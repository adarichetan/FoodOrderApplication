package com.foodorder.app.sqlqueries;

public class UserSqlQueries {
    private UserSqlQueries() {
    }

    public static final String INSERT_USER = "INSERT INTO users (name,password,email,address,role_type) VALUES (?,?,?,?,?)";

    public static final String SELECT_USER_BY_EMAIL = "SELECT user_id, name, email, password, address, role_type, is_logged_in FROM users WHERE email = ?";

    public static final String SELECT_ALL_USERS = "SELECT * FROM users";

    public static final String GRANT_ADMIN = "UPDATE users SET role_type = 'ADMIN' WHERE user_id = ?";

    public static final String UPDATE_USER = "UPDATE users SET name = ?, password = ?, email = ?, address = ?, role_type = ?, is_logged_in = ? WHERE user_id = ?";

}
