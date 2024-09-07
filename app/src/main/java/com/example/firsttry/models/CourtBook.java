package com.example.firsttry.models;

import com.example.firsttry.enums.BookState;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.Repository;

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

    public CourtBook(
            String CourtId,
            Date StartsAt,
            Date EndsAt)
    {
        this.UserIds = new ArrayList<>();
        this.CourtId = CourtId;
        this.StartsAt = StartsAt;
        this.EndsAt = EndsAt;
    }

    private List<String> UserIds;
    private String CourtId;
    private Date StartsAt;
    private Date EndsAt;
    private BookState State = BookState.Available;

    public List<String> getUserIds() { return UserIds; }

    public void setUserIds(List<String> UserIds) { this.UserIds = UserIds; }

    public String getCourtId() { return CourtId; }

    public void setCourtId(String CourtId) { this.CourtId = CourtId; }

    public Date getStartsAt() { return StartsAt; }

    public void setStartsAt(Date StartsAt) { this.StartsAt = StartsAt; }

    public Date getEndsAt() { return EndsAt; }

    public void setEndsAt(Date EndsAt) { this.EndsAt = EndsAt; }

    public BookState getState() { return State; }

    public void setState(BookState state) { State = state; }

    public void addUserId(String userId)
    {
        UserIds.add(userId);
    }

    public CompletableFuture<Array<User>> users()
    {
        return DatabaseHandler.list(new User().tableName(), User.class)
                .thenApply(users -> users
                        .where(user -> this.getUserIds().contains(user.getId())));
    }

    public void calculateState()
    {
        int usersCount = this.getUserIds().size();

        if (usersCount == 0)
        {
            this.setState(BookState.Available);
            return;
        }

        if (usersCount <= 3)
        {
            this.setState(BookState.SemiBooked);
            return;
        }

        if (usersCount == 4)
            this.setState(BookState.Booked);
    }

    public CompletableFuture<CourtBook> saveCourtBook()
    {
        this.calculateState();
        return this.save();
    }

    @Override
    public CompletableFuture<CourtBook> save()
    {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<CourtBook> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), CourtBook.class)
                .thenApply(book -> book);
    }

    @Override
    public CompletableFuture<CourtBook> softDelete()
    {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<CourtBook>> list()
    {
        return Repository.list(CourtBook.class);
    }
}
