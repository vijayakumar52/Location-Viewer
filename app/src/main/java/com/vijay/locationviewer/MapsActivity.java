package com.vijay.locationviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.vijay.androidutils.Logger;
import com.vijay.androidutils.ToastUtils;
import com.vijay.locationviewer.firebase.Constants;

import org.json.JSONException;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, View.OnClickListener {
    private final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    ClusterManager<ClusterMarker> clusterManager;

    DatabaseReference locationReference;

    Button clearHistory;
    TextView count;

    ClusterData clusterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate called");

        clusterData = new ClusterData();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clearHistory = findViewById(R.id.remove_all);
        count = findViewById(R.id.count);

        clearHistory.setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        locationReference = database.getReference(Constants.LOCATIONS);
        locationReference.addValueEventListener(this);
        Logger.d(TAG, "locationReference event registered");

        drawAllCluster();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.remove_all) {
            long maxCount = Constants.MAX_COUNT;
            removeLocations(maxCount);
        }
    }


    private void removeLocations(long count) {
        locationReference.setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                ToastUtils.showToast(MapsActivity.this, getResources().getString(R.string.toast_location_cleared));
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<LocationData> list = new ArrayList<>();
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            LocationData locationData = messageSnapshot.getValue(LocationData.class);
            if (locationData != null) {
                list.add(locationData);
            }
        }
        clusterData.setCoordinates(list);
        drawAllCluster();

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void addChild(Object value) {
        if (value != null) {
            LocationData data = (LocationData) value;
            clusterData.addCoordinate(data);
            updateCluster(data);
        }
    }

    private void removeChild() {

    }


    private void drawAllCluster() {
        LatLng position = null;
        if (clusterManager != null) {
            clusterManager.clearItems();
            List<LocationData> locations = clusterData.getCoordinates();
            if (locations.size() > 0) {
                for (int i = 0; i < locations.size(); i++) {
                    LocationData data = locations.get(i);
                    ClusterMarker marker = new ClusterMarker(data.getLatLng(), getRelativeTime(data.getTime()), getTime(data.getTime()));
                    clusterManager.addItem(marker);
                    position = data.getLatLng();
                }
            }

            clusterManager.cluster();
            if (position != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            }
        }
        updateCount();

    }

    private String getTime(Long millis) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        return df.format(new Date(millis));
    }

    private String getRelativeTime(Long millis) {
        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(new Date(millis));
    }


    private void updateCluster(LocationData locationData) {
        if (clusterManager != null) {
            ClusterMarker marker = new ClusterMarker(locationData);
            clusterManager.addItem(marker);

            clusterManager.cluster();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationData.getLatLng()));

        }
        updateCount();
    }


    private void updateCount() {
        count.setText("Size : " + clusterData.getCoordinates().size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy called");
        locationReference.removeEventListener(this);
        Logger.d(TAG, "locationReference event removed");
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
        clusterManager.setRenderer(new CustomRendering(this, mMap, clusterManager));
        mMap.setOnCameraChangeListener(clusterManager);

        drawAllCluster();

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

    public static Double[] getLocationObject(LocationData data) {
        Double[] coordinates = new Double[2];
        coordinates[0] = data.getLatitude();
        coordinates[1] = data.getLongitude();
        return coordinates;
    }
}
