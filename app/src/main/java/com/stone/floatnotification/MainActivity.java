package com.stone.floatnotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 10003;
    public static int measuredHeight;
    private NotificationBean notificationBean;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            loadImage();
            return true;
        }
    });
    private Bitmap imageBitmap;

    private void loadImage() {
        if (TextUtils.isEmpty(notificationBean.getImageUrl())){
            showNotification();
            return;
        }
        Glide.with(this.getApplicationContext()).asBitmap().load(notificationBean.getImageUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                MainActivity.this.imageBitmap = resource;
                showNotification();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                showNotification();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



    }

    private void showFloat() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.floating_window_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageBitmap(imageBitmap);

        WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = imageBitmap.getHeight() * width / imageBitmap.getWidth();
        LogUtils.d(TAG, "imageBitmap.getWidth=" + imageBitmap.getWidth());
        LogUtils.d(TAG, "imageBitmap.getHeight=" + imageBitmap.getHeight());

        view.measure(0,0);
        LogUtils.d(TAG, "height=" + height);
        measuredHeight = height;
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(width)                               //设置控件宽高
                .setHeight(measuredHeight)
                .setY(-ViewUtils.getStatusBarHeight(this))
                .setMoveType(MoveType.inactive)
                .setDesktopShow(true)                        //桌面显示
//                        .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
//                        .setPermissionListener(mPermissionListener)  //监听权限申请结果
                .build();

        IFloatWindow floatWindow = FloatWindow.get();
        floatWindow.show();
        view.setTranslationY(-measuredHeight);

        if (floatWindow != null) {
            final View mFloatLayout = floatWindow.getView();
            float translationY = mFloatLayout.getTranslationY();
            mFloatLayout.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mFloatLayout.setVisibility(View.VISIBLE);
                }
            }).translationYBy(measuredHeight).setDuration(500).setInterpolator(new DecelerateInterpolator(3));
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                close();
            }
        }, 3000);
    }

    private void close() {
        // 你的代码
        IFloatWindow floatWindow = FloatWindow.get();
//                floatWindow.show();
        if (floatWindow != null) {
            final View mFloatLayout = floatWindow.getView();
            float translationY = mFloatLayout.getTranslationY();
            mFloatLayout.animate().setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFloatLayout.setVisibility(View.INVISIBLE);
                    FloatWindow.destroy();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);

                }
            }).translationYBy(-MainActivity.measuredHeight).setDuration(500).setInterpolator(new DecelerateInterpolator(3));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatWindow.destroy();
        notificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * 展示通知栏
     */
    public void showNotification() {

        String id = "channel_demo";
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, this.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("通知栏");
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{0});
            notificationManager.createNotificationChannel(mChannel);
            notification = new NotificationCompat.Builder(this, id)
                    .setSmallIcon(R.mipmap.ic_lan)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(getDefaultIntent(Notification.FLAG_ONGOING_EVENT))
                    .setCustomBigContentView(getContentView(true))
                    .setCustomContentView(getContentView(true))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setChannelId(mChannel.getId())
                    .build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new NotificationCompat.Builder(this, id)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_lan)
                    .setContentIntent(getDefaultIntent(Notification.FLAG_ONGOING_EVENT))
                    .setCustomBigContentView(getContentView(true))
                    .setCustomContentView(getContentView(false))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, id)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_lan)
                    .setContentIntent(getDefaultIntent(Notification.FLAG_ONGOING_EVENT))
                    .setContent(getContentView(false))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
        }


        notificationManager.notify(NOTIFICATION_ID, notification);


        showFloat();
    }

    /**
     * 获取自定义通知栏view
     *
     * @param showBigView
     * @return
     */
    private RemoteViews getContentView(boolean showBigView) {
        int layout = -1;
        layout = R.layout.view_notify_big;
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), layout);
//        if (logoBitmap != null) {
//        }
//        mRemoteViews.setImageViewResource(R.id.iv_logo, R.mipmap.ic_launcher);
//        mRemoteViews.setTextViewText(R.id.content, "content");
//        mRemoteViews.setTextViewText(R.id.tv_title, "app名字");
//        mRemoteViews.setTextViewText(R.id.title, "title");
//        mRemoteViews.setOnClickPendingIntent(R.id.rootview, getClickPendingIntent());

        if (imageBitmap != null) {

            mRemoteViews.setImageViewBitmap(R.id.image, imageBitmap);
        }
        NotificationCompatColor.AutomationUse(this)
//                .setContentTitleColor(mRemoteViews, R.id.title)
//                .setContentTitleSize(mRemoteViews, R.id.title)
//                .setContentTextSize(mRemoteViews, R.id.content)
//                .setContentTextColor(mRemoteViews, R.id.content)
                .setTitleColor(mRemoteViews, R.id.title)
                .setTitleSize(mRemoteViews, R.id.title)

        ;
        return mRemoteViews;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAccessibilitySettingsOn(this, ListeningService.class.getCanonicalName())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ListeningService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);

            BmobQuery<NotificationBean> notificationBeanBmobQuery = new BmobQuery<>();
            notificationBeanBmobQuery.findObjects(new FindListener<NotificationBean>() {
                @Override
                public void done(List<NotificationBean> list, BmobException e) {
                    Log.d(TAG, "done: ");
                    if (list != null && !list.isEmpty()) {
                        notificationBean = list.get(0);
                        Log.d(TAG, "done: " + notificationBean.toString());
                        handler.sendEmptyMessageDelayed(0, notificationBean.getDelayTime());
                    }
                }
            });

        }
//        showNotification();
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

    private PendingIntent getClickPendingIntent() {
//        Intent intent = new Intent(this, MyBroatCast.class);
//        intent.setAction("notification_card");
//        PendingIntent pendingIntentClick0 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        return pendingIntentClick0;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://www.baidu.com");
        intent.setData(content_url);
        PendingIntent pendingIntentClick0 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntentClick0;
    }

    private PendingIntent getDefaultIntent(int flags) {
        return PendingIntent.getActivity(this, 1, new Intent(), flags);
    }
}