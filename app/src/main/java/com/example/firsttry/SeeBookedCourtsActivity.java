package com.example.firsttry;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.firsttry.extensions.ValidatedCompatActivity;

public class SeeBookedCourtsActivity extends ValidatedCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_booked_courts);
    }
}
