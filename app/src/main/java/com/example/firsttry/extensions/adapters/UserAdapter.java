package com.example.firsttry.extensions.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>
{
    private Boolean isOnlyForTeachers = false;
    private final Array<User> userList;
    private final OnUserActionListener listener;

    public UserAdapter(Array<User> userList, OnUserActionListener listener)
    {
        this.userList = userList;
        this.listener = listener;
    }

    public UserAdapter(Array<User> userList, OnUserActionListener listener, Boolean isOnlyForTeachers)
    {
        this.userList = userList;
        this.listener = listener;
        this.isOnlyForTeachers = isOnlyForTeachers;
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
        setStatusText(holder, user);
        holder.editButton.setOnClickListener(v -> listener.onEdit(user));

        if (isOnlyForTeachers)
        {
            holder.editButton.setText(R.string.gestisci_orari);
        }
    }

    private void setStatusText(@NonNull UserViewHolder holder, User user)
    {
        if (user.getStatus() == null || user.getStatus() == UserStatus.ACTIVE)
        {
            holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.custom_green, null));
            holder.status.setText(UserStatus.ACTIVE.getDescription());
            return;
        }

        holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.custom_red, null));
        holder.status.setText(user.getStatus().getDescription());
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView email;
        TextView status;
        Button editButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            status = itemView.findViewById(R.id.status);
            editButton = itemView.findViewById(R.id.action_edit);
        }
    }

    public interface OnUserActionListener
    {
        void onEdit(User user);
    }
}

