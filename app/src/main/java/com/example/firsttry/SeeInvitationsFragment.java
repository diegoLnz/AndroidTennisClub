package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.SearchedLessonAdapter;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SeeInvitationsFragment extends ValidatedFragment
{

    private RecyclerView recyclerView;
    private SearchedLessonAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_see_booked_lessons, container, false);
        updateRecyclerView();
        return currentView;
    }

    private void updateRecyclerView()
    {
        getUserLessons().thenAccept(this::setRecyclerView);
    }

    private void setRecyclerView(Array<Lesson> lessons)
    {
        recyclerView = currentView.findViewById(R.id.availableLessonsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

//        adapter = new SearchedLessonAdapter(lessons, this);
//        recyclerView.setAdapter(adapter);
    }

    private CompletableFuture<Array<Lesson>> getUserLessons()
    {
        return Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenCompose(user -> LessonsBl.getLessonsByStudentId(user.getId()));
    }
}
