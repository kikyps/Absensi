package com.kp.absensi.common;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.kp.absensi.Preferences;
import com.kp.absensi.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class CollectorActivity extends AppCompatActivity {

    TextView error;
    Button restart;
    boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        error = findViewById(R.id.error_text);
        restart = findViewById(R.id.restart_app);
        error.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent()));

        onClick();
    }

    private void onClick() {
        restart.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
            CustomActivityOnCrash.restartApplicationWithIntent(this, intent, config);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.error_copy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.copy_log) {
            copyErrorLog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyErrorLog() {
        ClipboardManager cm = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(error.getText().toString());
        Toast.makeText(this, getResources().getString(R.string.customactivityoncrash_error_activity_error_details_copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Preferences.clearDataUpdateDialog(this);
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double-click untuk keluar", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}