package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.utilities.FragmentHandler;

public class GenericSettingsFragment extends ValidatedFragment
{
    private Button _usersSettingsButton;
    private Button _courtsSettingsButton;
    private Button _teacherSettingsButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_generic_settings, container, false);
        checkAuthenticated();
        setUsersSettingsButton();
        setCourtsSettingsButton();
        setTeacherSettingsButton();
        return currentView;
    }

    private void setUsersSettingsButton()
    {
        _usersSettingsButton = currentView.findViewById(R.id.buttonUsersSettings);
        _usersSettingsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new UsersSettingsFragment()));
    }

    private void setCourtsSettingsButton()
    {
        _courtsSettingsButton = currentView.findViewById(R.id.buttonCourtsSettings);
        _courtsSettingsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new CourtsSettingsFragment()));
    }

    private void setTeacherSettingsButton()
    {
        _teacherSettingsButton = currentView.findViewById(R.id.buttonTeacherSettings);
        _teacherSettingsButton.setOnClickListener(v -> FragmentHandler.replaceFragment(requireActivity(), new TeacherManagerFragment()));
    }
}
