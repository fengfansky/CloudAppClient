package com.rokid.cloudappclient.bean.response;

import com.rokid.cloudappclient.bean.request.session.SessionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;

/**
 * The response should be replied by CloudApps for client side execution.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class CloudAppResponse {

    private String version;
    private SessionBean session;
    private ResponseBean response;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

}
