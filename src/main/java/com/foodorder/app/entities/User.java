package com.foodorder.app.entities;

import com.foodorder.app.enums.UserRole;
import com.foodorder.app.utility.Formattable;
import lombok.*;

import java.util.List;
@Builder
@Data
public class
User implements Formattable {
    private Integer userId;
    private String name;
    private String email;
    private String password;
    private String address;
    private UserRole role;
    private boolean isLoggedIn;

    @Override
    public List<String> getColumns() {
        return List.of("id", "Name", "Email", "role", "Logged in");
    }

    @Override
    public List<String> getValues() {
        return List.of(String.valueOf(this.userId), this.name, this.email, String.valueOf(this.role), String.valueOf(this.isLoggedIn));
    }

    @Override
    public String getTitle() {
        return "USERS";
    }
}