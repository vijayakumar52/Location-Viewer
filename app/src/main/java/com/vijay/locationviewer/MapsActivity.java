package com.vijay.locationviewer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.vijay.locationviewer.firebase.Constants;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, View.OnClickListener {
    private final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    ClusterManager<ClusterMarker> clusterManager;

    DatabaseReference locationReference;

    Button clearHistory;
    FloatingActionButton fab;
    TextView count;

    Coordinates coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate called");

        coordinates = new Coordinates();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clearHistory = findViewById(R.id.remove_all);
        fab = findViewById(R.id.fab);
        count = findViewById(R.id.count);

        clearHistory.setOnClickListener(this);
        fab.setOnClickListener(this);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        locationReference = database.getReference(Constants.HISTORY);
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
        } else if (id == R.id.fab) {
            String title = getResources().getString(R.string.dialog_remove_location);
            String hint = getResources().getString(R.string.dialog_remove_no);
            String prefillNo = "1";
            String posText = getResources().getString(R.string.dialog_ok);
            String negText = getResources().getString(R.string.dialog_cancel);
            DialogUtils.getInstance().editTextDialog(this, title, hint, prefillNo, posText, negText, true, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (which == DialogAction.POSITIVE) {
                        Long value = null;
                        try {
                            String enteredText = ((TextInputLayout) dialog.getCustomView()).getEditText().getText().toString().trim();
                            value = Long.parseLong(enteredText);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (value != null) {
                            removeLocations(value);
                        }
                    }
                }
            });
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
        List<Double[]> list = new ArrayList<>();
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            LocationData locationData = messageSnapshot.getValue(LocationData.class);
            if (locationData != null) {
                Double[] coordinates = getLocationObject(locationData);
                list.add(coordinates);
            }
        }
        coordinates.setCoordinates(list);
        drawAllCluster();

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void addChild(Object value) {
        if (value != null) {
            Double[] location = getLocationObject((LocationData) value);
            if (location != null) {
                coordinates.addCoordinate(location);
                updateCluster(location);
            }
        }
    }

    private void removeChild() {

    }


    private void drawAllCluster() {
        LatLng position = null;
        if (clusterManager != null) {
            clusterManager.clearItems();
            List<Double[]> positions = coordinates.getCoordinates();
            if (positions.size() > 0) {
                for (int i = 0; i < positions.size(); i++) {
                    Double[] locations = positions.get(i);
                    LatLng latLng = new LatLng(locations[0], locations[1]);
                    ClusterMarker marker = new ClusterMarker(latLng);
                    clusterManager.addItem(marker);
                    position = latLng;
                }
            }

            clusterManager.cluster();
            if (position != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            }
        }
        updateCount();

    }


    private void updateCluster(Double[] doubles) {
        if (clusterManager != null) {
            LatLng latLng = new LatLng(doubles[0], doubles[1]);
            ClusterMarker marker = new ClusterMarker(latLng);
            clusterManager.addItem(marker);

            clusterManager.cluster();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }
        updateCount();
    }


    private void updateCount() {
        count.setText("Size : " + coordinates.getCoordinates().size());
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
