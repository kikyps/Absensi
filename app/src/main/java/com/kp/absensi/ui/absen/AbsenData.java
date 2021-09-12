package com.kp.absensi.ui.absen;

public class AbsenData {

    String sKehadiran, sJam, sKet;

    public String getsKehadiran() {
        return sKehadiran;
    }

    public void setsKehadiran(String sKehadiran) {
        this.sKehadiran = sKehadiran;
    }

    public String getsJam() {
        return sJam;
    }

    public void setsJam(String sJam) {
        this.sJam = sJam;
    }

    public String getsKet() {
        return sKet;
    }

    public void setsKet(String sKet) {
        this.sKet = sKet;
    }

    public AbsenData(String sKehadiran, String sJam, String sKet) {
        this.sKehadiran = sKehadiran;
        this.sJam = sJam;
        this.sKet = sKet;
    }
}
