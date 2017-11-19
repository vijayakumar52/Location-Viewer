package com.vijay.locationviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vijay.locationviewer.firebase.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ValueEventListener {
    SwitchCompat switchCompat;
    AppCompatEditText intervalBox;

    AsyncTaskListener toggleTracking = new AsyncTaskListener() {
        @Override
        public void onTaskCompleted(String response, String extras) {
            if (response != null) {
                try {
                    JSONObject serverResponse = new JSONObject(response);
                    String successValue = serverResponse.optString("success");
                    if (successValue.equals("1")) {
                        ToastUtils.showToast(MainActivity.this, R.string.notification_sent);
                    } else {
                        ToastUtils.showToast(MainActivity.this, R.string.notification_failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToast(MainActivity.this, R.string.notification_error);
                }
            }else{
                ToastUtils.showToast(MainActivity.this, R.string.response_null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchCompat = findViewById(R.id.tracking_switch);
        intervalBox = findViewById(R.id.interval_box);


        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NetworkManager.getInstance().toggleTracking(switchCompat.isChecked(), toggleTracking);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference toggleReference = database.getReference(Constants.TRACKING_STATUS);
        toggleReference.addValueEventListener(this);

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Boolean trackingValue = dataSnapshot.getValue(Boolean.class);
        if (trackingValue != null) {
            if (trackingValue) {
                switchCompat.setChecked(true);
            } else {
                switchCompat.setChecked(false);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
