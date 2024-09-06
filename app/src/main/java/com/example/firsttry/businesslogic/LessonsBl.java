package com.example.firsttry.businesslogic;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;
import static com.example.firsttry.utilities.DateTimeExtensions.getDay;

import com.example.firsttry.models.Lesson;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class LessonsBl
{
    public static CompletableFuture<Array<Lesson>> getLessonsByTeacherId(String teacherId)
    {
        return DatabaseHandler.list(new Lesson().tableName(), Lesson.class).thenApply(lessons -> lessons
                .where(lesson -> lesson.getTeacherId().equals(teacherId)));
    }

    public static CompletableFuture<Boolean> isThereOverlappingLesson(Lesson newLesson)
    {
        return DatabaseHandler.list(new Lesson().tableName(), Lesson.class)
                .thenApply(lessons -> lessons
                        .any(lesson -> {
                            if (lesson.getIsDeleted())
                            {
                                return false;
                            }

                            Boolean isOverlapping = !lesson.getEndTime().before(newLesson.getStartTime()) &&
                                    !lesson.getStartTime().after(newLesson.getEndTime());

                            Boolean sameCourt = lesson.getCourtId().equals(newLesson.getCourtId());

                            return isOverlapping && sameCourt;
                        }));
    }

    public static CompletableFuture<Array<Lesson>> getLessonsByDay(String day)
    {
        Date dayToDate = getDay(convertToDate(day));
        return DatabaseHandler.list(new Lesson().tableName(), Lesson.class).thenApply(lessons -> lessons
                .where(lesson -> !lesson.getIsDeleted())
                .where(lesson -> {
                    Date startDay = getDay(lesson.getStartTime());
                    return startDay.equals(dayToDate);
                }));
    }
}
