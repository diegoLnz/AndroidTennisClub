package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;
import static com.example.firsttry.utilities.DateTimeExtensions.now;

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
import android.widget.Toast;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.enums.LessonState;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.SearchedLessonAdapter;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.models.LessonBook;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;
import com.example.firsttry.utilities.FragmentHandler;

import java.util.Date;
import java.util.Objects;

public class BookLessonFragment
        extends ValidatedFragment
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
        checkAuthenticated();
        _currentView = inflater.inflate(R.layout.fragment_book_lesson, container, false);
        dateEditText = _currentView.findViewById(R.id.edit_date);
        dateEditText.setRequired(true);
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
        if (!validateFields())
        {
            setRecyclerView(new Array<>());
            return;
        }

        String dateValue = Objects.requireNonNull(dateEditText.getText()).toString();
        LessonsBl.getLessonsByDay(dateValue).thenAccept(lessons -> {
            lessons = lessons
                    .where(lesson -> lesson.getStatus() == LessonState.NotBooked)
                    .where(lesson -> lesson.getStartTime().after(now()));
            setRecyclerView(lessons);
        });
    }

    @Override
    public void onBook(Lesson lesson)
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            LessonBook lessonBook = new LessonBook(user.getId(), lesson.getId(), new Date());
            lessonBook.save()
                    .thenAccept(lessonBook1 -> {
                        lesson.setStatus(LessonState.Booked);
                        lesson.save();
                        Toast.makeText(getContext(), "Lezione prenotata con successo!", Toast.LENGTH_SHORT).show();
                        searchAvailableLessons();
                    });
        });
    }

    @Override
    public void onDelete(Lesson lesson)
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> LessonBook.list(book -> book.getLessonId().equals(lesson.getId())
                        && book.getUserId().equals(user.getId())
                        && !book.getIsDeleted())
                .thenAccept(list -> list.firstOrDefault().softDelete().thenAccept(res -> {
                    lesson.setStatus(LessonState.NotBooked);
                    lesson.save();
                    Toast.makeText(getContext(), "Prenotazione annullata!", Toast.LENGTH_SHORT).show();
                    searchAvailableLessons();
                })));
    }
}