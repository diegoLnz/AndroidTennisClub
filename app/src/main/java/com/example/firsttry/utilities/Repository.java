package com.example.firsttry.utilities;

import android.util.Log;

import com.example.firsttry.models.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

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
            Log.e("Repository", "list: ", e);
            return new CompletableFuture<>();
        }
    }

    public static <T extends Model> CompletableFuture<Array<T>> list(Class<T> clazz, Function<T, Boolean> where)
    {
        try {
            return DatabaseHandler.list(clazz.getDeclaredConstructor().newInstance().tableName(), clazz)
                    .thenApply(list -> list.where(where));
        } catch (IllegalAccessException
                 | InstantiationException
                 | InvocationTargetException
                 | NoSuchMethodException e)
        {
            Log.e("Repository", "list: ", e);
            return new CompletableFuture<>();
        }
    }
}
