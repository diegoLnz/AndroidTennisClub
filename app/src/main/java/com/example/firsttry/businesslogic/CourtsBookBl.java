package com.example.firsttry.businesslogic;

import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isAvailable;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isOverlapping;

import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CourtsBookBl
{
    public static CompletableFuture<Array<Court>> getAvailableCourts(Date dateTime)
    {
        return DatabaseHandler.list(new CourtBook().tableName(), CourtBook.class).thenCompose(bookings ->
        {
            Array<CourtBook> overlappingBookings = bookings
                    .where(isOverlapping(dateTime));

            return DatabaseHandler.list(new Court().tableName(), Court.class).thenApply(courts -> courts
                    .where(court -> !court.getDeleted())
                    .where(isAvailable(overlappingBookings)));
        });
    }
}
