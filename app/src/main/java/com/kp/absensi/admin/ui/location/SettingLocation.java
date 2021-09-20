package com.kp.absensi.admin.ui.location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.R;

public class SettingLocation extends Fragment {

    TextInputLayout latitude, longitude, distance;
    TextInputEditText latText, longText, distanceText;
    Button save;

    private Context mContext;

    LinearLayout progressBar;

    LocationManager locationManager;
    boolean GpsStatus;

    double titikLatitude;
    double titikLongitude;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting_location, container, false);

        latitude = root.findViewById(R.id.latitude_input);
        longitude = root.findViewById(R.id.longitude_input);
        distance = root.findViewById(R.id.distance);
        save = root.findViewById(R.id.simpan_kordinat);
        latText = root.findViewById(R.id.latitude_text);
        longText = root.findViewById(R.id.longitude_text);
        distanceText = root.findViewById(R.id.distance_text);
        progressBar = root.findViewById(R.id.progresbar);
        latText.addTextChangedListener(latlong);
        longText.addTextChangedListener(latlong);
        distanceText.addTextChangedListener(latlong);
        showLatLong();
        layoutListener();
        GPSStatus();
        return root;
    }

    private void layoutListener(){
        save.setOnLongClickListener(view -> {
            if (!GpsStatus){
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                builder.setTitle("Location Manager")
                        .setMessage("Aktifkan lokasi untuk melihat titik lokasi anda!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })
                        .setCancelable(true)
                        .show();
            } else {
                setLatLong();
            }
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        GPSStatus();
    }

    private void showLatLong(){
        databaseReference.child("data").child("latlong").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String latitudeValue = snapshot.child("sLatitude").getValue().toString();
                    String longitudeValue = snapshot.child("sLongitude").getValue().toString();
                    String jarak = snapshot.child("sDistance").getValue().toString();

                    latitude.getEditText().setText(latitudeValue);
                    longitude.getEditText().setText(longitudeValue);
                    distance.getEditText().setText(jarak);
                } else {
                    Toast.makeText(mContext, "Data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLatLong(){
        String getlat = latitude.getEditText().getText().toString();
        String getLong = longitude.getEditText().getText().toString();
        String getDistance = distance.getEditText().getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin mengedit data ini?")
                .setPositiveButton("ya", (dialogInterface, i) -> {
                    DataKordinat dataKordinat = new DataKordinat(getlat, getLong, getDistance);
                    databaseReference.child("data").child("latlong").setValue(dataKordinat).addOnSuccessListener(unused -> {
                        Toast.makeText(mContext, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setCancelable(true);
        builder.show();
    }

    public void GPSStatus(){
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setLatLong(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi")
                .setMessage("Set titik lokasi anda saat ini sebagai lokasi absensi?")
                .setPositiveButton("ya", (dialogInterface, i) -> {
                    getCurrentLocation();
                })
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setCancelable(true);
        builder.show();
    }

    private TextWatcher latlong = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String latText = latitude.getEditText().getText().toString();
            String longText = longitude.getEditText().getText().toString();
            String distanceText = distance.getEditText().getText().toString();

            if (latText.isEmpty() && longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (!latText.isEmpty() && longText.isEmpty() && !distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (latText.isEmpty() && !longText.isEmpty() && !distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (!latText.isEmpty() && !longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (!latText.isEmpty() && longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (latText.isEmpty() && !longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
                save.setText("Simpan");
            } else if (latText.isEmpty() && longText.isEmpty() && !distanceText.isEmpty()){
                save.setEnabled(true);
                save.setText("Set lokasi saat ini");
                save.setOnClickListener(view -> {
                    if (!GpsStatus){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                        builder.setTitle("Location Manager")
                                .setMessage("Aktifkan lokasi untuk melihat titik lokasi anda!")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                })
                                .setCancelable(true)
                                .show();
                    } else {
                        setLatLong();
                    }
                });
            } else {
                save.setEnabled(true);
                save.setText("Simpan");
                save.setOnClickListener(view -> {
                    updateLatLong();
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(mContext).removeLocationUpdates(this);
                    if (locationResult.getLocations().size() > 0){
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        titikLatitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        titikLongitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                        String getDistance = distance.getEditText().getText().toString();

                        DataKordinat dataKordinat = new DataKordinat(String.valueOf(titikLatitude), String.valueOf(titikLongitude), getDistance);
                        databaseReference.child("data").child("latlong").setValue(dataKordinat).addOnSuccessListener(unused -> {
                            latitude.getEditText().setText(String.valueOf(titikLatitude));
                            longitude.getEditText().setText(String.valueOf(titikLongitude));
                            Toast.makeText(mContext, "Lokasi anda saat ini di set sebagai lokasi absensi karyawan", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(mContext, "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_SHORT).show();
                        });
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, Looper.getMainLooper());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext == null)
            mContext = context.getApplicationContext();
    }
}