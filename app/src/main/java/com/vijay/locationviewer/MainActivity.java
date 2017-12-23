package com.vijay.locationviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vijay.locationviewer.firebase.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ValueEventListener {
    private final String TAG = MainActivity.class.getSimpleName();
    SwitchCompat switchCompat;
    AppCompatEditText intervalBox;
    Button changeIntervalButton;
    Button viewLocation;
    NetworkManager networkManager;

    DatabaseReference toggleReference;
    DatabaseReference timeIntervalReference;
    DatabaseReference deviceTokenReference;
    AsyncTaskListener firebaseCallback = new AsyncTaskListener() {
        @Override
        public void onTaskCompleted(String response, String extras) {
            Logger.d(TAG, "Firebase notification response : " + response);
            if (response != null) {
                try {
                    JSONObject serverResponse = new JSONObject(response);
                    String successValue = serverResponse.optString("success");
                    if (successValue.equals("1")) {
                        ToastUtils.showToast(MainActivity.this, R.string.notification_sent);
                        Logger.d(TAG, "Notification successfully sent!");
                    } else {
                        ToastUtils.showToast(MainActivity.this, R.string.notification_failed);
                        Logger.d(TAG, "Notification failed!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToast(MainActivity.this, R.string.notification_error);
                    Logger.d(TAG, "Notification error!");
                }
            } else {
                ToastUtils.showToast(MainActivity.this, R.string.response_null);
                Logger.d(TAG, "Notification response null!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);
        switchCompat = findViewById(R.id.tracking_switch);
        intervalBox = findViewById(R.id.interval_box);
        changeIntervalButton = findViewById(R.id.change_interval);
        viewLocation = findViewById(R.id.view_location);


        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (networkManager != null) {
                        networkManager.setTracking(switchCompat.isChecked(), firebaseCallback);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        changeIntervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = intervalBox.getText().toString();
                Long timeInMillis = null;
                try {
                    timeInMillis = Long.parseLong(text);
                } catch (NumberFormatException e) {
                    ToastUtils.showToast(MainActivity.this, getResources().getString(R.string.toast_no_format_exception));
                }
                if (timeInMillis != null) {
                    try {
                        if (networkManager != null) {
                            networkManager.setInterval(timeInMillis, firebaseCallback);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapActivity = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapActivity);
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        toggleReference = database.getReference(Constants.TRACKING_STATUS);
        toggleReference.addValueEventListener(this);

        timeIntervalReference = database.getReference(Constants.ALARM_INTERVAL);
        timeIntervalReference.addValueEventListener(this);

        deviceTokenReference = database.getReference(Constants.DEVICE_TOKEN);
        deviceTokenReference.addValueEventListener(this);

        Logger.d(TAG, "Firebase event listeners registered");
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        Logger.d(TAG, "onDataChange called : key = " + key + " value = " + dataSnapshot.getValue());
        if (Constants.TRACKING_STATUS.equals(key)) {
            Boolean trackingValue = dataSnapshot.getValue(Boolean.class);
            if (trackingValue != null) {
                if (trackingValue) {
                    switchCompat.setChecked(true);
                } else {
                    switchCompat.setChecked(false);
                }
            }
        } else if (Constants.ALARM_INTERVAL.equals(key)) {
            Long timeInterval = dataSnapshot.getValue(Long.class);
            if (timeInterval != null) {
                intervalBox.setText(String.valueOf(timeInterval));
            }
        } else if (Constants.DEVICE_TOKEN.equals(key)) {
            String token = dataSnapshot.getValue(String.class);
            if(token != null && !"".equals(token)) {
                networkManager = new NetworkManager(token);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy called");
        toggleReference.removeEventListener(this);
        timeIntervalReference.removeEventListener(this);
        deviceTokenReference.removeEventListener(this);

        Logger.d(TAG, "Firebase event listeners removed");
    }
}
