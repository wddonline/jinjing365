package net.wdd.lib.common.utils;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BadgeNumberUtils {

    public static void setBadgeCount(Context context, int count, Notification... notifications) {
        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }
        if (Build.MANUFACTURER.toLowerCase().contains("huawei")) {
            setBadgeOfEXUI(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {
            setBadgeOfNova(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("lenovo")) {
            setBadgeOfZuk(context, count);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            setBadgeOfSony(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung") || Build.MANUFACTURER.toLowerCase().contains("lg")) {
            setBadgeOfSumsung(context, count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {
            setBadgeOfHTC(context, count);
        } else if(Build.MANUFACTURER.toLowerCase().contains("xiaomi")) {
            if (notifications == null || notifications.length == 0) return;
            setBadgeOfMINU(notifications[0], count);
        } else if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {
            setBadgeOfOPPO(context, count);
        }else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            setBadgeOfVIVO(context, count);
        } else{

        }
    }

    /**
     * 设置华为Badge
     * @param context
     * @param count
     */
    private static void setBadgeOfEXUI(Context context, int count){
        try{
            Bundle badgeBundle = new Bundle();
            badgeBundle.putString( "package", context.getPackageName());
            badgeBundle.putString( "class", getLauncherClassName(context));
            badgeBundle.putInt( "badgenumber", count);
            context.getContentResolver().call(Uri.parse( "content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, badgeBundle);
        } catch(Exception e) {
        }
    }

    /**
     * 设置Nova的Badge
     * @param context
     * @param count
     */
    private static void setBadgeOfNova(Context context, int count){
        ContentValues contentValues = new ContentValues();
        contentValues.put( "tag", context.getPackageName() + "/"+ getLauncherClassName(context));
        contentValues.put( "count", count);
        context.getContentResolver().insert(Uri.parse( "content://com.teslacoilsw.notifier/unread_count"), contentValues);
    }

    /**
     * 设置联想ZUK的Badge
     * @param context
     * @param count
     */
    private static void setBadgeOfZuk(Context context, int count) {
        final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");
        Bundle extra = new Bundle();
        ArrayList<String> ids = null;
//        ArrayList<String> ids = new ArrayList<String>();
        // 以列表形式传递快捷方式id，可以添加多个快捷方式id
//        ids.add("custom_id_1");
//        ids.add("custom_id_2");
        extra.putStringArrayList("app_shortcut_custom_id", ids);
        extra.putInt("app_badge_count", count);
        Bundle b = context.getContentResolver().call(CONTENT_URI,"setAppBadgeCount", null, extra);
    }

    /**
     * 设置MIUI的Badge 小米需要和通知栏进行绑定 MMP
     * @param notification
     * @param count
     */
    private static void setBadgeOfMINU(Notification notification, int count) {
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置索尼的Badge
     * @param context
     * @param count
     */
    private static void setBadgeOfSony(Context context, int count){
        String launcherClassName = getLauncherClassName(context);
        if(launcherClassName == null) {
            return;
        }
        boolean isShow = true;
        if(count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.setAction( "com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra( "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow); // 是否显示
        localIntent.putExtra( "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName); // 启动页
        localIntent.putExtra( "com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count)); // 数字
        localIntent.putExtra( "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName()); // 包名
        context.sendBroadcast(localIntent);
    }

    /**
     * 设置三星的Badge设置LG的Badge
     * @param context
     * @param count
     */
    private static void setBadgeOfSumsung(Context context, int count){
        // 获取你当前的应用
        String launcherClassName = getLauncherClassName(context);
        if(launcherClassName == null) {
            return;
        }
        Intent intent = new Intent( "android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra( "badge_count", count);
        intent.putExtra( "badge_count_package_name", context.getPackageName());
        intent.putExtra( "badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    /**
     * 设置HTC的Badge
     * @paramcontext context
     * @paramcount count
     */
    private static void setBadgeOfHTC(Context context, int count){
        Intent intentNotification = new Intent( "com.htc.launcher.action.SET_NOTIFICATION");
        ComponentName localComponentName = new ComponentName(context.getPackageName(), getLauncherClassName(context));
        intentNotification.putExtra( "com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
        intentNotification.putExtra( "com.htc.launcher.extra.COUNT", count);
        context.sendBroadcast(intentNotification);
        Intent intentShortcut = new Intent( "com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra( "packagename", context.getPackageName());
        intentShortcut.putExtra( "count", count);
        context.sendBroadcast(intentShortcut);
    }

    /**
     * 设置vivo的Badge :vivoXplay5 vivo x7无效果
     */
    private static void setBadgeOfVIVO(Context context,int count){
        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
            intent.putExtra("className", launchClassName); intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *设置oppo的Badge :oppo角标提醒目前只针对内部软件还有微信、QQ开放，其他的暂时无法提供
     */
    private static void setBadgeOfOPPO(Context context,int count){
        try {
            Bundle extras = new Bundle();
            extras.putInt("app_badge_count", count);
            context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", String.valueOf(count), extras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }


}
