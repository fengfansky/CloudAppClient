package com.rokid.cloudappclient.util;

import com.rokid.cloudappclient.bean.DeviceInfoBean;

/**
 * Created by fanfeng on 2017/5/11.
 */

public class DeviceInfoUtil {

    private static final String APP_ID = "R0F4D9F07E9646BAA11B1CE36229B26B";

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
