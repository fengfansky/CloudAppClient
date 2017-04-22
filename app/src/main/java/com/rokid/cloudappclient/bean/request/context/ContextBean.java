package com.rokid.cloudappclient.bean.request.context;

import com.rokid.cloudappclient.bean.request.context.device.DeviceBean;
import com.rokid.cloudappclient.bean.request.context.device.LocationBean;
import com.rokid.cloudappclient.bean.request.context.device.MediaBean;
import com.rokid.cloudappclient.bean.request.context.device.ScreenBean;

/**
 * Context indicates the main status of current device, the user bound and the application.
 * Information in Context may help CloudApps to determin further actions and to choose proper version of response.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class ContextBean {

    /**
     * The application info
     */
    private ApplicationBean application;
    /**
     * The device info
     */
    private DeviceBean device;
    /**
     * User info
     */
    private UserBean user;

    public ApplicationBean getApplication() {
        return application;
    }

    public void setApplication(ApplicationBean application) {
        this.application = application;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class Builder {

        private ContextBean contextBean;

        public Builder() {
            contextBean = new ContextBean();
        }

        public Builder application(ApplicationBean application) {
            contextBean.setApplication(application);
            return this;
        }

        public Builder application_Id(String applicationId) {
            checkApplication();
            contextBean.getApplication().setApplicationId(applicationId);
            return this;
        }

        public Builder device(DeviceBean device) {
            contextBean.setDevice(device);
            return this;
        }

        public Builder device_Id(String deviceId) {
            checkDevice();
            contextBean.getDevice().setDeviceId(deviceId);
            return this;
        }

        public Builder device_Type(String deviceType) {
            checkDevice();
            contextBean.getDevice().setDeviceType(deviceType);
            return this;
        }

        public Builder device_Vendor(String vendor) {
            checkDevice();
            contextBean.getDevice().setVendor(vendor);
            return this;
        }

        public Builder device_Screen(ScreenBean screen) {
            checkDevice();
            contextBean.getDevice().setScreen(screen);
            return this;
        }

        public Builder device_Screen_X(String x) {
            checkDeviceScreen();
            contextBean.getDevice().getScreen().setX(x);
            return this;
        }

        public Builder device_Screen_Y(String y) {
            checkDeviceScreen();
            contextBean.getDevice().getScreen().setY(y);
            return this;
        }

        public Builder device_Locale(String locale) {
            checkDevice();
            contextBean.getDevice().setLocale(locale);
            return this;
        }

        public Builder device_Timestamp(long timestamp) {
            checkDevice();
            contextBean.getDevice().setTimestamp(timestamp);
            return this;
        }

        public Builder device_Media(MediaBean media) {
            checkDevice();
            contextBean.getDevice().setMedia(media);
            return this;
        }

        public Builder device_Media_State(String state) {
            checkDeviceMedia();
            contextBean.getDevice().getMedia().setState(state);
            return this;
        }

        public Builder device_Location(LocationBean location) {
            checkDevice();
            contextBean.getDevice().setLocation(location);
            return this;
        }

        public Builder device_Location_Latitude(String latitude) {
            checkDeviceLocation();
            contextBean.getDevice().getLocation().setLatitude(latitude);
            return this;
        }

        public Builder device_Location_Longitude(String longitude) {
            checkDeviceLocation();
            contextBean.getDevice().getLocation().setLongitude(longitude);
            return this;
        }

        public Builder user(UserBean user) {
            contextBean.setUser(user);
            return this;
        }

        public Builder user_Id(String userId) {
            checkUser();
            contextBean.getUser().setUserId(userId);
            return this;
        }

        public ContextBean build() {
            return contextBean;
        }

        private void checkApplication() {
            if (null == contextBean.getApplication()) {
                contextBean.setApplication(new ApplicationBean());
            }
        }

        private void checkDevice() {
            if (null == contextBean.getDevice()) {
                contextBean.setDevice(new DeviceBean());
            }
        }

        private void checkDeviceScreen() {
            checkDevice();
            if (null == contextBean.getDevice().getScreen()) {
                contextBean.getDevice().setScreen(new ScreenBean());
            }
        }

        private void checkDeviceMedia() {
            checkDevice();
            if (null == contextBean.getDevice().getMedia()) {
                contextBean.getDevice().setMedia(new MediaBean());
            }
        }

        private void checkDeviceLocation() {
            checkDevice();
            if (null == contextBean.getDevice().getLocation()) {
                contextBean.getDevice().setLocation(new LocationBean());
            }
        }

        private void checkUser() {
            if (null == contextBean.getUser()) {
                contextBean.setUser(new UserBean());
            }
        }

    }

}
