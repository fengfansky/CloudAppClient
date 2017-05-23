package com.rokid.cloudappclient.util;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.DeviceInfoBean;

/**
 * Created by fanfeng on 2017/5/11.
 */

public class DeviceInfoUtil {

    private static final String APP_ID = "841f3558-f3d4-43f6-911a-6f80b62b352d";

    private static DeviceInfoBean deviceInfoBean;

    public static DeviceInfoBean getDeviceInfoBean() {
        return deviceInfoBean;
    }

    public static void setDeviceInfoBean(DeviceInfoBean deviceInfoBean) {
        DeviceInfoUtil.deviceInfoBean = deviceInfoBean;
    }

    /**
     * 获取appID
     * TODO 读取assets 中的appId
     */
    public static String getAppId() {
        return APP_ID;
    }

}
