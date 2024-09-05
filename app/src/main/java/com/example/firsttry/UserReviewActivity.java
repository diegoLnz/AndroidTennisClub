package com.example.firsttry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.Review;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserReviewActivity extends ValidatedCompatActivity
{
    private static final String extraKey = "userId";

    private ValidatedEditText reviewText;
    private User targetUser;

    private Button sendReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);
        checkAuthenticated();
        setCurrentUser();
        getTargetUser().thenAccept(user -> {
            targetUser = user;
            setFields();
            setRatingSpinner();
            setReportButton();
        });
    }

    private CompletableFuture<User> getTargetUser()
    {
        String userId = getIntent().getStringExtra(extraKey);
        return new User().getById(userId);
    }

    private void setFields()
    {
        reviewText = findViewById(R.id.review_text_field);
        reviewText.setRequired(true);
    }

    private void setRatingSpinner()
    {
        Integer[] ratings = {1, 2, 3, 4, 5};

        Spinner spinner = findViewById(R.id.rating_view_select);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setReportButton()
    {
        sendReviewButton = findViewById(R.id.btn_send_review);
        sendReviewButton.setOnClickListener(v -> sendReview());
    }

    private void sendReview()
    {
        if (!validateFields())
            return;

        Spinner spinner = findViewById(R.id.rating_view_select);
        Integer rating = (Integer) spinner.getSelectedItem();

        Review review = new Review(
                reviewText.getText().toString(),
                targetUser.getId(),
                CurrentUser.getId(),
                DateTimeExtensions.now(),
                rating);

        review.save()
                .thenCompose(Review::user)
                .thenCompose(User::updateReputation)
                .thenAccept(user -> ActivityHandler.LinkToWithPreviousToast(
                        this,
                        UserDetailActivity.class,
                        "userId",
                        user.getId(),
                        "Recensione inviata con successo!"
                ));
    }
}
