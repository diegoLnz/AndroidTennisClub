package com.example.firsttry.models;

import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class Report extends Model
{
    @Override
    public String tableName()
    {
        return
                new User().tableName()
                + "/"
                + "reports";
    }
    private String Message;
    private String UserId;

    public String getMessage() { return Message; }

    public void setMessage(String message) { Message = message; }

    public String getUserId() { return UserId; }

    public void setUserId(String userId) { UserId = userId; }

    @Override
    public CompletableFuture<Report> save()
    {
        return this.getId() == null || this.getId().isEmpty()
                ?
                DatabaseHandler.list(this.tableName(), this.getClass())
                        .thenCompose(reports -> {
                            int lastId = reports.size();
                            this.setId(String.valueOf(lastId + 1));
                            return DatabaseHandler.saveOrUpdate(this);
                        })
                        .thenApply(res -> res.match(
                                success -> success,
                                failure -> null
                        ))
                :
                DatabaseHandler.saveOrUpdate(this).thenApply(res -> res.match(
                        success -> success,
                        failure -> null
                ));
    }

    @Override
    public CompletableFuture<Report> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), Report.class)
                .thenApply(res -> res);
    }
}
