package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firsttry.models.ClubData;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.GlideHelper;
import com.example.firsttry.utilities.ImageUploader;
import com.example.firsttry.utilities.NotificationSender;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.CompletableFuture;

public class IntroActivity extends AppCompatActivity
{
    private ClubData configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        NotificationSender.requirePermission(this);
        setContentView(R.layout.activity_intro);
        AccountManager.checkFcmToken();
        currentConfig().thenAccept(conf -> {
            configuration = conf != null
                    ? conf
                    : new ClubData();
            buildStartBtn();
        });
    }

    private CompletableFuture<ClubData> currentConfig()
    {
        return ClubData.list().thenApply(Array::firstOrDefault);
    }

    private void buildStartBtn()
    {
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> buildIntent());

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.append(" " + configuration.getName() + "!");

        ImageView logo = findViewById(R.id.logo);
        configuration.currentPicture()
                .thenAccept(pic -> {
                    if (pic == null)
                        return;
                    ImageUploader.getDownloadUrl(pic.getUrl())
                            .addOnSuccessListener(uri -> GlideHelper.setRoundedImage(
                                    logo,
                                    uri.toString(),
                                    this
                            ));
                });
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