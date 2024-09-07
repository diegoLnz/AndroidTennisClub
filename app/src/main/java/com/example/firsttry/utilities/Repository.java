package com.example.firsttry.utilities;

import com.example.firsttry.models.Model;

import java.lang.reflect.InvocationTargetException;
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

    public static <T extends Model> CompletableFuture<Array<T>> list(Class<T> clazz)
    {
        try {
            return DatabaseHandler.list(clazz.getDeclaredConstructor().newInstance().tableName(), clazz);
        } catch (IllegalAccessException
                 | InstantiationException
                 | InvocationTargetException
                 | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
}
