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

import net.wdd.lib.common.network.error.HttpError;
import net.wdd.lib.common.network.utils.SerializeType;

/**
 * Created by wangdd on 16-11-26.
 */

public interface HttpConnector {

    HttpSession sendHttpRequest(ActivityFragmentActive host, HttpRequestEntry requestEntry, SerializeType type, HttpConnectCallback callback);

    HttpResponseEntry sendSyncHttpRequest(HttpRequestEntry requestEntry, SerializeType type) throws HttpError;

    HttpSession sendHttpRequest(ActivityFragmentActive host, HttpRequestEntry requestEntry, HttpConnectCallback callback);

    HttpSession downloadFile(ActivityFragmentActive host, HttpRequestEntry requestEntry, String localName, FileDownloadCallback callback);

    HttpSession downloadFile(ActivityFragmentActive host, HttpRequestEntry requestEntry, String localName);

    HttpResponseEntry sendSyncHttpRequest(HttpRequestEntry requestEntry) throws HttpError;

    void stopAllSession();

    void stopSessionByTag(Object tag);

}
