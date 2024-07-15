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

public class AddedCourtAdapter extends RecyclerView.Adapter<AddedCourtAdapter.AddedCourtViewHolder>
{
    private Array<Court> courtsList;
    private AddedCourtAdapter.OnUserActionListener listener;

    public AddedCourtAdapter(Array<Court> courtsList, AddedCourtAdapter.OnUserActionListener listener)
    {
        this.courtsList = courtsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddedCourtAdapter.AddedCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_court_card, parent, false);
        return new AddedCourtAdapter.AddedCourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedCourtAdapter.AddedCourtViewHolder holder, int position)
    {
        Court court = courtsList.get(position);
        holder.name.setText(court.getName());
        holder.type.setText(court.getType().toString());

        holder.viewDetailsButton.setOnClickListener(v -> listener.onDelete(court));
    }

    @Override
    public int getItemCount() { return courtsList.size(); }

    public static class AddedCourtViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView type;
        Button viewDetailsButton;

        public AddedCourtViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            viewDetailsButton = itemView.findViewById(R.id.action_delete);
        }
    }

    public interface OnUserActionListener
    {
        void onDelete(Court court);
    }
}

