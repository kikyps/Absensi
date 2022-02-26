package com.kp.absensi.common;

import android.app.Application;
import android.os.Bundle;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class Collector extends Application {

    public void onCreate() {
        super.onCreate();
        CaocConfig.Builder.create()
                .errorActivity(CollectorActivity.class)
                .apply();
    }
}