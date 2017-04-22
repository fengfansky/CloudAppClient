package com.rokid.cloudappclient.manager.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.manager.StateManager;
import com.rokid.cloudappclient.util.AudioPlayerUtils;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.rkaudioplayer.state.RKAudioState;

/**
 * Created by fanfeng on 2017/4/20.
 */

public class MediaAction extends BaseAction<TransferMediaBean> {


    private static volatile MediaAction mediaAction;

    private MediaAction() {

    }

    public static MediaAction getInstance() {
        if (mediaAction == null) {
            synchronized (MediaAction.class) {
                if (mediaAction == null)
                    mediaAction = new MediaAction();
            }
        }
        return mediaAction;
    }

    @Override
    public synchronized void startPlay() {
        if (null == mTransfer || !mTransfer.isValid()) {
            Logger.d("Now have a media in running or TransferMediaBean is empty.");
            return;
        }

        Logger.d(String.format("startMedia - %1$s %2$s", mTransfer.getDomain(), mTransfer.getShot()));
        Logger.d("The New Media is Running");


        StateManager.getInstance().updateMediaState(StateManager.MediaState.PLAYING);
        switch (mTransfer.getMediaBean().getItem().getType()) {
            case MediaItemBean.TYPE_AUDIO:
                // TODO
                mTransfer.setRkAudioPlayer(AudioPlayerUtils.playAudio(mTransfer.getDomain(),
                        mTransfer.getMediaBean(), new AudioPlayerUtils.AudioPlayerCallback() {
                            @Override
                            public void onMusicFinished(RKAudioState rkAudioState) {
                                Logger.d("Voice is finished!!!");
                                StateManager.getInstance().updateMediaState(StateManager.MediaState.STOPPED);
                                notifyPlayFinish(mTransfer);
                            }

                            @Override
                            public void onMusicNearFinishEd(RKAudioState rkAudioState) {
                                // TODO
                            }

                            @Override
                            public void onMusicStopped(RKAudioState rkAudioState) {
                                // TODO
                            }

                            @Override
                            public void onMusicPaused(RKAudioState rkAudioState) {
                                // TODO
                            }

                            @Override
                            public void onMusicResumed(RKAudioState rkAudioState) {
                                // TODO
                            }
                        }));
                break;
            case MediaItemBean.TYPE_VIDEO:
                // TODO
                break;
        }
    }

    @Override
    public synchronized void pausePlay() {
        if (mTransfer == null) {
            Logger.d("mTransfer == null");
            return;
        }

        Logger.d("The current media paused.");
        mTransfer.getRkAudioPlayer().pausePlaying();
        StateManager.getInstance().updateMediaState(StateManager.MediaState.PAUSED);
    }

    @Override
    public synchronized void stopPlay() {
        if (mTransfer == null) {
            Logger.d("mTransfer == null");
            return;
        }

        Logger.d("The current media stopped.");
        mTransfer.getRkAudioPlayer().stopPlaying();
        mTransfer = null;
        StateManager.getInstance().updateMediaState(StateManager.MediaState.STOPPED);
    }

}
