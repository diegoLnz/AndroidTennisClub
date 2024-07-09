package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.utilities.AccountManager;

public class LoginActivity extends ValidatedCompatActivity
{
    private ValidatedEditText EditTextEmail;
    private ValidatedEditText EditTextPassword;
    private Button LoginButton;
    private TextView RegisterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViews();
        setSubmitListener();
        setRegisterLinkListener();
    }

    private void setViews()
    {
        EditTextEmail = findViewById(R.id.editTextEmail);
        EditTextEmail.setRequired(true);
        EditTextPassword = findViewById(R.id.editTextPassword);
        EditTextPassword.setRequired(true);
        LoginButton = findViewById(R.id.buttonLogin);
        RegisterTextView = findViewById(R.id.textViewRegister);
    }

    private void setSubmitListener()
    {
        LoginButton.setOnClickListener(v -> {
            validateFields();
            if (!getIsValid())
                return;

            String email = EditTextEmail.getText()
                    .toString()
                    .trim();
            String password = EditTextPassword.getText()
                    .toString()
                    .trim();

            logUser(email, password);
        });
    }

    private void setRegisterLinkListener()
    {
        RegisterTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void logUser(String email, String password)
    {
        AccountManager.doLogin(email, password).thenApply(result -> result
                .match(
                        success -> {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            return true;
                        },
                        failure -> {
                            Toast.makeText(LoginActivity.this, "Autenticazione fallita: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                )
        );
    }
}
