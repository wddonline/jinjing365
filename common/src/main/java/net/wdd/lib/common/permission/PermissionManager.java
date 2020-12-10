/*
 * Copyright 2020-2020 wdd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wdd.lib.common.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bit Cat on 2016/9/9.
 */
public class PermissionManager {

    private static final String TAG = "PermissionManager";

    /**
     * Check if the calling mContext has a set of permissions.
     *
     * @param context     {@link Context}.
     * @param permissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) return false;
        }
        return true;
    }

    /**
     * Some privileges permanently disabled, may need to set up in the execute.
     *
     * @param activity          {@link Activity}.
     * @param deniedPermissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, @NonNull List<String>
            deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!PermissionUtils.shouldShowRationalePermissions(activity, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get default_address rationale dialog.
     *
     * @param context   {@link Context}.
     * @param rationale {@link Rationale}.
     * @return {@link RationaleDialog}.
     */
    public static
    @NonNull
    RationaleDialog rationaleDialog(@NonNull Context context, Rationale rationale) {
        return new RationaleDialog(context, rationale);
    }

    /**
     * Some privileges permanently disabled, may need to set up in the execute.
     *
     * @param fragment          {@link Fragment}.
     * @param deniedPermissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull Fragment fragment, @NonNull
            List<String>
            deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!PermissionUtils.shouldShowRationalePermissions(fragment, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Some privileges permanently disabled, may need to set up in the execute.
     *
     * @param fragment          {@link android.app.Fragment}.
     * @param deniedPermissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull android.app.Fragment fragment, @NonNull String...
            deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!PermissionUtils.shouldShowRationalePermissions(fragment, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get default_address setting dialog.
     *
     * @param activity    {@link Activity}.
     * @return {@link SettingDialog}.
     */
    public static
    @NonNull
    SettingDialog defaultSettingDialog(@NonNull Activity activity) {
        return new SettingDialog(activity, new SettingExecutor(activity));
    }

    /**
     * Get default_address setting dialog.
     *
     * @param fragment    {@link Fragment}.
     * @return {@link SettingDialog}.
     */
    public static
    @NonNull
    SettingDialog defaultSettingDialog(@NonNull Fragment fragment) {
        return new SettingDialog(fragment.getActivity(), new SettingExecutor(fragment));
    }

    /**
     * Get default_address setting dialog.
     *
     * @param fragment    {@link android.app.Fragment}.
     * @return {@link SettingDialog}.
     */
    public static
    @NonNull
    SettingDialog defaultSettingDialog(@NonNull android.app.Fragment fragment) {
        return new SettingDialog(fragment.getActivity(), new SettingExecutor(fragment));
    }

    /**
     * Get define setting dialog setting object.
     *
     * @param activity    {@link Activity}.
     * @return {@link SettingService}.
     */
    public static
    @NonNull
    SettingService defineSettingDialog(@NonNull Activity activity) {
        return new SettingExecutor(activity);
    }

    /**
     * Get define setting dialog setting object.
     *
     * @param fragment    {@link Fragment}.
     * @return {@link SettingService}.
     */
    public static
    @NonNull
    SettingService defineSettingDialog(@NonNull Fragment fragment) {
        return new SettingExecutor(fragment);
    }

    /**
     * Get define setting dialog setting object.
     *
     * @param fragment    {@link android.app.Fragment}.
     * @return {@link SettingService}.
     */
    public static
    @NonNull
    SettingService defineSettingDialog(@NonNull android.app.Fragment fragment) {
        return new SettingExecutor(fragment);
    }

    /**
     * In the AboutAppActivity.
     *
     * @param activity {@link Activity}.
     * @return {@link Permission}.
     */
    public static
    @NonNull
    Permission with(@NonNull Activity activity) {
        return new DefaultPermission(activity);
    }

    /**
     * In the AboutAppActivity.
     *
     * @param fragment {@link Fragment}.
     * @return {@link Permission}.
     */
    public static
    @NonNull
    Permission with(@NonNull Fragment fragment) {
        return new DefaultPermission(fragment);
    }

    /**
     * In the AboutAppActivity.
     *
     * @param fragment {@link android.app.Fragment}.
     * @return {@link Permission}.
     */
    public static
    @NonNull
    Permission with(@NonNull android.app.Fragment fragment) {
        return new DefaultPermission(fragment);
    }

    /**
     * Request permissions in the activity.
     *
     * @param activity    {@link Activity}.
     * @param requestCode request code.
     * @param permissions all permissions.
     */
    public static void send(@NonNull Activity activity, int requestCode, @NonNull String... permissions) {
        with(activity).requestCode(requestCode).permission(permissions).send();
    }

    /**
     * Request permissions in the activity.
     *
     * @param fragment    {@link Fragment}.
     * @param requestCode request code.
     * @param permissions all permissions.
     */
    public static void send(@NonNull Fragment fragment, int requestCode, @NonNull String...
            permissions) {
        with(fragment).requestCode(requestCode).permission(permissions).send();
    }

    /**
     * Request permissions in the activity.
     *
     * @param fragment    {@link android.app.Fragment}.
     * @param requestCode request code.
     * @param permissions all permissions.
     */
    public static void send(@NonNull android.app.Fragment fragment, int requestCode, @NonNull String...
            permissions) {
        with(fragment).requestCode(requestCode).permission(permissions).send();
    }

    /**
     * Parse the request results.
     *
     * @param activity     {@link Activity}.
     * @param requestCode  request code.
     * @param permissions  all permissions.
     * @param grantResults results.
     */
    public static void onRequestPermissionsResult(@NonNull Activity activity, int requestCode, @NonNull String[]
            permissions, int[] grantResults) {
        callbackAnnotation(activity, requestCode, permissions, grantResults);
    }

    /**
     * Parse the request results.
     *
     * @param fragment     {@link Fragment}.
     * @param requestCode  request code.
     * @param permissions  all permissions.
     * @param grantResults results.
     */
    public static void onRequestPermissionsResult(@NonNull Fragment fragment, int
            requestCode,
                                                  @NonNull String[] permissions, int[] grantResults) {
        callbackAnnotation(fragment, requestCode, permissions, grantResults);
    }

    /**
     * Parse the request results.
     *
     * @param fragment     {@link android.app.Fragment}.
     * @param requestCode  request code.
     * @param permissions  all permissions.
     * @param grantResults results.
     */
    public static void onRequestPermissionsResult(@NonNull android.app.Fragment fragment, int requestCode,
                                                  @NonNull String[] permissions, int[] grantResults) {
        callbackAnnotation(fragment, requestCode, permissions, grantResults);
    }

    /**
     * Parse the request results.
     *
     * @param o            {@link Activity} or {@link Fragment} or
     *                     {@link android.app.Fragment}.
     * @param requestCode  request code.
     * @param permissions  all permissions.
     * @param grantResults results.
     */
    private static void callbackAnnotation(@NonNull Object o, int requestCode, @NonNull String[] permissions, int[]
            grantResults) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                grantedList.add(permissions[i]);
            else
                deniedList.add(permissions[i]);
        }

        boolean isAllGrant = deniedList.isEmpty();

        Class<? extends Annotation> clazz = isAllGrant ? PermissionYes.class : PermissionNo.class;
        Method[] methods = findMethodForRequestCode(o.getClass(), clazz, requestCode);
        if (methods.length == 0) {
            Log.e(TAG, "Not found the callback method, do you forget @PermissionYes or @permissionNo" +
                    " for callback method ? Or you can use PermissionListener.");
        } else
            try {
                for (Method method : methods) {
                    if (!method.isAccessible()) method.setAccessible(true);
                    method.invoke(o, isAllGrant ? grantedList : deniedList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private static <T extends Annotation> Method[] findMethodForRequestCode(@NonNull Class<?> source, @NonNull
            Class<T> annotation, int requestCode) {
        List<Method> methods = new ArrayList<>(1);
        for (Method method : source.getDeclaredMethods())
            if (method.isAnnotationPresent(annotation))
                if (isSameRequestCode(method, annotation, requestCode))
                    methods.add(method);
        return methods.toArray(new Method[methods.size()]);
    }

    private static <T extends Annotation> boolean isSameRequestCode(@NonNull Method method, @NonNull Class<T>
            annotation, int requestCode) {
        if (PermissionYes.class.equals(annotation))
            return method.getAnnotation(PermissionYes.class).value() == requestCode;
        else if (PermissionNo.class.equals(annotation))
            return method.getAnnotation(PermissionNo.class).value() == requestCode;
        return false;
    }

    /**
     * Parse the request results.
     *
     * @param requestCode  request code.
     * @param permissions  one or more permissions.
     * @param grantResults results.
     * @param listener     {@link PermissionListener}.
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[]
            grantResults, @NonNull PermissionListener listener) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                grantedList.add(permissions[i]);
            else
                deniedList.add(permissions[i]);
        }

        if (deniedList.isEmpty())
            listener.onSucceed(requestCode, grantedList);
        else
            listener.onFailed(requestCode, deniedList);
    }

    private PermissionManager() {
    }

}
