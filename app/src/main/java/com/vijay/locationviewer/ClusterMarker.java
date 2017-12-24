package com.vijay.locationviewer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private LatLng position;
    private String mTitle;
    private String mSnippet;

    public ClusterMarker(LatLng latLng) {
        position = latLng;
    }

    public ClusterMarker(LocationData locationData) {
        position = new LatLng(locationData.getLatitude(), locationData.getLongitude());
    }


    public ClusterMarker(LatLng position, String mTitle, String mSnippet) {
        this.position = position;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }
}
