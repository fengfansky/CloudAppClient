package com.rokid.cloudappclient.manager.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.manager.StateManager;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.TTSHelper;

/**
 * Created by fanfeng on 2017/4/20.
 */

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
        mTransfer.setTtsId(TTSHelper.getInstance().speakTTS(ttsContent,
                new TTSHelper.TTSCallback() {
                    @Override
                    public void onStart(int id) {
                        mTransfer.setTtsId(id);
                    }

                    @Override
                    public void onTTSFinish() {
                        Logger.d("Voice is completed!!!");
                        StateManager.getInstance().updateVoiceState(StateManager.VoiceState.STOPPED);

                    }
                })
        );
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
