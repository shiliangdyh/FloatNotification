package com.stone.floatnotification;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import cn.bmob.v3.Bmob;

public class App extends Application {
    public static boolean isShowTongzhiLan;
    private static final String TAG = "App";
    @Override
    public void onCreate() {
        LogUtils.init(this);
        super.onCreate();
        Bmob.initialize(this, "c0770bc4b8769ce9bf873e6b306e9054");
//        if (!isAccessibilitySettingsOn(this, ListeningService.class.getCanonicalName())) {
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(this, ListeningService.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startService(intent);
//        }
    }

    /**
     * 检测辅助功能是否开启
     *
     * @param mContext
     * @return boolean
     */
    private boolean isAccessibilitySettingsOn(Context mContext, String serviceName) {
        int accessibilityEnabled = 0;
        // 对应的服务
        final String service = getPackageName() + "/" + serviceName;
        //Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}
