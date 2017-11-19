package com.vijay.locationviewer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private LatLng position;

    public ClusterMarker( LatLng latLng ) {
        position = latLng;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition( LatLng position ) {
        this.position = position;
    }
}
