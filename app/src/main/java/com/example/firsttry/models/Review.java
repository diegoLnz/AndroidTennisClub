package com.example.firsttry.models;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.Repository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class Review extends Model
{

    @Override
    public String tableName() { return "reviews"; }

    public Review() { }

    public Review(String Text, String UserId, String ReviewerId, Date Timestamp, Integer Rating)
    {
        this.ReviewerId = ReviewerId;
        this.UserId = UserId;
        this.Timestamp = Timestamp;
        this.Text = Text;
        this.Rating = Rating;
    }

    private String ReviewerId;
    private String UserId;
    private Date Timestamp;
    private String Text;
    private Integer Rating;

    public String getReviewerId() { return ReviewerId; }

    public void setReviewerId(String value) { ReviewerId = value; }

    public String getUserId() { return UserId; }

    public void setUserId(String value) { UserId = value; }

    public Date getTimestamp() { return Timestamp; }

    public void setTimestamp(Date value) { Timestamp = value; }

    public String getText() { return Text; }

    public void setText(String value) { Text = value; }

    public Integer getRating() { return Rating; }

    public void setRating(Integer value) { Rating = value; }

    public CompletableFuture<User> user()
    {
        return DatabaseHandler.getById(this.getUserId(), new User().tableName(), User.class);
    }

    @Override
    public CompletableFuture<Review> save()
    {
        return Repository.saveOrUpdateEntity(this);
    }

    @Override
    public CompletableFuture<Review> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), Review.class)
                .thenApply(res -> res);
    }

    @Override
    public CompletableFuture<Review> softDelete()
    {
        setIsDeleted(true);
        return save();
    }

    @Override
    public CompletableFuture<Array<Review>> list()
    {
        return Repository.list(Review.class);
    }
}
