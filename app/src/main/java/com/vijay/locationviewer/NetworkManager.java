package com.vijay.locationviewer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NetworkManager {
    private static NetworkManager networkManager = new NetworkManager();

    public static NetworkManager getInstance() {
        return networkManager;
    }

    public void toggleTracking(boolean shouldEnable, AsyncTaskListener listener) throws JSONException {
        String url = "https://fcm.googleapis.com/fcm/send";

        //Headers
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=AIzaSyAy9NHx6ZzseG_ONRrR7jcOxyo3canPCls");
        headers.put("Content-type", "application/json");

        JSONObject msgData = new JSONObject();
        msgData.put("tracking", String.valueOf(shouldEnable));

        JSONObject postParams = new JSONObject();
        postParams.put("data",msgData);
        postParams.put("to", "cef1xyvusQU:APA91bFK3YIZLDOmJnIE_wbKcm4D0MlgEBz8x7Xw6pafpSYq1eT_6kwYIFTgWPxvRRXLiZG5hBpnnytZARtbqmAWyB-lDoPk1Cj2cJ7LXw1Cjh8_9yCqczVHcUn0haxUWys7dWrN16DY");

        NetworkRequest networkManager = new NetworkRequest(url, headers, postParams.toString(), listener);
        networkManager.execute();
    }
}
