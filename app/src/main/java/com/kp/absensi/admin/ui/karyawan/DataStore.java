package com.kp.absensi.admin.ui.karyawan;

public class DataStore {

    String key;
    String sStatus;
    String sNama;

    public DataStore(String sStatus, String sNama, String sUsername, String sPassword) {
        this.sStatus = sStatus;
        this.sNama = sNama;
    }

    public DataStore(){
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
}
