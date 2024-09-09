package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.Review;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.DateTimeExtensions;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

import java.util.concurrent.CompletableFuture;

public class UserReviewFragment extends ValidatedFragment
{
    private static final String extraKey = "userId";

    private ValidatedEditText reviewText;
    private User targetUser;

    private Button sendReviewButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_user_review, container, false);
        checkAuthenticated();
        setCurrentUser();
        getTargetUser().thenAccept(user -> {
            targetUser = user;
            setFields();
            setRatingSpinner();
            setReportButton();
        });
        return currentView;
    }

    private CompletableFuture<User> getTargetUser()
    {
        String userId = requireActivity().getIntent().getStringExtra(extraKey);
        return new User().getById(userId);
    }

    private void setFields()
    {
        reviewText = currentView.findViewById(R.id.review_text_field);
        reviewText.setRequired(true);
    }

    private void setRatingSpinner()
    {
        Integer[] ratings = {1, 2, 3, 4, 5};

        Spinner spinner = currentView.findViewById(R.id.rating_view_select);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setReportButton()
    {
        sendReviewButton = currentView.findViewById(R.id.btn_send_review);
        sendReviewButton.setOnClickListener(v -> sendReview());
    }

    private void sendReview()
    {
        if (!validateFields())
            return;

        Spinner spinner = currentView.findViewById(R.id.rating_view_select);
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
                .thenAccept(user -> {
                    Toast.makeText(requireActivity(), "Recensione inviata con successo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragmentWithArguments(
                            requireActivity(),
                            new UserDetailFragment(),
                            HashMapExtensions.from("userId", user.getId())
                    );
                });
    }
}
