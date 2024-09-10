package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.UserReviewsAdapter;
import com.example.firsttry.models.Review;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

public class UserDetailFragment extends ValidatedFragment
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_user_detail, container, false);
        checkAuthenticated();
        setCurrentUser();

        recyclerView = currentView.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        String userId = getArguments().getString(extraKey);
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
        return currentView;
    }

    private void fillUserInfo()
    {
        assignFields();
        fillFields();
    }

    private void setReportButton()
    {
        reportButton = currentView.findViewById(R.id.btn_report_profile);
        reportButton.setOnClickListener(v -> FragmentHandler.replaceFragmentWithArguments(
                requireActivity(),
                new UserReportFragment(),
                HashMapExtensions.from("userId", targetUser.getId()))
        );
    }

    private void setReviewButton()
    {
        reviewButton = currentView.findViewById(R.id.btn_review_profile);
        reviewButton.setOnClickListener(v -> FragmentHandler.replaceFragmentWithArguments(
                requireActivity(),
                new UserReviewFragment(),
                HashMapExtensions.from("userId", targetUser.getId()))
        );
    }

    private void assignFields()
    {
        username = currentView.findViewById(R.id.username_view);
        email = currentView.findViewById(R.id.email_view);
        role = currentView.findViewById(R.id.role_view);
        bio = currentView.findViewById(R.id.bio_view);
        rank = currentView.findViewById(R.id.reputation_view);
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

        if (targetUser.getReputation() != null)
            rank.append("\n" + targetUser.getReputation().toString());
        else
            rank.setText("");
    }

    private void setReviewsAdapter()
    {
        targetUser.reviews().thenAccept(reviews -> {
                    if (reviews.isEmpty())
                    {
                        TextView reviewsLabel = currentView.findViewById(R.id.reviews_label);
                        reviewsLabel.setText(R.string.nessuna_recensione);
                        return;
                    }

                    reviews.orderByDescending(Review::getTimestamp);
                    adapter = new UserReviewsAdapter(reviews);
                    recyclerView.setAdapter(adapter);
                })
                .exceptionally(ex -> {
                    System.err.println(ex.getMessage());
                    return null;
                });
    }
}
