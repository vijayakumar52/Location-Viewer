package com.vijay.locationviewer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetworkRequest extends AsyncTask<String, Integer, String> {
    AsyncTaskListener asyncTaskListener;
    String url;
    HashMap<String, String> postParams;
    String postData;
    HashMap<String, String> header;

    public NetworkRequest(String url, HashMap<String, String> header, HashMap<String, String> postParams, AsyncTaskListener asyncTaskListener) {
        this.url = url;
        this.postParams = postParams;
        this.header = header;
        this.asyncTaskListener = asyncTaskListener;
    }

    public NetworkRequest(String url, HashMap<String, String> header, String postParams, AsyncTaskListener asyncTaskListener) {
        this.url = url;
        this.postData = postParams;
        this.header = header;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected String doInBackground(String... params) {


        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url);
        if (postData != null) {
            builder.post(RequestBody.create(MediaType.parse("application/json"), postData));
        }

        if (postParams != null) {
            builder.post(getRequestBody(postParams));
        }

        if (header != null) {
            builder.headers(getHeaders(header));
        }
        Request request = builder.build();
        Response response = null;
        String output = null;
        try {
            response = okHttpClient.newCall(request).execute();
            output = toString(response.body().byteStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    private RequestBody getRequestBody(HashMap<String, String> postParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        Set<String> iterator = postParams.keySet();
        for (String key : iterator) {
            String value = postParams.get(key);
            builder.addFormDataPart(key, value);
        }
        return builder.build();
    }

    private Headers getHeaders(HashMap<String, String> postParams) {
        Headers.Builder builder = new Headers.Builder();
        Set<String> iterator = postParams.keySet();
        for (String key : iterator) {
            String value = postParams.get(key);
            builder.add(key, value);
        }
        return builder.build();
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d("Vehicle Tracker", " Response : " + s);
        Log.d("Vehicle Tracker", "Coordinates sent");
        super.onPostExecute(s);
        asyncTaskListener.onTaskCompleted(s, null);
    }

    private String toString(InputStream inputStream) throws Exception {
        StringBuilder response = new StringBuilder();

        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); //No i18N
        String line;
        while ((line = r.readLine()) != null) {
            response.append(line).append('\n');
        }

        inputStream.close();

        return response.toString();
    }
}
