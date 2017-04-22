package com.rokid.cloudappclient.manager.action;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.manager.queue.MsgContainerManager;
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

    public void notifyPlayFinish(T transfer) {
        T mediaTemp = (T) MsgContainerManager.getInstance().poll(transfer);

        if (null != mediaTemp) {
            setDataSource(mediaTemp);
        }
    }

}
