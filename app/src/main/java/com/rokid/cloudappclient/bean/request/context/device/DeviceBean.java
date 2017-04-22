package com.rokid.cloudappclient.bean.request.context.device;

/**
 * DeviceInfo presents the main status of the device that created current request.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class DeviceBean {

    private String deviceId;
    private String deviceType;
    private String vendor;
    private ScreenBean screen;
    private String locale;
    private long timestamp;
    private MediaBean media;
    private LocationBean location;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public ScreenBean getScreen() {
        return screen;
    }

    public void setScreen(ScreenBean screen) {
        this.screen = screen;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MediaBean getMedia() {
        return media;
    }

    public void setMedia(MediaBean media) {
        this.media = media;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

}
