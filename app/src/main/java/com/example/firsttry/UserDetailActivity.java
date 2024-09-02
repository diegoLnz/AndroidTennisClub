package com.example.firsttry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.adapters.UserReportsAdapter;
import com.example.firsttry.extensions.adapters.UserReviewsAdapter;
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

    private Button reviewButton;
    private Button reportButton;

    private RecyclerView recyclerView;
    private UserReviewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        checkAuthenticated();
        setCurrentUser();

        recyclerView = findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userId = getIntent().getStringExtra(extraKey);
        DatabaseHandler.getById(userId, new User().tableName(), User.class)
                .thenAccept(user -> {
                    targetUser = user;
                    fillUserInfo();
                    setReviewButton();
                    setReportButton();
                    setReviewsAdapter();
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

    private void setReviewButton()
    {
        reviewButton = findViewById(R.id.btn_review_profile);
        reviewButton.setOnClickListener(v -> ActivityHandler.LinkToWithExtra(
                this,
                UserReviewActivity.class,
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
        username.append("\n" + targetUser.getUsername());
        email.append("\n" + targetUser.getEmail());
        bio.append("\n" + targetUser.getBio());

        if (targetUser.getRole() != null)
            role.append("\n" + targetUser.getRole().toString());
        else
            role.setText("");

        if (targetUser.getScore() != null)
            rank.append("\n" + targetUser.getScore().toString());
        else
            rank.setText("");
    }

    private void setReviewsAdapter()
    {
        targetUser.reviews().thenAccept(reviews -> {
                    if (reviews.isEmpty())
                    {
                        TextView reviewsLabel = findViewById(R.id.reviews_label);
                        reviewsLabel.setText(R.string.nessuna_recensione);
                        return;
                    }

                    adapter = new UserReviewsAdapter(reviews);
                    recyclerView.setAdapter(adapter);
                })
                .exceptionally(ex -> {
                    System.err.println(ex.getMessage());
                    return null;
                });
    }
}
