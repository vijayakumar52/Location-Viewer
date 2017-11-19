package com.vijay.locationviewer;

import java.util.HashMap;


public class NetworkManager {
    private static NetworkManager networkManager = new NetworkManager();
    public static NetworkManager getInstance(){
        return networkManager;
    }

    public void getRecords(AsyncTaskListener listener){

        String url = "https://creator.zoho.com/api/json/vehicle-tracker/view/Vehicle_Trace_Report";
        String authToken = "6185ed36adc4f3fa03f66305cc720789";
        String scope = "creatorapi";

        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("authtoken",authToken);
        postParams.put("scope",scope);
        postParams.put("raw","true");
        postParams.put("zc_ownername","vijayakumar12");

        new NetworkRequest(url,postParams,listener).execute();

    }

    public void deleteRecord(AsyncTaskListener listener){
        String url = "https://creator.zoho.com/api/vijayakumar12/json/vehicle-tracker/form/Vehicle_Trace/record/delete/";
        String authToken = "6185ed36adc4f3fa03f66305cc720789";
        String scope = "creatorapi";

        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("authtoken",authToken);
        postParams.put("scope",scope);
        postParams.put("criteria","time != 0");

        new NetworkRequest(url,postParams,listener).execute();
    }

}
