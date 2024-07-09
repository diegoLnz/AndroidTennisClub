package com.example.firsttry.models;

import com.example.firsttry.enums.UserRoles;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class User {
    private int Id;
    private String Username;
    private String Email;
    private String Password;
    private UserRoles Role;

    public User() { }

    public int getId() { return Id; }

    public void setId(int id) { Id = id; }

    public String getUsername() { return Username; }

    public void setUsername(String username) { Username = username; }

    public String getEmail() { return Email; }

    public void setEmail(String email) { Email = email; }

    public String getPassword() { return Password; }

    public void setPassword(String password) { Password = password; }

    public UserRoles getRole() { return Role; }

    public void setRole(UserRoles role) { Role = role; }

    public static User fromFirebaseUser(FirebaseUser user) {
        if (user == null) {
            return null;
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getDisplayName());
        return newUser;
    }
}
