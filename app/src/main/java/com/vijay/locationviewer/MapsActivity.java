package com.vijay.locationviewer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ClusterManager<ClusterMarker> clusterManager;
    int totalRecords = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getVehicleData();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraChangeListener(clusterManager);

    }

    private boolean isDeleteSuccess(JSONObject response) {
        JSONArray array = response.optJSONArray("formname");
        String name = array.optString(0);
        JSONObject object = array.optJSONObject(1);
        if ("Vehicle_Trace".equals(name)) {
            JSONArray array1 = object.optJSONArray("operation");
            String name1 = array1.optString(0);
            JSONObject object1 = array1.optJSONObject(1);
            if ("delete".equals(name1)) {
                String status = object1.optString("status");
                if ("Success".equals(status)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void getVehicleData() {
        AsyncTaskListener asyncTaskListener = new AsyncTaskListener() {
            @Override
            public void onTaskCompleted(String response, String extras) {
                if (response != null && mMap != null) {
                    try {
                        JSONObject receivedData = new JSONObject(response);
                        List<String[]> coordinates = getCoordinates(receivedData);
                        totalRecords = coordinates.size();
                        LatLng position = null;
                        if (coordinates.size() > 0) {
                            for (int i = 0; i < coordinates.size(); i++) {
                                String[] locations = coordinates.get(i);
                                LatLng latLng = new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]));
                                ClusterMarker marker = new ClusterMarker(latLng);
                                clusterManager.addItem(marker);
                                position = latLng;
                            }
                            clusterManager.cluster();
                            if (position != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        NetworkManager.getInstance().getRecords(asyncTaskListener);
    }

    private void drawCoordinates() throws JSONException {
        MarkerManager.Collection markers = clusterManager.getMarkerCollection();
        Collection<Marker> items = markers.getMarkers();
        Iterator<Marker> iterator = items.iterator();
        LatLng last = null;
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            LatLng position = marker.getPosition();
            mMap.addMarker(new MarkerOptions().position(position));
            last = position;
        }
        if (last != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
        }

    }

    List<String[]> getCoordinates(JSONObject response) throws JSONException {
        JSONArray array = response.optJSONArray("Vehicle_Trace");
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String[] coordinates = new String[2];
            JSONObject object = array.optJSONObject(i);
            coordinates[0] = object.optString("latitude");
            coordinates[1] = object.optString("longitude");
            list.add(coordinates);
        }
        return list;
    }
}
