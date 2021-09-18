package com.kp.absensi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CheckMyLocation extends AppCompatActivity {

    TextView lat, lon, alamat, inhere;
    Button valid;
    ProgressBar progressBar;
    FusedLocationProviderClient LocationProvider;

    LocationManager locationManager ;
    boolean GpsStatus ;
    boolean doubleBackToExitPressedOnce;

    double aoiLat = 0.4524095;
    double aoiLong = 101.4141706;

    double latitude;
    double longitude;

    private ResultReceiver resultReceiver;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklocation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        resultReceiver = new AddressResultRecaiver(new Handler());

        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        alamat = findViewById(R.id.address);
        inhere = findViewById(R.id.ditempat);
        valid = findViewById(R.id.hadir);
        progressBar = findViewById(R.id.progresbar);
        LocationProvider = LocationServices.getFusedLocationProviderClient(this);

        valid.setOnClickListener(v -> {
            if (GpsStatus == false){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Manager")
                        .setMessage("Aktifkan lokasi untuk melihat titik lokasi anda!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })
                        .setCancelable(true)
                        .show();
                return;
            } else {
                getCurrentLocation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GPSStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void GPSStatus(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckMyLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            return;
        } else {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(CheckMyLocation.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0){
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        lat.setText(String.valueOf(latitude));
                        lon.setText(String.valueOf(longitude));

                        Location location = new Location(String.valueOf(alamat));
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        fetchAddressFromLatLong(location);

                        if (!inLocation()){
                            valid.setBackgroundColor(Color.RED);
                            inhere.setText("Anda tidak berada di lokasi!");
                        } else {
                            valid.setBackgroundColor(Color.GREEN);
                            inhere.setText("Anda berada di lokasi!");
                        }

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    private boolean inLocation(){
        float[] results = new float[1];
        Location.distanceBetween(aoiLat, aoiLong, latitude, longitude, results);
        float distanceInMeters = results[0];
        boolean isWithin10km = distanceInMeters < 100;
        return isWithin10km;
    }

    private void showLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckMyLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            return;
        } else {
            LocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null){
                        Geocoder geocoder = new Geocoder(CheckMyLocation.this, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            lat.setText(String.valueOf(addressList.get(0).getLatitude()));
                            lon.setText(String.valueOf(addressList.get(0).getLongitude()));
                            alamat.setText(String.valueOf(addressList.get(0).getAddressLine(0)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this, FetchAddressIntentServices.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultRecaiver extends ResultReceiver {

        public AddressResultRecaiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT){
                alamat.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            } else {
                Toast.makeText(CheckMyLocation.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
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