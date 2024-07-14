package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;

import java.util.Objects;

public class ProfileFragment extends Fragment
{
    private ValidatedEditText _username;
    private ValidatedEditText _bio;
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_profile, container, false);
        setSaveBtnListener();
        setLogoutBtnListener();
        setFields();
        return _currentView;
    }

    private void setFields()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenAccept(user -> {
                    _username = _currentView.findViewById(R.id.edit_username);
                    _username.setText(user.getUsername());
                    _bio = _currentView.findViewById(R.id.edit_bio);
                    _bio.setText(user.getBio());
                });
    }

    private void setSaveBtnListener()
    {
        Button saveBtn = _currentView.findViewById(R.id.btn_save_profile);
        saveBtn.setOnClickListener(v -> saveUser());
    }

    private void setLogoutBtnListener()
    {
        Button logoutBtn = _currentView.findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(v -> doLogout());
    }

    private void doLogout()
    {
        AccountManager.doLogout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void saveUser()
    {
        AccountManager.getCurrentAccount().thenAccept(user -> {
            user.setUsername(_username.getText().toString());
            user.setBio(_bio.getText().toString());
            user.save()
                    .thenAccept(res -> Toast.makeText(
                            getActivity(),
                            "Modifiche salvate con successo!",
                            Toast.LENGTH_SHORT).show());
        });
    }
}
