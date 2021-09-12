package com.kp.absensi.ui.absen;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kp.absensi.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AbsenFragment extends Fragment {

    TextInputLayout ket;
    String keterangan;

    TextView inhere, tanggal, jam;
    Button hadir, izin;
    ProgressBar progressBar;
    FusedLocationProviderClient LocationProvider;
    ImageButton nxt, prev;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");

    LocationManager locationManager ;
    boolean GpsStatus ;

    double aoiLat = 0.4524095;
    double aoiLong = 101.4141706;

    double latitude;
    double longitude;

    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
    DateFormat jamFormat = new SimpleDateFormat("HH:mm:ss");
    Calendar calendar = Calendar.getInstance();

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_absen, container, false);
        layoutBinding(root);
        setJam();
        setTanggal();
        buttonOncreate();
        return root;
    }

    private void layoutBinding(View root){
        inhere = root.findViewById(R.id.ditempat);
        tanggal = root.findViewById(R.id.tanggal);
        jam = root.findViewById(R.id.jam);
        hadir = root.findViewById(R.id.hadir);
        izin = root.findViewById(R.id.izin);
        nxt = root.findViewById(R.id.next);
        prev = root.findViewById(R.id.previous);
        progressBar = root.findViewById(R.id.progresbar);
        LocationProvider = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    private void buttonOncreate(){
        hadir.setOnClickListener(v -> {
            if (!GpsStatus){
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Location Manager")
                        .setMessage("Aktifkan lokasi untuk melihat titik lokasi anda!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })
                        .setCancelable(true)
                        .show();
            } else {
                getCurrentLocation();
            }
        });

        izin.setOnClickListener(v -> {
            dialogKeterangan();
        });

        nxt.setOnClickListener(v -> {
            calendar.add(Calendar.DATE, 1);
            setTanggal();
        });

        prev.setOnClickListener(v -> {
            calendar.add(Calendar.DATE, -1);
            setTanggal();
        });

        DatePickerDialog.OnDateSetListener date = (datePicker, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTgl();
        };

        tanggal.setOnClickListener(v -> {
            calendar.setTime(Calendar.getInstance().getTime());
            new DatePickerDialog(requireContext(), R.style.my_dialog_theme, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateTgl() {
        Date today = calendar.getTime();
        tanggal.setText(dateFormat.format(today));
    }

    private void dialogKeterangan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.form_izin, null);
        builder.setView(dialogView)
                .setTitle("Izin")
                .setCancelable(false);
        ket = dialogView.findViewById(R.id.edit_izin);
        builder.setPositiveButton("Submit", (dialog, which) -> {
           keterangan = ket.getEditText().getText().toString();
           hadir.setEnabled(false);
           izin.setClickable(false);
           izin.setBackgroundColor(getResources().getColor(R.color.orange));
           dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
           dialog.cancel();
        });
        builder.show();
    }

    private void setTanggal(){
        String curentDate = dateFormat.format(calendar.getTime());
        tanggal.setText(curentDate);
    }

    private void setJam(){
        //this method is used to refresh Time every Second
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run(){
                jam.setText(AbsenFragment.this.jamFormat.format(new Date().getTime()));
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(getContext()).removeLocationUpdates(this);
                    if (locationResult.getLocations().size() > 0){
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                        if (!inLocation()){
                            izin.setEnabled(true);
                            hadir.setClickable(true);
                            hadir.setBackgroundColor(Color.RED);
                            inhere.setText("Anda tidak berada di lokasi!");
                        } else {
                            izin.setEnabled(false);
                            hadir.setClickable(false);
                            hadir.setBackgroundColor(Color.GREEN);
                            inhere.setText("Anda berada di lokasi!");
                        }
                    }
                        progressBar.setVisibility(View.INVISIBLE);
                }
            }, Looper.getMainLooper());
        }
    }

    public void GPSStatus(){
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        GPSStatus();
    }

    private boolean inLocation(){
        float[] results = new float[1];
        Location.distanceBetween(aoiLat, aoiLong, latitude, longitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < 100;
    }
}