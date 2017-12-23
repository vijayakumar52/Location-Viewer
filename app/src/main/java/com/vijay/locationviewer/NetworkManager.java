package com.vijay.locationviewer;

import com.vijay.locationviewer.firebase.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NetworkManager {
    String deviceToken;

    public NetworkManager(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    private JSONObject getDefaultNotificationContent(JSONObject data) throws JSONException {
        JSONObject postParams = new JSONObject();
        postParams.put("data", data);
        postParams.put("to", deviceToken);
        return postParams;
    }

    private HashMap<String, String> getDefaultHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=AIzaSyAy9NHx6ZzseG_ONRrR7jcOxyo3canPCls");
        headers.put("Content-type", "application/json");
        return headers;
    }

    public void setTracking(boolean enable, AsyncTaskListener listener) throws JSONException {
        String url = "https://fcm.googleapis.com/fcm/send";

        HashMap<String, String> header = getDefaultHeader();

        JSONObject msgData = new JSONObject();
        msgData.put(Constants.NOTIFICATION_TRACKING_STATUS, String.valueOf(enable));

        JSONObject postParams = getDefaultNotificationContent(msgData);

        NetworkRequest networkManager = new NetworkRequest(url, header, postParams.toString(), listener);
        networkManager.execute();
    }

    public void setInterval(Long timeInMillis, AsyncTaskListener listener) throws JSONException {
        String url = "https://fcm.googleapis.com/fcm/send";

        HashMap<String, String> header = getDefaultHeader();

        JSONObject msgData = new JSONObject();
        msgData.put(Constants.NOTIFICATION_ALARM_INTERVAL, String.valueOf(timeInMillis));

        JSONObject postParams = getDefaultNotificationContent(msgData);

        NetworkRequest networkManager = new NetworkRequest(url, header, postParams.toString(), listener);
        networkManager.execute();
    }

}
