package com.example.firsttry.businesslogic;

import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isAvailable;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isOverlapping;

import com.example.firsttry.enums.BookState;
import com.example.firsttry.extensions.adapters.SearchedUserAdapterForBooks;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
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
                    .where(court -> !court.getIsDeleted())
                    .where(isAvailable(overlappingBookings)));
        });
    }

    public static CompletableFuture<Array<User>> getNotInvitedUsersByCourtBook(CourtBook courtBook)
    {
        return CourtBookRequest.list(courtBookRequest -> courtBookRequest.getCourtBookId().equals(courtBook.getId()))
                .thenCompose(requests ->
                        User.list(user -> !requests.any(req -> req.getTargetUserId().equals(user.getId()))));
    }
}
