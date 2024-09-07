package com.example.firsttry.extensions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.R;
import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class SearchedLessonAdapter extends RecyclerView.Adapter<SearchedLessonAdapter.SearchedLessonViewHolder>
{
    private final Array<Lesson> lessons;
    private final OnUserActionListener listener;

    public SearchedLessonAdapter(Array<Lesson> lessons, OnUserActionListener listener)
    {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchedLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_lesson_card, parent, false);
        return new SearchedLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedLessonViewHolder holder, int position)
    {
        Lesson lesson = lessons.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String startTimeFormatted = dateFormat.format(lesson.getStartTime());
        String endTimeFormatted = dateFormat.format(lesson.getEndTime());

        holder.time.setText(startTimeFormatted + " - " + endTimeFormatted);

        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            LessonsBl.getLessonsByStudentId(user.getId()).thenAccept(lessons -> {
                if (lessons.any(lesson1 -> lesson.getId().equals(lesson1.getId())))
                {
                    holder.deleteLessonButton.setText(R.string.annulla_prenotazione);
                    holder.deleteLessonButton.setBackgroundColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.custom_red)
                    );
                    holder.deleteLessonButton.setOnClickListener(v -> listener.onDelete(lesson));
                }
                else
                {
                    holder.deleteLessonButton.setOnClickListener(v -> listener.onBook(lesson));
                }
                lesson.court().thenAccept(court -> {
                    holder.court.setText(court.getName());
                });
            });
        });
    }

    @Override
    public int getItemCount() { return lessons.size(); }

    public static class SearchedLessonViewHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        TextView court;
        Button deleteLessonButton;

        public SearchedLessonViewHolder(@NonNull View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            court = itemView.findViewById(R.id.court);
            deleteLessonButton = itemView.findViewById(R.id.action_book_lesson);
        }
    }

    public interface OnUserActionListener
    {
        void onBook(Lesson lesson);
        void onDelete(Lesson lesson);
    }

}