package com.example.firsttry.models;

import java.util.concurrent.CompletableFuture;

public abstract class Model implements IDatabaseModel
{
    private int id;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public abstract <T extends Model> CompletableFuture<T> save();
    public abstract <T extends Model> CompletableFuture<T> getById(int id);
}
