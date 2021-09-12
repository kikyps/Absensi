package com.kp.absensi.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.R;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout usernameValid, namaValid, passwordValid, confirmPassword;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    String sNama, sUsername, sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameValid = findViewById(R.id.signup_username);
        namaValid = findViewById(R.id.signup_nama);
        passwordValid = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_retype_password);

        buttonListener();


    }

    private void buttonListener(){

        Button register = findViewById(R.id.next2);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validateNama() | !validatePassword()){
                } else {
                    registerAccount();
                }
            }
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
        });
    }

    private void registerAccount(){
        String sNama = namaValid.getEditText().getText().toString();
        String sUsername = usernameValid.getEditText().getText().toString().trim();
        String sPassword = confirmPassword.getEditText().getText().toString().trim();

        Query checkUser = databaseReference.child("user").orderByChild("sUsername").equalTo(sUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(getApplicationContext(), "Akun dengan username " + sUsername + " sudah terdaftar!", Toast.LENGTH_SHORT).show();
                } else {
                    StoreUser storeUser = new StoreUser(sNama, sUsername,sPassword);
                    databaseReference.child("user").child(sUsername).setValue(storeUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Sukses")
                                    .setMessage("Akun anda berhasil dibuat klik login untuk masuk!")
                                    .setPositiveButton("login", (dialog, which) -> {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
                            builder.setCancelable(true);
                            builder.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
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
            usernameValid.setError("Username tidak boleh kosong");
            return false;
        } else if (val.length() > 20){
            usernameValid.setError("username terlalu panjang");
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

    private boolean validateNama(){
        String val = namaValid.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            namaValid.setError("Isi nama anda!");
            return false;
        } else {
            namaValid.setError(null);
            namaValid.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(){
        String val = passwordValid.getEditText().getText().toString().trim();
        String val2 = confirmPassword.getEditText().getText().toString().trim();

        String checkPassword = //"^" +
                "(.*[0-9].*)"; //+          //at least 1 digit
        //"(?=.*[a-z])" +          //at least 1 lower case letter
        //"(?=.*[A-Z])" +          //at least 1 upper case letter
        //"(?=.*[a-zA-Z])" +       //any letter
        //"(?=.*[@#$%^&+=-])" +    //at least 1 special character
        //"(?=\\S+$)" +            //no white spaces
        //".{6,}" +                //at least 6 characters
        //"$";

        if (val.isEmpty() & val2.isEmpty()){
            passwordValid.setError("Isi password anda!");
            confirmPassword.setError("Masukkan password yang sama!");
            return false;
        } else if (!val.matches(checkPassword)){
            passwordValid.setError("Password harus memiliki angka!");
            return false;
        } else if (val.length() < 6){
            passwordValid.setError("Password terlalu pendek!");
            return false;
        } else if (!val.equals(val2)){
            confirmPassword.setError("Password anda tidak sama dengan password sebelumnya!");
            return false;
        } else  {
            passwordValid.setError(null);
            passwordValid.setErrorEnabled(false);
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }
}