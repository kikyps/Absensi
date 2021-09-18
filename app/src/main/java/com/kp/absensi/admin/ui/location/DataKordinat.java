package com.kp.absensi.admin.ui.location;

public class DataKordinat {

    String sLatitude;
    String sLongitude;
    String sDistance;

    public DataKordinat(String sLatitude, String sLongitude, String sDistance) {
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
        this.sDistance = sDistance;
    }

    public String getsDistance() {
        return sDistance;
    }

    public void setsDistance(String sDistance) {
        this.sDistance = sDistance;
    }

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

}
