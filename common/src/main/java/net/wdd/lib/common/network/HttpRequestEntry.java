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

import android.animation.ObjectAnimator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpRequestEntry {

    private final int TIME_OUT = 15000;

    public enum Method {
        POST,
        GET,
        DELETE
    }

    public enum Format {
        JSON,
        FORM,
        FILE,
        MULTIPART
    }

    private Map<String, String> headers;
    private Map<String, Object> params;
    private Method method = Method.POST;
    private String url;
    private String tag;
    private String requestJson;

    private int timeOut = TIME_OUT;
    private Format format = Format.FORM;

    public HttpRequestEntry() {
        headers = new HashMap<>();
        params = new HashMap<>();
    }

    public void addRequestHeader(String headerKey, String headerVal) {
        headers.put(headerKey, headerVal);
    }

    public void addRequestHeaders(Map headers) {
        this.headers.putAll(headers);
    }

    public void addRequestParam(String paramKey, String paramVal) {
        params.put(paramKey, paramVal);
    }

    public void addRequestParam(String paramKey, Object paramVal) {
        params.put(paramKey, paramVal);
    }

    public void addRequestParams(Map params) {
        this.params.putAll(params);
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public Map<String, String> getRequestHeaders() {
        return headers;
    }

    public Map<String, Object> getRequestParams() {
        return params;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
