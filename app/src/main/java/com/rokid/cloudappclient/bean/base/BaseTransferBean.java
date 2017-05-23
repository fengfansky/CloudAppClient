package com.rokid.cloudappclient.bean.base;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;

/**
 * Description: TODO
 * Author: xupan.shi
 * Version: V0.1 2017/3/16
 */
public abstract class BaseTransferBean extends BaseBean {

    protected String domain;
    protected String shot;

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

    public boolean isValid() {
        return !TextUtils.isEmpty(domain) && isShotValid();
    }

    public boolean isShotValid() {
        return !TextUtils.isEmpty(shot)
                && (ActionBean.FORM_SCENE.equals(shot) || ActionBean.FORM_CUT.equals(shot));
    }

}
