package com.rokid.rkaudioplayer.monitor;

/**
 * Created by Bassam on 2016/11/1.
 */

public interface IRKAudioPlayMonitorListener {
    void onPrepareTimeout();

    void onFirstStuck();

    void onResumeFromStuck();

    void onLongStuck();

    void onPlayingPostion(int currentPosition, int duration);
}
