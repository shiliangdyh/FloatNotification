package com.stone.floatnotification;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;

public class ListeningService extends AccessibilityService {
    private static final String TAG = "MainActivity";



    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.d(TAG, "onServiceConnected: ");
//        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
//        //配置监听的事件类型为界面变化|点击事件
//        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
//        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        if (Build.VERSION.SDK_INT >= 16) {
//            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
//        }
//        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();//当前界面的可访问节点信息
//        LogUtils.d(TAG, "onAccessibilityEvent: " + event.getEventType());
        AccessibilityEvent accessibilityEvent = event;

        Log.d(TAG, "onAccessibilityEvent: getEventType=" + accessibilityEvent.getEventType() + " ,getPackageName=" + accessibilityEvent.getPackageName() + ",getClassName=  " + accessibilityEvent.getClassName());

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                accessibilityEvent.getPackageName().equals("com.android.systemui") &&
                (accessibilityEvent.getClassName().equals("com.android.systemui.statusbar.phone.PhoneStatusBa‌​r$ExpandedDialog") || accessibilityEvent.getClassName().equals("android.widget.FrameLayout") || accessibilityEvent.getClassName().equals("com.android.systemui.statusbar.StatusBarSe‌​rvice$ExpandedDialog"))) {
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
    }


//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//界面变化事件
//            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
//            ActivityInfo activityInfo = tryGetActivity(componentName);
//            boolean isActivity = activityInfo != null;
//            if (isActivity) {
//                Log.i(TAG, componentName.flattenToShortString());
//                //格式为：(包名/.+当前Activity所在包的类名)
//                //如果是模拟程序的操作界面
//                if (componentName.flattenToShortString().equals("com.demon.simulationclick/.MainActivity")) {
//                    //当前是模拟程序的主页面，则模拟点击按钮
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                        //通过id寻找控件，id格式为：(包名:id/+制定控件的id)
//                        //一般除非第三方应该是自己的，否则我们很难通过这种方式找到控件
//                        //List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.demon.simulationclick:id/btn_click");
//                        //通过控件的text寻找控件
//                        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("模拟点击");
//                        if (list != null && list.size() > 0) {
//                            list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        }
//                    }
//                }
//            }
//        }
//        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {//View点击事件
//            //Log.i(TAG, "onAccessibilityEvent: " + nodeInfo.getText());
//            if ((nodeInfo.getText() + "").equals("模拟点击")) {
//                //Toast.makeText(this, "这是来自监听Service的响应！", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onAccessibilityEvent: 这是来自监听Service的响应！");
//            }
//        }
//    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.d(TAG, "onInterrupt: ");
    }

}