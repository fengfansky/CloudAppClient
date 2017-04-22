package com.rokid.cloudappclient.manager.queue;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.manager.StateManager;
import com.rokid.cloudappclient.util.Logger;


/**
 * Created by fanfeng on 2017/4/20.
 */

public class MsgContainerManager implements IMsgQueue {


    private static volatile MsgContainerManager instance;

    private MsgContainer sceneContainer;
    private MsgContainer cutContainer;

    private MsgContainerManager() {
    }

    public static MsgContainerManager getInstance() {
        if (null == instance) {
            synchronized (MsgContainerManager.class) {
                if (null == instance) {
                    instance = new MsgContainerManager();
                }
            }
        }
        return instance;
    }


    @Override
    public void push(BaseTransferBean transferBean) {
        if (null == transferBean || !transferBean.isValid()) {
            Logger.d("ValidateObject is invalid!!!");
            return;
        }

        String domain = transferBean.getDomain();
        String shot = transferBean.getShot();
        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(shot)) {
            Logger.d("Domain or Shot is empty!!!");
            return;
        }

        MsgContainer msgContainer = null;

        switch (transferBean.getShot()) {
            case ResponseBean.SHOT_SCENE:
                if (null == sceneContainer || !transferBean.getDomain().equals(sceneContainer.domain)) {
                    sceneContainer = new MsgContainer(transferBean.getDomain());
                }
                msgContainer = sceneContainer;
                break;
            case ResponseBean.SHOT_CUT:
                if (null == cutContainer || !transferBean.getDomain().equals(cutContainer.domain)) {
                    cutContainer = new MsgContainer(transferBean.getDomain());
                }
                msgContainer = cutContainer;
                break;
        }

        msgContainer.push(transferBean);
    }

    @Override
    public BaseTransferBean poll(BaseTransferBean transferBean) {
        if (null == transferBean || !transferBean.isValid()) {
            Logger.d("ValidateObject is invalid!!!");
            return null;
        }
        String domain = transferBean.getDomain();
        String shot = transferBean.getShot();
        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(shot)) {
            Logger.d("Domain or Shot is empty!!!");
            return null;
        }

        Logger.d(String.format("pollVoice - %1$s %2$s", domain, shot));
        switch (shot) {
            case ResponseBean.SHOT_SCENE:
                if (null != sceneContainer && sceneContainer.domain.equals(domain)) {
                    return sceneContainer.poll(transferBean);
                }
            case ResponseBean.SHOT_CUT:
                if (null != cutContainer && cutContainer.domain.equals(domain)) {
                    return cutContainer.poll(transferBean);
                }
        }

        return null;
    }


    @Override
    public void clear() {
        String domain = StateManager.getInstance().getCurrentAppDomain();
        String shot = StateManager.getInstance().getCurrentAppShot();

        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(shot)) {
            Logger.d("Domain or Shot is empty!!!");
            return;
        }

        Logger.d(String.format("clearMedia - %1$s %2$s", domain, shot));
        switch (shot) {
            case ResponseBean.SHOT_SCENE:
                if (null != sceneContainer && sceneContainer.domain.equals(domain)) {
                    sceneContainer.clear();
                }
                break;
            case ResponseBean.SHOT_CUT:
                if (null != cutContainer && cutContainer.domain.equals(domain)) {
                    cutContainer.clear();
                }
                break;
        }
    }
}
