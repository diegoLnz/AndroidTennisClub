package com.example.firsttry.models;

import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.utilities.DatabaseHandler;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class User extends Model
{
    @Override
    public String tableName() { return "users"; }

    private String Username;
    private String Email;
    private String Bio;
    private String Password;
    private UserRoles Role;

    public User() { }

    public User(String username, String email)
    {
        Username = username;
        Email = email;
    }

    public String getUsername() { return Username; }

    public void setUsername(String username) { Username = username; }

    public String getEmail() { return Email; }

    public void setEmail(String email) { Email = email; }

    public String getBio() { return Bio; }

    public void setBio(String bio) { Bio = bio; }

    public String getPassword() { return Password; }

    public void setPassword(String password) { Password = password; }

    public UserRoles getRole() { return Role; }

    public void setRole(UserRoles role) { Role = role; }

    public static CompletableFuture<User> fromFirebaseUser(FirebaseUser user) {
        if (user == null) {
            return null;
        }

        User returnUser = new User();
        return returnUser.getById(user.getUid())
                .thenApply(result -> result);
    }

    @Override
    public CompletableFuture<User> save()
    {
        return DatabaseHandler.saveOrUpdate(this).thenApply(result -> result
                .match(
                        success -> success,
                        failure -> null
                ));
    }

    @Override
    public CompletableFuture<User> getById(String id)
    {
        return DatabaseHandler.getById(id, tableName(), User.class)
                .thenApply(result -> result);
    }
}
