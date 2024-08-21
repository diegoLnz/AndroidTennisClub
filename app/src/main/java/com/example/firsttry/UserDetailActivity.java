package com.example.firsttry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.HashMap;

public class UserDetailActivity extends ValidatedCompatActivity
{
    private static final String extraKey = "userId";

    private User targetUser;
    private TextView username;
    private TextView email;
    private TextView role;
    private TextView bio;
    private TextView rank;

    private Button reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        checkAuthenticated();
        setCurrentUser();

        String userId = getIntent().getStringExtra(extraKey);
        DatabaseHandler.getById(userId, new User().tableName(), User.class)
                .thenAccept(user -> {
                    targetUser = user;
                    fillUserInfo();
                    setReportButton();
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to retrieve user: " + ex.getMessage());
                    return null;
                });
    }

    private void fillUserInfo()
    {
        assignFields();
        fillFields();
    }

    private void setReportButton()
    {
        reportButton = findViewById(R.id.btn_report_profile);
        reportButton.setOnClickListener(v -> ActivityHandler.LinkToWithExtra(
                this,
                UserReportActivity.class,
                "userId",
                targetUser.getId())
        );
    }

    private void assignFields()
    {
        username = findViewById(R.id.username_view);
        email = findViewById(R.id.email_view);
        role = findViewById(R.id.role_view);
        bio = findViewById(R.id.bio_view);
        rank = findViewById(R.id.rank_view);
    }

    private void fillFields()
    {
        username.append(": " + targetUser.getUsername());
        email.append(": " + targetUser.getEmail());
        bio.append(": " + targetUser.getBio());

        if (targetUser.getRole() != null)
            role.append(": " + targetUser.getRole().toString());
        else
            role.setText("");

        if (targetUser.getScore() != null)
            rank.append(": " + targetUser.getScore().toString());
        else
            rank.setText("");
    }
}
