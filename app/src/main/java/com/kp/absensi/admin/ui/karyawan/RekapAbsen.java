package com.kp.absensi.admin.ui.karyawan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RekapAbsen extends AppCompatActivity {

    String idkaryawan;
    String eventDate;

    TextView namaKar, ketHadir, jamAbsen, ketAbsen, tanggalR;
    ImageButton nxt, prev;

    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
    DateFormat dateRekap = new SimpleDateFormat("ddMMyyyy");
    Calendar calendar = Calendar.getInstance();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_absen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idkaryawan = getIntent().getStringExtra("idKaryawan");

        namaKar = findViewById(R.id.nama_karyawan);
        ketHadir = findViewById(R.id.ket_hadir);
        jamAbsen = findViewById(R.id.jam_absen);
        ketAbsen = findViewById(R.id.ket_absen);
        nxt = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        tanggalR = findViewById(R.id.tanggal_rekap);

        setTanggal();
        layoutListener();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void layoutListener() {
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

        tanggalR.setOnClickListener(v -> {
            calendar.setTime(Calendar.getInstance().getTime());
            DatePickerDialog datePickerDialog = new DatePickerDialog(RekapAbsen.this, R.style.my_dialog_theme, date,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void showAbsen(){
        databaseReference.child(idkaryawan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nama = snapshot.child("sNama").getValue().toString();

                    namaKar.setText(nama);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(idkaryawan).child("sAbsensi").child(eventDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                        String kehadiran = snapshot.child("sKehadiran").getValue().toString();
                        String jam = snapshot.child("sJam").getValue().toString();
                        String Hadir = snapshot.child("sKet").getValue().toString();

                    if (kehadiran.equals("hadir")){
                        ketHadir.setText(kehadiran);
                        jamAbsen.setText(jam);
                        ketAbsen.setText("Telah Absen");
                        ketHadir.setTextColor(Color.GREEN);
                    } else if (kehadiran.equals("izin")){
                        ketHadir.setText(kehadiran);
                        jamAbsen.setText(jam);
                        ketAbsen.setText(Hadir);
                        ketHadir.setTextColor(ContextCompat.getColor(RekapAbsen.this, R.color.orange));
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

    private void setTanggal(){
        String curentDate = dateFormat.format(calendar.getTime());
        eventDate = dateRekap.format(calendar.getTime());
        tanggalR.setText(curentDate);
        seleksiAbsen();
        showAbsen();
    }

    private void seleksiAbsen(){
        String curentDate = dateFormat.format(calendar.getTime());
        String tgglNow = dateFormat.format(new Date().getTime());
        if (curentDate.equals(tgglNow)){
            nxt.setEnabled(false);
            nxt.setImageDrawable(ContextCompat.getDrawable(RekapAbsen.this, R.drawable.ic_next_disabled));
            ketHadir.setText("Belum Absen");
            ketHadir.setTextColor(ContextCompat.getColor(RekapAbsen.this, R.color.purple_500));
            jamAbsen.setText("-");
            ketAbsen.setText("-");
        } else {
            nxt.setEnabled(true);
            nxt.setImageDrawable(ContextCompat.getDrawable(RekapAbsen.this, R.drawable.ic_next));
            ketHadir.setText("X");
            ketHadir.setTextColor(Color.RED);
            jamAbsen.setText("-");
            ketAbsen.setText("Tidak ada data absen!");
        }
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