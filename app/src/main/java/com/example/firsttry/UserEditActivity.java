package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;

import java.util.ArrayList;
import java.util.List;

public class UserEditActivity extends ValidatedCompatActivity
{
    private User _currentUser;

    private TextView _username;
    private TextView _email;
    private Button _saveChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        fillUserData();
    }

    private void fillUserData()
    {
        String userId = getIntent().getStringExtra("userId");
        _currentUser = new User();
        _currentUser.getById(userId)
                .thenAccept(user -> {
                    _currentUser = user;
                    _username = findViewById(R.id.username_view);
                    _email = findViewById(R.id.email_view);
                    _username.setText(user.getUsername());
                    _email.setText(user.getEmail());
                    setUserRolesSpinner();
                    setSaveChangesBtn();
                });
    }

    private void setUserRolesSpinner()
    {
        List<String> roles = new ArrayList<>();
        for (UserRoles role : UserRoles.values())
        {
            if (role.equals(UserRoles.Admin))
                continue;

            roles.add(role.name());
        }
        int pos = roles.indexOf(_currentUser.getRole().name());

        Spinner spinner = findViewById(R.id.roles_view_select);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);
    }

    private void setSaveChangesBtn()
    {
        _saveChangesBtn = findViewById(R.id.btn_save_profile);
        _saveChangesBtn.setOnClickListener(view -> saveChanges());
    }

    private void saveChanges()
    {
        String role = ((Spinner) findViewById(R.id.roles_view_select))
                .getSelectedItem()
                .toString();

        UserRoles userRole = UserRoles.valueOf(role);

        _currentUser.setRole(userRole);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(UserEditActivity.this, "Utente salvato con successo!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, UsersSettingsActivity.class));
                    finish();
                });
    }
}
