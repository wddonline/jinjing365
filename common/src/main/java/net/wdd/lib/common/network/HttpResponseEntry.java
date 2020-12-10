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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdd on 16-11-26.
 */

public class HttpResponseEntry {

    private Map<String, String> headers;

    private int statusCode;
    private Object data;

    public HttpResponseEntry() {
        headers = new HashMap<>();
    }

    public String getResponseHeader(String headerKey) {
        return headers.get(headerKey);
    }

    public Map<String, String> getResponseHeaders(Map headers) {
        return headers;
    }

    public void addResponseHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void addResponseHeader(String headerKey, String headerVal) {
        this.headers.put(headerKey, headerVal);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
