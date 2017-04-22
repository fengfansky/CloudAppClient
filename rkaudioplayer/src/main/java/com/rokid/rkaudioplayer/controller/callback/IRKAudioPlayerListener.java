package com.rokid.rkaudioplayer.controller.callback;


import com.rokid.rkaudioplayer.state.RKAudioState;

/**
 * This interface is used for music play callbacks
 * <p>
 * Created by showingcp on 8/9/16.
 */
public interface IRKAudioPlayerListener {

    /**
     * callback when a song is started playing
     */
    void onMusicStarted(RKAudioState rkAudioState);

    /**
     * callback when a song is stopped
     */
    void onMusicFinished(RKAudioState rkAudioState);

    /**
     * callback when a song is near stop
     */
    void onMusicNearFinishEd(RKAudioState rkAudioState);

    /**
     * callback when a song is stopped
     */
    void onMusicStopped(RKAudioState rkAudioState);

    /**
     * callback when playing is paused
     */
    void onMusicPaused(RKAudioState rkAudioState);

    /**
     * callback when a song is resumed
     */
    void onMusicResumed(RKAudioState rkAudioState);


    /**
     * callback when playing is paused
     */
    void onMusicPending(RKAudioState rkAudioState);

    /**
     * callback when a song is resumed
     */
    void onMusicResumePending(RKAudioState rkAudioState);



    /**
     * callback when exception occurs
     */
    void onPlayBackFailed(RKAudioState rkAudioState);

    /**
     * @param rkAudioState
     * @param duration
     */
    void onMusicPlaybackChanged(RKAudioState rkAudioState, int duration);

    void onMusicBufferStart(RKAudioState rkAudioState, int duration);

    void onMusicBufferEnd(RKAudioState rkAudioState, int duration);

    void onAudioPlayerStateChange(RKAudioState rkAudioState);
}
