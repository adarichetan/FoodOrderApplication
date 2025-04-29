package com.foodorder.app.entities;

import com.foodorder.app.enums.UserRole;
import com.foodorder.app.utility.Formattable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Builder
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Formattable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;
    private String email;
    private String password;
    private String address;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Boolean isLoggedIn;

    @Override
    public List<String> getColumns() {
        return List.of("USER ID", "NAME", "EMAIL", "ROLE-TYPE", "LOGGED-IN-STATUS");
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