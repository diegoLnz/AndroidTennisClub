package com.example.firsttry.utilities;

import com.example.firsttry.models.IDatabaseModel;
import com.example.firsttry.models.Model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CompletableFuture;

public final class DatabaseHandler
{
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static <T extends Model> CompletableFuture<Result<T, Exception>> save(T object)
    {
        DatabaseReference ref = database.getReference(object.tableName());
        CompletableFuture<Result<T, Exception>> future = new CompletableFuture<>();

        ref.setValue(object)
                .addOnSuccessListener(aVoid -> future.complete(Result.success(object)))
                .addOnFailureListener(e -> future.complete(Result.failure(e)));
        return future;
    }

    public static <T> CompletableFuture<T> getById(int id, String tableName, Class<T> clazz)
    {
        CompletableFuture<T> future = new CompletableFuture<>();
        DatabaseReference ref = database.getReference(tableName)
                .child(String.valueOf(id));

        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                if (snapshot.exists()) {
                    T object = snapshot.getValue(clazz);
                    future.complete(object);
                } else {
                    future.completeExceptionally(new Exception("Data not found"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception("Error retrieving data: " + error.getMessage()));
            }
        });

        return future;
    }
}
