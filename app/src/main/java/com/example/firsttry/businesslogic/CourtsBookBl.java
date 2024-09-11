package com.example.firsttry.businesslogic;

import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isAvailable;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isBookerOrInvited;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.isOverlapping;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.relatedAcceptedRequests;
import static com.example.firsttry.businesslogic.functions.CourtBookFunctions.stillNotPlayed;
import static com.example.firsttry.utilities.DateTimeExtensions.now;

import com.example.firsttry.enums.BookState;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public static CompletableFuture<Array<CourtBookRequest>> getRequestsByTargetUser(User user)
    {
        return CourtBookRequest.list(request -> request.getTargetUserId().equals(user.getId()) && !request.getIsDeleted())
                .thenCompose(requests -> {
                    List<CompletableFuture<CourtBookRequest>> futures = new ArrayList<>();

                    requests.forEach(req -> {
                        CompletableFuture<CourtBookRequest> future = req.courtBook().thenApply(book -> {
                            if (book.getStartsAt().after(new Date()) || book.getStartsAt().equals(new Date())) {
                                return req;
                            } else {
                                return null;
                            }
                        });
                        futures.add(future);
                    });

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<CourtBookRequest> validRequests = futures.stream()
                                        .map(CompletableFuture::join)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                                return new Array<>(validRequests);
                            });
                });
    }


    public static CompletableFuture<Array<User>> getNotInvitedUsersByCourtBook(CourtBook courtBook)
    {
        return CourtBookRequest.list(courtBookRequest -> courtBookRequest.getCourtBookId().equals(courtBook.getId()))
                .thenCompose(requests ->
                        User.list(user -> !requests.any(req -> req.getTargetUserId().equals(user.getId()))));
    }

    public static CompletableFuture<Array<User>> getBookerAndAcceptedInvitedUsersByCourtBook(CourtBook courtBook)
    {
        return CourtBookRequest.list(relatedAcceptedRequests(courtBook))
                .thenCompose(requests -> {
                   Array<User> users = new Array<>();
                   return User.list(isBookerOrInvited(courtBook, requests))
                           .thenApply(u -> {
                               users.add(u);
                               return users;
                           });
                });
    }

    public static CompletableFuture<Array<CourtBook>> getCourtBooksByUser(User user)
    {
        return CourtBook.list(stillNotPlayed(user));
    }

    public static CompletableFuture<CourtBook> deleteCourtBookAndItsRequests(CourtBook courtBook)
    {
        deleteRequestsByCourtBook(courtBook);
        return courtBook.softDelete();
    }

    public static void deleteRequestsByCourtBook(CourtBook courtBook)
    {
        CourtBookRequest.list(request -> request.getCourtBookId().equals(courtBook.getId()))
                .thenAccept(requests -> requests.forEach(CourtBookRequest::softDelete));
    }

}
