package com.kakabuli.camerastream;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;

class LoginPopupView extends CenterPopupView {

    public LoginPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_login;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        EditText etAccount = findViewById(R.id.etAccount);
        EditText etPwd = findViewById(R.id.etPwd);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2021/11/28  save account and pwd than use to login.
            }
        });

    }
}
