package com.rokid.cloudappclient.msg.manager;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.queue.IMsgQueue;
import com.rokid.cloudappclient.msg.queue.MediaMsgQueue;
import com.rokid.cloudappclient.msg.queue.VoiceMsgQueue;

/**
 * Created by fanfeng on 2017/4/21.
 */

public class MsgContainer implements IMsgQueue<BaseTransferBean> {

    public MediaMsgQueue mediaMsgQueue;
    public VoiceMsgQueue voiceMsgQueue;

    public String domain;

    public MsgContainer(String domain){
        this.domain = domain;
        mediaMsgQueue = new MediaMsgQueue();
        voiceMsgQueue = new VoiceMsgQueue();
    }

    @Override
    public void push(BaseTransferBean transferBean) {
        if (transferBean instanceof TransferMediaBean) {
            mediaMsgQueue.push((TransferMediaBean) transferBean);
        } else if (transferBean instanceof TransferVoiceBean){
            voiceMsgQueue.push((TransferVoiceBean) transferBean);
        }
    }

    @Override
    public BaseTransferBean poll(BaseTransferBean transferBean) {
        if (transferBean instanceof TransferMediaBean){
            return mediaMsgQueue.poll();
        }else if (transferBean instanceof TransferVoiceBean){
            return voiceMsgQueue.poll();
        }
        return null;
    }

    @Override
    public void clear() {
        mediaMsgQueue.clear();
        voiceMsgQueue.clear();
    }
}
