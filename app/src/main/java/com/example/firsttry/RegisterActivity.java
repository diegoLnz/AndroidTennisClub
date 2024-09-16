package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firsttry.extensions.ValidatedActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;

import java.util.concurrent.CompletableFuture;

public class RegisterActivity extends ValidatedActivity
{
    private ValidatedEditText EditTextUsername;
    private ValidatedEditText EditTextEmail;
    private ValidatedEditText EditTextBio;
    private ValidatedEditText EditTextPassword;
    private Button RegisterButton;
    private TextView LoginTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setFields();
        setSubmitListener();
        setLoginLinkListener();
    }

    private void setFields()
    {
        EditTextUsername = findViewById(R.id.editTextUsernameReg);
        EditTextEmail = findViewById(R.id.editTextEmailReg);
        EditTextBio = findViewById(R.id.editTextBioReg);
        EditTextPassword = findViewById(R.id.editTextPasswordReg);
        EditTextUsername.setRequired(true);
        EditTextEmail.setRequired(true);
        EditTextPassword.setRequired(true);
        RegisterButton = findViewById(R.id.buttonRegister);
        LoginTextView = findViewById(R.id.textViewLogin);
    }

    private void setLoginLinkListener()
    {
        LoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setSubmitListener()
    {
        RegisterButton.setOnClickListener(v -> {
            if (!validateFields())
            {
                return;
            }

            String email = EditTextEmail
                    .getText()
                    .toString()
                    .trim();

            String password = EditTextPassword
                    .getText()
                    .toString()
                    .trim();

            String username = EditTextUsername
                    .getText()
                    .toString()
                    .trim();

            String bio = EditTextBio
                    .getText()
                    .toString()
                    .trim();

            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setBio(bio);

            registerUser(user, password);
        });
    }

    private void registerUser(User user, String password)
    {
        checkExistingUser(user).thenAccept(alreadyExists ->
        {
            if (alreadyExists)
            {
                Toast.makeText(this, "Registrazione fallita: " + "Utente già registrato", Toast.LENGTH_SHORT).show();
                return;
            }
            AccountManager.doRegister(user, password).thenApply(result -> result
                    .match(
                            success -> {
                                AccountManager.checkFcmToken();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                                return true;
                            },
                            failure -> {
                                Toast.makeText(this, "Registrazione fallita: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                    ));
        });
    }

    private CompletableFuture<Boolean> checkExistingUser(User user)
    {
        return AccountManager.checkExistingUser(user);
    }
}
