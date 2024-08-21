package com.example.firsttry.businesslogic;

import com.example.firsttry.models.Report;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.concurrent.CompletableFuture;

public class ReportsBl
{
    public static CompletableFuture<Array<Report>> getReportsByUserId(String userId)
    {
        return DatabaseHandler.list(new Report().tableName(), Report.class)
                .thenApply(reports -> reports
                        .where(report -> report.getUserId().equals(userId) && !report.getIsDeleted()));
    }
}
