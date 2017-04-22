package com.rokid.cloudappclient.msg.action;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.msg.manager.MsgContainerManager;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseTransferBean> {

    T mTransfer;

    public void setDataSource(T transfer){
        Logger.d("setDataSource transfer == null ? " + String.valueOf(transfer == null));
        if (transfer == null){
            return;
        }

        mTransfer = transfer;
        this.startPlay();
    }

    public abstract void startPlay();

    public abstract void pausePlay();

    public abstract void stopPlay();

    public void notifyPlayFinished(T transfer) {
        T mediaTemp = (T) MsgContainerManager.getInstance().poll(transfer);

        if (null != mediaTemp) {
            setDataSource(mediaTemp);
        }
    }

}
