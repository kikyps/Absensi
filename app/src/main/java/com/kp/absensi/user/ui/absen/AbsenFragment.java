package com.kp.absensi.user.ui.absen;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.Preferences;
import com.kp.absensi.R;
import com.kp.absensi.common.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AbsenFragment extends Fragment {

    TextInputLayout ket;
    TextView inhere, tanggal, jam, waktuAbsen;
    Button hadir, izin;
    ProgressBar progressBar;
    ImageButton nxt, prev;
    ImageView done;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    private Context mContext;

    String userLogin, eventDate;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");

    LocationManager locationManager;
    boolean GpsStatus;

//    double aoiLat = 0.4524095;
//    double aoiLong = 101.4141706;

    double aoiLat;
    double aoiLong;

    int distance;

    double latitude;
    double longitude;

    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
    DateFormat dateRekap = new SimpleDateFormat("ddMMyyyy");
    DateFormat jamFormat = new SimpleDateFormat("HH:mm:ss");
    DateFormat jamAbsen = new SimpleDateFormat("HH:mm");
    Calendar calendar = Calendar.getInstance();

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_absen, container, false);
        layoutBinding(root);
        setJam();
        setTanggal();
        buttonOncreate();
        progressBar.setVisibility(View.INVISIBLE);
        return root;
    }

    private void layoutBinding(View root){
        userLogin = Preferences.getDataUsername(requireContext());
        inhere = root.findViewById(R.id.ditempat);
        tanggal = root.findViewById(R.id.tanggal);
        jam = root.findViewById(R.id.jam);
        hadir = root.findViewById(R.id.hadir);
        izin = root.findViewById(R.id.izin);
        nxt = root.findViewById(R.id.next);

        prev = root.findViewById(R.id.previous);
        done = root.findViewById(R.id.icon_done);
        waktuAbsen = root.findViewById(R.id.jam_absen);
        progressBar = root.findViewById(R.id.progresbar);
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
//            dialogKeterangan();
            throw new RuntimeException("Boom!");
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
            setTanggal();
        };

        tanggal.setOnClickListener(v -> {
            calendar.setTime(Calendar.getInstance().getTime());
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.my_dialog_theme, date,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getLatlong();
        setTanggal();
    }

    private void getLatlong(){
        DatabaseReference dataLatlong = FirebaseDatabase.getInstance().getReference();
        dataLatlong.child("data").child("latlong").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    comeBack();
                } else if (snapshot.child("sLatitude").getValue().toString().isEmpty() && snapshot.child("sLongitude").getValue().toString().isEmpty() && !snapshot.child("sDistance").getValue().toString().isEmpty()){
                    comeBack();
                } else {
                    String latitudeValue = snapshot.child("sLatitude").getValue().toString();
                    String longitudeValue = snapshot.child("sLongitude").getValue().toString();
                    String distanceValue = snapshot.child("sDistance").getValue().toString();

                    aoiLat = Double.parseDouble(latitudeValue);
                    aoiLong = Double.parseDouble(longitudeValue);
                    distance = Integer.parseInt(distanceValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void comeBack(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Warning")
                .setMessage("Data kordinat kosong, isikan kordinat lokasi absen untuk menggunakan aplikasi ini!\n\nAtau anda bisa menghubungi admin.")
                .setPositiveButton("Oke", (dialogInterface, i) -> {
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    Preferences.clearData(requireContext());
                });
        builder.setCancelable(false);
        builder.show();
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
            progressBar.setVisibility(View.INVISIBLE);
            String keterangan = ket.getEditText().getText().toString();
            String jamAbsen = AbsenFragment.this.jamAbsen.format(new Date().getTime());
            String stathadir = "izin";

            if (keterangan.isEmpty()){
                Toast.makeText(mContext, "Isi Keterangan Terlebih Dahulu!", Toast.LENGTH_SHORT).show();
            } else {
                AbsenData absenData = new AbsenData(stathadir, jamAbsen, keterangan);
                databaseReference.child(userLogin).child("sAbsensi").child(eventDate).setValue(absenData).addOnSuccessListener(unused -> validIzin()).addOnFailureListener(e -> Toast.makeText(requireContext(), "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_SHORT).show());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setTanggal(){
        String curentDate = dateFormat.format(calendar.getTime());
        eventDate = dateRekap.format(calendar.getTime());
        tanggal.setText(curentDate);
        showAbsenToday();
        seleksiAbsen();
    }

    private void setJam(){
        //this method is used to refresh Time every Second
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run(){
                jam.setText(jamFormat.format(new Date().getTime()));
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }


    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(mContext).removeLocationUpdates(this);
                    if (locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                        if (!inLocation()) {
                            izin.setEnabled(true);
                            hadir.setClickable(true);
                            hadir.setBackgroundColor(Color.RED);
                            Toast.makeText(requireContext(), "Anda tidak berada di lokasi!", Toast.LENGTH_SHORT).show();
                        } else {
                            absenRekap();
                            validHadir();
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
        getLatlong();
        GPSStatus();
        setTanggal();
    }

    private boolean inLocation(){
        float[] results = new float[1];
        Location.distanceBetween(aoiLat, aoiLong, latitude, longitude, results);
        float distanceInMeters = results[0];
        return distanceInMeters < distance;
    }

    private void absenRekap(){
        String tggl = AbsenFragment.this.dateRekap.format(new Date().getTime());
        String jamAbsen = AbsenFragment.this.jamAbsen.format(new Date().getTime());
        String ketHadir = getString(R.string.ket_hadir);
        String hadir = "hadir";

        AbsenData absenData = new AbsenData(hadir, jamAbsen, ketHadir);
        databaseReference.child(userLogin).child("sAbsensi").child(tggl).setValue(absenData).addOnFailureListener(e -> Toast.makeText(mContext, "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_SHORT).show());
    }

    private void showAbsenToday(){
        databaseReference.child(userLogin).child("sAbsensi").child(eventDate).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   String kehadiran = snapshot.child("sKehadiran").getValue().toString();
                   String jamAbsen = snapshot.child("sJam").getValue().toString();
                   String ketHadir = snapshot.child("sKet").getValue().toString();

                   if (kehadiran.equals("hadir")){
                       waktuAbsen.setText(jamAbsen);
                       inhere.setText(ketHadir);
                       validHadir();
                   } else if (kehadiran.equals("izin")){
                       waktuAbsen.setText(jamAbsen);
                       inhere.setText(ketHadir);
                       validIzin();
                   }
               } else {
                   seleksiAbsen();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void validHadir(){
        done.setVisibility(View.VISIBLE);
        Drawable drawable = done.getDrawable();

        if (drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable){
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        izin.setEnabled(false);
        hadir.setClickable(false);
        hadir.setEnabled(true);
        hadir.setBackgroundColor(Color.GREEN);
        izin.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shot_black));
    }

    private void validIzin(){
        done.setVisibility(View.INVISIBLE);
        hadir.setEnabled(false);
        izin.setEnabled(true);
        izin.setClickable(false);
        izin.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange));
        hadir.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shot_black));
    }

    private void validNoData(){
        done.setVisibility(View.INVISIBLE);
        izin.setEnabled(false);
        hadir.setEnabled(false);
        hadir.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shot_black));
        izin.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shot_black));
    }

    private void belumAbsen(){
        done.setVisibility(View.INVISIBLE);
        izin.setEnabled(true);
        izin.setClickable(true);
        hadir.setEnabled(true);
        hadir.setClickable(true);
        izin.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_500));
        hadir.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_500));
    }

    private void seleksiAbsen(){
        String curentDate = dateFormat.format(calendar.getTime());
        String tgglNow = dateFormat.format(new Date().getTime());
        if (curentDate.equals(tgglNow)){
            nxt.setEnabled(false);
            nxt.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_next_disabled));
            belumAbsen();
            waktuAbsen.setText("-");
            inhere.setText("Anda belum absen hari ini!");
        } else {
            nxt.setEnabled(true);
            nxt.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_next));
            validNoData();
            waktuAbsen.setText("-");
            inhere.setText("Tidak ada data absen!");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext == null)
            mContext = context.getApplicationContext();
    }
}