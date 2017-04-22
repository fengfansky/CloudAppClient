package com.rokid.rkaudioplayer;

import android.text.TextUtils;
import android.util.Log;

import com.rokid.rkaudioplayer.controller.RKAudioPlayer;
import com.rokid.rkaudioplayer.state.RKAudioState;

import java.util.HashMap;
import java.util.Map;

/**
 * This class offers a bunch of interfaces for the RKMusicPlayService to do related actions
 * <p>
 * Created by Bassam on 2016/10/31.
 */

public class RKAudioPlayerManger {
    private static final String TAG = RKAudioPlayerManger.class.getSimpleName();

    private Map<String, RKAudioPlayer> mHashMap;

    private String currentDomain;
    /**
     * 当前类的实例对象
     */
    private static RKAudioPlayerManger instance;

    /**
     * 单例方法调用
     *
     * @return
     */
    public static RKAudioPlayerManger getInstance() {
        if (instance == null) {
            synchronized (RKAudioPlayerManger.class) {
                if (instance == null) {
                    instance = new RKAudioPlayerManger();
                }
            }
        }
        return instance;
    }

    public RKAudioPlayerManger() {
        mHashMap = new HashMap<String, RKAudioPlayer>();
    }

    public RKAudioPlayer getRKAudioPlayer(String domain) {
        if (TextUtils.isEmpty(domain)) {
            Log.e(TAG, "domain is" + null);
            return null;
        }
        Log.i(TAG, "getRKAudioPlayer of" + domain);
        currentDomain = domain;
        if (mHashMap.containsKey(domain) && mHashMap.get(domain) != null) {
            Log.w(TAG, "RKAudioPlayer exist.");
            return mHashMap.get(domain);
        } else {
            RKAudioPlayer rkAudioPlayer = new RKAudioPlayer(domain);
            mHashMap.put(domain, rkAudioPlayer);
            Log.w(TAG, "RKAudioPlayer not exist.");
            return rkAudioPlayer;
        }
    }

    public void removeRKAudioPlayer(String domain) {
        if (TextUtils.isEmpty(currentDomain)) {
            Log.e(TAG, "domain is" + null);
            return;
        }
        Log.i(TAG, "removeRKAudio of" + domain);
        currentDomain = null;
        if (!mHashMap.containsKey(domain)) {
            Log.w(TAG, "RKAudioPlayer not exist.");
            return;
        }
        RKAudioPlayer musicPlayer = mHashMap.remove(domain);
        if (musicPlayer != null) {
            Log.i(TAG, "stopPlaying of" + domain + " by removeRKAudioPlayer");
            musicPlayer.stopPlaying();
        }
        mHashMap.remove(domain);
    }

    public RKAudioState getCurrentStatus() {
        if (!TextUtils.isEmpty(currentDomain) && getRKAudioPlayer(currentDomain) != null) {
            return getRKAudioPlayer(currentDomain).getRKAudioPlayState();
        } else {
            RKAudioState state = new RKAudioState("");
            return state;

        }
    }


    /**
     * 清理所有的音频
     */
    public void clearMediaPlayer() {
        stopAllMediaPlayer();
        mHashMap.clear();
    }

    /**
     * 停止所有的声音
     */
    public void stopAllMediaPlayer() {
        for (String key : mHashMap.keySet()) {
            RKAudioPlayer musicPlayer = mHashMap.get(key);
            if (musicPlayer != null) {
                musicPlayer.stopPlaying();
            }
        }
    }

    /**
     * 停止所有的声音
     */
    public void pauseAllMediaPlayer() {
        for (String key : mHashMap.keySet()) {
            RKAudioPlayer musicPlayer = mHashMap.get(key);
            if (musicPlayer != null) {
                musicPlayer.pausePlaying();
            }
        }
    }
}
