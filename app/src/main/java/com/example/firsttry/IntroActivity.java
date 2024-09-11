package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firsttry.utilities.AccountManager;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
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