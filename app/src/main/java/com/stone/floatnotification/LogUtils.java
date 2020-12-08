package com.stone.floatnotification;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class LogUtils {
    private static final String TAG = "LogUtils";

    private static boolean IS_DEBUG = true;
    private static final int LOG_LEVEL = Log.VERBOSE;

    private static final String LOG_TEMP_FILE = "log.temp";
    private static final String LOG_LAST_FILE = "log_last.temp";

    private static OutputStream mLogStream;
    private static Context mContext;
    private static Calendar mDate = Calendar.getInstance();
    private static StringBuilder mBuffer = new StringBuilder();
    private static int LOG_MAXSIZE = 2 * 1024 * 1024;
    private static long mFileSize;

    private LogUtils() {
    }

    public static void init(Context mContext) {
        LogUtils.mContext = mContext.getApplicationContext();
    }

    public static boolean isIsDebug() {
        return IS_DEBUG;
    }

    public static void setIsDebug(boolean isDebug) {
        IS_DEBUG = isDebug;
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        log(tag, msg, Log.DEBUG);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, Log.DEBUG);
    }

    private static void log(String tag, String msg, int level) {
        if (!IS_DEBUG) {
            return;
        }

        if (level < LOG_LEVEL) {
            return;
        }

        if (tag == null) {
            tag = "TAG_NULL";
        }
        if (msg == null) {
            msg = "MSG_NULL";
        }

        logToScreen(tag, msg, level);
        logToFile(tag, msg, level);
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            logToConsole(tag, msg.substring(0, max_str_length), level);
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        logToConsole(tag, msg, level);
    }

    private static void logToConsole(String tag, String msg, int level) {
        switch (level) {
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
        }
    }

    private static boolean renameLogFile() {
        synchronized (LogUtils.class) {

            File file = new File(getLogFolder(), LOG_TEMP_FILE);
            File destFile = new File(getLogFolder(), LOG_LAST_FILE);
            if (destFile.exists()) {
                destFile.delete();
            }
            file.renameTo(destFile);
            if (file.exists()) {
                return file.delete();
            } else {
                return true;
            }
        }
    }

    private static String getLogStr(String tag, String msg) {
        mDate.setTimeInMillis(System.currentTimeMillis());

        mBuffer.setLength(0);
        mBuffer.append("[");
        mBuffer.append(tag);
        mBuffer.append(" : ");
        mBuffer.append(mDate.get(Calendar.MONTH) + 1);
        mBuffer.append("-");
        mBuffer.append(mDate.get(Calendar.DATE));
        mBuffer.append(" ");
        mBuffer.append(mDate.get(Calendar.HOUR_OF_DAY));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.MINUTE));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.SECOND));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.MILLISECOND));
        mBuffer.append("] ");
        mBuffer.append(msg);

        return mBuffer.toString();
    }

    private static void logToFile(String tag, String msg, int level) {
        synchronized(LogUtils.class) {
            OutputStream outStream = openLogFileOutStream();
            if (outStream != null) {
                try {
                    byte[] d = getLogStr(tag, msg).getBytes("utf-8");

                    if (mFileSize < LOG_MAXSIZE) {
                        outStream.write(d);
                        outStream.write("\r\n".getBytes("utf-8"));
                        outStream.flush();
                        mFileSize += d.length;
                    } else {
                        closeLogFileOutStream();
                        if (renameLogFile()) {
                            logToFile(tag, msg, level);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void logToScreen(String tag, String msg, int level) {

    }

    private static OutputStream openLogFileOutStream() {
        if (mLogStream == null) {
            try {
                File file = new File(getLogFolder(), LOG_TEMP_FILE);
                Log.d(TAG, "openLogFileOutStream: " + file.getAbsolutePath());
                if (file.exists()) {
                    mLogStream = new FileOutputStream(file, true);
                    mFileSize = file.length();
                } else {
                    mLogStream = new FileOutputStream(file);
                    mFileSize = 0;
                }
            } catch (FileNotFoundException e){

                e.printStackTrace();
            }

        }
        return mLogStream;
    }

    private static void closeLogFileOutStream() {
        try {
            if (mLogStream != null) {
                mLogStream.close();
                mLogStream = null;
                mFileSize = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static File getLogFolder() {
        File folder = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String logFile = mContext.getPackageName();
            Log.d(TAG, "getLogFolder: " + logFile);
            folder = mContext.getExternalFilesDir(logFile);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else {
            folder = mContext.getFilesDir();
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }

        return folder;
    }

}
