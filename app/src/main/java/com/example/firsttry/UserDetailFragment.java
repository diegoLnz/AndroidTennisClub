package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.firsttry.utilities.ImageUploader;

public class UserDetailFragment extends ValidatedFragment
{
    private static final String extraKey = "userId";

    private User targetUser;
    private TextView username;
    private TextView email;
    private TextView role;
    private TextView bio;
    private TextView reputation;
    private TextView score;
    private TextView rank;


    private Button reviewButton;
    private Button reportButton;

    private RecyclerView recyclerView;
    private UserReviewsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        checkAuthenticated();
        setCurrentUser();

        recyclerView = currentView.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        String userId = getArguments().getString(extraKey);
        DatabaseHandler.getById(userId, new User().tableName(), User.class)
                .thenAccept(user -> {
                    targetUser = user;
                    setProfilePicture();
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
        reputation = currentView.findViewById(R.id.reputation_view);
        score = currentView.findViewById(R.id.score_view);
        rank = currentView.findViewById(R.id.rank_view);
    }

    private void fillFields()
    {
        username.setText(targetUser.getUsername());
        email.setText(targetUser.getEmail());
        bio.setText(targetUser.getBio());
        score.setText(targetUser.getScore().toString());

        if (targetUser.getRole() != null)
            role.setText(targetUser.getRole().toString());
        else
            role.setText("");

        if (targetUser.getReputation() != null)
            reputation.setText(targetUser.getReputation().toString());
        else
            reputation.setText("");

        targetUser.rank().thenAccept(userRank -> rank.setText(userRank.toString()));
    }

    private void setProfilePicture()
    {
        ImageView imageView = currentView.findViewById(R.id.profile_picture);
        targetUser.currentProfilePicture().thenAccept(pic ->
                ImageUploader.setImage(
                        pic.getUrl(),
                        imageView,
                        requireContext()
                ));
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
