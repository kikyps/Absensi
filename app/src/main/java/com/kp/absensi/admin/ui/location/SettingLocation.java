package com.kp.absensi.admin.ui.location;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
        latText.addTextChangedListener(latlong);
        longText.addTextChangedListener(latlong);
        distanceText.addTextChangedListener(latlong);
        buttonListener();
        showLatLong();
        return root;
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

    private void setLatLong(){
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
            } else if (!latText.isEmpty() && longText.isEmpty() && !distanceText.isEmpty()){
                save.setEnabled(false);
            } else if (latText.isEmpty() && !longText.isEmpty() && !distanceText.isEmpty()){
                save.setEnabled(false);
            } else if (!latText.isEmpty() && !longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
            } else if (!latText.isEmpty() && longText.isEmpty() && distanceText.isEmpty()){
                save.setEnabled(false);
            } else if (latText.isEmpty() && !longText.isEmpty() && distanceText.isEmpty()){
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext == null)
            mContext = context.getApplicationContext();
    }
}