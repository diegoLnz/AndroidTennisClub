package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ClubPicture extends Model
{

    @Override
    public String tableName() { return "clubpictures"; }

    public ClubPicture() { }

    public ClubPicture(String url)
    {
        this.url = url;
    }

    private String url;

    @Override
    public String getId() { return "1"; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public CompletableFuture<ClubPicture> save()
    {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<ClubPicture> getById(String id) {
        return Repository.getById(id, ClubPicture.class);
    }

    @Override
    public CompletableFuture<ClubPicture> softDelete() {
        setIsDeleted(true);
        return save();
    }

    public static CompletableFuture<Array<ClubPicture>> list()
    {
        return Repository.list(ClubPicture.class);
    }

    public static CompletableFuture<Array<ClubPicture>> list(Function<ClubPicture, Boolean> predicate)
    {
        return Repository.list(ClubPicture.class, predicate);
    }
}
