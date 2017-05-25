package com.rokid.cloudappclient.msg.action;

import android.net.Uri;
import android.text.TextUtils;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.player.RKAudioPlayer;
import com.rokid.cloudappclient.util.Logger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MediaAction extends BaseAction<TransferMediaBean> {

    private static volatile MediaAction mediaAction;

    private RKAudioPlayer rkAudioPlayer;

    private static final String STREAMING_PLAY = "streaming_play";
    private static final String STREAMING_PAUSE = "streaming_pause";
    private static final String STREAMING_RESUME = "streaming_resume";
    private static final String STREAMING_STOP = "streaming_stop";
    private static final String STREAMING_FORWARD = "streaming_forward";
    private static final String STREAMING_BACKWARD = "streaming_backward";


    private MediaAction() {
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        rkAudioPlayer = new RKAudioPlayer(RKCloudAppApplication.getInstance());

        rkAudioPlayer.setmOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Logger.d("MediaAction startAction onPrepared");
                StateManager.getInstance().updateMediaState(StateManager.MediaState.PLAYING);
            }
        });
        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                Logger.d("MediaAction startAction onCompletion");
                StateManager.getInstance().updateMediaState(StateManager.MediaState.STOPPED);
                notifyPlayFinished(mTransfer);
            }
        });
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
    public synchronized void startAction() {
        if (null == mTransfer || !mTransfer.isValid()) {
            Logger.d("Now have a media in running or TransferMediaBean is empty.");
            return;
        }

        Logger.d(String.format("startMedia - %1$s %2$s", mTransfer.getDomain(), mTransfer.getShot()));
        Logger.d("MediaAction startAction " + mTransfer.toString());

        MediaBean mediaBean = mTransfer.getMediaBean();

        if (mediaBean == null) {
            Logger.d("MediaAction startAction mediaBean null!");
            return;
        }

        String action = mediaBean.getAction();

        Logger.d(" startAction action : " + action);
        switch (action) {
            case STREAMING_PLAY:
                startPlay(mediaBean);
                break;
            case STREAMING_PAUSE:
                pausePlay();
                break;
            case STREAMING_RESUME:
                resumePlay();
                break;
            case STREAMING_STOP:
                pausePlay();
                break;
            case STREAMING_FORWARD:
                forward();
            case STREAMING_BACKWARD:
                backward();
            default:
                Logger.d(" invalidate action !" + action);
        }

    }

    @Override
    public synchronized void stopAction() {

        if (mTransfer == null) {
            Logger.d("mTransfer == null");
            return;
        }
        Logger.d("MediaAction stopAction");

        rkAudioPlayer.release(true);
        StateManager.getInstance().updateMediaState(StateManager.MediaState.STOPPED);

    }

    private void startPlay(MediaBean mediaBean) {
        if (rkAudioPlayer != null) {
            MediaItemBean mediaBeanItem = mediaBean.getItem();

            if (mediaBeanItem == null) {
                Logger.d("MediaAction startAction mediaBeanItem null!");
                return;
            }

            String url = mediaBeanItem.getUrl();

            if (TextUtils.isEmpty(url)) {
                Logger.d("MediaAction startAction url invalidate!");
                return;
            }

            rkAudioPlayer.setVideoURI(Uri.parse(url));
            rkAudioPlayer.start();
        }
    }

    private void pausePlay() {

        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.pause();
        }
    }

    private void resumePlay() {
        if (rkAudioPlayer != null && !rkAudioPlayer.isPlaying()) {
            rkAudioPlayer.start();
        }
    }

    private void forward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime + totalTime / 5;
            if (seekTime > totalTime) {
                seekTime = totalTime;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }

    private void backward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime - totalTime / 5;
            if (seekTime <= 0) {
                seekTime = 0;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }


}
