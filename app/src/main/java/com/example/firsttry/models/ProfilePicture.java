package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProfilePicture extends Model
{

    @Override
    public String tableName() { return "profilepictures"; }

    public ProfilePicture() { }

    public ProfilePicture(String url, String userId)
    {
        this.url = url;
        this.userId = userId;
    }

    private String url;
    private String userId;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public CompletableFuture<ProfilePicture> save() {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<ProfilePicture> getById(String id) {
        return Repository.getById(id, ProfilePicture.class);
    }

    @Override
    public CompletableFuture<ProfilePicture> softDelete() {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<ProfilePicture>> list()
    {
        return Repository.list(ProfilePicture.class);
    }

    public static CompletableFuture<Array<ProfilePicture>> list(Function<ProfilePicture, Boolean> predicate)
    {
        return Repository.list(ProfilePicture.class, predicate);
    }
}
