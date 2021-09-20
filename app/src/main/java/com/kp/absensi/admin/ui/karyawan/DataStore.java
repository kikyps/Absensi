package com.kp.absensi.admin.ui.karyawan;

import java.util.Comparator;

public class DataStore {

    String key;
    String sStatus;
    String sNama;

    public DataStore(String sStatus, String sNama, String sUsername, String sPassword) {
        this.sStatus = sStatus;
        this.sNama = sNama;
    }

    public static Comparator<DataStore> dataStoreComparator = (dataStore, t1) -> dataStore.getsNama().compareTo(t1.sNama);

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
