package com.rokid.rkaudioplayer.monitor;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Bassam on 2016/11/1.
 */

public class RKAudioPlayMonitor {
    private static final long PREPARE_TIMEOUT_COUNT = 15000; // in millisecond which is 15 sec
    // the service to check if media prepare is time out
    private ScheduledExecutorService mPrepareTimeoutCheckService = null;
    private ScheduledExecutorService mUpdateCheckExecutorService = null;
    private IRKAudioPlayMonitorListener mRKAudioPlaybackListener;
    private MediaPlayer mMediaPlayer;
    private static final String TAG = RKAudioPlayMonitor.class.getSimpleName();
    private int mPrePosition;
    private int mCurrentPosition;
    private int mStuckCount;
    private Runnable mCheckStuckRunnable;

    public RKAudioPlayMonitor(IRKAudioPlayMonitorListener irkAudioPlayMonitorListener) {
        mRKAudioPlaybackListener = irkAudioPlayMonitorListener;
    }

    public void startPrepareMonitor() {
        Log.d(TAG, "startMonitor is called");
        cancelAllMonitor();
        mPrepareTimeoutCheckService = Executors.newSingleThreadScheduledExecutor();
        mPrepareTimeoutCheckService.schedule(new Runnable() {
            @Override
            public void run() {
                if (mRKAudioPlaybackListener != null) {
                    mRKAudioPlaybackListener.onPrepareTimeout();
                }
            }
        }, PREPARE_TIMEOUT_COUNT, TimeUnit.MILLISECONDS);

    }

    public void startPlayMonitor(MediaPlayer mediaPlayer) {
        Log.d(TAG, "startMonitor is called");
        cancelAllMonitor();
        mMediaPlayer = mediaPlayer;
        mUpdateCheckExecutorService = Executors.newSingleThreadScheduledExecutor();
        mCheckStuckRunnable = new CheckStuckRunnable();
        mUpdateCheckExecutorService.scheduleAtFixedRate(mCheckStuckRunnable, 1000, 1000, TimeUnit
                .MILLISECONDS);

    }

    private class CheckStuckRunnable implements Runnable {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                mCurrentPosition = getCurrentPosition();
                Log.d(TAG, "prePosition: " + mPrePosition + "currentPosition: " +
                        mCurrentPosition + "stuckCount: " + mStuckCount);
                if (mCurrentPosition == mPrePosition && mCurrentPosition < getDuration()) { //
                    // sometimes current position may greater than duration
                    mStuckCount++;
                    if (mStuckCount == 1) {
                        mRKAudioPlaybackListener.onFirstStuck();
                    }
                    if (mStuckCount >= 5) {
                        mRKAudioPlaybackListener.onLongStuck();
                        cancelAllMonitor();
                    }
                } else {
                    mRKAudioPlaybackListener.onPlayingPostion(mCurrentPosition, getDuration());
                    if (mStuckCount >= 1) {
                        mRKAudioPlaybackListener.onResumeFromStuck();
                        mStuckCount = 0;
                    }
                }
            }
            mPrePosition = mCurrentPosition;
        }

    }

    public int getCurrentPosition() {
        return mMediaPlayer == null ? -1 : mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        Log.d(TAG, "get duration : " + String.valueOf(mMediaPlayer == null ? -1 : mMediaPlayer
                .getDuration()));
        return mMediaPlayer == null ? -1 : mMediaPlayer.getDuration();
    }

    public void cancelAllMonitor() {
        if (mPrepareTimeoutCheckService != null) {
            Log.i(TAG, "shutdown prepare timeout check service on prepared");
            mPrepareTimeoutCheckService.shutdownNow();
        }
        Log.d(TAG, "cancelMonitor is called");
        if (mUpdateCheckExecutorService != null) {
            mUpdateCheckExecutorService.shutdownNow();
        }
        mPrePosition = -1;
        mStuckCount = 0;
        mMediaPlayer = null;
    }
}
