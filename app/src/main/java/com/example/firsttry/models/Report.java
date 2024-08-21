package com.example.firsttry.models;

import com.example.firsttry.utilities.DatabaseHandler;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class Report extends Model
{
    @Override
    public String tableName() { return "reports"; }

    public Report() { }

    public Report(String Message, String UserId, String ReporterId, Date Timestamp)
    {
        this.Message = Message;
        this.UserId = UserId;
        this.ReporterId = ReporterId;
        this.Timestamp = Timestamp;
        this.IsDeleted = false;
    }

    private String Message;
    private String UserId;
    private String ReporterId;
    private Date Timestamp;
    private Boolean IsDeleted;

    public String getMessage() { return Message; }

    public void setMessage(String message) { Message = message; }

    public String getUserId() { return UserId; }

    public void setUserId(String userId) { UserId = userId; }

    public String getReporterId() { return ReporterId; }

    public void setReporterId(String reporterId) { ReporterId = reporterId; }

    public Date getTimestamp() { return Timestamp; }

    public void setTimestamp(Date timestamp) { Timestamp = timestamp; }

    public Boolean getIsDeleted() { return IsDeleted; }

    public void setIsDeleted(Boolean isDeleted) { IsDeleted = isDeleted; }

    @Override
    public CompletableFuture<Report> save()
    {
        return this.getId() == null || this.getId().isEmpty()
                ?
                DatabaseHandler.list(this.tableName(), this.getClass())
                        .thenCompose(reports -> {
                            int lastId = reports.size();
                            this.setId(String.valueOf(lastId + 1));
                            return DatabaseHandler.saveOrUpdate(this);
                        })
                        .thenApply(res -> res.match(
                                success -> success,
                                failure -> null
                        ))
                :
                DatabaseHandler.saveOrUpdate(this).thenApply(res -> res.match(
                        success -> success,
                        failure -> null
                ));
    }

    @Override
    public CompletableFuture<Report> getById(String id)
    {
        return DatabaseHandler.getById(id, this.tableName(), Report.class)
                .thenApply(res -> res);
    }
}
