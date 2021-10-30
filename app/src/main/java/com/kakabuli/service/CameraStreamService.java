package com.kakabuli.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.serenegiant.usb.USBMonitor;

public class CameraStreamService extends Service {

    private static final String TAG = "CameraStreamService";

    private USBMonitor mUSBMonitor;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
