package com.rokid.cloudappclient.msg.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.tts.TTSHelper;

public class VoiceAction extends BaseAction<TransferVoiceBean> {

    private static volatile VoiceAction voiceAction;

    public static VoiceAction getInstance() {
        if (voiceAction == null) {
            synchronized (VoiceAction.class) {
                if (voiceAction == null)
                    voiceAction = new VoiceAction();
            }
        }
        return voiceAction;
    }

    @Override
    public synchronized void startPlay() {
        if (null == mTransfer || !mTransfer.isValid()) {
            Logger.d("Now have a voice in running or TransferVoiceBean is empty.");
            return;
        }

        // To check whether the voiceBean have confirm, if have confirm speak confirm TTS.
        VoiceItemBean VoiceItemBean = mTransfer.getVoiceBean().getItem();
        String ttsContent;
        if (VoiceItemBean.isConfirmValid()) {
            ttsContent = VoiceItemBean.getConfirm().getTts();
        } else {
            ttsContent = VoiceItemBean.getTts();
        }

        StateManager.getInstance().updateVoiceState(StateManager.VoiceState.PLAYING);
        int ttsId = TTSHelper.getInstance().speakTTS(ttsContent);
        mTransfer.setTtsId(ttsId);
    }

    @Override
    public synchronized void pausePlay() {
        //do nothing
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d("stopVoice");

        if (mTransfer == null) {
            Logger.d("voiceTransfer == null");
            return;
        }

        Logger.d("Stop the current voice!");
        TTSHelper.getInstance().stopTTS(mTransfer.getTtsId());
        StateManager.getInstance().updateVoiceState(StateManager.VoiceState.STOPPED);
    }

}
