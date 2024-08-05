package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.Court;
import com.example.firsttry.utilities.Array;

public class AvailableCourtAdapter extends RecyclerView.Adapter<AvailableCourtAdapter.AvailableCourtViewHolder>
{
    private Array<Court> courtsList;
    private AvailableCourtAdapter.OnUserActionListener listener;

    public AvailableCourtAdapter(Array<Court> courtsList, AvailableCourtAdapter.OnUserActionListener listener)
    {
        this.courtsList = courtsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvailableCourtAdapter.AvailableCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_court_card, parent, false);
        return new AvailableCourtAdapter.AvailableCourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableCourtAdapter.AvailableCourtViewHolder holder, int position)
    {
        Court court = courtsList.get(position);
        holder.name.setText(court.getName());
        holder.type.setText(court.getType().toString());

        holder.viewDetailsButton.setOnClickListener(v -> listener.onClick(court));
    }

    @Override
    public int getItemCount() { return courtsList.size(); }

    public static class AvailableCourtViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView type;
        TextView users;
        Button viewDetailsButton;

        public AvailableCourtViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            users = itemView.findViewById(R.id.users);
            viewDetailsButton = itemView.findViewById(R.id.action_set_yourself_available);
        }
    }

    public interface OnUserActionListener
    {
        void onClick(Court court);
    }
}
