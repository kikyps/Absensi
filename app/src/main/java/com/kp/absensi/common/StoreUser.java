package com.kp.absensi.common;

public class StoreUser {

    String key;
    String sStatus;
    String sNama;
    String sUsername;
    String sPassword;

    public StoreUser(String sStatus, String sNama, String sUsername, String sPassword) {
        this.sStatus = sStatus;
        this.sNama = sNama;
        this.sUsername = sUsername;
        this.sPassword = sPassword;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getsStatus() {
        return sStatus;
    }

    public void setsStatus(String sStatus) {
        this.sStatus = sStatus;
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
