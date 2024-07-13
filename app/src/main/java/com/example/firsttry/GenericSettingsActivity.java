package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.firsttry.extensions.ValidatedCompatActivity;

public class GenericSettingsActivity extends ValidatedCompatActivity
{
    private Button _usersSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_settings);
        setUsersSettingsButton();
    }

    private void setUsersSettingsButton()
    {
        _usersSettingsButton = findViewById(R.id.buttonUsersSettings);
        _usersSettingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UsersSettingsActivity.class));
            finish();
        });
    }
}
