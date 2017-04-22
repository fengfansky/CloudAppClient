package com.rokid.cloudappclient.msg.queue;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.action.VoiceAction;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/4/21.
 */

public class VoiceMsgQueue extends BaseMsgQueue<TransferVoiceBean> {

    @Override
    public void push(TransferVoiceBean voiceTransfer) {
        if (null == voiceTransfer || !voiceTransfer.isValid()) {
            Logger.d("Node or TransferVoiceBean is invalid!!!");
            return;
        }

        Logger.d("TransferVoiceBean: ", voiceTransfer.toString());

        String behaviour = voiceTransfer.getVoiceBean().getBehaviour();
        switch (behaviour) {
            case VoiceBean.BEHAVIOUR_CLEAR:
                VoiceAction.getInstance().stopPlay();
                msgQueue.clear();
                break;
            case VoiceBean.BEHAVIOUR_APPEND:
                msgQueue.add(voiceTransfer);
                break;
            case VoiceBean.BEHAVIOUR_REPLACE_APPEND:
                msgQueue.clear();
                msgQueue.add(voiceTransfer);
                break;
            case VoiceBean.BEHAVIOUR_REPLACE_ALL:
                VoiceAction.getInstance().stopPlay();
                msgQueue.clear();
                msgQueue.add(voiceTransfer);
                break;
        }
        Logger.d(String.format("voiceQueue size: %s", msgQueue.size()));

        VoiceAction.getInstance().notifyPlayFinished(voiceTransfer);
    }

}
