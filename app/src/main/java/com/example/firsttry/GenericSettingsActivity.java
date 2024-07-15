package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.firsttry.extensions.ValidatedCompatActivity;

public class GenericSettingsActivity extends ValidatedCompatActivity
{
    private Button _usersSettingsButton;
    private Button _courtsSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_settings);
        checkAuthenticated();
        setUsersSettingsButton();
        setCourtsSettingsButton();
    }

    private void setUsersSettingsButton()
    {
        _usersSettingsButton = findViewById(R.id.buttonUsersSettings);
        _usersSettingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UsersSettingsActivity.class));
            finish();
        });
    }

    private void setCourtsSettingsButton()
    {
        _courtsSettingsButton = findViewById(R.id.buttonCourtsSettings);
        _courtsSettingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CourtsSettingsActivity.class));
            finish();
        });
    }
}
