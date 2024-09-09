package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.Repository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CourtBookRequest extends Model
{
    @Override
    public String tableName() { return "courtbookrequests"; }

    public CourtBookRequest() { }

    public CourtBookRequest(String userId, String targetUserId, String courtBookId, Date timestamp)
    {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.courtBookId = courtBookId;
        this.timestamp = timestamp;
    }

    private String userId;
    private String targetUserId;
    private String courtBookId;
    private Date timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getCourtBookId() {
        return courtBookId;
    }

    public void setCourtBookId(String courtBookId) {
        this.courtBookId = courtBookId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public CompletableFuture<User> user()
    {
        return new User().getById(userId);
    }

    public CompletableFuture<User> targetUser()
    {
        return new User().getById(targetUserId);
    }

    public CompletableFuture<CourtBook> courtBook()
    {
        return new CourtBook().getById(courtBookId);
    }

    @Override
    public CompletableFuture<CourtBookRequest> save() {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<CourtBookRequest> getById(String id) {
        return Repository.getById(id, CourtBookRequest.class);
    }

    @Override
    public CompletableFuture<CourtBookRequest> softDelete() {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<CourtBookRequest>> list()
    {
        return Repository.list(CourtBookRequest.class);
    }

    public static CompletableFuture<Array<CourtBookRequest>> list(Function<CourtBookRequest, Boolean> where)
    {
        return Repository.list(CourtBookRequest.class, where);
    }
}
