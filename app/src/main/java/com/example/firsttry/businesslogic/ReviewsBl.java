package com.example.firsttry.businesslogic;

import com.example.firsttry.models.Review;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class ReviewsBl
{
    public static CompletableFuture<Array<Review>> getReviewsByUserId(String userId)
    {
        return DatabaseHandler.list(new Review().tableName(), Review.class)
                .thenApply(reviews -> reviews
                        .where(review -> !review.getIsDeleted())
                        .where(review -> review.getUserId().equals(userId)));
    }
}
