package com.kakabuli.camerastream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            LogUtils.d("打开应用");
            Intent startIntent = new Intent();
            ComponentName componentName = new ComponentName("com.kakabuli.camerastream", "com.kakabuli.camerastream.MainActivity");
//            Intent startIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            //1.如果自启动APP，参数为需要自动启动的应用包名
            //Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            //下面这句话必须加上才能开机自动运行app的界面
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setComponent(componentName);
            //2.如果自启动Activity
            //context.startActivity(intent);
            //3.如果自启动服务
//            context.startService(startIntent);
            context.startActivity(startIntent);
        }
    }
}
