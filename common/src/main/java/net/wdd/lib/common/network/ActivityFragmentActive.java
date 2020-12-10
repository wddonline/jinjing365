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

package net.wdd.lib.common.network;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.fragment.app.Fragment;

import java.lang.ref.SoftReference;

public class ActivityFragmentActive {

    private SoftReference<Activity> mActivity;
    private SoftReference<Fragment> mFragment;

    public ActivityFragmentActive(Activity activity) {
        mActivity = new SoftReference<>(activity);
    }

    public ActivityFragmentActive(Fragment fragment) {
        this.mFragment = new SoftReference<>(fragment);
    }

    public void destroy() {
        if (mActivity != null) {
            mActivity.clear();
            mActivity = null;
        }
        if (mFragment != null) {
            mFragment.clear();
            mFragment = null;
        }
    }

    public boolean isActive() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    return !activity.isDestroyed() && !activity.isFinishing();
                } else {
                    return !activity.isFinishing();
                }
            }
            return true;
        }
        if (mFragment != null) {
            Fragment fragment = mFragment.get();
            if (fragment != null) {
                Activity activity = fragment.getActivity();
                if (activity == null) return false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (activity.isDestroyed() || activity.isFinishing()) {
                        return false;
                    }
                } else {
                    if (activity.isFinishing()) {
                        return false;
                    }
                }
                return !fragment.isRemoving() && !fragment.isDetached();
            }
            return true;
        }
        return false;
    }

    public Context getContext() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null)
                return activity;
        }
        if (mFragment != null) {
            Fragment fragment = mFragment.get();
            if (fragment != null)
                return fragment.getContext();
        }
        return null;
    }
}
