package com.example.firsttry.utilities;

import com.example.firsttry.models.Model;

import java.util.concurrent.CompletableFuture;

public class Repository
{
    public static <T extends Model> CompletableFuture<T> saveOrUpdateEntity(T entity)
    {
        return entity.getId() == null || entity.getId().isEmpty()
                ?
                DatabaseHandler.list(entity.tableName(), entity.getClass())
                        .thenCompose(reports -> {
                            int lastId = reports.size();
                            entity.setId(String.valueOf(lastId + 1));
                            return DatabaseHandler.saveOrUpdate(entity);
                        })
                        .thenApply(res -> res.match(
                                success -> success,
                                failure -> null
                        ))
                :
                DatabaseHandler.saveOrUpdate(entity).thenApply(res -> res.match(
                        success -> success,
                        failure -> null
                ));
    }
}
