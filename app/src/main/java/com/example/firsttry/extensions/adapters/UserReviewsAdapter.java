package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.Review;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

public class UserReviewsAdapter extends RecyclerView.Adapter<UserReviewsAdapter.UserReviewViewHolder>
{
    private final Array<Review> reviewsList;

    public UserReviewsAdapter(Array<Review> reviewsList)
    {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public UserReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_card, parent, false);
        return new UserReviewsAdapter.UserReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewsAdapter.UserReviewViewHolder holder, int position)
    {
        Review review = reviewsList.get(position);
        DatabaseHandler.getById(review.getReviewerId(), new User().tableName(), User.class).thenAccept(user -> {
            holder.username.setText(user.getUsername());
            holder.reviewText.setText(review.getText());
            holder.reviewDate.setText(review.getTimestamp().toString());
            addStars(holder.starsContainer, review.getRating());
        });
    }

    @Override
    public int getItemCount() { return reviewsList.size(); }

    private void addStars(LinearLayout starsContainer, int rating)
    {
        starsContainer.removeAllViews();
        int remainingStars = 5;
        for (int i = 0; i < rating; i++)
        {
            ImageView star = new ImageView(starsContainer.getContext());
            star.setImageResource(android.R.drawable.star_on);
            star.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            starsContainer.addView(star);
            remainingStars--;
        }
        for (int i = 0; i < remainingStars; i++)
        {
            ImageView star = new ImageView(starsContainer.getContext());
            star.setImageResource(android.R.drawable.star_off);
            star.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            starsContainer.addView(star);
        }
    }

    public static class UserReviewViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView reviewText;
        TextView reviewDate;
        LinearLayout starsContainer;

        public UserReviewViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            reviewText = itemView.findViewById(R.id.text);
            reviewDate = itemView.findViewById(R.id.date);
            starsContainer = itemView.findViewById(R.id.stars_container);
        }
    }
}
