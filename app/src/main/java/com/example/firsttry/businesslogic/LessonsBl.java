package com.example.firsttry.businesslogic;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;
import static com.example.firsttry.utilities.DateTimeExtensions.getDay;

import com.example.firsttry.models.Lesson;
import com.example.firsttry.models.LessonBook;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.Date;
import java.util.Objects;
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

    public static CompletableFuture<Array<Lesson>> getLessonsByStudentId(String studentId)
    {
        return DatabaseHandler.list(new LessonBook().tableName(), LessonBook.class)
                .thenCompose(books -> {
                    Array<LessonBook> finalBooks = books.where(book -> !book.getIsDeleted());
                    return DatabaseHandler.list(new Lesson().tableName(), Lesson.class)
                            .thenApply(lessons -> lessons
                                    .where(lesson -> !lesson.getIsDeleted())
                                    .where(lesson -> finalBooks.any(book -> book.getLessonId().equals(lesson.getId())
                                            && book.getUserId().equals(studentId))));
                });
    }

    public static CompletableFuture<LessonBook> deleteLessonBookByLesson(Lesson lesson)
    {
        return Objects.requireNonNull(AccountManager.getCurrentAccount()).thenCompose(user -> LessonBook.list(book -> book.getLessonId().equals(lesson.getId())
                        && book.getUserId().equals(user.getId())
                        && !book.getIsDeleted())
                .thenCompose(list -> list.firstOrDefault().softDelete()));
    }
}
