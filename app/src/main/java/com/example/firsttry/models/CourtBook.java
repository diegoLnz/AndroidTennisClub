package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CourtBook extends Model
{

    @Override
    public String tableName() { return "courtbooks"; }

    public CourtBook() { UserIds = new ArrayList<>(); }

    public CourtBook(
            List<String> UserIds,
            String CourtId,
            Date StartsAt,
            Date EndsAt)
    {
        this.UserIds = UserIds;
        this.CourtId = CourtId;
        this.StartsAt = StartsAt;
        this.EndsAt = EndsAt;
    }

    private List<String> UserIds;
    private String CourtId;
    private Date StartsAt;
    private Date EndsAt;

    public List<String> getUserIds() { return UserIds; }

    public void setUserIds(List<String> UserIds) { this.UserIds = UserIds; }

    public String getCourtId() { return CourtId; }

    public void setCourt(String CourtId) { this.CourtId = CourtId; }

    public Date getStartsAt() { return StartsAt; }

    public void setStartsAt(Date StartsAt) { this.StartsAt = StartsAt; }

    public Date getEndsAt() { return EndsAt; }

    public void setEndsAt(Date EndsAt) { this.EndsAt = EndsAt; }

    public void addUserId(String userId)
    {
        UserIds.add(userId);
    }

    public CompletableFuture<Array<User>> getUsers()
    {
        return DatabaseHandler.list(new User().tableName(), User.class)
                .thenApply(users -> users
                        .where(user -> this.getUserIds().contains(user.getId())));
    }

    @Override
    public CompletableFuture<CourtBook> save()
    {
        return this.getId() == null || this.getId().isEmpty()
                ?
                DatabaseHandler.list(this.tableName(), this.getClass())
                        .thenCompose(courts -> {
                            int lastId = courts.size();
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
    public CompletableFuture<CourtBook> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), CourtBook.class)
                .thenApply(book -> book);
    }
}
