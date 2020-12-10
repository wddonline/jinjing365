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

package net.wdd.lib.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import net.wdd.lib.common.R;
import net.wdd.lib.common.utils.AppUtils;
import net.wdd.lib.common.utils.DensityUtils;

public class AlertDialog {

    private Dialog dialog;
    TextView titleView;
    TextView messageView;
    TextView cancelView;
    TextView okView;

    public AlertDialog(@NonNull Context context) {
        dialog = new Dialog(context, R.style.CustomDialog);
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        titleView = rootView.findViewById(R.id.dialog_alert_title);
        messageView = rootView.findViewById(R.id.dialog_alert_message);
        cancelView = rootView.findViewById(R.id.dialog_alert_cancel);
        okView = rootView.findViewById(R.id.dialog_alert_ok);
        dialog.setContentView(rootView);

        View.OnClickListener defaultListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
        okView.setOnClickListener(defaultListener);
        cancelView.setOnClickListener(defaultListener);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = AppUtils.getScreenWidth(context) - DensityUtils.dip2px(context, 66);
        window.setAttributes(lp);
    }

    public void dimiss() {
        dialog.dismiss();
    }

    public AlertDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        dialog.setOnCancelListener(onCancelListener);
        return this;
    }

    public AlertDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        dialog.setOnDismissListener(onDismissListener);
        return this;
    }

    public AlertDialog setTitle(int resId) {
        titleView.setText(resId);
        return this;
    }

    public AlertDialog setTitle(String title) {
        titleView.setText(title);
        return this;
    }

    public AlertDialog setMessage(int resId) {
        messageView.setText(resId);
        return this;
    }

    public AlertDialog setMessage(String message) {
        messageView.setText(message);
        return this;
    }

    public AlertDialog setNegativeButtonVisible(boolean isVisible) {
        cancelView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }

    public AlertDialog setPositiveButton(int resId, final OnDialogButtonClickedListener onDialogButtonClickedListener) {
        okView.setText(resId);
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonClickedListener.onDialogButtonClicked(dialog);
            }
        });
        return this;
    }

    public AlertDialog setNegativeButton(int resId, final OnDialogButtonClickedListener onDialogButtonClickedListener) {
        cancelView.setText(resId);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonClickedListener.onDialogButtonClicked(dialog);
            }
        });
        return this;
    }

    public AlertDialog setPositiveButton(String okLabel, final OnDialogButtonClickedListener onDialogButtonClickedListener) {
        okView.setText(okLabel);
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonClickedListener.onDialogButtonClicked(dialog);
            }
        });
        return this;
    }

    public AlertDialog setNegativeButton(String cancelLabel, final OnDialogButtonClickedListener onDialogButtonClickedListener) {
        cancelView.setText(cancelLabel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonClickedListener.onDialogButtonClicked(dialog);
            }
        });
        return this;
    }

    public AlertDialog setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public void show() {
        if (TextUtils.isEmpty(messageView.getText())) {
            messageView.setVisibility(View.GONE);
        }
        dialog.show();
    }

    public interface OnDialogButtonClickedListener {

        void onDialogButtonClicked(DialogInterface dialogInterface);

    }
}
