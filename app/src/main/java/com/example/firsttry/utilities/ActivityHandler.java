package com.example.firsttry.utilities;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;

public class ActivityHandler
{
    public static void LinkTo(Activity previous, Class next)
    {
        previous.startActivity(new Intent(previous, next));
        previous.finish();
    }

    public static void LinkToWithExtra(Activity previous, Class next, String key, String value)
    {
        Intent intent = new Intent(previous, next);
        intent.putExtra(key, value);
        previous.startActivity(intent);
        previous.finish();
    }

    public static void LinkToWithExtras(Activity previous, Class next, HashMap<String, String> extras)
    {
        Intent intent = new Intent(previous, next);

        for (String key : extras.keySet())
        {
            intent.putExtra(key, extras.get(key));
        }

        previous.startActivity(intent);
        previous.finish();
    }

    public static void LinkToWithPreviousToast(Activity previous, Class next, String extraKey, String extraValue, String message)
    {
        Toast.makeText(previous, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(previous, next);
        intent.putExtra(extraKey, extraValue);
        previous.startActivity(intent);
        previous.finish();
    }

    public static void LinkToWithPreviousToast(Activity previous, Class next, String message)
    {
        Toast.makeText(previous, message, Toast.LENGTH_SHORT).show();
        previous.startActivity(new Intent(previous, next));
        previous.finish();
    }
}
