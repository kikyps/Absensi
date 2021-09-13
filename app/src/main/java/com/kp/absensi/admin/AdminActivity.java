package com.kp.absensi.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.Preferences;
import com.kp.absensi.R;
import com.kp.absensi.common.LoginActivity;

public class AdminActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce;

    TextInputLayout latitude, longitude;
    TextInputEditText latText, longText;
    Button save;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        latitude = findViewById(R.id.latitude_input);
        longitude = findViewById(R.id.longitude_input);
        save = findViewById(R.id.simpan_kordinat);
        latText = findViewById(R.id.latitude_text);
        longText = findViewById(R.id.longitude_text);
        latText.addTextChangedListener(latlong);
        longText.addTextChangedListener(latlong);
        buttonListener();
        showLatLong();
    }

    private void buttonListener() {
        save.setOnClickListener(view -> {
            setLatLong();
        });
    }

    private void showLatLong(){
        databaseReference.child("data").child("latlong").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String latitudeValue = snapshot.child("sLatitude").getValue().toString();
                    String longitudeValue = snapshot.child("sLongitude").getValue().toString();
                    latitude.getEditText().setText(latitudeValue);
                    longitude.getEditText().setText(longitudeValue);
                } else {
                    Toast.makeText(AdminActivity.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLatLong(){
        String getlat = latitude.getEditText().getText().toString();
        String getLong = longitude.getEditText().getText().toString();

        DataKordinat dataKordinat = new DataKordinat(getlat, getLong);
        databaseReference.child("data").child("latlong").setValue(dataKordinat).addOnSuccessListener(unused -> {
            Toast.makeText(AdminActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(AdminActivity.this, "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_SHORT).show();
        });
    }

    private TextWatcher latlong = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String latText = latitude.getEditText().getText().toString();
            String longText = longitude.getEditText().getText().toString();

            if (latText.isEmpty() && longText.isEmpty()){
                save.setEnabled(false);
            } else if (!latText.isEmpty() && longText.isEmpty()){
                save.setEnabled(false);
            } else if (latText.isEmpty() && !longText.isEmpty()){
                save.setEnabled(false);
            } else {
                save.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                onLogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onLogOut() throws Resources.NotFoundException{
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Anda yakin ingin logout?")
                .setPositiveButton("Ya", (dialogInterface, i) -> {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Preferences.clearData(getApplicationContext());
                    finish();
                })
                .setNegativeButton("tidak", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
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