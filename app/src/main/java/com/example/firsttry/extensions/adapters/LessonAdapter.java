package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.Array;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder>
{
    private final Array<Lesson> lessons;
    private final OnUserActionListener listener;

    public LessonAdapter(Array<Lesson> lessons, OnUserActionListener listener)
    {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_card, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position)
    {
        Lesson lesson = lessons.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String startTimeFormatted = dateFormat.format(lesson.getStartTime());
        String endTimeFormatted = dateFormat.format(lesson.getEndTime());

        holder.time.setText(startTimeFormatted + " - " + endTimeFormatted);

        lesson.court().thenAccept(court -> {
            holder.court.setText(court.getName());
            holder.deleteLessonButton.setOnClickListener(v -> listener.onClick(lesson));
        });
    }

    @Override
    public int getItemCount() { return lessons.size(); }

    public static class LessonViewHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        TextView court;
        Button deleteLessonButton;

        public LessonViewHolder(@NonNull View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            court = itemView.findViewById(R.id.court);
            deleteLessonButton = itemView.findViewById(R.id.action_delete_lesson);
        }
    }

    public interface OnUserActionListener
    {
        void onClick(Lesson lesson);
    }

}