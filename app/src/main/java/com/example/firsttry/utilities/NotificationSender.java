package com.example.firsttry.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationSender
{
    public static void sendNotification(
            String userId,
            String title,
            String body)
    {
        AccountManager.getFcmToken(userId).thenAccept(token
                -> NotificationSender.sendNotificationWithToken(
                token,
                title,
                body)
        );
    }

    private static void sendNotificationWithToken(
            String token,
            String title,
            String body)
    {
        HttpManager.buildMessageRequest(composeRequestBody(title, body, token), new HttpManager.RequestCallback()
        {
            @Override
            public void onRequestBuilt(Request request)
            {
                executeCall(request);
            }

            @Override
            public void onError(Exception e)
            {
                Log.e("HTTP", "Error: " + e.getMessage());
            }
        });
    }

    private static RequestBody composeRequestBody(
            String title,
            String body,
            String token
    )
    {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject payload = composeNotificationPayload(title, body, token);
        return RequestBody.create(payload.toString(), mediaType);
    }

    private static JSONObject composeNotificationPayload(
            String title,
            String body,
            String token
    )
    {
        JSONObject notificationObj = new JSONObject();
        JSONObject messageObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try
        {
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            messageObj.put("token", token);
            messageObj.put("notification", notificationObj);
            dataObj.put("message", messageObj);
        }
        catch (JSONException e)
        {
            Log.d("SendNotification", Objects.requireNonNull(e.getMessage()));
        }
        return dataObj;
    }

    private static void executeCall(Request request)
    {
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e)
            {
                Log.e("HTTP", "Request fallita: " + e.getMessage());
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response)
            { }
        });
    }
}
