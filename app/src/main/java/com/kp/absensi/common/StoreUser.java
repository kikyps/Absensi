package com.kp.absensi.common;

import java.util.HashMap;
import java.util.Map;

public class StoreUser {

    String key, sNama, sUsername, sPassword;

    public StoreUser(String sNama, String sUsername, String sPassword) {
        this.sNama = sNama;
        this.sUsername = sUsername;
        this.sPassword = sPassword;
    }

    public Map<String, Object> cnName(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("sNama", sNama);
        return result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getsNama() {
        return sNama;
    }

    public void setsNama(String sNama) {
        this.sNama = sNama;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }
}
