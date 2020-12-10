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

import android.content.Context;
import android.util.Log;

import net.wdd.lib.common.network.error.HttpError;
import net.wdd.lib.common.network.impl.OkHttpConnector;
import net.wdd.lib.common.network.utils.SerializeType;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpManager {
    private final String TAG = "HttpManager";

    private static HttpManager INSTANCE;

    public static HttpManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private HttpConnector connecter;

    private HttpManager(Context context) {
        connecter = new OkHttpConnector(context);
    }

    public void setConnecter(HttpConnector connecter) {
        this.connecter = connecter;
    }

    public HttpSession sendHttpRequest(ActivityFragmentActive host, HttpRequestEntry request, SerializeType type, HttpConnectCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendHttpRequest(host, request, type, callback);
    }

    public HttpResponseEntry sendSyncHttpRequest(HttpRequestEntry request, SerializeType type) throws HttpError {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendSyncHttpRequest(request, type);
    }

    public HttpSession sendHttpRequest(ActivityFragmentActive host, HttpRequestEntry request, HttpConnectCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendHttpRequest(host, request, callback);
    }

    public HttpResponseEntry sendSyncHttpRequest(HttpRequestEntry request) throws HttpError {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.sendSyncHttpRequest(request);
    }

    public HttpSession downloadFile(ActivityFragmentActive host, HttpRequestEntry requestEntry, String localName) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.downloadFile(host, requestEntry, localName);
    }

    public HttpSession downloadFile(final ActivityFragmentActive host, HttpRequestEntry requestEntry, String localName, final FileDownloadCallback callback) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return null;
        }
        return connecter.downloadFile(host, requestEntry, localName, callback);
    }

    public void stopAllSession() {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return;
        }
        connecter.stopAllSession();
    }

    public void stopSessionByTag(String tag) {
        if (connecter == null) {
            Log.e(TAG, "A HttpConnector getInstance isn\'t setted for HttpManager");
            return;
        }
        connecter.stopSessionByTag(tag);
    }

}
