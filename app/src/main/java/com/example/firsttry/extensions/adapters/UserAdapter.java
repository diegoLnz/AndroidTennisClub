package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>
{
    private final Array<User> userList;
    private final OnUserActionListener listener;

    public UserAdapter(Array<User> userList, OnUserActionListener listener)
    {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position)
    {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());

        holder.editButton.setOnClickListener(v -> listener.onEdit(user));
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView email;
        Button editButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            editButton = itemView.findViewById(R.id.action_edit);
        }
    }

    public interface OnUserActionListener
    {
        void onEdit(User user);
    }
}

