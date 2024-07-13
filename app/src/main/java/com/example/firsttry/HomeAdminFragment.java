package com.example.firsttry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeAdminFragment extends Fragment
{
    private Button _settingsBtn;

    private View _currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _currentView = inflater.inflate(R.layout.fragment_home_admin, container, false);
        return _currentView;
    }

    private void setSettingsButtonListener()
    {
        _settingsBtn = _currentView.findViewById(R.id.buttonGenericSettings);
        _settingsBtn.setOnClickListener(v -> {
            
        });
    }
}