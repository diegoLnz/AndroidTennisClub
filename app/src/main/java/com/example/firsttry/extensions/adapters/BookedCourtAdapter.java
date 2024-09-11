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
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.utilities.Array;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookedCourtAdapter extends RecyclerView.Adapter<BookedCourtAdapter.BookedCourtViewHolder>
{
    private final Array<CourtBook> courtBooks;
    private final OnUserActionListener listener;

    public BookedCourtAdapter(Array<CourtBook> courtBooks, OnUserActionListener listener)
    {
        this.courtBooks = courtBooks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookedCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_court_card, parent, false);
        return new BookedCourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedCourtViewHolder holder, int position)
    {
        CourtBook courtBook = courtBooks.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String startTimeFormatted = dateFormat.format(courtBook.getStartsAt());
        String endTimeFormatted = dateFormat.format(courtBook.getEndsAt());

        holder.time.setText(startTimeFormatted + " - " + endTimeFormatted);

        new Court().getById(courtBook.getCourtId())
                .thenAccept(court -> holder.court.setText(court.getName()));

        holder.deleteCourtButton.setOnClickListener(v -> listener.onDelete(courtBook));
    }

    @Override
    public int getItemCount() { return courtBooks.size(); }

    public static class BookedCourtViewHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        TextView court;
        Button deleteCourtButton;

        public BookedCourtViewHolder(@NonNull View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            court = itemView.findViewById(R.id.court);
            deleteCourtButton = itemView.findViewById(R.id.action_book_court);
        }
    }

    public interface OnUserActionListener
    {
        void onDelete(CourtBook courtBook);
    }

}