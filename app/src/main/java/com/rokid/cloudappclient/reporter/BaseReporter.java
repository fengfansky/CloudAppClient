package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.http.BaseParameter;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.proto.SendEventCreator;
import com.rokid.cloudappclient.util.DeviceInfoUtil;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;


/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String event;
    String extra;

    public BaseReporter(String event, String extra) {
        this.event = event;
        this.extra = extra;
    }

    private ReporterResponseCallBack mCallBack;

    public void setOnResponseCallback(ReporterResponseCallBack callback) {
        if (callback != null) {
            mCallBack = callback;
        }
    }

    public void setEvent(String event) {
        this.event = event;
    }

    //TODO set extra value
    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getEvent() {
        return event;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public void run() {
        report();
    }

    public void report() {
        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(DeviceInfoUtil.getAppId(), event, extra);
        BaseParameter baseParameter = new BaseParameter();
        baseParameter.setDeviceInfo(DeviceInfoUtil.getDeviceInfoBean());
        Logger.d("deviceInfo : " + DeviceInfoUtil.getDeviceInfoBean());
        HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), baseParameter, eventRequest);
    }

    public interface ReporterResponseCallBack {
        void callBack(Response response);
    }
}
