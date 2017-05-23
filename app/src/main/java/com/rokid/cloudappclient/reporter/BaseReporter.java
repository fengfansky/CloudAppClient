package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.http.BaseParameter;
import com.rokid.cloudappclient.protro.SendEvent;
import com.rokid.cloudappclient.protro.SendEventCreator;
import com.rokid.cloudappclient.util.DeviceInfoUtil;

/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String event;
    String extra;

    @Override
    public void run() {
        report();
    }

    public void setEvent(String event) {
        this.event = event;
    }

    //TODO set extra value
    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void report() {
        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(DeviceInfoUtil.getAppId(), event, extra);
        BaseParameter baseParameter = new BaseParameter();
        baseParameter.setDeviceInfo(DeviceInfoUtil.getDeviceInfoBean());
        HttpClientWrapper.sendRequest(BaseUrlConfig.getUrl(baseParameter.generateParams()), eventRequest);
    }
}
