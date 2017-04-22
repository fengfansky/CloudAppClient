package com.rokid.cloudappclient.bean;

import com.rokid.cloudappclient.bean.response.CloudAppResponse;

/**
 * Created by showingcp on 3/13/17.
 */

public class CommonResponse {

    /**
     * corresponding asr result for current response
     */
    private String asr;

    /**
     * corresponding nlp result for current response
     */
    private NLPBean nlp;

    /**
     * corresponding CloudAppResponse for current response
     */
    private CloudAppResponse action;

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

    public CloudAppResponse getAction() {
        return action;
    }

    public void setAction(CloudAppResponse action) {
        this.action = action;
    }
}
