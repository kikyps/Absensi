package com.kp.absensi.common;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.Preferences;
import com.kp.absensi.R;
import com.kp.absensi.admin.AdminActivity;
import com.kp.absensi.user.UserActivity;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameValid, passwordValid;
    boolean doubleBackToExitPressedOnce;
    private ProgressDialog progressDialog;
    private static final int REQUEST_PERMISSION_CODE = 111;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        usernameValid = findViewById(R.id.login_username);
        passwordValid = findViewById(R.id.login_password);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);  //progress dialog

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(v -> {
            if (!validateUsername() | !validatePassword()) {
            } else {
                turnLogin();
            }
        });

        Button register = findViewById(R.id.register_akun);
        register.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        });
    }


    private void turnLogin() {
        if (!Preferences.isConnected(this)){
            Preferences.dialogNetwork(this);
        } else {
            progressDialog.setMessage("Proses Login Tunggu Sebentar...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String input1 = usernameValid.getEditText().getText().toString();
            String input2 = passwordValid.getEditText().getText().toString();

            Query checkUser = databaseReference.child("user").orderByChild("sUsername").equalTo(input1);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String passwordFromDB = snapshot.child(input1).child("sPassword").getValue(String.class);
                        String nameFromDB = snapshot.child(input1).child("sNama").getValue(String.class);
                        String status = snapshot.child(input1).child("sStatus").getValue(String.class);

                        if (passwordFromDB.equals(input2)) {
                            if (status.equals("admin")) {
                                Preferences.setDataLogin(LoginActivity.this, true);
                                Preferences.setDataStatus(LoginActivity.this, status);
                                Preferences.setDataNama(LoginActivity.this, nameFromDB);
                                Preferences.setDataUsername(LoginActivity.this, input1);
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                            } else if (status.equals("user")) {
                                Preferences.setDataLogin(LoginActivity.this, true);
                                Preferences.setDataStatus(LoginActivity.this, status);
                                Preferences.setDataNama(LoginActivity.this, nameFromDB);
                                Preferences.setDataUsername(LoginActivity.this, input1);
                                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Password salah!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Akun tidak terdaftar!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        }

    private boolean validateUsername(){
        String val = usernameValid.getEditText().getText().toString().trim();
        String checkspace = "\\A\\w{1,20}\\z";      //white spaces validate

        if (val.isEmpty()){
            usernameValid.setError("Username tidak boleh kosong!");
            return false;
        } else if (val.length() > 20){
            usernameValid.setError("Username terlalu panjang!");
            return false;
        } else if (!val.matches(checkspace)){
            usernameValid.setError("Username tidak menggunakan spasi!");
            return false;
        } else {
            usernameValid.setError(null);
            usernameValid.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(){
        String val = passwordValid.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +          //at least 1 digit
                //"(?=.*[a-z])" +          //at least 1 lower case letter
                //"(?=.*[A-Z])" +          //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +       //any letter
                //"(?=.*[@#$%^&+=-])" +    //at least 1 special character
                "(?=\\S+$)" +            //no white spaces
                //".{4,}" +                //at least 4 characters
                "$";

        if (val.isEmpty()){
            passwordValid.setError("Username tidak boleh kosong!");
            return false;
        } else if (val.matches(checkPassword)){
            passwordValid.setError("Kata sandi harus memiliki 1 angka atau lebih mis:(katasandi12)");
            return false;
        } else {
            passwordValid.setError(null);
            passwordValid.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isPermissionGranted(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            // For Android 11 (R)
            return Environment.isExternalStorageManager();
        } else {
            // For Below
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStoreagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED && writeExternalStoreagePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermissions(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception exception){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296){
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){
                    Toast.makeText(this, "Permission granted in android 11 and above", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage && writeExternalStorage) {
                    Toast.makeText(this, "Permission granted in android 10 or below", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPermissionGranted()) {
            takePermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getDataLogin(this)) {
            if (Preferences.getDataStatus(this).equals("admin")){
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, UserActivity.class));
                finish();
            }
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