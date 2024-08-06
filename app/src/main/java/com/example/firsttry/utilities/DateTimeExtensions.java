package com.example.firsttry.utilities;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeExtensions
{
    public static Date convertToDate(String dateStr, String timeStr)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
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
}
