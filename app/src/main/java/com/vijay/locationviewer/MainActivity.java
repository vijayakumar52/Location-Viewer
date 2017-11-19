package com.vijay.locationviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {
    SwitchCompat switchCompat;
    AppCompatEditText intervalBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchCompat = findViewById(R.id.tracking_switch);
        intervalBox = findViewById(R.id.interval_box);
    }
}
