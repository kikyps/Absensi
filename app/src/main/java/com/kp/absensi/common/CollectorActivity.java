package com.kp.absensi.common;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.kp.absensi.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CollectorActivity extends AppCompatActivity {

    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        error = findViewById(R.id.error_text);
        error.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent()));
    }
}