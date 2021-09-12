package com.kp.absensi.common;

import java.util.HashMap;
import java.util.Map;

public class StoreUser {

    String sStatus, sNama, sUsername, sPassword;

    public StoreUser(String sStatus, String sNama, String sUsername, String sPassword) {
        this.sStatus = sStatus;
        this.sNama = sNama;
        this.sUsername = sUsername;
        this.sPassword = sPassword;
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
