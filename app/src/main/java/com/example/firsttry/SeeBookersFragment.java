package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.BookRequestAdapter;
import com.example.firsttry.extensions.adapters.LessonAdapter;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

public class SeeBookersFragment
        extends ValidatedFragment
        implements LessonAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private LessonAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_see_bookers, container, false);
        setCurrentUser();
        updateRecyclerView();
        return currentView;
    }

    private void updateRecyclerView()
    {
        AccountManager.getCurrentAccount()
                .thenAccept(user -> LessonsBl.getTeacherLessons(user).thenAccept(this::setRecyclerView));
    }

    private void setRecyclerView(Array<Lesson> lessons)
    {
        recyclerView = currentView.findViewById(R.id.availableLessonsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new LessonAdapter(lessons, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(Lesson lesson)
    {
        lesson.softDelete()
                .thenAccept(result -> {
                    Toast.makeText(requireActivity(), "Lezione cancellata", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                });
    }
}
