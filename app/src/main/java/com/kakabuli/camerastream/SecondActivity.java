package com.kakabuli.camerastream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kakabuli.service.CameraStreamService;

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void startServer(View view) {

        Intent startServer = new Intent(this, CameraStreamService.class);
        startService(startServer);
    }

    public void stopServer(View view) {
        Intent stopServer = new Intent(this, CameraStreamService.class);
        stopService(stopServer);
    }
}