package com.example.firsttry.utilities;

import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpManager {

    public static final String firebaseMessagingUrl
            = "https://fcm.googleapis.com/v1/projects/tennisclub-8c146/messages:send";

    public interface RequestCallback
    {
        void onRequestBuilt(Request request);
        void onError(Exception e);
    }

    public static void buildMessageRequest(
            RequestBody body,
            RequestCallback callback)
    {
        AccessToken.getAccessTokenAsync(token -> {
            if (token != null)
            {
                try
                {
                    Request request = buildRequest(body, token);
                    callback.onRequestBuilt(request);
                }
                catch (Exception e)
                {
                    callback.onError(e);
                }
            }
            else
            {
                callback.onError(new Exception("Token is null"));
            }
        });
    }

    private static Request buildRequest(
            RequestBody body,
            String token)
    {
        return new Request.Builder()
                .url(HttpManager.firebaseMessagingUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
