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

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.ActivityHandler;

import java.util.Objects;

public class ProfileFragment extends Fragment
{
    private ValidatedEditText _username;
    private ValidatedEditText _bio;

    private Button seeBookedLessonsButton;
    private Button seeBookedCourtsButton;
    private Button seeInvitationsButton;
    private Button seeBookersButton;

    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_profile, container, false);
        setupButtons();
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

    private void setupButtons()
    {
        setSaveBtnListener();
        setSeeBookedLessonsBtnListener();
        setSeeBookedCourtsBtnListener();
        setSeeInvitationsBtnListener();
        setSeeBookersBtnListener();
    }

    private void setSaveBtnListener()
    {
        Button saveBtn = _currentView.findViewById(R.id.btn_save_profile);
        saveBtn.setOnClickListener(v -> saveUser());
    }

    private void setSeeBookedLessonsBtnListener()
    {
        seeBookedLessonsButton = _currentView.findViewById(R.id.btn_see_lessonbooks);
        seeBookedLessonsButton.setOnClickListener(v -> ActivityHandler.LinkTo(requireActivity(), SeeBookedLessonsActivity.class));
    }

    private void setSeeBookedCourtsBtnListener()
    {
        seeBookedCourtsButton = _currentView.findViewById(R.id.btn_see_courtbooks);
        seeBookedCourtsButton.setOnClickListener(v -> ActivityHandler.LinkTo(requireActivity(), SeeBookedCourtsActivity.class));
    }

    private void setSeeInvitationsBtnListener()
    {
        seeInvitationsButton = _currentView.findViewById(R.id.btn_see_invitations);
        seeInvitationsButton.setOnClickListener(v -> ActivityHandler.LinkTo(requireActivity(), SeeInvitationsActivity.class));
    }

    private void setSeeBookersBtnListener()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            seeBookersButton = _currentView.findViewById(R.id.btn_see_bookers);

            if (!user.getRole().equals(UserRole.Teacher))
            {
                seeBookersButton.setVisibility(View.GONE);
                return;
            }

            seeBookersButton.setVisibility(View.VISIBLE);
            seeBookersButton.setOnClickListener(v -> ActivityHandler.LinkTo(requireActivity(), SeeBookersActivity.class));
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
        requireActivity().finish();
    }

    private void saveUser()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            user.setUsername(_username.getText().toString());
            user.setBio(_bio.getText().toString());
            user.save().thenAccept(res -> Toast.makeText(
                    getActivity(),
                    "Modifiche salvate con successo!",
                    Toast.LENGTH_SHORT).show());
        });
    }
}
