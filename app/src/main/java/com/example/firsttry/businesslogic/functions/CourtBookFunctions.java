package com.example.firsttry.businesslogic.functions;

import static com.example.firsttry.utilities.DateTimeExtensions.now;

import com.example.firsttry.enums.BookState;
import com.example.firsttry.enums.CourtBookRequestStatus;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.Array;

import java.util.Date;
import java.util.function.Function;

public class CourtBookFunctions
{
    public static Function<CourtBook, Boolean> isOverlapping(Date dateTime)
    {
        return booking
                -> (booking.getStartsAt().before(dateTime) || booking.getStartsAt().equals(dateTime)
                && (booking.getEndsAt().after(dateTime) || booking.getEndsAt().equals(dateTime)));
    }

    public static Function<Court, Boolean> isAvailable(Array<CourtBook> overlappingBookings)
    {
        return court
                -> !overlappingBookings.any(isAssignedToCourt(court));
    }

    public static Function<CourtBook, Boolean> isAssignedToCourt(Court court)
    {
        return booking
                -> booking.getCourtId().equals(court.getId());
    }

    public static Function<CourtBookRequest, Boolean> relatedAcceptedRequests(CourtBook courtBook)
    {
        return courtBookRequest ->
                courtBookRequest.getCourtBookId().equals(courtBook.getId())
                        && (courtBookRequest.getStatus().equals(CourtBookRequestStatus.Accepted));
    }

    public static Function<User, Boolean> isBookerOrInvited(CourtBook courtBook, Array<CourtBookRequest> requests)
    {
        return user -> user.getId().equals(courtBook.getBookerId())
                || requests.any(req -> req.getTargetUserId().equals(user.getId()));
    }

    public static Function<CourtBook, Boolean> stillNotPlayed(User user)
    {
        return book -> (book.getStartsAt().equals(now())
                || book.getStartsAt().after(now()))
                && book.getBookerId().equals(user.getId())
                && !book.getIsDeleted();
    }
}
