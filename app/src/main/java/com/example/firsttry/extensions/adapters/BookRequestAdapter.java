package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.enums.CourtBookRequestStatus;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookRequestAdapter extends RecyclerView.Adapter<BookRequestAdapter.BookRequestViewHolder>
{
    private final Array<CourtBookRequest> requests;
    private final OnUserActionListener listener;

    public BookRequestAdapter(Array<CourtBookRequest> requests, OnUserActionListener listener)
    {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_request_card, parent, false);
        return new BookRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRequestViewHolder holder, int position)
    {
        CourtBookRequest request = requests.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        setButtons(holder, request);

        new CourtBook().getById(request.getCourtBookId()).thenAccept(courtBook -> {
            new User().getById(courtBook.getBookerId())
                    .thenAccept(user -> holder.username.setText(user.getUsername()));

            String startTimeFormatted = dateFormat.format(courtBook.getStartsAt());
            String endTimeFormatted = dateFormat.format(courtBook.getEndsAt());

            holder.time.setText(startTimeFormatted + " - " + endTimeFormatted);

            new Court().getById(courtBook.getCourtId())
                    .thenAccept(court -> holder.court.setText(court.getName()));

            holder.acceptButton.setOnClickListener(v -> listener.onAccept(request));
            holder.denyButton.setOnClickListener(v -> listener.onDeny(request));
        });
    }

    private void setButtons(BookRequestViewHolder holder, CourtBookRequest request)
    {
        if (request.getStatus().equals(CourtBookRequestStatus.Accepted))
        {
            holder.acceptButton.setText(R.string.accettato);
            holder.acceptButton.setEnabled(false);

            holder.denyButton.setVisibility(View.GONE);
            holder.denyButton.setEnabled(false);
        }
        else if (request.getStatus().equals(CourtBookRequestStatus.Pending))
        {
            holder.acceptButton.setText(R.string.accetta);
            holder.acceptButton.setEnabled(true);

            holder.denyButton.setVisibility(View.VISIBLE);
            holder.denyButton.setEnabled(true);
        }
        else if (request.getStatus().equals(CourtBookRequestStatus.NotAccepted))
        {
            holder.acceptButton.setText(R.string.rifiutata);
            holder.acceptButton.setEnabled(false);

            holder.denyButton.setVisibility(View.GONE);
            holder.denyButton.setEnabled(false);
        }
        else
        {
            holder.acceptButton.setText(R.string.scaduto);
            holder.acceptButton.setEnabled(false);

            holder.denyButton.setVisibility(View.GONE);
            holder.denyButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() { return requests.size(); }

    public static class BookRequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView time;
        TextView court;
        Button acceptButton;
        Button denyButton;

        public BookRequestViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            court = itemView.findViewById(R.id.court);
            acceptButton = itemView.findViewById(R.id.action_accept);
            denyButton = itemView.findViewById(R.id.action_deny);
        }
    }

    public interface OnUserActionListener
    {
        void onAccept(CourtBookRequest request);
        void onDeny(CourtBookRequest request);
    }

}