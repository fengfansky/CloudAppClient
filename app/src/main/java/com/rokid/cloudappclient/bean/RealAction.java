package com.rokid.cloudappclient.bean;

import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;

/**
 * Created by showingcp on 3/16/17.
 */

public class RealAction {
    private boolean valid;
    private String asr;
    private NLPBean nlp;
    private String respId;
    private String resType;
    private String domain;
    private String shot;
    private String actionType;
    private boolean shouldEndSession;
    private VoiceBean voice;
    private MediaBean media;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public NLPBean getNlp() {
        return nlp;
    }

    public void setNlp(NLPBean nlp) {
        this.nlp = nlp;
    }

    public String getRespId() {
        return respId;
    }

    public void setRespId(String respId) {
        this.respId = respId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String respType) {
        this.resType = respType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getShot() {
        return shot;
    }

    public void setShot(String shot) {
        this.shot = shot;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public boolean isShouldEndSession() {
        return shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
    }

    public VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceBean voice) {
        this.voice = voice;
    }

    public MediaBean getMedia() {
        return media;
    }

    public void setMedia(MediaBean media) {
        this.media = media;
    }
}
