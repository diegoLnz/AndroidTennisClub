package com.example.firsttry.models;

import com.example.firsttry.enums.CourtType;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class Court extends Model
{
    @Override
    public String tableName() { return "courts"; }

    public Court() { }

    public Court(String name, CourtType type)
    {
        this.Name = name;
        this.Type = type;
        setIsDeleted(false);
    }

    private String Name;
    private CourtType Type;

    public String getName() { return Name; }

    public void setName(String name) { this.Name = name; }

    public CourtType getType() { return Type; }

    public void setType(CourtType type) { this.Type = type; }

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

    @Override
    public CompletableFuture<Court> softDelete()
    {
        setIsDeleted(true);
        return save();
    }
}
