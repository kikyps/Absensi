package com.kp.absensi.admin.ui.karyawan;

import java.util.Comparator;

public class DataStore {

    String key;
    String sNama;

    public DataStore(String key, String sNama) {
        this.key = key;
        this.sNama = sNama;
    }

    public DataStore() {
    }

    public static Comparator<DataStore> dataStoreComparator = (dataStore, t1) -> dataStore.getsNama().compareTo(t1.sNama);

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getsNama() {
        return sNama;
    }
}
