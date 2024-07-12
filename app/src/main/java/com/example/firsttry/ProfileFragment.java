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

import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;

import java.util.Objects;

public class ProfileFragment extends Fragment
{
    private ValidatedEditText _username;
    private ValidatedEditText _email;
    private ValidatedEditText _bio;
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_profile, container, false);
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
                    _email = _currentView.findViewById(R.id.edit_email);
                    _email.setText(user.getEmail());
                    _bio = _currentView.findViewById(R.id.edit_bio);
                    _bio.setText(user.getBio());
                });
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
}
