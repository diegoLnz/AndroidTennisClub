package com.example.firsttry.utilities;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeExtensions
{
    public static Date now() { return new Date(); }

    public static Date convertToDate(
            String dateStr,
            String timeStr)
    {
        String format = timeStr.isEmpty()
                ? "dd/MM/yyyy"
                : "dd/MM/yyyy HH:mm";

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try
        {
            date = dateFormat.parse(dateStr + " " + timeStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static Date convertToDate(
            String dateStr,
            String timeStr,
            Boolean ignoreMinutes)
    {
        String format = timeStr.isEmpty()
                ? "dd/MM/yyyy"
                : "dd/MM/yyyy HH:mm";

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try
        {
            date = dateFormat.parse(dateStr + " " + timeStr);
            if (ignoreMinutes)
                date = getDateTimeWithoutMinutes(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    private static Date getDateTimeWithoutMinutes(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    public static String getTimeTextForBooking(Date dateTime)
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String startTime = timeFormat.format(dateTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        String endTime = timeFormat.format(calendar.getTime());

        return startTime + " - " + endTime;
    }

    public static Date addHours(Date dateTime, int hours)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
