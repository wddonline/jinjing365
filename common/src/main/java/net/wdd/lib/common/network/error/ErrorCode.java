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

/**
 * Created by wangdd on 16-11-26.
 */

public class ErrorCode {

    public static final int UNKNOW_ERROR = 10000;//未知错误

    public static final int AUTH_FAILURE_ERROR = 10001;//如果在做一个HTTP的身份验证，可能会发生这个错误。

    public static final int NETWORK_ERROR = 10002;//Socket关闭，服务器宕机，DNS错误都会产生这个错误。

    public static final int NO_CONNECTION_ERROR = 10003;//这个是客户端没有网络连接。

    public static final int PARSE_ERROR = 10004;//接收到的JSON是畸形

    public static final int SERVER_ERROR = 10005;//服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。

    public static final int TIMEOUT_ERROR = 10006;////Socket超时，服务器太忙或网络延迟会产生这个异常。

}
