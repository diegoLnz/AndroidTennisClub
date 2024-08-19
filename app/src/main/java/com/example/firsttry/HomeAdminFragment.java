package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.utilities.AccountManager;

import java.util.Objects;

public class HomeAdminFragment extends Fragment
{
    private Button _settingsBtn;

    private View _currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        redirectIfNotAdmin();
        _currentView = inflater.inflate(R.layout.fragment_home_admin, container, false);
        setSettingsButtonListener();
        return _currentView;
    }

    private void redirectIfNotAdmin()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            if (!user.getRole().equals(UserRole.Admin))
            {
                startActivity(new Intent(getActivity(), HomeFragment.class));
                getActivity().finish();
            }
        });
    }

    private void setSettingsButtonListener()
    {
        _settingsBtn = _currentView.findViewById(R.id.buttonGenericSettings);
        _settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GenericSettingsActivity.class));
            getActivity().finish();
        });
    }
}