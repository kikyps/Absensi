package com.kp.absensi.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kp.absensi.Preferences;
import com.kp.absensi.R;
import com.kp.absensi.common.LoginActivity;
import com.kp.absensi.common.EditProfile;

public class AdminInfoActivity extends AppCompatActivity {

    TextView nama, username, status;
    Button deleteAdmin, updateProfile;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nama = findViewById(R.id.nama_admin);
        username = findViewById(R.id.username_admin);
        status = findViewById(R.id.status_admin);
        updateProfile = findViewById(R.id.update_admin);
        deleteAdmin = findViewById(R.id.delete_admin);

        layoutListener();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void layoutListener() {
        nama.setText(Preferences.getDataNama(this));
        username.setText(Preferences.getDataUsername(this));
        status.setText(Preferences.getDataStatus(this));

        updateProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfile.class));
        });

        deleteAdmin.setOnClickListener(v -> {
            onDeletedAdmin();
        });
    }

    private void onDeletedAdmin() {
        String namaData = Preferences.getDataNama(getApplicationContext());
        String usernameData = Preferences.getDataUsername(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Akun")
                .setMessage("Apakah anda yakin ingin menghapus akun " + namaData+ "? \n\njika anda menghapus akun ini semua data yang terekap pada akun ini akan di hapus dan tidak dapat di pulihkan!")
                .setPositiveButton("Hapus", (dialogInterface, i) -> {
                    databaseReference.child("user").child(usernameData).removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Akun anda berhasil di hapus", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        Preferences.clearData(getApplicationContext());
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi internet dan coba lagi!", Toast.LENGTH_LONG).show();
                    });
                }).setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.cancel();
        }).setCancelable(true).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}