package com.example.firsttry.utilities;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NotificationSender
{
    public static void sendNotification(
            String token,
            String title,
            String body)
    {
        Request request = HttpManager.buildRequest(composeRequestBody(
                title,
                body,
                token));
        executeCall(request);
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
            messageObj.put("topic", token);
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
        try
        {
            client.newCall(request).execute();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
