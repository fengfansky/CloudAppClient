package com.rokid.cloudappclient.http;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.DeviceInfoBean;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.MD5Utils;
import com.rokid.cloudappclient.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/15.
 */

public class BaseParameter {

    private static final String SERVICE = "rest";
    public static final String PARAM_KEY_KEY = "key";
    public static final String PARAM_KEY_DEVICE_TYPE_ID = "device_type_id";
    public static final String PARAM_KEY_DEVICE_ID = "device_id";
    public static final String PARAM_KEY_SERVICE = "service";
    public static final String PARAM_KEY_VERSION = "version";
    public static final String PARAM_KEY_TIME = "time";
    public static final String PARAM_KEY_SIGN = "sign";

    Map<String, String> params = new HashMap<>();

    DeviceInfoBean deviceInfo;

    public void setDeviceInfo(DeviceInfoBean deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void putUnEmptyParam(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            Logger.d("param invalidate ! key " + key + " value : " + value);
            return;
        }
        params.put(key, value);
    }

    public Map<String, String> generateParams() {
        if (deviceInfo == null) {
            Logger.d("deviceInfo is null");
            return null;
        }
        putUnEmptyParam(PARAM_KEY_KEY, deviceInfo.getKey());
        putUnEmptyParam(PARAM_KEY_DEVICE_TYPE_ID, deviceInfo.getDevice_type_id());
        putUnEmptyParam(PARAM_KEY_DEVICE_ID, deviceInfo.getDevice_id());
        putUnEmptyParam(PARAM_KEY_VERSION, deviceInfo.getApi_version());
        putUnEmptyParam(PARAM_KEY_TIME, TimeUtils.getCurrentTimeStamp());
        putUnEmptyParam(PARAM_KEY_SERVICE, SERVICE);

        putUnEmptyParam(PARAM_KEY_SIGN, MD5Utils.generateMD5(params));
        return params;
    }

}
