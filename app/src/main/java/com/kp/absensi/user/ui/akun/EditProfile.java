package com.kp.absensi.user.ui.akun;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    TextInputLayout editNama, editusername, passwordLama, passwordBaru, confirmPass;
    Button simpan;
    String namaprof, usernameprof, passwordprof;

    TextInputEditText ambilNama, passlama, passbaru, passConfirm;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);
        editNama = findViewById(R.id.edit_nama);
        editusername = findViewById(R.id.edit_username);
        simpan = findViewById(R.id.save_edit_profil);
        passwordLama = findViewById(R.id.current_password);
        passwordBaru = findViewById(R.id.new_password);
        confirmPass = findViewById(R.id.confirm_password);

        ambilNama = findViewById(R.id.ambil_nama);
        passlama = findViewById(R.id.old_password);
        passbaru = findViewById(R.id.password_new);
        passConfirm = findViewById(R.id.ulang_password);
        buttonListener();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void buttonListener() {
        String usernameData = Preferences.getDataUsername(this);

        databaseReference.child("user").orderByChild("sUsername").equalTo(usernameData).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    namaprof = dataSnapshot.child("sNama").getValue().toString();
                    usernameprof = dataSnapshot.child("sUsername").getValue().toString();
                    passwordprof = dataSnapshot.child("sPassword").getValue().toString();

                    editNama.getEditText().setText(namaprof);
                    editusername.getEditText().setText(usernameprof);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        simpan.setOnClickListener(view -> {
            if (validForm()){
            } else {

            }
        });

        ambilNama.addTextChangedListener(gantiNama);
        passlama.addTextChangedListener(gantiNama);
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpan.setEnabled(false);
    }

    private TextWatcher gantiNama = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String val = passwordLama.getEditText().getText().toString().trim();
            String namaData = Preferences.getDataNama(getApplicationContext());
            String name = editNama.getEditText().getText().toString();

            if (name.equals(namaData) && val.isEmpty()){
                simpan.setEnabled(false);
                passwordLama.setError(null);
                passwordLama.setErrorEnabled(false);
                passwordBaru.setError(null);
                passwordBaru.setErrorEnabled(false);
                confirmPass.setError(null);
                confirmPass.setErrorEnabled(false);
            } else if (name.isEmpty()){
                simpan.setEnabled(false);
            } else if (!val.isEmpty()){
                simpan.setEnabled(true);
            } else {
                simpan.setEnabled(true);
                passwordLama.setError(null);
                passwordLama.setErrorEnabled(false);
                passwordBaru.setError(null);
                passwordBaru.setErrorEnabled(false);
                confirmPass.setError(null);
                confirmPass.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean validForm(){
        String val = passwordLama.getEditText().getText().toString().trim();
        String val2 = passwordBaru.getEditText().getText().toString().trim();
        String val3 = confirmPass.getEditText().getText().toString().trim();
        String name = editNama.getEditText().getText().toString();

        if (!name.isEmpty() && !val.isEmpty() && val2.isEmpty() && val3.isEmpty()){
            validatePassword();
            return false;
        } else {
            updateProfil();
            return true;
        }
    }

    private void updateProfil(){
        String usernameData = Preferences.getDataUsername(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit profile")
                .setMessage("Apakah anda yakin ingin merubah identitas profil anda?")
                .setPositiveButton("ya", (dialogInterface, i) -> {
                    String val = passwordLama.getEditText().getText().toString().trim();
                    String val2 = passwordBaru.getEditText().getText().toString().trim();
                    String val3 = confirmPass.getEditText().getText().toString().trim();
                    String name = editNama.getEditText().getText().toString();

                    if (!name.isEmpty() && val.isEmpty()){
                        Map<String, Object> updatesNama = new HashMap<>();
                        updatesNama.put("sNama", name);

                        databaseReference.child("user").child(usernameData).updateChildren(updatesNama).addOnSuccessListener(unused -> {
                            Preferences.setDataNama(this, name);
                            Toast.makeText(getApplicationContext(), "Data berhasil di edit", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat mengedit data, periksa koneksi internet dan coba lagi!", Toast.LENGTH_LONG).show();
                        });
                    } else if (!name.isEmpty() && !val.isEmpty()){
                        if (!val.equals(passwordprof)){
                            passwordLama.setError("Password lama anda salah!");
                        } else if (!validatePassword()){
                            passwordLama.setError(null);
                            passwordLama.setErrorEnabled(false);
                        } else {
                            Map<String, Object> updatesNamaPass = new HashMap<>();
                            updatesNamaPass.put("sNama", name);
                            updatesNamaPass.put("sPassword", val2);

                            databaseReference.child("user").child(usernameData).updateChildren(updatesNamaPass).addOnSuccessListener(unused -> {
                                Preferences.setDataNama(this, name);
                                Toast.makeText(getApplicationContext(), "Data berhasil di edit", Toast.LENGTH_SHORT).show();
                                passwordLama.getEditText().setText(null);
                                passwordBaru.getEditText().setText(null);
                                confirmPass.getEditText().setText(null);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat mengedit data, periksa koneksi internet dan coba lagi!", Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }).setNegativeButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.cancel();
        }).setCancelable(true).show();
    }

    private boolean validatePassword(){
        String val2 = passwordBaru.getEditText().getText().toString().trim();
        String val3 = confirmPass.getEditText().getText().toString().trim();

        String checkPassword = //"^" +
                "(.*[0-9].*)"; //+          //at least 1 digit
        //"(?=.*[a-z])" +          //at least 1 lower case letter
        //"(?=.*[A-Z])" +          //at least 1 upper case letter
        //"(?=.*[a-zA-Z])" +       //any letter
        //"(?=.*[@#$%^&+=-])" +    //at least 1 special character
        //"(?=\\S+$)" +            //no white spaces
        //".{6,}" +                //at least 6 characters
        //"$";

        if (val2.isEmpty() & val3.isEmpty()){
            passwordBaru.setError("Isi password baru");
            confirmPass.setError("Isi password baru");
            return false;
        } else if (!val2.matches(checkPassword)){
            passwordBaru.setError("Password harus memiliki angka!");
            return false;
        } else if (val2.length() < 6){
            passwordBaru.setError("Password terlalu pendek!");
            return false;
        } else if (!val2.equals(val3)){
            confirmPass.setError("Password anda tidak sama dengan password sebelumnya!");
            return false;
        } else  {
            passwordBaru.setError(null);
            passwordBaru.setErrorEnabled(false);
            confirmPass.setError(null);
            confirmPass.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 this.finish();
                 return true;
            case R.id.delete_account:
                onDeleted();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDeleted() {
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
}