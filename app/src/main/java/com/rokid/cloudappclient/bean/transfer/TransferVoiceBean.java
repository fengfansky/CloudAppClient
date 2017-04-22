package com.rokid.cloudappclient.bean.transfer;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;

/**
 * Description: TODO
 * Author: xupan.shi
 * Version: V0.1 2017/3/14
 */
public class TransferVoiceBean extends BaseTransferBean {

    private int ttsId = -1;
    private VoiceBean voiceBean;

    public TransferVoiceBean(String domain, String shot, VoiceBean voiceBean) {
        this.domain = domain;
        this.shot = shot;
        this.voiceBean = voiceBean;
    }

    public int getTtsId() {
        return ttsId;
    }

    public void setTtsId(int ttsId) {
        this.ttsId = ttsId;
    }

    public VoiceBean getVoiceBean() {
        return voiceBean;
    }

    public void setVoiceBean(VoiceBean voiceBean) {
        this.voiceBean = voiceBean;
    }

    public boolean isValid() {
        return super.isValid() && null != voiceBean && voiceBean.isValid();
    }

}
