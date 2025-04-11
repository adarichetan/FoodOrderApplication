package com.foodorder.app.sqlqueries;

public class UserSqlQueries {
    private UserSqlQueries() {
    }

    public static final String INSERT_USER = "INSERT INTO users (name,password,email,address,role_type) VALUES (?,?,?,?,?)";

    public static final String SELECT_USER_BY_EMAIL = "SELECT user_id, name, email, password, address, role_type, is_logged_in FROM users WHERE email = ?";

    public static final String SELECT_ALL_USERS = "SELECT * FROM users";

    public static final String GRANT_ADMIN = "UPDATE users SET role_type = 'ADMIN' WHERE name = ?";

    public static final String UPDATE_LOGIN_STATUS_BY_EMAIL = "UPDATE users SET is_logged_in = ? WHERE email = ?";

    public static final String LOGOUT_BY_EMAIL = "UPDATE users SET is_logged_in = ? WHERE email = ?";
}
