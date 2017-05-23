package com.rokid.cloudappclient.http;

import com.rokid.cloudappclient.protro.SendEvent;
import com.rokid.cloudappclient.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

/**
 * Created by fanfeng on 2017/5/11.
 */

public class HttpClientWrapper {

    private static OkHttpClient okHttpClient;
    private static final int CONNECTION_TIME_OUT = 5;
    private static final int READ_TIME_OUT = 5;
    private static final int WRITE_TIME_OUT = 5;

    private static final String CONTENT_TYPE = "application/octet-stream";

    public HttpClientWrapper() {

        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    public static HttpClientWrapper getInstance() {
        return SingleHolder.instance;
    }

    public static void sendRequest(String url, SendEvent.SendEventRequest eventRequest) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            eventRequest.writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "text/plain")
                .addHeader("Accept-Charset", "utf-8")
                .addHeader("Cache-Control", "no-cache")
                .post(RequestBody.create(MediaType.parse(CONTENT_TYPE)
                        , byteArrayOutputStream.toByteArray()))
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            Logger.d("sendRequest response callback is null!");
        } else if (!response.isSuccessful()) {
            Logger.d("sendRequest response not success! response : " + response);
        } else {
            Logger.d("sendRequest response success! response : " + response);
        }
    }

    private static class SingleHolder {
        private static final HttpClientWrapper instance = new HttpClientWrapper();
    }

}
