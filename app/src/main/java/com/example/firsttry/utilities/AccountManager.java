package com.example.firsttry.utilities;

import android.util.Log;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.CompletableFuture;

public class AccountManager
{
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static CompletableFuture<Result<FirebaseUser, Exception>> doLogin(String email, String password)
    {
        CompletableFuture<Result<FirebaseUser, Exception>> future = new CompletableFuture<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> future.complete(Result.success(mAuth.getCurrentUser())))
                .addOnFailureListener(e -> future.complete(Result.failure(e)));
        return future;
    }

    public static CompletableFuture<Result<User, Exception>> doRegister(User user, String password)
    {
        CompletableFuture<Result<User, Exception>> future = new CompletableFuture<>();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(authResult -> {
                    if (authResult.isSuccessful())
                    {
                        user.setId(authResult.getResult().getUser().getUid());
                        user.setRole(UserRole.Common);
                        user.save().thenApply(account -> future.complete(Result.success(account)));
                    }
                    else
                    {
                        future.complete(Result.failure(authResult.getException()));
                    }
                })
                .addOnFailureListener(e -> future.complete(Result.failure(e)));
        return future;
    }

    public static void checkFcmToken()
    {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("IntroActivity", "getToken failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("", "Token: " + token);
                    saveFcmToken(token);
                });
    }

    public static void saveFcmToken(String token)
    {
        getCurrentAccount().thenAccept(user -> {
            user.setFcmToken(token);
            user.save();
        });
    }

    public static CompletableFuture<String> getFcmToken(String userId)
    {
        return Repository.getById(userId, User.class).thenApply(User::getFcmToken);
    }

    public static void doLogout() { mAuth.signOut(); }

    public static boolean isLogged() { return mAuth.getCurrentUser() != null; }
    public static CompletableFuture<User> getCurrentAccount()
    {
        if (mAuth.getCurrentUser() == null)
            return CompletableFuture.completedFuture(null);

        return User.fromFirebaseUser(mAuth.getCurrentUser());
    }
}
