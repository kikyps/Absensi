package com.kp.absensi.admin;

public class DataKordinat {

    String sLatitude, sLongitude;

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public DataKordinat(String sLatitude, String sLongitude) {
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
    }
}
