package com.kp.absensi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    private static final String DATA_LOGIN = "status_login",
            DATA_NAMA = "nama", DATA_USERNAME = "username";

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

    public static void clearData(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_NAMA);
        editor.remove(DATA_USERNAME);
        editor.remove(DATA_LOGIN);
        editor.apply();
    }
}
