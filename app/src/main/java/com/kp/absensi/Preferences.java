package com.kp.absensi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class Preferences {

    public static int currentVersionCode;
    public static FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String DATA_LOGIN = "status_login",
            DATA_NAMA = "nama", DATA_USERNAME = "username"
            , DATA_STATUS = "status", DATA_DIALOG = "dialog_show";

    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setDataNama(Context context, String data){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_NAMA,data);
        editor.apply();
    }

    public static String getDataNama(Context context){
        return getSharedPreferences(context).getString(DATA_NAMA,"");
    }

    public static String getDataUsername(Context context){
        return getSharedPreferences(context).getString(DATA_USERNAME, "");
    }

    public static void setDataUsername(Context context, String data){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_USERNAME,data);
        editor.apply();
    }

    public static void setDataLogin(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DATA_LOGIN,status);
        editor.apply();
    }

    public static boolean getDataLogin(Context context){
        return getSharedPreferences(context).getBoolean(DATA_LOGIN,false);
    }

    public static void setDataStatus(Context context, String data){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_STATUS,data);
        editor.apply();
    }

    public static String getDataStatus(Context context){
        return getSharedPreferences(context).getString(DATA_STATUS,"");
    }

    public static void setUpdateDialog(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DATA_DIALOG,status);
        editor.apply();
    }

    public static boolean getUpdateDialog(Context context){
        return getSharedPreferences(context).getBoolean(DATA_DIALOG,false);
    }

    public static void clearData(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_STATUS);
        editor.remove(DATA_NAMA);
        editor.remove(DATA_USERNAME);
        editor.remove(DATA_LOGIN);
        editor.remove(DATA_DIALOG);
        editor.apply();
    }

    public static void clearDataUpdateDialog(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_DIALOG);
        editor.apply();
    }

    public static void checkUpdate(Context context){
        currentVersionCode = Preferences.getCurrentVersionCode(context);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                final String new_version_code = remoteConfig.getString("new_version_code");
                if (Integer.parseInt(new_version_code) > Preferences.getCurrentVersionCode(context)){
                    Preferences.showUpdateDialog(context);
                }
            }
        });
    }

    public static void showUpdateDialog(Context context){
        databaseReference.child("data").child("updateURL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String deskripsi = snapshot.child("sDescription").getValue().toString();
                    String url = snapshot.child("sUrl").getValue().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Pembaruan Aplikasi Tersedia")
                            .setMessage(deskripsi)
                            .setPositiveButton("Update", (dialogInterface, i) -> {
                                try {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                } catch (Exception e){
                                    Toast.makeText(context.getApplicationContext(), "Terjadi Kesalahan, Coba lagi!", Toast.LENGTH_SHORT).show();
                                }
                            }).setNeutralButton("Ingat nanti", (dialogInterface, i) -> Preferences.setUpdateDialog(context, true)).setCancelable(true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static int getCurrentVersionCode(Context context){
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }
        return packageInfo.versionCode;
    }


}
