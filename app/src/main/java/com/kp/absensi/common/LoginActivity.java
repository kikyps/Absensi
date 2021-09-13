package com.kp.absensi.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.admin.AdminActivity;
import com.kp.absensi.MainActivity;
import com.kp.absensi.Preferences;
import com.kp.absensi.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameValid, passwordValid;
    boolean doubleBackToExitPressedOnce;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        usernameValid = findViewById(R.id.login_username);
        passwordValid = findViewById(R.id.login_password);

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                } else {
                    turnLogin();
                }
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
                        if (status.equals("admin")){
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password salah!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Akun tidak terdaftar!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getDataLogin(this)) {
            if (Preferences.getDataStatus(this).equals("admin")){
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
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