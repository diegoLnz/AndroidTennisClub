package com.example.firsttry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BookLessonFragment extends Fragment
{
    private Button searchAvailableCourtsBtn;
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_book_lesson, container, false);
        return _currentView;
    }

    private void setSearchAvailableCourtsButton()
    {
        searchAvailableCourtsBtn = _currentView.findViewById(R.id.btn_search_available_courts);
        searchAvailableCourtsBtn.setOnClickListener(v -> {

        });
    }
}