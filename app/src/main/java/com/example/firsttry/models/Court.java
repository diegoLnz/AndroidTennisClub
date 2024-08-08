package com.example.firsttry.models;

import com.example.firsttry.enums.CourtTypes;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class Court extends Model
{
    @Override
    public String tableName() { return "courts"; }

    public Court() { this.isDeleted = false; }

    public Court(String name, CourtTypes type)
    {
        this.Name = name;
        this.Type = type;
        this.isDeleted = false;
    }

    private String Name;
    private CourtTypes Type;
    private Boolean isDeleted;

    public String getName() { return Name; }

    public void setName(String name) { this.Name = name; }

    public CourtTypes getType() { return Type; }

    public void setType(CourtTypes type) { this.Type = type; }

    public Boolean getDeleted() { return isDeleted; }

    public void setDeleted(Boolean deleted) { this.isDeleted = deleted; }

    public CompletableFuture<Array<CourtBook>> relatedBookings()
    {
        return DatabaseHandler.list(this.tableName(), CourtBook.class)
                .thenApply(bookings -> bookings
                        .where(booking -> booking.getCourtId().equals(this.getId())));
    }

    @Override
    public CompletableFuture<Court> save()
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
    public CompletableFuture<Court> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), Court.class)
                .thenApply(court -> court);
    }
}
