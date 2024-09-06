package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;
import static com.example.firsttry.utilities.DateTimeExtensions.now;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.LessonsBl;
import com.example.firsttry.enums.CourtType;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.LessonAdapter;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.Lesson;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TeacherScheduleManagerActivity
        extends ValidatedCompatActivity
        implements LessonAdapter.OnUserActionListener
{
    private static final String extraKey = "userId";

    private User targetUser;
    private ValidatedEditText date;
    private ValidatedEditText startTime;
    private ValidatedEditText endTime;
    private Button addLessonButton;
    private Spinner courtsSpinner;

    private LessonAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_schedule_manager);
        setBackButton(TeacherManagerActivity.class);
        setCurrentUser();
        setTargetUser().thenAccept(tUser -> {
            targetUser = tUser;
            setCourtsSpinner();
            setFields();
            setRecyclerView();
            setAddLessonButton();
        });
    }

    private CompletableFuture<User> setTargetUser()
    {
        String userId = getIntent().getStringExtra(extraKey);
        return DatabaseHandler.getById(userId, new User().tableName(), User.class);
    }

    private void setFields()
    {
        date = findViewById(R.id.edit_date);
        date.setRequired(true);
        date.addValidationCondition(
                value -> {
                    Date today = new Date();
                    today.setHours(0);
                    today.setMinutes(0);
                    today.setSeconds(0);

                    Date date = convertToDate(value);
                    date.setSeconds(1);

                    Boolean ok = date.before(today);
                    return ok;
                },
                "La data non deve essere precedente alla data di oggi"
        );

        startTime = findViewById(R.id.edit_start_time);
        startTime.setRequired(true);
        endTime = findViewById(R.id.edit_end_time);
        endTime.setRequired(true);
    }

    private void setRecyclerView()
    {
        recyclerView = findViewById(R.id.addedLessonsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LessonsBl.getLessonsByTeacherId(targetUser.getId()).thenAccept(lessons -> {
            lessons = lessons.where(lesson -> !lesson.getIsDeleted());
            lessons.orderBy(Lesson::getStartTime);
            adapter = new LessonAdapter(lessons, this);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setCourtsSpinner()
    {
        DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(courts -> {
            courts.where(court -> !court.getIsDeleted());

            List<String> courtNames = new ArrayList<>();
            for (Court court : courts.getList())
                courtNames.add(court.getName() + " - " + court.getId());

            courtsSpinner = findViewById(R.id.court_view_select);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courtNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courtsSpinner.setAdapter(adapter);
        });
    }

    private void setAddLessonButton()
    {
        addLessonButton = findViewById(R.id.btn_add);
        addLessonButton.setOnClickListener(v -> addLesson());
    }

    private void addLesson()
    {
        if (!validateFields())
        {
            return;
        }

        String date = Objects.requireNonNull(this.date.getText()).toString();
        String startTime = Objects.requireNonNull(this.startTime.getText()).toString();
        String endTime = Objects.requireNonNull(this.endTime.getText()).toString();

        Date startDate = convertToDate(date, startTime);
        Date endDate = convertToDate(date, endTime);

        if (startDate.after(endDate))
        {
            Toast.makeText(this, "L' orario di inizio non può essere successivo a quello di fine", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.equals(endDate))
        {
            Toast.makeText(this, "L' orario di inizio non può essere uguale a quello di fine", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.before(now()) || endDate.before(now()))
        {
            Toast.makeText(this, "Le date non possono essere precedenti alla data di oggi", Toast.LENGTH_SHORT).show();
            return;
        }

        String courtId = new Array<>(Objects.requireNonNull(courtsSpinner.getSelectedItem())
                .toString()
                .split(" - "))
                .lastOrDefault();

        DatabaseHandler.getById(courtId, new Court().tableName(), Court.class).thenAccept(court -> {
            Lesson lesson = new Lesson(
                    targetUser.getId(),
                    court.getId(),
                    startDate,
                    endDate
            );

            LessonsBl.isThereOverlappingLesson(lesson).thenAccept(isThere -> {
                if (isThere)
                {
                    Toast.makeText(this, "La lezione si sovrappone ad una già esistente", Toast.LENGTH_SHORT).show();
                    return;
                }

                lesson.save()
                        .thenAccept(result -> ActivityHandler.LinkToWithPreviousToast(
                                this,
                                TeacherScheduleManagerActivity.class,
                                extraKey,
                                targetUser.getId(),
                                "Lezione salvata con successo"));
            });
        });
    }

    @Override
    public void onClick(Lesson lesson) {
        lesson.softDelete()
                .thenAccept(result -> ActivityHandler.LinkToWithPreviousToast(
                        this,
                        TeacherScheduleManagerActivity.class,
                        extraKey,
                        targetUser.getId(),
                        "Lezione cancellata con successo"));
    }
}
