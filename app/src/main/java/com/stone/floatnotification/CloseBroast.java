package com.stone.floatnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CloseBroast extends BroadcastReceiver {
    private static final String TAG = "CloseBroast";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "收到通知: " + intent.getAction());
        MainActivity.appCompatActivity.cancel();
    }
}
