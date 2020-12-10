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

package net.wdd.lib.common.network.error;

import android.content.Context;

import net.wdd.lib.common.BuildConfig;
import net.wdd.lib.common.R;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpError extends Throwable {

    private Context mContext;
    private int errorCode;
    private String errorMsg;
    private String extra;
    private int statusCode = -1;

    public HttpError(Context context) {
        this.mContext = context;
    }

    public HttpError(Context context, int errorCode, String errorMsg) {
        this.mContext = context;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public HttpError(Context context, int errorCode, String errorMsg, int statusCode) {
        this.mContext = context;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.statusCode = statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        if (BuildConfig.DEBUG) {
            return errorMsg;
        } else {
            return mContext.getResources().getString(R.string.http_error);
        }

    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
