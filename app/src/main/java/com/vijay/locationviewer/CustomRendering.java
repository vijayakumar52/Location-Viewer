package com.vijay.locationviewer;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by vijay-3593 on 24/12/17.
 */

public class CustomRendering extends DefaultClusterRenderer<ClusterMarker> {
    public CustomRendering(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker markerItem, MarkerOptions markerOptions) {
        markerOptions.snippet(markerItem.getSnippet());
        markerOptions.title(markerItem.getTitle());
        super.onBeforeClusterItemRendered(markerItem, markerOptions);
    }
}
