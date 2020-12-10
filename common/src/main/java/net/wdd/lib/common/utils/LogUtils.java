package net.wdd.lib.common.utils;

import android.util.Log;

import com.cloudthink.android.fancy.common.BuildConfig;

public class LogUtils {

    private static final String TAG = "Fancy";

    public static int v(String tag, String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.v(tag, msg);
    }

    public static int v(String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.v(TAG, msg);
    }

    public static int d(String tag, String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.d(tag, msg);
    }

    public static int d(String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.d(TAG, msg);
    }

    public static int i(String tag, String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.i(tag, msg);
    }

    public static int i(String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.i(TAG, msg);
    }

    public static int w(String tag, String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.w(tag, msg);
    }

    public static int w(String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.w(TAG, msg);
    }

    public static int e(String tag, String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.e(tag, msg);
    }

    public static int e(String msg) {
        if (!BuildConfig.DEBUG) return -1;
        return Log.e(TAG, msg);
    }

}
