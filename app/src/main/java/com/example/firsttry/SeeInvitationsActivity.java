package com.example.firsttry;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.adapters.SearchedLessonAdapter;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SeeInvitationsActivity extends ValidatedCompatActivity
{

    private RecyclerView recyclerView;
    private SearchedLessonAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_booked_lessons);
        setBackButton(MainActivity.class);
        updateRecyclerView();
    }

    private void updateRecyclerView()
    {
        getUserLessons().thenAccept(this::setRecyclerView);
    }

    private void setRecyclerView(Array<Lesson> lessons)
    {
        recyclerView = findViewById(R.id.availableLessonsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        adapter = new SearchedLessonAdapter(lessons, this);
//        recyclerView.setAdapter(adapter);
    }

    private CompletableFuture<Array<Lesson>> getUserLessons()
    {
        return Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenCompose(user -> LessonsBl.getLessonsByStudentId(user.getId()));
    }
}
