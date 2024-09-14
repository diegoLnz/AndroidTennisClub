package com.example.firsttry.utilities;

import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpManager
{
    public static final String firebaseMessagingUrl = "https://fcm.googleapis.com/v1/projects/tennisclub-8c146/messages:send";

    public static Request buildRequest(RequestBody body)
    {
        String bearer = AccessToken.getAccessToken();
        return new Request.Builder().url(HttpManager.firebaseMessagingUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + bearer)
                .addHeader("Content-Type", "application/json; UTF-8")
                .build();
    }
}
