package com.example.firsttry.extensions.adapters;

import static com.example.firsttry.utilities.DateTimeExtensions.getTimeTextForBooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AvailableCourtAdapter extends RecyclerView.Adapter<AvailableCourtAdapter.AvailableCourtViewHolder>
{
    private final Array<Court> courtsList;
    private final Date dateTime;
    private final AvailableCourtAdapter.OnUserActionListener listener;

    public AvailableCourtAdapter(Array<Court> courtsList, Date dateTime, AvailableCourtAdapter.OnUserActionListener listener)
    {
        this.courtsList = courtsList;
        this.dateTime = dateTime;
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
        holder.time.setText(getTimeTextForBooking(dateTime));
        holder.type.setText(court.getType().toString());
        holder.viewDetailsButton.setOnClickListener(v -> listener.onClick(court));

        getRelatedCourtBook(court).thenAccept(courtBook ->
                getRelatedUsers(courtBook).thenAccept(users -> {
                    if (!users.isEmpty())
                        printUsers(holder, users);
                })
        );
    }

    private void printUsers(
            @NonNull AvailableCourtAdapter.AvailableCourtViewHolder holder,
            Array<User> users
    )
    {
        StringBuilder output = new StringBuilder();
        output.append("\nUtenti disponibili\n");
        users.forEach(user -> output.append("- ").append(user.getUsername()).append("\n"));
        holder.users.append(output.toString());
    }

    private CompletableFuture<CourtBook> getRelatedCourtBook(Court court)
    {
        return DatabaseHandler.list(new CourtBook().tableName(), CourtBook.class).thenApply(courtBooks -> courtBooks
                .where(courtBook
                        -> Objects.equals(courtBook.getCourtId(), court.getId())
                        && courtBook.getStartsAt().equals(dateTime))
                .firstOrDefault());
    }

    private CompletableFuture<Array<User>> getRelatedUsers(CourtBook courtBook)
    {
        return DatabaseHandler.list(new User().tableName(), User.class)
                .thenApply(users -> users.where(user -> courtBook.getUserIds().contains(user.getId())));
    }

    @Override
    public int getItemCount() { return courtsList.size(); }

    public static class AvailableCourtViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView type;
        TextView time;
        TextView users;
        Button viewDetailsButton;

        public AvailableCourtViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            time = itemView.findViewById(R.id.time);
            users = itemView.findViewById(R.id.users);
            viewDetailsButton = itemView.findViewById(R.id.action_set_yourself_available);
        }
    }

    public interface OnUserActionListener
    {
        void onClick(Court court);
    }
}
