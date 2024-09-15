package com.example.firsttry.models;

import com.example.firsttry.enums.CourtBookRequestStatus;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.NotificationSender;
import com.example.firsttry.utilities.Repository;

import java.util.Date;
import java.util.List;
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

    public CourtBookRequest(String userId, String targetUserId, String courtBookId, Date timestamp, CourtBookRequestStatus status)
    {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.courtBookId = courtBookId;
        this.timestamp = timestamp;
        this.status = status;
    }

    private String userId;
    private String targetUserId;
    private String courtBookId;
    private Date timestamp;
    private CourtBookRequestStatus status = CourtBookRequestStatus.Pending;

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

    public CourtBookRequestStatus getStatus() {
        return status;
    }

    public void setStatus(CourtBookRequestStatus status) {
        this.status = status;
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

    public void updateCourtBook()
    {
        courtBook().thenAccept(this::addIdToBookListIfNotFull);
    }

    private void addIdToBookListIfNotFull(CourtBook book)
    {
        List<String> ids = book.getUserIds();

        if (ids.size() >= 4)
        {
            return;
        }

        ids.add(targetUserId);
        book.setUserIds(ids);
        book.safeSave();
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
