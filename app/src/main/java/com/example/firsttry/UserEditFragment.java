package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.UserReportsAdapter;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserEditFragment
        extends ValidatedFragment
        implements UserReportsAdapter.OnUserActionListener
{
    private User _currentUser;

    private TextView _username;
    private TextView _email;

    private RecyclerView recyclerView;
    private UserReportsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_user_edit, container, false);
        recyclerView = currentView.findViewById(R.id.reportsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        checkAuthenticated();
        fillUserData();
        return currentView;
    }

    private void fillUserData()
    {
        String userId = getArguments().getString("userId");
        _currentUser = new User();
        _currentUser.getById(userId)
                .thenAccept(user -> {
                    _currentUser = user;
                    _username = currentView.findViewById(R.id.username_view);
                    _email = currentView.findViewById(R.id.email_view);
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
        _currentUser.reports().thenAccept(reports -> {
            if (reports.isEmpty())
            {
                TextView reportsLabel = currentView.findViewById(R.id.reports_label);
                reportsLabel.setText(R.string.nessuna_segnalazione_ricevuta);
                return;
            }

            adapter = new UserReportsAdapter(reports, this);
            recyclerView.setAdapter(adapter);
        })
        .exceptionally(ex -> {
            System.err.println(ex.getMessage());
            return null;
        });
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

        Spinner spinner = currentView.findViewById(R.id.roles_view_select);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);
    }

    private void setSaveChangesBtn()
    {
        Button _saveChangesBtn = currentView.findViewById(R.id.btn_save_profile);
        _saveChangesBtn.setOnClickListener(view -> saveChanges());
    }

    private void setSuspendUserBtn()
    {
        Button _suspendUserBtn = currentView.findViewById(R.id.btn_suspend_profile);

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
        Button _banditUserBtn = currentView.findViewById(R.id.btn_bandit_profile);
        _banditUserBtn.setOnClickListener(view -> banditUser());
    }

    private void saveChanges()
    {
        String role = ((Spinner) currentView.findViewById(R.id.roles_view_select))
                .getSelectedItem()
                .toString();

        UserRole userRole = UserRole.valueOf(role);

        _currentUser.setRole(userRole);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(requireActivity(), "Utente salvato con successo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragment(requireActivity(), new UsersSettingsFragment());
                });
    }

    private void suspendUser()
    {
        _currentUser.setStatus(UserStatus.SUSPENDED);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(requireActivity(), "Utente sospeso dal circolo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragment(requireActivity(), new UsersSettingsFragment());
                });
    }

    private void readmitUser()
    {
        _currentUser.setStatus(UserStatus.ACTIVE);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(requireActivity(), "Utente riammesso al circolo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragment(requireActivity(), new UsersSettingsFragment());
                });
    }

    private void banditUser()
    {
        _currentUser.setStatus(UserStatus.BANDITED);
        _currentUser.save()
                .thenAccept(user -> {
                    Toast.makeText(requireActivity(), "Utente bandito definitivamente dal circolo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragment(requireActivity(), new UsersSettingsFragment());
                });
    }

    @Override
    public void onDelete(Report report)
    {
        report.softDelete()
                .thenAccept(deletedReport -> {
                    Toast.makeText(requireActivity(), "Segnalazione cancellata!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragmentWithArguments(
                            requireActivity(),
                            new UserEditFragment(),
                            HashMapExtensions.from("userId", _currentUser.getId()));
                });
    }
}
