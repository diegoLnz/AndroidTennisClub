package com.example.firsttry.models;

import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class ClubData extends Model
{
    @Override
    public String tableName() { return "clubdata"; }

    public String Name;
    public String profilePicPath;

    public ClubData() { }

    public ClubData(String name, String profilePicPath)
    {
        this.Name = name;
        this.profilePicPath = profilePicPath;
    }

    public String getName() { return Name; }

    public void setName(String name) { this.Name = name; }

    public String getProfilePicPath() { return profilePicPath; }

    public void setProfilePicPath(String profilePicPath) { this.profilePicPath = profilePicPath; }

    @Override
    public CompletableFuture<ClubData> save()
    {
        return DatabaseHandler.saveOrUpdate(this).thenApply(result -> result
                .match(
                        success -> success,
                        failure -> null
                ));
    }

    @Override
    public CompletableFuture<ClubData> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), this.getClass())
                .thenApply(result -> result);
    }
}
