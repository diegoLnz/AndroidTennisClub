package com.example.firsttry.businesslogic.functions;

import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.utilities.Array;

import java.util.Date;
import java.util.function.Function;

public class CourtBookFunctions
{
    public static Function<CourtBook, Boolean> isOverlapping(Date dateTime)
    {
        return booking
                -> booking.getStartsAt().compareTo(dateTime) < 0 && booking.getEndsAt().compareTo(dateTime) > 0;
    }

    public static Function<Court, Boolean> isAvailable(Array<CourtBook> overlappingBookings)
    {
        return court
                -> !overlappingBookings.any(booking -> booking.getCourtId().equals(court.getId()));
    }
}
