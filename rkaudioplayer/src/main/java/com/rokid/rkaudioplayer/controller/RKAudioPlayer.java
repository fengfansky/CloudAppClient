package com.rokid.rkaudioplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.rokid.rkaudioplayer.constants.RKMediaPlayerConstants;
import com.rokid.rkaudioplayer.controller.callback.IRKAudioPlayerListener;
import com.rokid.rkaudioplayer.entities.RKPlayerItemObject;
import com.rokid.rkaudioplayer.monitor.IRKAudioPlayMonitorListener;
import com.rokid.rkaudioplayer.monitor.RKAudioPlayMonitor;
import com.rokid.rkaudioplayer.state.RKAudioState;

import java.util.HashMap;

/**
 * Created by showingcp on 8/7/16.
 * <p>
 * This class offers a bunch of interfaces for the RKMusicPlayService to do related actions
 */
public class RKAudioPlayer implements IRKAudioPlayerController, IRKAudioPlayMonitorListener {
    private static final long NEAR_FINISH_COUNT = 15000; // in millisecond which is 15 sec
    private float volume = 1.0f;
    private String domain;
    /**
     * Logcat TAG for Logs from RKAudioPlayer
     */
    private static final String TAG = RKAudioPlayer.class.getSimpleName();

    /**
     * state manager for managing playing state
     */
    private RKAudioState mStateManager;

    /**
     * The Play Item of Audio Player
     */
    private RKPlayerItemObject rkPlayerObject;//当前播放的列表项


    /**
     * Universal MediaPlayer
     */
    private MediaPlayer mMediaPlayer;


    private Context mContext;

    // main thread handler
    private Handler mMainThreadHandler;

    private IRKAudioPlayerListener irkAudioPlayerListener;

    private RKAudioPlayMonitor rkAudioPlayMonitor;

    /**
     * current playing position holder
     */
    private int mCurrentPlayingPosition;

    // if the music is prepare ready, using to fix get duration not in the right state error in
    // media player
    private boolean isMusicReady = false;

    // lock for Media player
    private Object mMediaPlayerLock = new Object();

    // near finish callback called flag
    private boolean nearFinishCallbacked;

    /**
     * Constructor
     */
    public RKAudioPlayer(String domain) {
        this.domain = domain;
        init();
    }

    /**
     * Initialization
     */
    private void init() {
        Log.i(TAG, "initialize");
        /** init a main thread handler */
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        /** instantiate MediaPlayer */
        mMediaPlayer = new MediaPlayer();
        /** instantiate RKAudioState */
        mStateManager = new RKAudioState(domain);
        initAudioPlayerState(null);
        /**default rkMediaItem is null**/
        rkPlayerObject = null;
        mCurrentPlayingPosition = 0;
        rkAudioPlayMonitor = new RKAudioPlayMonitor(this);

        /** init listener for meidaPlayer */
        isMusicReady = false;
        initListener();
    }

    private void initListener() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion: ");
                onPlayCompletion();

            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onError：what=" + what + ";extra=" + extra);
                onPlayError();
                return true;
            }
        });
    }

    /*************
     * 外部调用
     ******************/
    public void setIrkAudioPlayerListener(IRKAudioPlayerListener irkAudioPlayerListener) {
        this.irkAudioPlayerListener = irkAudioPlayerListener;
    }

    @Override
    public RKAudioState getRKAudioPlayState() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mCurrentPlayingPosition = mMediaPlayer.getCurrentPosition();
            }
        }
        mStateManager.setOffsetInMilliseconds(mCurrentPlayingPosition);
        mStateManager.upTimestamp();
        return mStateManager;
    }

    @Override
    public void playAudio(Context context, RKPlayerItemObject rkPlayerObject) {
        Log.d(TAG, "playAudio is called");
        /** initialize variables */
        mContext = context;
        initPlay();
        if (rkPlayerObject == null) {
            Log.e(TAG, "Has no paly Item");
            onPlayError();
        } else {
            this.rkPlayerObject = rkPlayerObject;
            initAudioPlayerState(rkPlayerObject);
            mCurrentPlayingPosition = rkPlayerObject.getOffsetInMilliseconds();
            if (TextUtils.isEmpty(rkPlayerObject.getUrl())) {//对于url进行校验
                onPlayError();
                return;
            }
            updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                    .PLAYER_ACTIVITY_LOADING);
            try {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                }
                if (TextUtils.isEmpty(rkPlayerObject.getUrl())) {
                    onPlayError();
                }
                synchronized (mMediaPlayerLock) {
                    isMusicReady = false;
                    mMediaPlayer.reset();
                    rkAudioPlayMonitor.cancelAllMonitor();
                    /** set AudioStreamType as STREAM_MUSIC */
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    Log.i(TAG, "songUrl: " + rkPlayerObject.getUrl());
                    String finalSongUrl = rkPlayerObject.getUrl();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Host", new java.net.URI(rkPlayerObject.getUrl()).getHost());
                    mMediaPlayer.setDataSource(mContext, Uri.parse(finalSongUrl), headers);
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.i(TAG, "onPrepared in tid: " + Thread.currentThread().getId());
                            synchronized (mMediaPlayerLock) {
                                isMusicReady = true;
                                rkAudioPlayMonitor.cancelAllMonitor();
                            }
                            // stop previous playing, do the new one
                            if (getRKAudioPlayState().getState() == RKMediaPlayerConstants
                                    .PlayActivity.PLAYER_ACTIVITY_LOADING) {
                                doStartMediaPlayer();
                            }
                        }
                    });
                    mMediaPlayer.prepareAsync();
                    rkAudioPlayMonitor.startPrepareMonitor();
                }
            } catch (Exception e) {
                Log.d(TAG, "prepare failed");
                e.printStackTrace();
                onPlayError();
            }
        }
    }

    @Override
    public void pausePlaying() {
        Log.d(TAG, "pausePlaying is called ");
        rkAudioPlayMonitor.cancelAllMonitor();
        if (getRKAudioPlayState().getState().equals(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PLAYING) || getRKAudioPlayState().getState().equals
                (RKMediaPlayerConstants.PlayActivity.PLAYER_ACTIVITY_PAUSED) ||
                getRKAudioPlayState().getState().equals(RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_BUFFER_UNDERRUN)) {//缓冲中，暂停播放，正在播放
            if (mMediaPlayer != null) {
                synchronized (mMediaPlayerLock) {
                    onPaused();
                    mMediaPlayer.pause();
                }
            } else {
                onPlayError();
            }
        } else {//准备状态，结束状态，没有播放任何资源状态，停止状态
            onPlayError();
        }
    }

    @Override
    public void resumePlaying() {
        Log.d(TAG, "resumePlaying is called");
        if (getRKAudioPlayState().getState().equals(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PLAYING)){
            Log.d(TAG, "resumePlaying state is playing do nothing");
            return;
        }
        if (getRKAudioPlayState().getState().equals
                (RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_PLAYING) || getRKAudioPlayState().getState()
                .equals
                        (RKMediaPlayerConstants.PlayActivity.PLAYER_ACTIVITY_PAUSED) ||
                getRKAudioPlayState()
                        .getState().equals(RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_BUFFER_UNDERRUN) ||
                getRKAudioPlayState()
                        .getState().equals(RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_PENDING)) {//缓冲中，暂停播放，正在播放, pending
            if (mMediaPlayer != null) {
                synchronized (mMediaPlayerLock) {

                    rkAudioPlayMonitor.startPlayMonitor(mMediaPlayer);
                    onResumed();
                    mMediaPlayer.start();
                }
            } else {
                onPlayError();
            }
        } else {
            onPlayError();
        }
    }

    @Override
    public void pendingPlaying() {
        Log.d(TAG, "pendingPlaying is called ");
        rkAudioPlayMonitor.cancelAllMonitor();
        if (getRKAudioPlayState().getState().equals(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_LOADING) || getRKAudioPlayState().getState().equals
                (RKMediaPlayerConstants.PlayActivity.PLAYER_ACTIVITY_PLAYING) ||
                getRKAudioPlayState().getState().equals(RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_BUFFER_UNDERRUN)) {//缓冲中，暂停播放，正在播放
            if (mMediaPlayer != null) {
                synchronized (mMediaPlayerLock) {
                    onPending();
                    mMediaPlayer.pause();
                }
            } else {
                onPlayError();
            }
        } else {//准备状态，结束状态，没有播放任何资源状态，停止状态
//            onPlayError();
        }
    }

    @Override
    public void pendingResumePlaying() {
        Log.d(TAG, "pendingResumePlaying is called " + getRKAudioPlayState().getState());
        if (getRKAudioPlayState().getState().equals
                (RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_PENDING)) {//缓冲中，暂停播放，正在播放
            if (mMediaPlayer != null) {
                synchronized (mMediaPlayerLock) {

                    rkAudioPlayMonitor.startPlayMonitor(mMediaPlayer);
                    onResumePending();
                    mMediaPlayer.start();
                }
            } else {
                onPlayError();
            }
        } else {
//            onPlayError();
        }
    }

    @Override
    public void stopPlaying() {
        Log.d(TAG, "stopPlaying is called ");
        synchronized (mMediaPlayerLock) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
        }
        onStopped();
        rkAudioPlayMonitor.cancelAllMonitor();
        initPlay();
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;

    }

    private void updataVolume() {

    }

    /******************内部调用***********************/
    /**
     * init play，play states and play monitor
     */
    private void initPlay() {
        rkPlayerObject = null;//重置播放对象
        mCurrentPlayingPosition = 0;//重置播放进度
        mStateManager.initState();//重置状态
        //将播放器重置为没有在播放状态
        synchronized (mMediaPlayerLock) {
            nearFinishCallbacked = false;
            isMusicReady = false;//状态为非准备状态
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();//重置播放器
            }
            rkAudioPlayMonitor.cancelAllMonitor();//关闭所有的监听器
        }
    }

    // do start media player
    private void doStartMediaPlayer() {
        Log.d(TAG, "doStartMediaPlayer is called");
        synchronized (mMediaPlayerLock) {
            if (mCurrentPlayingPosition >= mMediaPlayer.getDuration()) {
                Log.d(TAG, "mCurrentPlayingPosition >= mMediaPlayer.getDuration(), current position :" + mCurrentPlayingPosition + ", duration : " + mMediaPlayer.getDuration());
                onPlayError();
                return;
            }
            mMediaPlayer.seekTo(mCurrentPlayingPosition);
            mMediaPlayer.setVolume(volume, volume);
            mMediaPlayer.start();
            onStarted();
            rkAudioPlayMonitor.startPlayMonitor(mMediaPlayer);
        }
    }

    /******************
     * 回调外部，更新请求状态
     **************************/
    private void onStarted() {
        Log.d(TAG, "onStarted is called ");

        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PLAYING);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicStarted
                    (getRKAudioPlayState());
        }
    }

    private void onPaused() {
        Log.d(TAG, "onPaused is called ");

        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PAUSED);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicPaused
                    (getRKAudioPlayState());
        }
    }

    private void onResumed() {
        Log.d(TAG, "onResumed is called ");

        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PLAYING);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicResumed
                    (getRKAudioPlayState());
        }

    }

    private void onPending() {
        Log.d(TAG, "onPending is called ");

        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PENDING);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicPending
                    (getRKAudioPlayState());
        }
    }

    private void onResumePending() {
        Log.d(TAG, "onResumePending is called ");
        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_PLAYING);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicResumePending
                    (getRKAudioPlayState());
        }
    }

    private void onNearFinishEd() {
        Log.d(TAG, "onNearFinishEd is called ");
        boolean flag = false;
        synchronized (mMediaPlayerLock) {
            flag = nearFinishCallbacked;
        }

        if (!flag) {
            Log.i(TAG, "should callback near finish");
            if (irkAudioPlayerListener != null) {
                irkAudioPlayerListener.onMusicNearFinishEd
                        (getRKAudioPlayState());
            }
            synchronized (mMediaPlayerLock) {
                nearFinishCallbacked = true;
            }
        }
    }

    private void onStopped() {
        Log.d(TAG, "onStopped is called ");
        updataAudioPlayerState(RKMediaPlayerConstants.PlayActivity
                .PLAYER_ACTIVITY_STOPPED);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicStopped
                    (getRKAudioPlayState());
        }
    }

    private void onPlayCompletion() {
        Log.d(TAG, "onPlayCompletion is called ");
        synchronized (mMediaPlayerLock) {
            isMusicReady = false;//状态为非准备状态
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();//重置播放器
            }
            rkAudioPlayMonitor.cancelAllMonitor();
        }
        updataAudioPlayerState(RKMediaPlayerConstants
                .PlayActivity.PLAYER_ACTIVITY_FINISHED);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicFinished
                    (getRKAudioPlayState());
        }
    }

    private void onPlayError() {
        Log.d(TAG, "onPlayError is called ");
        synchronized (mMediaPlayerLock) {
            isMusicReady = false;//状态为非准备状态
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();//重置播放器
            }
            rkAudioPlayMonitor.cancelAllMonitor();
        }
        updataAudioPlayerState(RKMediaPlayerConstants
                .PlayActivity.PLAYER_ACTIVITY_IDLE);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onPlayBackFailed
                    (getRKAudioPlayState());
        }
    }

    private void onBufferStart() {
        Log.d(TAG, "onBufferStart is called ");
        updataAudioPlayerState(null);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicBufferStart(getRKAudioPlayState
                    (), mMediaPlayer.getDuration());
        }
    }

    private void onBufferStop() {
        Log.d(TAG, "onBufferStop is called ");
        updataAudioPlayerState(null);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicBufferEnd(getRKAudioPlayState
                    (), mMediaPlayer.getDuration());
        }
    }

    private void onPlaybackChanged() {
        Log.d(TAG, "onPlaybackChanged is called ");

        updataAudioPlayerState(null);
        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onMusicPlaybackChanged
                    (getRKAudioPlayState
                            (), mMediaPlayer.getDuration());
            if (mMediaPlayer.getDuration() - mCurrentPlayingPosition <=
                    NEAR_FINISH_COUNT) {
                boolean flag = false;
                synchronized (mMediaPlayerLock) {
                    flag = nearFinishCallbacked;
                }

                if (!flag) {
                    Log.i(TAG, "should callback near finish");
                    irkAudioPlayerListener.onMusicNearFinishEd
                            (getRKAudioPlayState
                                    ());

                    synchronized (mMediaPlayerLock) {
                        nearFinishCallbacked = true;
                    }
                }
            }
        }

    }

    private void onAudioPlayerStateChanged() {
        Log.d(TAG, "onAudioPlayerStateChanged is called ");

        if (irkAudioPlayerListener != null) {
            irkAudioPlayerListener.onAudioPlayerStateChange(getRKAudioPlayState());
        }
    }


    /************
     * 媒体监听器回调回来
     **************/
    @Override
    public void onPrepareTimeout() {
        Log.d(TAG, "onPrepareTimeout is called ");
        onPlayError();
    }

    @Override
    public void onFirstStuck() {
        Log.d(TAG, "onFirstStuck is called ");
        if (mStateManager != null && mMediaPlayer != null) {
            synchronized (mMediaPlayerLock) {
                if (getRKAudioPlayState().equals(RKMediaPlayerConstants.PlayActivity
                        .PLAYER_ACTIVITY_PLAYING)) {
                    onBufferStart();
                } else {
                    rkAudioPlayMonitor.cancelAllMonitor();
                }
            }
        } else {
            rkAudioPlayMonitor.cancelAllMonitor();
            onPlayError();
        }
    }

    @Override
    public void onResumeFromStuck() {
        Log.d(TAG, "onResumeFromStuck is called ");
        if (mStateManager != null && mMediaPlayer != null) {
            synchronized (mMediaPlayerLock) {
                if (getRKAudioPlayState().getState().equals(RKMediaPlayerConstants
                        .PlayActivity
                        .PLAYER_ACTIVITY_PLAYING)) {
                    onBufferStop();
                } else {
                    rkAudioPlayMonitor.cancelAllMonitor();
                }
            }
        } else {
            rkAudioPlayMonitor.cancelAllMonitor();
            onPlayError();
        }
    }

    @Override
    public void onLongStuck() {
        Log.d(TAG, "onLongStuck is called ");
        onPlayError();
    }

    @Override
    public void onPlayingPostion(int currentPosition, int duration) {
        Log.d(TAG, "onPlayingPostion is called currentPosition : " + currentPosition + ", duration : " + duration);
        if (mStateManager != null && mMediaPlayer != null) {
            synchronized (mMediaPlayerLock) {
                if (getRKAudioPlayState().getState().equals(RKMediaPlayerConstants
                        .PlayActivity
                        .PLAYER_ACTIVITY_PLAYING)) {
                    onPlaybackChanged();
                } else {
                    rkAudioPlayMonitor.cancelAllMonitor();
                }
            }
        } else {
            rkAudioPlayMonitor.cancelAllMonitor();
            onPlayError();
        }
    }

    /************
     * 此媒体播放器状态重置处理
     **************/
    private void initAudioPlayerState(RKPlayerItemObject rkPlayerObject) {
        Log.d(TAG, "initAudioPlayerState is called ");
        mStateManager.initState(rkPlayerObject);
        onAudioPlayerStateChanged();
    }

    private void updataAudioPlayerState(String playerActivity) {
        Log.d(TAG, "updataAudioPlayerState is called playerActivity : " + playerActivity);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mCurrentPlayingPosition = mMediaPlayer.getCurrentPosition();
            }
        }
        if (!TextUtils.isEmpty(playerActivity)) {
            mStateManager.setState(playerActivity);
        }
        mStateManager.setOffsetInMilliseconds(mCurrentPlayingPosition);
        mStateManager.upTimestamp();

        onAudioPlayerStateChanged();
    }
}
