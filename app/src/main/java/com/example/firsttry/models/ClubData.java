package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.Repository;
import com.google.firebase.database.core.Repo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ClubData extends Model
{
    @Override
    public String tableName() { return "clubdata"; }

    private String Name;

    public ClubData() { }

    public ClubData(String name)
    {
        this.Name = name;
    }

    @Override
    public String getId() { return "1"; }

    public String getName() { return Name; }

    public void setName(String name) { this.Name = name; }

    public CompletableFuture<ClubPicture> currentPicture()
    {
        return Repository.list(ClubPicture.class)
                .thenApply(Array::firstOrDefault);
    }

    @Override
    public CompletableFuture<ClubData> save()
    {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<ClubData> getById(String id)
    {
        return Repository.getById(id, ClubData.class);
    }

    @Override
    public CompletableFuture<ClubData> softDelete()
    {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<ClubData>> list()
    {
        return Repository.list(ClubData.class);
    }

    public static CompletableFuture<Array<ClubData>> list(Function<ClubData, Boolean> predicate)
    {
        return Repository.list(ClubData.class, predicate);
    }
}
