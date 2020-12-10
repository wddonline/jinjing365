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

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.wdd.lib.common.R;
import net.wdd.lib.common.widget.AlertDialog;

/**
 * Created by Bit Cat on 2016/12/28.
 */
public class RationaleDialog {

    private AlertDialog mDialog;
    private Rationale mRationale;

    RationaleDialog(@NonNull Context context, @NonNull Rationale rationale) {
        mDialog = new AlertDialog(context);
        mDialog.setTitle(R.string.permission_title_permission_rationale);
        mDialog.setMessage(R.string.permission_message_permission_rationale);
        mDialog.setPositiveButton(R.string.permission_resume, mPositiveClickListener);
        mDialog.setNegativeButton(R.string.permission_cancel, mNegativeClickListener);
        this.mRationale = rationale;
    }

    @NonNull
    public RationaleDialog setTitle(@NonNull String title) {
        mDialog.setTitle(title);
        return this;
    }

    @NonNull
    public RationaleDialog setTitle(@StringRes int title) {
        mDialog.setTitle(title);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@NonNull String message) {
        mDialog.setMessage(message);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@StringRes int message) {
        mDialog.setMessage(message);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeButton(@NonNull String text, @Nullable AlertDialog.OnDialogButtonClickedListener
            negativeListener) {
        mDialog.setNegativeButton(text, negativeListener);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeButton(@StringRes int text, @Nullable AlertDialog.OnDialogButtonClickedListener
            negativeListener) {
        mDialog.setNegativeButton(text, negativeListener);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveButton(@NonNull String text) {
        mDialog.setPositiveButton(text, mPositiveClickListener);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveButton(@StringRes int text) {
        mDialog.setPositiveButton(text, mPositiveClickListener);
        return this;
    }

    public void show() {
        mDialog.show();
    }

    /**
     * The mDialog's btn click listener.
     */
    private AlertDialog.OnDialogButtonClickedListener mNegativeClickListener = new AlertDialog.OnDialogButtonClickedListener() {
        @Override
        public void onDialogButtonClicked(DialogInterface dialogInterface) {
            dialogInterface.dismiss();
            mRationale.cancel();
        }
    };

    private AlertDialog.OnDialogButtonClickedListener mPositiveClickListener = new AlertDialog.OnDialogButtonClickedListener() {
        @Override
        public void onDialogButtonClicked(DialogInterface dialogInterface) {
            dialogInterface.dismiss();
            mRationale.resume();
        }
    };

}
