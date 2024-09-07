package com.example.firsttry.models;

import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.Repository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class LessonBook extends Model
{
    @Override
    public String tableName() { return "lessonbooks"; }

    public LessonBook() { }

    public LessonBook(String userId, String lessonId, Date timestamp)
    {
        this.userId = userId;
        this.lessonId = lessonId;
        this.timestamp = timestamp;
    }

    private String userId;
    private String lessonId;
    private Date timestamp;

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getLessonId() { return lessonId; }

    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public CompletableFuture<LessonBook> save() {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<LessonBook> getById(String id) {
        return DatabaseHandler.getById(id, this.tableName(), this.getClass())
                .thenApply(res -> res);
    }

    @Override
    public CompletableFuture<LessonBook> softDelete() {
        setIsDeleted(true);
        return save();
    }
}
