package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

public class UserReportsAdapter extends RecyclerView.Adapter<UserReportsAdapter.UserReportViewHolder>
{
    private final Array<Report> reportsList;
    private final UserReportsAdapter.OnUserActionListener listener;

    public UserReportsAdapter(Array<Report> reportsList, UserReportsAdapter.OnUserActionListener listener)
    {
        this.reportsList = reportsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_report_card, parent, false);
        return new UserReportsAdapter.UserReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReportsAdapter.UserReportViewHolder holder, int position)
    {
        Report report = reportsList.get(position);
        DatabaseHandler.getById(report.getReporterId(), new User().tableName(), User.class).thenAccept(user -> {
            holder.username.setText(user.getUsername());
            holder.reportText.setText(report.getMessage());
            holder.reportDate.setText(report.getTimestamp().toString());
            holder.deleteReportButton.setOnClickListener(v -> listener.onDelete(report));
        });
    }

    @Override
    public int getItemCount() { return reportsList.size(); }

    public static class UserReportViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView reportText;
        TextView reportDate;

        Button deleteReportButton;

        public UserReportViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            reportText = itemView.findViewById(R.id.text);
            reportDate = itemView.findViewById(R.id.date);
            deleteReportButton = itemView.findViewById(R.id.action_delete_report);
        }
    }

    public interface OnUserActionListener
    {
        void onDelete(Report report);
    }
}
