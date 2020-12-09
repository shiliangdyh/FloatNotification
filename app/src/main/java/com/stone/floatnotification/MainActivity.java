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
import android.widget.Toast;

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
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private long firstShowTime;

    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 10003;
    public static int measuredHeight;
    private FloatNotification notificationBean;
    public static MainActivity appCompatActivity;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            loadImage();
            return true;
        }
    });
    private Bitmap imageBitmap;

    private void loadImage() {
        LogUtils.d(TAG, "开始下载图片 : " + notificationBean.getImageUrl());
        if (TextUtils.isEmpty(notificationBean.getImageUrl())){
            showNotification();
            return;
        }
        Glide.with(this.getApplicationContext()).asBitmap().load(notificationBean.getImageUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                LogUtils.d(TAG, "图片下载成功: ");
                MainActivity.this.imageBitmap = resource;
                showNotification();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                LogUtils.d(TAG, "图片下载失败: ");
//                showNotification();
                Toast.makeText(MainActivity.this, "图片下载失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void cancel(){
        LogUtils.d(TAG, "取消通知: ");
        handler.removeCallbacksAndMessages(null);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FloatWindow.destroy();
        super.onCreate(savedInstanceState);
        appCompatActivity = this;
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        addNotification();


        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }


    private void addNotification() {
        FloatNotification floatNotification = new FloatNotification();
        floatNotification.setImageUrl("http://www.kupan123.com/upload/1590151889x-1404755431.png");
        floatNotification.setAppName("火山小视频");
        floatNotification.setTitle("今日头条");
        floatNotification.setDelayTime(3000);
        floatNotification.setJumpUrl("https://www.baidu.com");
        floatNotification.setDownloadUrl("https://www.baidu.com");

        floatNotification.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: ");
                } else {
                    Log.d(TAG, "error: " + e.getLocalizedMessage());
                }
            }
        });
    }

    private void showFloat() {
        if (App.isShowTongzhiLan){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.floating_window_layout, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(notificationBean.getJumpUrl()));
                startActivity(intent);
                FloatWindow.destroy();
                notificationManager.cancel(NOTIFICATION_ID);
            }
        });
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
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 展示通知栏
     */
    public void showNotification() {

        String id = "channel_demo";
        Notification notification = null;
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
                    .setCustomBigContentView(getContentView(R.layout.view_notify_big))
                    .setContent(getContentView(R.layout.view_notify_small))
                    .setCustomContentView(getContentView(R.layout.view_notify_small))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setChannelId(mChannel.getId())
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        } else {
        }


        notificationManager.notify(NOTIFICATION_ID, notification);



        showFloat();
    }

    /**
     * 获取自定义通知栏view
     *
     * @return
     */
    private RemoteViews getContentView(int layout) {
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), layout);
        mRemoteViews.setTextViewText(R.id.title, notificationBean.getTitle());
        mRemoteViews.setTextViewText(R.id.adtext, notificationBean.getAppName());
        mRemoteViews.setOnClickPendingIntent(R.id.rootview, getClickPendingIntent());
        mRemoteViews.setOnClickPendingIntent(R.id.download, getDownPendingIntent());
        mRemoteViews.setOnClickPendingIntent(R.id.close, getClosePendingIntent());

        if (imageBitmap != null) {
            mRemoteViews.setImageViewBitmap(R.id.image, imageBitmap);
        }
        NotificationCompatColor.AutomationUse(this)
                .setContentTitleColor(mRemoteViews, R.id.title)
                .setContentTitleSize(mRemoteViews, R.id.title)
//                .setContentTextSize(mRemoteViews, R.id.content)
//                .setContentTextColor(mRemoteViews, R.id.content)
//                .setTitleColor(mRemoteViews, R.id.title)
//                .setTitleSize(mRemoteViews, R.id.title)

        ;
        return mRemoteViews;
    }

    private PendingIntent getClosePendingIntent() {
        Intent intent = new Intent();
        intent.setAction("com.stone.close");
        PendingIntent pendingIntentClick0 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return pendingIntentClick0;
    }

    private PendingIntent getDownPendingIntent() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(notificationBean.getDownloadUrl());
        intent.setData(content_url);
        PendingIntent pendingIntentClick0 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return pendingIntentClick0;
    }

    private PendingIntent getClickPendingIntent() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(notificationBean.getJumpUrl());
        intent.setData(content_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntentClick0 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntentClick0;
    }

    public void start(){
        notificationManager.cancel(NOTIFICATION_ID);
        FloatWindow.destroy();
        moveTaskToBack(false);
        BmobQuery<FloatNotification> notificationBeanBmobQuery = new BmobQuery<>();
        notificationBeanBmobQuery.findObjects(new FindListener<FloatNotification>() {
            @Override
            public void done(List<FloatNotification> list, BmobException e) {
                LogUtils.d(TAG, "done: ");
                if (list != null && !list.isEmpty()) {
                    notificationBean = list.get(0);
                    LogUtils.d(TAG, "done: " + notificationBean.toString());
                    handler.sendEmptyMessageDelayed(0, notificationBean.getDelayTime());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private PendingIntent getDefaultIntent(int flags) {
        return PendingIntent.getActivity(this, 1, new Intent(), flags);
    }
}