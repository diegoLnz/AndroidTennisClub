package com.example.firsttry.utilities;

import android.app.Activity;
import android.content.Intent;

public class ActivityHandler
{
    public static void LinkTo(Activity previous, Class next)
    {
        previous.startActivity(new Intent(previous, next));
        previous.finish();
    }
}
