package com.stone.floatnotification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DispacheActivity extends AppCompatActivity {
    private static final String TAG = "DispacheActivity";
    private boolean isSkip;

    @Override
    protected void onResume() {
        super.onResume();
        if (isSkip){
            finish();
            return;
        }
        moveTaskToBack(true);
        MainActivity.canShow = false;
        MainActivity.appCompatActivity.cancel();


        String tag = getIntent().getStringExtra("url");
        LogUtils.d(TAG, "onCreate: " + tag);

        if (!TextUtils.isEmpty(tag)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(tag);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(content_url);
            startActivity(intent);
            MainActivity.isSkipUC = true;
            isSkip = true;
        }


//        finish();
    }
}
