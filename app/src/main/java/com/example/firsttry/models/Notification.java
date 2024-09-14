package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.Repository;
import com.google.firebase.database.core.Repo;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Notification extends Model
{
    @Override
    public String tableName() { return "notifications"; }

    public Notification() { }

    public Notification(
            String title,
            String body,
            String targetUserId,
            Date timestamp)
    {
        this.title = title;
        this.body = body;
        this.targetUserId = targetUserId;
        this.timestamp = timestamp;
    }

    private String title;
    private String body;
    private String targetUserId;
    private Date timestamp;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    public String getTargetUserId() { return targetUserId; }

    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public CompletableFuture<Notification> save() {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<Notification> getById(String id) {
        return Repository.getById(id, Notification.class);
    }

    @Override
    public CompletableFuture<Notification> softDelete() {
        setIsDeleted(true);
        return save();
    }

    public CompletableFuture<Array<Notification>> list()
    {
        return Repository.list(Notification.class);
    }

    public CompletableFuture<Array<Notification>> list(Function<Notification, Boolean> predicate)
    {
        return Repository.list(Notification.class, predicate);
    }
}
