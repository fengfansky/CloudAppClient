package com.rokid.cloudappclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Created by fanfeng on 2017/5/15.
 */

public class DeviceInfoBean extends BaseBean implements Parcelable {

    private String key;
    private String deviceTypeId;
    private String deviceId;
    private String secret;
    private String api_version;

    protected DeviceInfoBean(Parcel in) {
        key = in.readString();
        deviceTypeId = in.readString();
        deviceId = in.readString();
        secret = in.readString();
    }

    public static final Creator<DeviceInfoBean> CREATOR = new Creator<DeviceInfoBean>() {
        @Override
        public DeviceInfoBean createFromParcel(Parcel in) {
            return new DeviceInfoBean(in);
        }

        @Override
        public DeviceInfoBean[] newArray(int size) {
            return new DeviceInfoBean[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(deviceTypeId);
        dest.writeString(deviceId);
        dest.writeString(secret);
    }
}
