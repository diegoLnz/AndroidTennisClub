package com.example.firsttry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.FragmentHandler;

import java.util.Objects;

public class HomeFragment extends Fragment
{
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handleAdmin();
        _currentView = inflater.inflate(R.layout.fragment_home, container, false);
        return _currentView;
    }

    private void handleAdmin()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenAccept(user -> {
                    if (user.getRole() == UserRoles.Admin)
                        FragmentHandler.replaceFragment(this.getActivity(), new HomeAdminFragment());
                });
    }
}