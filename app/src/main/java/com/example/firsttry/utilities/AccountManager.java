package com.example.firsttry.utilities;

import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

    public static CompletableFuture<Result<User, Exception>> doRegister(User user)
    {
        CompletableFuture<Result<User, Exception>> future = new CompletableFuture<>();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(authResult -> {
                    if (authResult.isSuccessful())
                    {
                        user.setId(authResult.getResult().getUser().getUid());
                        user.setPassword(Sha256Encryptor.encrypt(user.getPassword()));
                        user.setRole(UserRoles.Common);
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

    public static void doLogout() { mAuth.signOut(); }

    public static boolean isLogged() { return mAuth.getCurrentUser() != null; }
    public static CompletableFuture<User> getCurrentAccount()
    {
        if (mAuth.getCurrentUser() == null)
            return null;

        return User.fromFirebaseUser(mAuth.getCurrentUser());
    }
}
