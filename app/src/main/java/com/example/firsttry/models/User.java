package com.example.firsttry.models;

import com.example.firsttry.businesslogic.ReportsBl;
import com.example.firsttry.businesslogic.ReviewsBl;
import com.example.firsttry.enums.UserRole;
import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class User extends Model
{
    @Override
    public String tableName() { return "users"; }

    private String Username;
    private String Email;
    private String Bio;
    private String Password;
    private UserRole Role;
    private UserStatus Status = UserStatus.ACTIVE;
    private Double Reputation;

    public User() { }

    public User(String username, String email)
    {
        Username = username;
        Email = email;
        Status = UserStatus.ACTIVE;
    }

    public String getUsername() { return Username; }

    public void setUsername(String username) { Username = username; }

    public String getEmail() { return Email; }

    public void setEmail(String email) { Email = email; }

    public String getBio() { return Bio; }

    public void setBio(String bio) { Bio = bio; }

    public String getPassword() { return Password; }

    public void setPassword(String password) { Password = password; }

    public UserRole getRole() { return Role; }

    public void setRole(UserRole role) { Role = role; }

    public UserStatus getStatus() { return Status; }

    public void setStatus(UserStatus status) { Status = status; }

    public Double getReputation() { return Reputation; }

    public void setReputation(Double reputation) { Reputation = reputation; }

    public CompletableFuture<Array<Report>> reports()
    {
        return ReportsBl.getReportsByUserId(this.getId());
    }

    public CompletableFuture<Array<Review>> reviews()
    {
        return ReviewsBl.getReviewsByUserId(this.getId());
    }

    public static CompletableFuture<User> fromFirebaseUser(@NotNull FirebaseUser user)
    {
        return new User().getById(user.getUid())
                .thenApply(result -> result);
    }

    public CompletableFuture<User> updateReputation()
    {
        return reviews().thenCompose(reviews -> {
            if (reviews.isEmpty()) {
                Reputation = 0.0;
                return save();
            }
            Double sum = 0.0;
            for (Review review : reviews.getList()) {
                sum += review.getRating();
            }
            Reputation = sum / reviews.size();
            return save();
        });
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

    @Override
    public CompletableFuture<User> softDelete()
    {
        setIsDeleted(true);
        return save();
    }
}
