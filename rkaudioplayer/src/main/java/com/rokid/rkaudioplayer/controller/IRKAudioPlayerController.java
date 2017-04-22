package com.rokid.rkaudioplayer.controller;

import android.content.Context;

import com.rokid.rkaudioplayer.entities.RKPlayerItemObject;
import com.rokid.rkaudioplayer.state.RKAudioState;

/**
 * Created by Bassam on 2016/11/1.
 */
public interface IRKAudioPlayerController {
    /**
     * 获得播放器状态
     *
     * @return
     */
    RKAudioState getRKAudioPlayState();

    void playAudio(Context context, RKPlayerItemObject rkPlayerObject);

    void pausePlaying();

    void resumePlaying();

    void pendingPlaying();

    void pendingResumePlaying();

    void stopPlaying();

    float getVolume();

    void setVolume(float volume);

}
