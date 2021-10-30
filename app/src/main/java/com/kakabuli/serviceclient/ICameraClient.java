package com.kakabuli.serviceclient;

import android.hardware.usb.UsbDevice;
import android.view.Surface;

public interface ICameraClient {
    public void select(UsbDevice device);
    public void release();
    public UsbDevice getDevice();
    public void resize(int width, int height);
    public void connect();
    public void disconnect();
    public void addSurface(Surface surface, boolean isRecordable);
    public void removeSurface(Surface surface);
    public void startRecording();
    public void stopRecording();
    public boolean isRecording();
    public void captureStill(String path);
}