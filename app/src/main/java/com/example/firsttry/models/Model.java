package com.example.firsttry.models;

import java.util.concurrent.CompletableFuture;

public abstract class Model implements IDatabaseModel
{
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public abstract <T extends Model> CompletableFuture<T> save();
    public abstract <T extends Model> CompletableFuture<T> getById(String id);
}
