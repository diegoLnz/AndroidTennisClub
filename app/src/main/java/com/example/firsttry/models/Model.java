package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;

import java.util.concurrent.CompletableFuture;

public abstract class Model implements IDatabaseModel
{
    private String id;
    private Boolean isDeleted = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public abstract <T extends Model> CompletableFuture<T> save();
    public abstract <T extends Model> CompletableFuture<T> getById(String id);
    public abstract <T extends Model> CompletableFuture<T> softDelete();
}
