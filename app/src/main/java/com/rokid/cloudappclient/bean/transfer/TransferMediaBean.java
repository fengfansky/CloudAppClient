package com.rokid.cloudappclient.bean.transfer;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;

/**
 * Description: TODO
 * Author: xupan.shi
 * Version: V0.1 2017/3/14
 */
public class TransferMediaBean extends BaseTransferBean {

    private String mediaType;
    // TODO
    // private VideoPlayer videoPlayer;
    private MediaBean mediaBean;

    public TransferMediaBean(String domain, String shot, MediaBean mediaBean) {
        this.domain = domain;
        this.shot = shot;
        this.mediaBean = mediaBean;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public MediaBean getMediaBean() {
        return mediaBean;
    }

    public void setMediaBean(MediaBean mediaBean) {
        this.mediaBean = mediaBean;
    }

    public boolean isValid() {
        return super.isValid() && null != mediaBean && mediaBean.isValid();
    }
    
}
