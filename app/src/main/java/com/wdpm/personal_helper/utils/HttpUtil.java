package com.wdpm.personal_helper.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class HttpUtil {

    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例化对象

    static {
        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
    }

    public static void get(String urlString, JsonHttpResponseHandler jsonRes) {
        client.get(urlString, jsonRes);
    }

    public static void get(String urlString, AsyncHttpResponseHandler res) {
        client.get(urlString, res);
    }

    public static void post(String urlString, RequestParams params,
                            AsyncHttpResponseHandler res) {
        client.post(urlString, params, res);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }



}