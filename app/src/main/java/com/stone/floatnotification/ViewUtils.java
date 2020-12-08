package com.stone.floatnotification;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;

/**
 * view工具类
 * @author houmiao.xiong
 * @date 2012-8-9
 */
public class ViewUtils {

	public static final String TAG = "ViewUtils";

	static int screenHeight;		//屏幕高度
	static int screenWidth;			//屏幕宽度
	static float screenDensity;		//屏幕分辨率

	static Bitmap defMarketBitmap;  //超市卡片默认bitmap
	static Bitmap defHomeBitmap;    //首页卡片默认bitmap
	static Bitmap defFullBitmap;    //首页卡片默认bitmap
	static Bitmap defManageBitmap;  //管理卡片默认bitmap
	/**
	 * 获得屏幕大小
	 * @param acti
	 */
	public static void getScreenSize(Activity acti){
		if(screenDensity==0||screenWidth==0||screenHeight==0){
			DisplayMetrics dm = new DisplayMetrics();
			acti.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenDensity = dm.density;
			screenHeight = dm.heightPixels;
			screenWidth = dm.widthPixels;
		}
	}

	/**
	 * 获得屏幕宽度
	 * @author houmiao.xiong
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context){
		if(screenWidth == 0){
            if(context == null){
                throw new NullPointerException("context can not be null");
            }else if(context instanceof Activity){
				DisplayMetrics dm = new DisplayMetrics();
				((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
				screenWidth = dm.widthPixels;
			}else{
				throw new IllegalArgumentException("context must be instanceof Activity");
			}
		}

		return screenWidth;
	}

	/**
	 * 获得屏幕高度
	 * @author houmiao.xiong
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context){
		if(screenHeight == 0){
            if(context == null){
                throw new NullPointerException("context can not be null");
            } else if(context instanceof Activity){
				Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
				DisplayMetrics dm = new DisplayMetrics();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					display.getRealMetrics(dm);
				} else {
					display.getMetrics(dm);
				}
				screenHeight = dm.heightPixels;
			} else {
				throw new IllegalArgumentException("context must be instanceof Activity");
			}
		}

		return screenHeight;
	}

	/**
	 * 获得屏幕密度
	 * @author houmiao.xiong
	 * @param context
	 * @return
	 */
	public static float getScreenDesity(Context context){
		if(screenDensity == 0){
            if(context == null){
                throw new NullPointerException("context can not be null");
            } else if(context instanceof Activity){
				DisplayMetrics dm = new DisplayMetrics();
				((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
				screenDensity = dm.density;
			} else {
				throw new IllegalArgumentException("context must be instanceof Activity");
			}
		}

		return screenDensity;
	}


	/**
	 * 获得超市页卡图标默认卡片大小
	 * @param context
	 * @return
	 */


//	/**
//	 * 设置管理界面  卡片Logo默认大小
//	 * @param view
//	 * @param context
//	 */
//	public static void setManageCardSize(Context context,ImageView view){
//		if(defManageBitmap == null){
//			defManageBitmap = BitmapFactory.decodeResource(context.getResources(),
//					R.drawable.default_manage_card);
//		}
//		setViewMaxSize(view, defManageBitmap);
//	}
	

	

	
	

	/**
	 * 设置图片最大大小
	 * @param view
	 * @param defaultBitmap
	 */
	public static void setViewMaxSize(ImageView view,Bitmap defaultBitmap){
		view.setAdjustViewBounds(true);
		view.setMaxWidth(defaultBitmap.getWidth());
		view.setMaxHeight(defaultBitmap.getHeight());
//		view.setMinimumHeight(defaultBitmap.getHeight());
//		view.setMinimumWidth(defaultBitmap.getWidth());
	}
	
	/**
	 * 设置图片最大大小
	 * @param view
	 */
	public static void setViewMaxSize(ImageView view,int width,int height){
		view.setAdjustViewBounds(true);
		view.setMaxWidth(width);
		view.setMaxHeight(height);
//		view.setMinimumHeight(defaultBitmap.getHeight());
//		view.setMinimumWidth(defaultBitmap.getWidth());
	}

	/**
	 * 设置bitmap为固定图片的大小
	 * @param defaultBitmap
	 */
	public static Bitmap setBitmapSize(Bitmap defaultBitmap,Bitmap destBitmap){
		
		try
		{
			 return Bitmap.createScaledBitmap(destBitmap, defaultBitmap.getWidth(), defaultBitmap.getHeight(), false);
		}
		catch(Exception e)
		{
			return defaultBitmap;
		}
	}

	/**
	 * 缩放Bitmap
	 * @param bitmap
	 * @param multiple
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap,double multiple){
		
		return Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*multiple),
				(int)(bitmap.getHeight()*multiple), true);
	}

	/**
	 * px转dip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / getScreenDesity(context) + 0.5f);
	}


	/**
	 * dip转px
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		return (int) (dipValue * getScreenDesity(context) + 0.5f);
	}

	// 

	/**
	 * shouli.luo
	 * @param file
	 * @return 按图片大小(字节大小)缩放图片
	 */
	public static Bitmap fitSizeImg(File file) {
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		if (file.length() < 20480) {       // 0-20k
			opts.inSampleSize = 1;
		} else if (file.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (file.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (file.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (file.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		return resizeBmp;
	}


	/**
	 * shouli.luo
	 * @param listView
	 * 根据item的高度确定listView的高度
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView,BaseAdapter listAdapter) {

		if (listAdapter == null)
		{
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
		listView.setLayoutParams(params);

	}


    /**
     * zhousu
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity){
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

	public static int getViewHeightByWidth(int width,double ratio){
		return (int)(width/ratio);
	}

    /**
     * 判断手机是否是全面屏，屏幕比例大于等2认为是全面屏，
     * @param context
     * @return true是全面屏，false普通屏
     */
	public static boolean isWidthScreen(Context context) {
		double width = getScreenWidth(context);
		double height = getScreenHeight(context);
		double ratio = height / width;
		if (ratio >= 2) {
			return true;
		} else {
			return false;
		}
	}

	public static int getStatusBarHeight(Context context) {
		try {
			Resources resources = context.getResources();
			int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
			int height = resources.getDimensionPixelSize(resourceId);
			return height;
		}catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}

	public static void changeViewHeightByRate(View view,int screenWidth,float rate){
		if(rate==0)
			return;
		ViewGroup.LayoutParams params=view.getLayoutParams();
		params.width=screenWidth;
		params.height=(int)(screenWidth/rate);
	}

	/**
	 * 安全的转换颜色
	 * @param color
	 * @return
	 */
	public static int safetyParserColor(String color){
		return safetyParserColor(color,"#FFFFFFFF");
	}

	/**
	 * 安全的转换颜色
	 * @param color
	 * @param defaultColor
	 * @return
	 */
	public static int safetyParserColor(String color,String defaultColor){
		try {
			return Color.parseColor(color);
		}catch (Exception e){
			//e.printStackTrace();
		}
		return 	Color.parseColor(defaultColor);

	}
}
