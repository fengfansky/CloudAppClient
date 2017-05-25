package com.rokid.cloudappclient.msg.queue;

import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.msg.action.MediaAction;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/4/21.
 */

public class MediaMsgQueue extends BaseMsgQueue<TransferMediaBean> {

    @Override
    public void push(TransferMediaBean mediaTransfer) {
        if (null == mediaTransfer || !mediaTransfer.isValid()) {
            Logger.d("Node or TransferMediaBean is invalid!!!");
            return;
        }
        Logger.d("MediaMsgQueue.push mediaTransfer: " + mediaTransfer);

        String behaviour = mediaTransfer.getMediaBean().getBehaviour();
        switch (behaviour) {
            case MediaBean.BEHAVIOUR_CLEAR:
                MediaAction.getInstance().stopAction();
                msgQueue.clear();
                break;
            case MediaBean.BEHAVIOUR_APPEND:
                msgQueue.add(mediaTransfer);
                break;
            case MediaBean.BEHAVIOUR_REPLACE_APPEND:
                msgQueue.clear();
                msgQueue.add(mediaTransfer);
                break;
            case MediaBean.BEHAVIOUR_REPLACE_ALL:
                MediaAction.getInstance().stopAction();
                msgQueue.clear();
                msgQueue.add(mediaTransfer);
                break;
        }
        Logger.d(String.format("MediaMsgQueue queue size: %s", msgQueue.size()));

        MediaAction.getInstance().notifyPlayFinished(mediaTransfer);
    }

}
