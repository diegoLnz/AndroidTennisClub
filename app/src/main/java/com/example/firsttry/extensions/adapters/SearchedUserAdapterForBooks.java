package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.ImageUploader;

public class SearchedUserAdapterForBooks extends RecyclerView.Adapter<SearchedUserAdapterForBooks.SearchedUserViewHolder>
{
    private final Array<User> userList;
    private final SearchedUserAdapterForBooks.OnUserActionListener listener;

    public SearchedUserAdapterForBooks(Array<User> userList, SearchedUserAdapterForBooks.OnUserActionListener listener)
    {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchedUserAdapterForBooks.SearchedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_user_for_book_card, parent, false);
        return new SearchedUserAdapterForBooks.SearchedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedUserAdapterForBooks.SearchedUserViewHolder holder, int position)
    {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        setProfilePicture(user, holder);

        holder.viewDetailsButton.setOnClickListener(v -> listener.onInvitation(user));
        user.rank().thenAccept(userRank -> holder.rank.setText(userRank.toString()));
    }

    private void setProfilePicture(
            User user,
            @NonNull SearchedUserAdapterForBooks.SearchedUserViewHolder holder)
    {
        user.currentProfilePicture().thenAccept(pic -> ImageUploader.setImage(
                pic.getUrl(),
                holder.profilePic,
                holder.itemView.getContext()
        ));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class SearchedUserViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView email;
        TextView rank;
        Button viewDetailsButton;
        ImageView profilePic;

        public SearchedUserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            rank = itemView.findViewById(R.id.rank);
            viewDetailsButton = itemView.findViewById(R.id.action_view_details);
            profilePic = itemView.findViewById(R.id.profile_picture);
        }
    }

    public interface OnUserActionListener
    {
        void onInvitation(User user);
    }
}
