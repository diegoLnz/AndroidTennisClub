package com.example.firsttry;

import android.os.Bundle;

import com.example.firsttry.extensions.ValidatedCompatActivity;

public class GenericSettingsActivity extends ValidatedCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_settings);
    }
}
