package com.rokid.cloudappclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Created by fanfeng on 2017/5/15.
 */

public class DeviceInfoBean extends BaseBean implements Parcelable {

    private String server_address;
    private String ssl_roots_pem;
    private String key;
    private String device_type_id;
    private String device_id;
    private String api_version;
    private String secret;
    private String codec;

    protected DeviceInfoBean(Parcel in) {
        server_address = in.readString();
        ssl_roots_pem = in.readString();
        key = in.readString();
        device_type_id = in.readString();
        device_id = in.readString();
        api_version = in.readString();
        secret = in.readString();
        codec = in.readString();
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

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public String getSsl_roots_pem() {
        return ssl_roots_pem;
    }

    public void setSsl_roots_pem(String ssl_roots_pem) {
        this.ssl_roots_pem = ssl_roots_pem;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(String device_type_id) {
        this.device_type_id = device_type_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(server_address);
        dest.writeString(ssl_roots_pem);
        dest.writeString(key);
        dest.writeString(device_type_id);
        dest.writeString(device_id);
        dest.writeString(api_version);
        dest.writeString(secret);
        dest.writeString(codec);
    }
}
