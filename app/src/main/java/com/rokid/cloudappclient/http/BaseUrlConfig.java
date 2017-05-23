package com.rokid.cloudappclient.http;

import android.util.Log;

import com.rokid.cloudappclient.util.Logger;

import java.util.Map;

/**
 * Created by fanfeng on 2017/5/11.
 */

public class BaseUrlConfig {

    public static final String TAG = "BaseUrlConfig";

    public static final String BASE_URL = "https://apigwrest-dev.open.rokid.com";

    public static String getUrl(Map<String, String> params) {
        if (params == null || params.isEmpty())
            return BASE_URL;

        String baseUrl = BASE_URL + "?";

        String paramStr = getParamStr(params);

        return baseUrl.concat(paramStr);
    }


    public static String getParamStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        sb.deleteCharAt(sb.length() - 1);
        Log.d(TAG, "param : " + sb.toString());
        return sb.toString();
    }
}
