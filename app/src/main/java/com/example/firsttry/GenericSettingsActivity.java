package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.firsttry.extensions.ValidatedCompatActivity;

public class GenericSettingsActivity extends ValidatedCompatActivity
{
    private Button _usersSettingsButton;
    private Button _courtsSettingsButton;
    private Button _teacherSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_settings);
        setBackButton(MainActivity.class);
        checkAuthenticated();
        setUsersSettingsButton();
        setCourtsSettingsButton();
        setTeacherSettingsButton();
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

    private void setTeacherSettingsButton()
    {
        _teacherSettingsButton = findViewById(R.id.buttonTeacherSettings);
        _teacherSettingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherManagerActivity.class));
            finish();
        });
    }
}
