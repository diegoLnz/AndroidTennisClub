package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firsttry.utilities.AccountManager;
import com.google.firebase.messaging.FirebaseMessaging;

public class IntroActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        AccountManager.checkFcmToken();
        buildStartBtn();
    }

    private void buildStartBtn()
    {
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> buildIntent());
    }

    private void buildIntent()
    {
        MainActivity.setIntroCompleted(true);
        Intent intent = AccountManager.isLogged()
                ? new Intent(IntroActivity.this, MainActivity.class)
                : new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}