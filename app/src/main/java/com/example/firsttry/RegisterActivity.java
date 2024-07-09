package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;

public class RegisterActivity extends ValidatedCompatActivity
{
    private ValidatedEditText EditTextEmail;
    private ValidatedEditText EditTextPassword;
    private Button RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setFields();
        setSubmitListener();
    }

    private void setFields()
    {
        EditTextEmail = findViewById(R.id.editTextEmailReg);
        EditTextPassword = findViewById(R.id.editTextPasswordReg);
        RegisterButton = findViewById(R.id.buttonRegister);
    }

    private void setSubmitListener()
    {
        RegisterButton.setOnClickListener(v -> {
            String email = EditTextEmail
                    .getText()
                    .toString()
                    .trim();
            String password = EditTextPassword
                    .getText()
                    .toString()
                    .trim();

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);

            registerUser(user);
        });
    }

    private void registerUser(User user)
    {
        AccountManager.doRegister(user).thenApply(result -> result
                .match(
                        success -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                            return true;
                        },
                        failure -> {
                            Toast.makeText(this, "Registrazione fallita: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                ));
    }
}
