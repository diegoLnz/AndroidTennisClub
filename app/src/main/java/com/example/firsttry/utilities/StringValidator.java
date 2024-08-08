package com.example.firsttry.utilities;

public class StringValidator
{
    private static String dayPattern = "^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$";
    private static String timePattern = "^([01][0-9]|2[0-3]):[0-5][0-9]$";

    public static Boolean matchDate(String input)
    {
        return match(input, ValidatorType.DATE);
    }

    public static Boolean matchTime(String input)
    {
        return match(input, ValidatorType.TIME);
    }

    public static Boolean match(String input, ValidatorType validatorType)
    {
        switch (validatorType)
        {
            case DATE:
                return input.matches(dayPattern);
            case TIME:
                return input.matches(timePattern);
            default:
                return false;
        }
    }
}