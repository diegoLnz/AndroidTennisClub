package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.Repository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lesson extends Model
{

    @Override
    public String tableName() { return "lessons"; }

    public Lesson() { }

    public Lesson(String teacherId, String courtId, Date startTime, Date endTime)
    {
        this.teacherId = teacherId;
        this.courtId = courtId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private String teacherId;
    private String courtId;
    private Date startTime;
    private Date endTime;

    public String getTeacherId() { return teacherId; }

    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getCourtId() { return courtId; }

    public void setCourtId(String courtId) { this.courtId = courtId; }

    public Date getStartTime() { return startTime; }

    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }

    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public CompletableFuture<Court> court()
    {
        return DatabaseHandler.getById(courtId, new Court().tableName(), Court.class);
    }

    @Override
    public CompletableFuture<Lesson> save() {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<Lesson> getById(String id) {
        return DatabaseHandler.getById(id, this.tableName(), Lesson.class)
                .thenApply(res -> res);
    }

    @Override
    public CompletableFuture<Lesson> softDelete() {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<Lesson>> list()
    {
        return Repository.list(Lesson.class);
    }

    public static CompletableFuture<Array<Lesson>> list(Function<Lesson, Boolean> predicate)
    {
        return Repository.list(Lesson.class, predicate);
    }
}
