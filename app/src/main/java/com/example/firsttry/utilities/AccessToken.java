package com.example.firsttry.utilities;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.firsttry.services.fcmdata.FcmData;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccessToken
{
    private static String cachedToken = null;
    private static Long tokenExpiration = null;
    private static final String FIREBASE_MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String TAG = "AccessToken";

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface Callback
    {
        void onTokenReceived(String token);
    }

    public static void getAccessTokenAsync(Callback callback)
    {
        executorService.submit(() ->
        {
            String token = getAccessToken();
            new Handler(Looper.getMainLooper()).post(() -> callback.onTokenReceived(token));
        });
    }

    private static String getAccessToken()
    {
        if (isStillValidToken())
        {
            return cachedToken;
        }

        try
        {
            InputStream stream = new ByteArrayInputStream(FcmData.jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(FIREBASE_MESSAGING_SCOPE);
            googleCredentials.refreshIfExpired();

            com.google.auth.oauth2.AccessToken accessToken = googleCredentials.getAccessToken();
            cachedToken = accessToken.getTokenValue();
            tokenExpiration = accessToken.getExpirationTime().getTime();

            return cachedToken;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error getting access token: " + e.getMessage(), e);
            return null;
        }
    }

    private static boolean isStillValidToken()
    {
        return cachedToken != null
                && tokenExpiration != null
                && System.currentTimeMillis() < tokenExpiration;
    }
}