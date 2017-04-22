package com.rokid.cloudappclient.util;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.rkaudioplayer.RKAudioPlayerManger;
import com.rokid.rkaudioplayer.controller.RKAudioPlayer;
import com.rokid.rkaudioplayer.controller.callback.IRKAudioPlayerListener;
import com.rokid.rkaudioplayer.entities.RKPlayerItemObject;
import com.rokid.rkaudioplayer.state.RKAudioState;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;

/**
 * Description: TODO
 * Author: xupan.shi
 * Version: V0.1 2017/3/14
 */
public class AudioPlayerUtils {

    public static RKAudioPlayer playAudio(String domain, MediaBean mediaBean,
                                          final AudioPlayerCallback callback) {

        return playAudio(domain, mediaBean, new IRKAudioPlayerListener() {
            @Override
            public void onMusicStarted(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onStart");
                //callback.onMusicStarted(rkAudioState);
            }

            @Override
            public void onMusicFinished(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicFinished");
                callback.onMusicFinished(rkAudioState);
            }

            @Override
            public void onMusicNearFinishEd(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicNearFinishEd");
                callback.onMusicNearFinishEd(rkAudioState);
            }

            @Override
            public void onMusicStopped(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicStopped");
                callback.onMusicStopped(rkAudioState);
            }

            @Override
            public void onMusicPaused(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicPaused");
                callback.onMusicPaused(rkAudioState);
            }

            @Override
            public void onMusicResumed(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicResumed");
                callback.onMusicResumed(rkAudioState);
            }

            @Override
            public void onMusicPending(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicPending");
                // callback.onMusicPending(rkAudioState);
            }

            @Override
            public void onMusicResumePending(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onMusicResumePending");
                // callback.onMusicResumePending(rkAudioState);
            }

            @Override
            public void onPlayBackFailed(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onPlayBackFailed");
                // callback.onPlayBackFailed(rkAudioState);
            }

            @Override
            public void onMusicPlaybackChanged(RKAudioState rkAudioState, int duration) {
                Logger.d("RKAudioPlayer is onMusicPlaybackChanged - duration: " + duration);
                // callback.onMusicPlaybackChanged(rkAudioState, duration);
            }

            @Override
            public void onMusicBufferStart(RKAudioState rkAudioState, int duration) {
                Logger.d("RKAudioPlayer is onMusicBufferStart - duration: " + duration);
                // callback.onMusicBufferStart(rkAudioState, duration);
            }

            @Override
            public void onMusicBufferEnd(RKAudioState rkAudioState, int duration) {
                Logger.d("RKAudioPlayer is onMusicBufferEnd - duration: " + duration);
                // callback.onMusicBufferEnd(rkAudioState, duration);
            }

            @Override
            public void onAudioPlayerStateChange(RKAudioState rkAudioState) {
                Logger.d("RKAudioPlayer is onAudioPlayerStateChange");
                // callback.onAudioPlayerStateChange(rkAudioState);
            }
        });

    }

    private static RKAudioPlayer playAudio(String domain, MediaBean mediaBean,
                                           IRKAudioPlayerListener irkAudioPlayerListener) {

        RKAudioPlayer rkAudioPlayer = RKAudioPlayerManger.getInstance().getRKAudioPlayer(domain);
        rkAudioPlayer.setIrkAudioPlayerListener(irkAudioPlayerListener);

        RKPlayerItemObject playerItemObject = new RKPlayerItemObject();
        playerItemObject.setOffsetInMilliseconds(mediaBean.getItem().getOffsetInMilliseconds());
        playerItemObject.setToken(mediaBean.getItem().getToken());
        playerItemObject.setUrl(mediaBean.getItem().getUrl());
        rkAudioPlayer.playAudio(RKCloudAppApplication.getInstance().getApplicationContext(), playerItemObject);

        return rkAudioPlayer;
    }

    public interface AudioPlayerCallback {
        // void onMusicStarted(RKAudioState rkAudioState);

        void onMusicFinished(RKAudioState rkAudioState);

        void onMusicNearFinishEd(RKAudioState rkAudioState);

        void onMusicStopped(RKAudioState rkAudioState);

        void onMusicPaused(RKAudioState rkAudioState);

        void onMusicResumed(RKAudioState rkAudioState);

        // void onMusicPending(RKAudioState rkAudioState);

        // void onMusicResumePending(RKAudioState rkAudioState);

        // void onPlayBackFailed(RKAudioState rkAudioState);

        // void onMusicPlaybackChanged(RKAudioState rkAudioState, int duration);

        // void onMusicBufferStart(RKAudioState rkAudioState, int duration);

        // void onMusicBufferEnd(RKAudioState rkAudioState, int duration);

        // void onAudioPlayerStateChange(RKAudioState rkAudioState);
    }

}
