package com.rokid.cloudappclient.msg.action;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.msg.manager.MsgContainerManager;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseTransferBean> {

    T mTransfer;

    public abstract void startAction();

    public abstract void stopAction();

    public synchronized void notifyPlayFinished(T transfer) {
        T mediaTemp = (T) MsgContainerManager.getInstance().poll(transfer);

        if (null != mediaTemp) {
            mTransfer = transfer;
        }
        this.startAction();

    }

}
