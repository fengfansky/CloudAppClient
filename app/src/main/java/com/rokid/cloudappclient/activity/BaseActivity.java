package com.rokid.cloudappclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.rokid.cloudappclient.R;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.parser.IntentParser;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;

import java.lang.ref.WeakReference;

/**
 * This is a basic Activity, all the Activity in the project are to extends it.
 * It management common life cycle and have some common methods to parse intent、NLP and error TTS.
 *
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public abstract class BaseActivity extends Activity implements TTSHelper.TTSCallback, TTSSpeakInterface {


    private WeakReference<IntentParser> parserWeakReference = new WeakReference<>(new IntentParser(this));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("RKCloudAppActivity OnCreated");

        initViews(savedInstanceState);
        initTTS();
        parserWeakReference.get().startParse(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("RKCloudAppActivity onNewIntent");

        parserWeakReference.get().startParse(getIntent());
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("RKCloudAppActivity onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("RKCloudAppActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("RKCloudAppActivity onResume");

        if (StateManager.getInstance().getAppState() == StateManager.AppState.PENDING) {
            StateManager.getInstance().restoreAllLastState();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("RKCloudAppActivity onPause");

        StateManager.getInstance().updateAppState(StateManager.AppState.PENDING);
        StateManager.getInstance().storeAllLastState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("RKCloudAppActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("RKCloudApp onDestroy");
    }

    /**
     * ------------------TTS REFERENCE START--------------------
     **/
    TTSHelper ttsHelper = TTSHelper.getInstance();

    private void initTTS() {
        ttsHelper.setTTSCallback(this);
    }

    @Override
    public void onTTSStart(int id) {

    }

    @Override
    public void onTTSFinish() {
        Logger.d("onTTSFinish finish()");
        StateManager.getInstance().updateVoiceState(StateManager.VoiceState.STOPPED);
        finish();
    }

    @Override
    public void speakIntentEmptyErrorTTS() {
        speakTTSError(getResources().getString(R.string.tts_intent_empty_error));
    }

    @Override
    public void speakNLPEmptyErrorTTS() {
        speakTTSError(getResources().getString(R.string.tts_nlp_empty_error));
    }

    @Override
    public void speakNLPDateEmptyErrorTTS() {
        speakTTSError(getResources().getString(R.string.tts_nlp_data_empty_error));
    }

    @Override
    public void speakNLPInvalidErrorTTS() {
        speakTTSError(getResources().getString(R.string.tts_nlp_invalid_error));
    }

    private void speakTTSError(String ttsContent) {
        ttsHelper.speakTTSError(ttsContent, new TTSHelper.TTSErrorCallback() {
            @Override
            public void onTTSStart(int id) {
            }

            @Override
            public void onTTSFinish() {
                // TODO
                finish();
            }
        });
    }

    @Override
    public void finishActivity() {
        finish();
    }

    /**
     * ------------------TTS REFERENCE END---------------------
     **/


    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);



}
