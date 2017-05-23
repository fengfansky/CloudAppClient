package com.rokid.cloudappclient.msg.manager;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * Updated by fengfan on 5/18/17.
 * <p>
 * StateManager is a singleton class to manage the state for application, voice and media.
 * All the operations for the state should be handled here. All the operations are volatile.
 */

public class StateManager {

    private SceneStateNode mSceneStateNode;
    private CutStateNode mCutStateNode;

    private String mCurrentDomain;
    private String mCurrentShot;

    /**
     * the single instance holder
     */
    private static volatile StateManager instance;

    /**
     * state operation lock
     */
    private static final Object mLock = new Object();

    /**
     * Definition of app state
     */
    public static class AppState {
        public static final int STOPPED = 0;
        public static final int LOADING = 1;
        public static final int EXECUTING = 2;
        public static final int PENDING = 3;
        public static final int IDLE = 4;
    }

    /**
     * Definition of voice state
     */
    public static class VoiceState {
        public static final int STOPPED = 0;
        public static final int PLAYING = 1;
    }

    /**
     * Definition of media state
     */
    public static class MediaState {
        public static final int STOPPED = 0;
        public static final int LOADING = 1;
        public static final int PLAYING = 2;
        public static final int PAUSED = 3;
    }

    /**
     * Method to get the single instance of {@link StateManager}
     *
     * @return the single instance of {@link StateManager}
     */
    public static StateManager getInstance() {
        if (null == instance) {
            synchronized (StateManager.class) {
                if (null == instance) {
                    instance = new StateManager();
                }
            }
        }

        return instance;
    }

    /**
     * Method to release the singleton
     */
    public static void release() {
        synchronized (StateManager.class) {
            instance = null;
        }
    }

    /**
     * Method to update the current application info including DOMAIN and SHOT
     * CAUTION: this method should NOT be callded by any classes but {@link com.rokid.cloudappclient.activity.BaseActivity}
     *
     * @param domain current application domain to update
     * @param shot   current application shot to update. Currently, ONLY SCENE and SHOT are available
     */
    public void setCurrentAppInfo(final String domain, final String shot) {
        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(shot)) {
            Logger.e("current app info to update is invalid");
            return;
        }

        if (!ActionBean.FORM_CUT.equals(shot) && !ActionBean.FORM_SCENE.equals(shot)) {
            Logger.e("current app shot is unknown");
            return;
        }

        synchronized (mLock) {
            mCurrentDomain = domain;
            mCurrentShot = shot;
            Logger.i("current app info updated - DOMAIN: " + mCurrentDomain + ", SHOT: " + mCurrentShot);
        }
    }

    /**
     * Method to get current application domain
     *
     * @return the current application domain
     */
    public String getCurrentAppDomain() {
        String domain;
        synchronized (mLock) {
            domain = mCurrentDomain;
        }

        Logger.i("get current app domain: " + domain);
        return domain;
    }

    /**
     * Method to get current application shot
     *
     * @return the current application shot
     */
    public String getCurrentAppShot() {
        String shot;
        synchronized (mLock) {
            shot = mCurrentShot;
        }

        Logger.i("get current app shot: " + shot);
        return shot;
    }

    /**
     * Method to get the current app state.
     *
     * @return the current app state. {@link AppState}
     */
    public int getAppState() {
        return getAppState(getCurrentAppShot());
    }

    private int getAppState(String shot) {
        int appState = -1;
        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                appState = mCutStateNode.mAppState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                appState = mSceneStateNode.mAppState;
            }
        }

        Logger.d("get app state: " + appState);
        return appState;
    }

    /**
     * Method to update the current app state
     *
     * @param appState the given app state to update. {@link AppState}
     *                 if the given app state is invalid, the update operation will be ignored.
     */
    public void updateAppState(int appState) {
        updateAppState(appState, getCurrentAppShot());
    }

    private void updateAppState(int appState, String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("shot can't be empty!!!");
            return;
        }

        if (!isValidAppState(appState)) {
            Logger.i("ILLEGLE APP STATE TO UPDATE");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.mAppState = appState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.mAppState = appState;
            }
        }

        Logger.i("APP state updated: " + appState + ", for SHOT: " + shot);
    }

    /**
     * Method to get the current voice state.
     *
     * @return the current voice state. {@link VoiceState}
     */
    public int getVoiceState() {
        return getVoiceState(getCurrentAppShot());
    }

    private int getVoiceState(String shot) {
        int voiceState = -1;
        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                voiceState = mCutStateNode.mVoiceState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                voiceState = mSceneStateNode.mVoiceState;
            }
        }

        Logger.i("get voice State : " + voiceState);
        return voiceState;
    }

    /**
     * Method to update the current voice state
     *
     * @param voiceState the given voice state to update. {@link VoiceState}
     *                   if the given voice state is invalid, the update operation will be ignored.
     */
    public void updateVoiceState(int voiceState) {
        updateVoiceState(voiceState, getCurrentAppShot());
    }

    private void updateVoiceState(int voiceState, String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("shot can't be empty!!!");
            return;
        }

        if (!isValidVoiceState(voiceState)) {
            Logger.e("ILLEGLE VOICE STATE TO UPDATE");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.mVoiceState = voiceState;
                Logger.i("VOICE state updated: " + mCutStateNode.mVoiceState + ", for SHOT: " + shot);
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.mVoiceState = voiceState;
                Logger.i("VOICE state updated: " + mSceneStateNode.mVoiceState + ", for SHOT: " + shot);
            }
        }

    }

    /**
     * Method to get the current media state
     *
     * @return the current media state. {@link MediaState}
     */
    public int getMediaState() {
        return getMediaState(getCurrentAppShot());
    }

    private int getMediaState(String shot) {
        int mediaState = -1;
        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mediaState = mCutStateNode.mMediaState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mediaState = mSceneStateNode.mMediaState;
            }
        }

        Logger.i("get media State : " + mediaState);
        return mediaState;
    }

    /**
     * Method to update the current media state
     *
     * @param mediaState the given media state to update. {@link MediaState}
     *                   if the given media state is invalid, the update operation will be ignored.
     */
    public void updateMediaState(int mediaState) {
        updateMediaState(mediaState, getCurrentAppShot());
    }

    private void updateMediaState(int mediaState, String shot) {
        if (!isValidMediaState(mediaState)) {
            Logger.i("ILLEGLE MEDIA STATE TO UPDATE");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.mMediaState = mediaState;
                Logger.i("MEDIA state updated: " + mCutStateNode.mMediaState + ", for SHOT: " + shot);
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.mMediaState = mediaState;
                Logger.i("MEDIA state updated: " + mSceneStateNode.mMediaState + ", for SHOT: " + shot);
            }
        }
    }

    /**
     * Method to store the last app state
     */
    public void storeLastAppState() {
        storeLastAppState(getCurrentAppShot());
    }

    private void storeLastAppState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.storeLastAppState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.storeLastAppState();
            }
        }
    }

    /**
     * Method to restore the last app state
     */
    public void restoreLastAppState() {
        restoreLastAppState(getCurrentAppShot());
    }

    private void restoreLastAppState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.restoreLastAppState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.restoreLastAppState();
            }
        }
    }

    /**
     * Method to store the last voice state
     */
    public void storeLastVoiceState() {
        storeLastVoiceState(getCurrentAppShot());
    }

    private void storeLastVoiceState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.storeLastVoiceState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.storeLastVoiceState();
            }
        }
    }

    /**
     * Method to restore the last voice state
     */
    public void restoreLastVoiceState() {
        restoreLastVoiceState(getCurrentAppShot());
    }

    private void restoreLastVoiceState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.restoreLastVoiceState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.restoreLastVoiceState();
            }
        }
    }

    /**
     * Method to store the last media state
     */
    public void storeLastMediaState() {
        storeLastMediaState(getCurrentAppShot());
    }

    private void storeLastMediaState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.storeLastMediaState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.storeLastMediaState();
            }
        }
    }

    /**
     * Method to restore the last media state
     */
    public void restoreLastMediaState() {
        restoreLastMediaState(getCurrentAppShot());
    }

    private void restoreLastMediaState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mCutStateNode.restoreLastMediaState();
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mSceneStateNode.restoreLastMediaState();
            }
        }
    }

    /**
     * Method to get the last app state stored
     *
     * @return the last app state
     */
    public int getLastAppState() {
        return getLastAppState(getCurrentAppShot());
    }

    private int getLastAppState(String shot) {
        int appState = -1;

        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return appState;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                appState = mCutStateNode.mLastAppState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                appState = mSceneStateNode.mLastAppState;
            }
        }

        Logger.i("get LastAppState : " + appState);
        return appState;
    }

    /**
     * Method to get the last voice state stored
     *
     * @return the last voice state
     */
    public int getLastVoiceState() {
        return getLastVoiceState(getCurrentAppShot());
    }

    private int getLastVoiceState(String shot) {
        int voiceState = -1;

        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return voiceState;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                voiceState = mCutStateNode.mLastVoiceState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                voiceState = mSceneStateNode.mLastVoiceState;
            }
        }

        Logger.i("get LastVoiceState : " + voiceState);
        return voiceState;
    }

    /**
     * Method to get the last media state stored
     *
     * @return the last media state
     */
    public int getLastMediaState() {
        return getLastMediaState(getCurrentAppShot());
    }

    private int getLastMediaState(String shot) {
        int mediaState = -1;

        if (TextUtils.isEmpty(shot)) {
            Logger.e("The shot can't be empty!!!");
            return mediaState;
        }

        synchronized (mLock) {
            if (ActionBean.FORM_CUT.equals(shot)) {
                mediaState = mCutStateNode.mLastMediaState;
            } else if (ActionBean.FORM_SCENE.equals(shot)) {
                mediaState = mSceneStateNode.mLastMediaState;
            }
        }

        Logger.i("get LastMediaState : " + mediaState);
        return mediaState;
    }

    /**
     * Method to store last app, voice and media state together
     */
    public void storeAllLastState() {
        storeAllLastState(getCurrentAppShot());
    }

    private void storeAllLastState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("shot can't be empty!!! : ");
            return;
        }

        storeLastAppState(shot);
        storeLastVoiceState(shot);
        storeLastMediaState(shot);
    }

    /**
     * Method to restore last app, voice and media state together
     */
    public void restoreAllLastState() {
        restoreAllLastState(getCurrentAppShot());
    }

    private void restoreAllLastState(String shot) {
        if (TextUtils.isEmpty(shot)) {
            Logger.e("shot can't be empty!!! : ");
            return;
        }

        restoreLastAppState(shot);
        restoreLastMediaState(shot);
        restoreLastVoiceState(shot);
    }

    /**
     * To check whether the given app state is valid
     *
     * @param appState the given app state
     * @return true is valid. false is invalid
     */
    private boolean isValidAppState(int appState) {
        return !(appState != AppState.IDLE
                && appState != AppState.LOADING
                && appState != AppState.EXECUTING
                && appState != AppState.PENDING
                && appState != AppState.STOPPED);
    }

    /**
     * To check whether the given voice state is valid
     *
     * @param voiceState the given voice state
     * @return true is valid. false is invalid
     */
    private boolean isValidVoiceState(int voiceState) {
        return !(voiceState != VoiceState.PLAYING
                && voiceState != VoiceState.STOPPED);
    }

    /**
     * To check whether the given media state is valid
     *
     * @param mediaState the given media state
     * @return true is valid. false is invalid
     */
    private boolean isValidMediaState(int mediaState) {
        return !(mediaState != MediaState.LOADING
                && mediaState != MediaState.PLAYING
                && mediaState != MediaState.PAUSED
                && mediaState != MediaState.STOPPED);
    }

    /**
     * private constructor
     */
    private StateManager() {
        init();
    }

    private void init() {
        mSceneStateNode = new SceneStateNode();
        mCutStateNode = new CutStateNode();
    }

    private class StateNode {
        /* app state holder */
        public int mAppState;
        /* voice state holder */
        public int mVoiceState;
        /* media state holder */
        public int mMediaState;
        /* last app state holder */
        public int mLastAppState;
        /* last voice state holder */
        public int mLastVoiceState;
        /* last media state holder */
        public int mLastMediaState;

        public void storeLastAppState() {
            mLastAppState = mAppState;
            Logger.d(String.format("LastAppState: %1$s ; AppState: %2$s ", mLastAppState, mAppState));
        }

        public void storeLastVoiceState() {
            mLastVoiceState = mVoiceState;
            Logger.d(String.format("LastVoiceState: %1$s ; VoiceState: %2$s ", mLastVoiceState, mVoiceState));
        }

        public void storeLastMediaState() {
            mLastMediaState = mMediaState;
            Logger.d(String.format("LastMediaState: %1$s ; MediaState: %2$s ", mLastMediaState, mMediaState));
        }

        public void restoreLastAppState() {
            mAppState = mLastAppState;
            Logger.d(String.format("AppState: %1$s ; LastAppState: %2$s ", mAppState, mLastAppState));
        }

        public void restoreLastVoiceState() {
            mVoiceState = mLastVoiceState;
            Logger.d(String.format("VoiceState: %1$s ; LastVoiceState: %2$s ", mVoiceState, mLastVoiceState));
        }

        public void restoreLastMediaState() {
            mMediaState = mLastMediaState;
            Logger.d(String.format("MediaState: %1$s ; LastMediaState: %2$s ", mMediaState, mLastMediaState));
        }
    }

    private class SceneStateNode extends StateNode {
    }

    private class CutStateNode extends StateNode {
    }


}
