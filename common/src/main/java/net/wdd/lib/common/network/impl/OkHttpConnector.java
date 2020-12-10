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

package net.wdd.lib.common.network.impl;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import net.wdd.lib.common.R;
import net.wdd.lib.common.network.ActivityFragmentActive;
import net.wdd.lib.common.network.FileDownloadCallback;
import net.wdd.lib.common.network.FileEntity;
import net.wdd.lib.common.network.HttpConnectCallback;
import net.wdd.lib.common.network.HttpConnector;
import net.wdd.lib.common.network.HttpRequestEntry;
import net.wdd.lib.common.network.HttpResponseEntry;
import net.wdd.lib.common.network.HttpSession;
import net.wdd.lib.common.network.StatusCode;
import net.wdd.lib.common.network.error.ErrorCode;
import net.wdd.lib.common.network.error.HttpError;
import net.wdd.lib.common.network.utils.SerializeType;
import net.wdd.lib.common.network.utils.Utils;
import net.wdd.lib.common.utils.AppUtils;
import net.wdd.lib.common.utils.LogUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangdd on 16-11-26.
 */

public class OkHttpConnector implements HttpConnector {

    private final String TAG = "OkHttpConnector";
    private final int TIME_OUT = 60;

    private Context mContext;
    private OkHttpClient mHttpClient;

    public OkHttpConnector(Context context) {
        this.mContext = context;
        File file = new File(context.getExternalCacheDir(), "cache/data");
        Cache cache = new Cache(file, 100 * 1024 * 1024);
        //有网时候的缓存
        final Interceptor netCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                int onlineCacheTime = 0;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
                return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + onlineCacheTime)
                    .removeHeader("Pragma")
                    .build();
            }
        };

        //没有网时候的缓存
        final Interceptor offlineCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!AppUtils.isNetworkAvalible(mContext)) {
                    int offlineCacheTime = Integer.MAX_VALUE;//离线的时候的缓存的过期时间
                    request = request.newBuilder()
//                        .cacheControl(new CacheControl
//                                .Builder()
//                                .maxStale(60,TimeUnit.SECONDS)
//                                .onlyIfCached()
//                                .build()
//                        ) 两种方式结果是一样的，写法不同
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                        .build();
                }
                return chain.proceed(request);
            }
        };
        mHttpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(netCacheInterceptor)
            .addInterceptor(offlineCacheInterceptor)
            .cache(cache)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }

    @Override
    public HttpSession sendHttpRequest(final ActivityFragmentActive host, final HttpRequestEntry requestEntry,
                                       final SerializeType type, final HttpConnectCallback callback) {
        final Request request = buildHttpRequest(requestEntry);
        Call call = mHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    return;
                }
                if (host != null && !host.isActive()) {
                    return;
                }

                dispatchHttpError(host, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }
                if (host != null && !host.isActive()) {
                    return;
                }
                Map<String, String> headers = new HashMap<>();
                Headers okHeaders = response.headers();
                Set<String> headerNames = okHeaders.names();
                for (String headerName : headerNames) {
                    headers.put(headerName, okHeaders.get(headerName));
                }
                if (response.isSuccessful()) {
                    String text = response.body().string();
                    handleResponse(headers, text, type, callback);
                } else {
                    int code = response.code();
                    HttpError error;
                    if (!AppUtils.isNetworkAvalible(mContext) && code == 504) {
                        error =
                            new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, "Server Error:" + response.code());
                    } else {
                        error = new HttpError(mContext, ErrorCode.SERVER_ERROR, "Server Error:" + response.code());
                    }
                    callback.onRequestFailure(error);
                }
            }
        });
        HttpSession session = new OkHttpSession(call, requestEntry);
        return session;
    }

    @Override
    public HttpResponseEntry sendSyncHttpRequest(HttpRequestEntry requestEntry, SerializeType type) throws HttpError {
        Request request = buildHttpRequest(requestEntry);
        Call call = mHttpClient.newCall(request);
        try {
            Response response = call.execute();
            Map<String, String> headers = new HashMap<>();
            Headers okHeaders = response.headers();
            Set<String> headerNames = okHeaders.names();
            for (String headerName : headerNames) {
                headers.put(headerName, okHeaders.get(headerName));
            }
            if (response.isSuccessful()) {
                String text = response.body().string();
                JSONObject json = JSON.parseObject(text);
                int status = json.getInteger("code");
                if (status == 200) {//请求成功
                    HttpResponseEntry responseEntry = new HttpResponseEntry();
                    responseEntry.addResponseHeaders(headers);
                    responseEntry.setStatusCode(StatusCode.HTTP_OK);

                    String segment = json.getString("data");
                    if (!TextUtils.isEmpty(segment)) {
                        Object data;
                        if (segment.equals("{}") || segment.equals("[]")) {
                            if (type.isMulti()) {
                                data = new ArrayList();
                            } else {
                                data = null;
                            }
                        } else {
                            if (type.isMulti()) {//是json数组
                                JSONArray array = JSON.parseArray(segment);
                                List<Object> list = new ArrayList<>();
                                for (int i = 0; i < array.size(); i++) {
                                    list.add(JSON.parseObject(array.getString(i), type.getType()));
                                }
                                data = list;
                            } else {//是json对象
                                data = JSON.parseObject(segment, type.getType());
                            }
                        }
                        responseEntry.setData(data);
                    }
                    return responseEntry;
                } else {//请求失败
                    HttpError error = new HttpError(mContext, status, json.getString("message"));
                    throw error;
                }
            } else {
                int code = response.code();
                HttpError error;
                if (!AppUtils.isNetworkAvalible(mContext) && code == 504) {
                    error = new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, "Server Error:" + response.code());
                } else {
                    error = new HttpError(mContext, ErrorCode.SERVER_ERROR, "Server Error:" + response.code());
                }
                throw error;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw handleHttpError(e);
        }
    }

    @Override
    public HttpSession sendHttpRequest(final ActivityFragmentActive host, final HttpRequestEntry requestEntry,
                                       final HttpConnectCallback callback) {
        final Request request = buildHttpRequest(requestEntry);
        Call call = mHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    return;
                }
                if (host != null && !host.isActive()) {
                    return;
                }
                dispatchHttpError(host, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }
                if (host != null && !host.isActive()) {
                    return;
                }
                Map<String, String> headers = new HashMap<>();
                Headers okHeaders = response.headers();
                Set<String> headerNames = okHeaders.names();
                for (String headerName : headerNames) {
                    headers.put(headerName, okHeaders.get(headerName));
                }
                if (response.isSuccessful()) {
                    String text = response.body().string();
                    handleResponse(headers, text, callback);
                } else {
                    int code = response.code();
                    HttpError error;
                    if (!AppUtils.isNetworkAvalible(mContext) && code == 504) {
                        error =
                            new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, "Server Error:" + response.code());
                    } else {
                        error = new HttpError(mContext, ErrorCode.SERVER_ERROR, "Server Error:" + response.code());
                    }
                    callback.onRequestFailure(error);
                }
            }
        });
        HttpSession session = new OkHttpSession(call, requestEntry);
        return session;
    }

    @Override
    public HttpSession downloadFile(ActivityFragmentActive host, HttpRequestEntry requestEntry, String localName) {
        return downloadFile(host, requestEntry, localName, null);
    }

    @Override
    public HttpSession downloadFile(final ActivityFragmentActive host, HttpRequestEntry requestEntry,
                                    final String localName,
                                    final FileDownloadCallback callback) {
        final String fileName;
        if (TextUtils.isEmpty(localName)) {
            fileName = host.getContext().getExternalCacheDir().getAbsolutePath() + File.separator +
                Utils.md5(requestEntry.getUrl());
        } else {
            fileName = localName;
        }
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        final Request request = buildHttpRequest(requestEntry);
        final Call call = mHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback == null) {
                    return;
                }
                if (host != null && !host.isActive()) {
                    return;
                }
                HttpError error = new HttpError(mContext, ErrorCode.SERVER_ERROR, e.getMessage());
                callback.onDownloadProgressFailure(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (host != null && !host.isActive()) {
                    return;
                }
                if (response.isSuccessful()) {
                    Map<String, String> headers = new HashMap<>();
                    Headers okHeaders = response.headers();
                    Set<String> headerNames = okHeaders.names();
                    for (String headerName : headerNames) {
                        headers.put(headerName, okHeaders.get(headerName));
                    }
                    String fileName;
                    if (!localName.contains(".")) {
                        String contentType = headers.get("Content-SerializeType");
                        String ext;
                        if (TextUtils.isEmpty(contentType)) {
                            ext = "jpg";
                        } else {
                            String[] pieces = contentType.split(";");
                            if (pieces.length > 1) {
                                ext = pieces[1].split("/")[1];
                            } else {
                                ext = pieces[0].split("/")[1];
                            }
                        }
                        fileName = localName + "." + ext;
                    } else {
                        fileName = localName;
                    }

                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        long total = response.body().contentLength();
                        long progress = 0;
                        is = response.body().byteStream();
                        File file = new File(fileName);
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            progress += len;
                            fos.write(buf, 0, len);
                            if (callback == null) {
                                continue;
                            }
                            callback.onDownloadProgressUpdate(progress, total);
                        }
                        fos.flush();

                        HttpResponseEntry responseEntry = new HttpResponseEntry();
                        responseEntry.addResponseHeaders(headers);
                        responseEntry.setStatusCode(StatusCode.HTTP_OK);
                        responseEntry.setData(fileName);
                        if (callback != null) {
                            callback.onDownloadSuccess(responseEntry);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            HttpError error = handleHttpError(e);
                            callback.onDownloadProgressFailure(error);
                        }
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (callback == null) {
                        return;
                    }
                    int code = response.code();
                    HttpError error;
                    if (!AppUtils.isNetworkAvalible(mContext) && code == 504) {
                        error =
                            new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, "Server Error:" + response.code());
                    } else {
                        error = new HttpError(mContext, ErrorCode.SERVER_ERROR, "Server Error:" + response.code());
                    }
                    callback.onDownloadProgressFailure(error);
                }
            }
        });
        HttpSession session = new OkHttpSession(call, requestEntry);
        return session;
    }

    @Override
    public HttpResponseEntry sendSyncHttpRequest(final HttpRequestEntry requestEntry) throws HttpError {
        Request request = buildHttpRequest(requestEntry);
        Call call = mHttpClient.newCall(request);
        try {
            Response response = call.execute();
            Map<String, String> headers = new HashMap<>();
            Headers okHeaders = response.headers();
            Set<String> headerNames = okHeaders.names();
            for (String headerName : headerNames) {
                headers.put(headerName, okHeaders.get(headerName));
            }
            if (response.isSuccessful()) {
                String text = response.body().string();
                JSONObject json = JSON.parseObject(text);
                int status = json.getInteger("code");
                if (status == 200) {//请求成功
                    String data = json.getString("data");
                    HttpResponseEntry responseEntry = new HttpResponseEntry();
                    responseEntry.addResponseHeaders(headers);
                    responseEntry.setStatusCode(StatusCode.HTTP_OK);
                    responseEntry.setData(data);
                    return responseEntry;
                } else {//请求失败
                    HttpError error = new HttpError(mContext, status, json.getString("message"));
                    throw error;
                }
            } else {
                int code = response.code();
                HttpError error;
                if (!AppUtils.isNetworkAvalible(mContext) && code == 504) {
                    error = new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, "Server Error:" + response.code());
                } else {
                    error = new HttpError(mContext, ErrorCode.SERVER_ERROR, "Server Error:" + response.code());
                }
                throw error;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw handleHttpError(e);
        }
    }

    private Request buildHttpRequest(HttpRequestEntry requestEntry) {
        Request.Builder builder = new Request.Builder();
        if (requestEntry.getRequestHeaders().size() > 0) {
            Set<String> keys = requestEntry.getRequestHeaders().keySet();
            Iterator<String> it = keys.iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                builder.addHeader(key, requestEntry.getRequestHeaders().get(key));
            }
        }
        Request request = null;
        if (requestEntry.getMethod() == HttpRequestEntry.Method.GET) {
            String realUrl = generateGetUrl(requestEntry.getUrl(), requestEntry.getRequestParams());
            request = builder.url(realUrl).build();
            LogUtils.e(TAG, realUrl);
        } else {
            builder.url(requestEntry.getUrl());
            LogUtils.e(TAG, requestEntry.getUrl());
            MediaType mediaType;
            Map<String, Object> params;
            File file;
            switch (requestEntry.getFormat()) {
                case JSON:
                    mediaType = MediaType.parse("application/json; charset=utf-8");
                    String jsonStr = generatePostJson(requestEntry.getRequestParams());
                    if (requestEntry.getRequestParams().size() == 0) {
                        jsonStr = requestEntry.getRequestJson();
                    }
                    LogUtils.e(TAG, jsonStr);
                    request = builder.post(RequestBody.create(mediaType, jsonStr)).build();
                    break;
                case FORM:
                    LogUtils.e(TAG, requestEntry.getRequestParams().toString());
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    params = requestEntry.getRequestParams();
                    if (params != null && !params.isEmpty()) {
                        Set<String> set = params.keySet();
                        for (String key : set) {
                            try {
                                formBuilder.add(key, params.get(key).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    RequestBody requestBody = formBuilder.build();
                    builder.post(requestBody);
                    request = builder.build();
                    break;
                case FILE:
                    mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                    file = new File(requestEntry.getRequestParams().get("file").toString());
                    if (file.exists()) {
                        request = builder.post(RequestBody.create(mediaType, file)).build();
                    } else {
                        request = builder.post(RequestBody.create(mediaType, new byte[] {})).build();
                    }
                    break;
                case MULTIPART:
                    LogUtils.e(TAG, requestEntry.getRequestParams().toString());
                    MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
                    multiBuilder.setType(MultipartBody.FORM);
                    params = requestEntry.getRequestParams();
                    if (params != null && !params.isEmpty()) {
                        RequestBody fileBody;
                        for (String key : params.keySet()) {
                            if (key.equalsIgnoreCase(FileEntity.KEY_HTTP_FILE)) {
                                FileEntity fileEntity = (FileEntity) params.get(FileEntity.KEY_HTTP_FILE);
                                for (String filepath : fileEntity.getFilePaths()) {
                                    file = new File(filepath);
                                    if (file.exists()) {
                                        fileBody =
                                            RequestBody.create(MediaType.parse("application/octet-stream"), file);
                                        try {
                                            multiBuilder.addFormDataPart(fileEntity.getName(),
                                                URLEncoder.encode(file.getName(), "UTF-8"), fileBody);
//                                            multiBuilder.addFormDataPart(fileEntity.getName(), file.getName(),
//                                            fileBody, file));
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                multiBuilder.addFormDataPart(key, params.get(key).toString());
//                                multiBuilder.addPart(
//                                        Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
//                                        RequestBody.create(null, params.get(key).toString()));
                            }
                        }
                    }
                    RequestBody multiBody = multiBuilder.build();
                    request = builder.post(multiBody).build();
                    break;
            }
        }
        LogUtils.e(TAG, requestEntry.getRequestHeaders().toString());
        return request;
    }

    private void dispatchHttpError(ActivityFragmentActive host, IOException e, HttpConnectCallback callback) {
        HttpError error;
        if (e instanceof UnknownHostException) {
            error = new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, e.getMessage());
        } else if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
            if (host != null && host.getContext() != null) {
                error = new HttpError(mContext, ErrorCode.TIMEOUT_ERROR,
                    host.getContext().getString(R.string.error_connect_time_out));
            } else {
                error = new HttpError(mContext, ErrorCode.NETWORK_ERROR, e.getMessage());
            }
        } else {
            if (host != null && host.getContext() != null) {
                error = new HttpError(mContext, ErrorCode.NETWORK_ERROR,
                    host.getContext().getString(R.string.error_connect_server_error));
            } else {
                error = new HttpError(mContext, ErrorCode.NETWORK_ERROR, e.getMessage());
            }
        }
        callback.onRequestFailure(error);
    }

    private HttpError handleHttpError(IOException e) {
        HttpError error = null;
        if (e instanceof UnknownHostException) {
            error = new HttpError(mContext, ErrorCode.NO_CONNECTION_ERROR, e.getMessage());
        } else if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
            error = new HttpError(mContext, ErrorCode.TIMEOUT_ERROR, e.getMessage());
        } else {
            error = new HttpError(mContext, ErrorCode.NETWORK_ERROR, e.getMessage());
        }
        return error;
    }

    private void handleResponse(Map<String, String> headers, String txt, SerializeType type,
                                HttpConnectCallback callback) {
//        try {
//            txt = EncryptionUtils.decrypt3DES(txt, Constant.DES3_KEY.getBytes("UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        LogUtils.e(TAG, txt);
        JSONObject json = JSON.parseObject(txt);
        int status = json.getInteger("code");
        if (status == 200) {//请求成功
            HttpResponseEntry responseEntry = new HttpResponseEntry();
            responseEntry.addResponseHeaders(headers);
            responseEntry.setStatusCode(StatusCode.HTTP_OK);

            String segment = json.getString("data");
            if (!TextUtils.isEmpty(segment)) {
                Object data;
                if (segment.equals("{}") || segment.equals("[]")) {
                    if (type.isMulti()) {
                        data = new ArrayList();
                    } else {
                        data = null;
                    }
                } else {
                    if (type.isMulti()) {//是json数组
                        JSONArray array = JSON.parseArray(segment);
                        List<Object> list = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            list.add(JSON.parseObject(array.getString(i), type.getType()));
                        }
                        data = list;
                    } else {//是json对象
                        data = JSON.parseObject(segment, type.getType());
                    }
                }
                responseEntry.setData(data);
            }
            callback.onRequestOk(responseEntry);
        } else {//请求失败
            HttpError error = new HttpError(mContext, status, json.getString("message"));
            callback.onRequestFailure(error);
        }
    }

    private void handleResponse(Map<String, String> headers, String txt, HttpConnectCallback callback) {
//        try {
//            txt = EncryptionUtils.decrypt3DES(txt, Constant.DES3_KEY.getBytes("UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        LogUtils.e(TAG, txt);
        try {
            JSONObject json = JSON.parseObject(txt);
            int status = json.getInteger("code");

            if (status == 200) {//请求成功
                String data = json.getString("data");
                HttpResponseEntry responseEntry = new HttpResponseEntry();
                responseEntry.addResponseHeaders(headers);
                responseEntry.setStatusCode(StatusCode.HTTP_OK);
                responseEntry.setData(data);
                callback.onRequestOk(responseEntry);
            } else {//请求失败
                HttpError error = new HttpError(mContext, status, json.getString("message"));
                callback.onRequestFailure(error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            HttpResponseEntry responseEntry = new HttpResponseEntry();
            responseEntry.addResponseHeaders(headers);
            responseEntry.setStatusCode(StatusCode.HTTP_OK);
            responseEntry.setData(txt);
            callback.onRequestOk(responseEntry);
        }
    }

    private String generateGetUrl(String url, Map<String, Object> params) {
        StringBuffer urlBuff = new StringBuffer(url);
        urlBuff.append("?");
        Set<String> keys = params.keySet();
        Iterator<String> it = keys.iterator();
        String key;
        String value;
        while (it.hasNext()) {
            key = it.next();
            try {
                value = params.get(key).toString();
            } catch (Exception ex) {
                value = "";
            }
            urlBuff.append(key + "=" + value + "&");
        }
        return urlBuff.subSequence(0, urlBuff.length() - 1).toString();
    }

    private String generatePostJson(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        JSONObject json = new JSONObject(params);
        return json.toJSONString();
    }

    @Override
    public void stopAllSession() {
        try {
            mHttpClient.dispatcher().cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopSessionByTag(Object tag) {
        try {
            for (Call call : mHttpClient.dispatcher().queuedCalls()) {
                if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            }
            for (Call call : mHttpClient.dispatcher().runningCalls()) {
                if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}