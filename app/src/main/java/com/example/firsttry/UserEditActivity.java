package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.adapters.UserAdapter;
import com.example.firsttry.extensions.adapters.UserReportsAdapter;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.Array;

import java.util.ArrayList;
import java.util.List;

public class UserEditActivity
        extends ValidatedCompatActivity
        implements UserReportsAdapter.OnUserActionListener
{
    private User _currentUser;

    private TextView _username;
    private TextView _email;

    private RecyclerView recyclerView;
    private UserReportsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        checkAuthenticated();
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
                    setSuspendUserBtn();
                    setBanditUserBtn();
                    setReportsAdapter();
                });
    }

    private void setReportsAdapter()
    {
        Array<Report> reports = new Array<>(_currentUser.getReports());

        if (reports.isEmpty())
        {
            TextView reportsLabel = findViewById(R.id.reports_label);
            reportsLabel.setText(R.string.nessuna_segnalazione_ricevuta);
            return;
        }

        adapter = new UserReportsAdapter(reports, this);
        recyclerView.setAdapter(adapter);
    }

    private void setUserRolesSpinner()
    {
        List<String> roles = new ArrayList<>();
        for (UserRole role : UserRole.values())
        {
            if (role.equals(UserRole.Admin))
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
        Button _saveChangesBtn = findViewById(R.id.btn_save_profile);
        _saveChangesBtn.setOnClickListener(view -> saveChanges());
    }

    private void setSuspendUserBtn()
    {
        Button _suspendUserBtn = findViewById(R.id.btn_suspend_profile);

        if (_currentUser.getStatus().equals(UserStatus.SUSPENDED))
        {
            _suspendUserBtn.setText(R.string.riammetti_al_circolo);
            _suspendUserBtn.setOnClickListener(view -> readmitUser());
        }
        else
        {
            _suspendUserBtn.setText(R.string.sospendi);
            _suspendUserBtn.setOnClickListener(view -> suspendUser());
        }
    }

    private void setBanditUserBtn()
    {
        Button _banditUserBtn = findViewById(R.id.btn_bandit_profile);
        _banditUserBtn.setOnClickListener(view -> banditUser());
    }

    private void saveChanges()
    {
        String role = ((Spinner) findViewById(R.id.roles_view_select))
                .getSelectedItem()
                .toString();

        UserRole userRole = UserRole.valueOf(role);

        _currentUser.setRole(userRole);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(UserEditActivity.this, "Utente salvato con successo!", Toast.LENGTH_SHORT).show();
                    ActivityHandler.LinkTo(this, UsersSettingsActivity.class);
                });
    }

    private void suspendUser()
    {
        _currentUser.setStatus(UserStatus.SUSPENDED);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(UserEditActivity.this, "Utente sospeso dal circolo!", Toast.LENGTH_SHORT).show();
                    ActivityHandler.LinkTo(this, UsersSettingsActivity.class);
                });
    }

    private void readmitUser()
    {
        _currentUser.setStatus(UserStatus.ACTIVE);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(UserEditActivity.this, "Utente riammesso al circolo!", Toast.LENGTH_SHORT).show();
                    ActivityHandler.LinkTo(this, UsersSettingsActivity.class);
                });
    }

    private void banditUser()
    {
        _currentUser.setStatus(UserStatus.BANDITED);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(UserEditActivity.this, "Utente bandito definitivamente dal circolo!", Toast.LENGTH_SHORT).show();
                    ActivityHandler.LinkTo(this, UsersSettingsActivity.class);
                });
    }

    @Override
    public void onDelete(Report report)
    {

    }
}
