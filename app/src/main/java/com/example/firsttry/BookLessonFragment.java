package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.SearchedLessonAdapter;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.Date;
import java.util.Objects;

public class BookLessonFragment
        extends Fragment
        implements SearchedLessonAdapter.OnUserActionListener
{
    private ValidatedEditText dateEditText;
    private Button searchAvailableLessonsBtn;
    private View _currentView;

    private RecyclerView recyclerView;
    private SearchedLessonAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_book_lesson, container, false);
        dateEditText = _currentView.findViewById(R.id.edit_date);
        setSearchAvailableLessonsButton();
        return _currentView;
    }

    private void setSearchAvailableLessonsButton()
    {
        searchAvailableLessonsBtn = _currentView.findViewById(R.id.btn_search_available_lessons);
        searchAvailableLessonsBtn.setOnClickListener(v -> searchAvailableLessons());
    }

    private void setRecyclerView(Array<Lesson> lessons)
    {
        recyclerView = _currentView.findViewById(R.id.availableLessonsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SearchedLessonAdapter(lessons, this);
        recyclerView.setAdapter(adapter);
    }

    private void searchAvailableLessons()
    {
        String dateValue = Objects.requireNonNull(dateEditText.getText()).toString();
        LessonsBl.getLessonsByDay(dateValue).thenAccept(this::setRecyclerView);
    }

    @Override
    public void onClick(Lesson lesson) {

    }
}