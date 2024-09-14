package com.example.firsttry.utilities;

import android.util.Log;

import com.example.firsttry.services.fcmdata.FcmData;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class AccessToken
{
    private static final String TAG = "AccessToken";
    private static final String FIREBASE_MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    private static String cachedToken;
    private static Date tokenExpiration;

    public static String getAccessToken()
    {
        if (isStillValidToken())
            return cachedToken;

        try
        {
            InputStream stream = new ByteArrayInputStream(FcmData.jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(FIREBASE_MESSAGING_SCOPE);
            googleCredentials.refreshIfExpired();

            com.google.auth.oauth2.AccessToken accessToken = googleCredentials.getAccessToken();
            cachedToken = accessToken.getTokenValue();
            tokenExpiration = accessToken.getExpirationTime();

            return cachedToken;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error getting access token: " + e.getMessage(), e);
            return null;
        }
    }

    private static Boolean isStillValidToken()
    {
        return cachedToken != null
                && tokenExpiration != null
                && new Date().before(tokenExpiration);
    }
}