package com.rokid.cloudappclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.R;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.bean.RealAction;
import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.action.MediaAction;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.msg.action.VoiceAction;
import com.rokid.cloudappclient.msg.manager.MsgContainerManager;
import com.rokid.cloudappclient.util.CommonResponseHelper;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.TTSHelper;

import java.util.Map;

/**
 * This is a basic Activity, all the Activity in the project are to extends it.
 * It management common life cycle and have some common methods to parse intent、NLP and error TTS.
 *
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public abstract class BaseActivity extends Activity {

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("RKCloudAppActivity OnCreated");

        initViews(savedInstanceState);

        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("RKCloudAppActivity onNewIntent");

        checkIntent(intent);
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

    // TODO
    private void checkIntent(Intent intent) {
        if (null == intent) {
            Logger.d("Intent is null!!!");
            speakIntentEmptyErrorTTS();
        } else {
            CommonResponse commonResponse = parseIntent(intent);
            if (null == commonResponse) {
                Logger.d("parse common response failed");
                speakNLPDateEmptyErrorTTS();
                return;
            }

            RealAction realAction = CommonResponseHelper.validateCommonResponse(commonResponse);
            if (null == realAction || !realAction.isValid()) {

                Logger.d("action for cloud app is illegal");
                // TODO: check app state. if app is running well, ignore this exception. Otherwise, show exception.
                return;
            }

            // update current application info for further use. App info consists: DOMAIN and SHOT
            updateCurrentApplicationInfo(realAction);

            preHandlingForRealAction(realAction);

            processRealAction(realAction);
        }
    }

    private CommonResponse parseIntent(Intent intent) {
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            speakNLPEmptyErrorTTS();
            return null;
        }

        Logger.d("Intent - Action: ", nlp);
        NLPBean intentObject = new Gson().fromJson(nlp, NLPBean.class);

        if (null == intentObject) {
            Logger.d("NLPData is empty!!!");
            speakNLPDateEmptyErrorTTS();
            return null;
        }

        Map<String, String> slots = intentObject.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            return null;
        }

        if (!slots.containsKey(KEY_COMMON_RESPONSE)) {
            Logger.i("NLP slots has no COMMON_RESPONSE info");
            return null;
        }

        String extraString = slots.get(KEY_COMMON_RESPONSE);

        if (TextUtils.isEmpty(extraString)) {
            Logger.i("COMMON_RESPONSE info is invalid");
            return null;
        }

        CommonResponse commonResponse = null;

        try {
            commonResponse = new Gson().fromJson(extraString, CommonResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return commonResponse;
    }


    // TODO
    private void speakIntentEmptyErrorTTS() {
        speakTTS(getResources().getString(R.string.tts_intent_empty_error), true);
    }

    // TODO
    private void speakNLPEmptyErrorTTS() {
        speakTTS(getResources().getString(R.string.tts_nlp_empty_error), true);
    }

    // TODO
    private void speakNLPDateEmptyErrorTTS() {
        speakTTS(getResources().getString(R.string.tts_nlp_data_empty_error), true);
    }

    // TODO
    private void speakNLPInvalidErrorTTS() {
        speakTTS(getResources().getString(R.string.tts_nlp_invalid_error), true);
    }

    // TODO
    private void speakTTS(String ttsContent, final boolean shouldFinish) {
        TTSHelper.getInstance().speakTTS(ttsContent, new TTSHelper.TTSCallback() {
            @Override
            public void onStart(int id) {
            }

            @Override
            public void onTTSFinish() {
                // TODO
                if (shouldFinish) {
                    finish();
                }
            }
        });
    }

    private void updateCurrentApplicationInfo(final RealAction realAction) {
        Logger.d("updateCurrentApplicationInfo");

        StateManager.getInstance().setCurrentAppInfo(realAction.getDomain(), realAction.getShot());
    }

    private void preHandlingForRealAction(RealAction realAction) {

        String responseType = realAction.getResType();
        String actionType = realAction.getActionType();
        String shot = realAction.getShot();

        Logger.d("primaryReadActionHandling - responseType: " + responseType + ", actionType: " + actionType + ", shot: " + shot);

        if (!TextUtils.isEmpty(actionType) && ActionBean.TYPE_EXIT.equals(actionType)) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            finish();
        } else if (ResponseBean.TYPE_INTENT.equals(responseType) && ResponseBean.SHOT_CUT.equals(shot)) {
            // when the response type is INTENT and the application shot is CUT, current action should be
            // stopped and the queue of cut should be cleared.

            MediaAction.getInstance().stopPlay();
            VoiceAction.getInstance().stopPlay();

            MsgContainerManager.getInstance().clear();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * To process real action
     *
     * @param realAction the validated action
     */
    protected void processRealAction(RealAction realAction){
        if (realAction == null)
            return;

        if (realAction.getVoice() != null) {
            dispatcher(new TransferVoiceBean(realAction.getDomain(), realAction.getShot(), realAction.getVoice()));
        }
        if (realAction.getMedia() != null) {
            dispatcher(new TransferMediaBean(realAction.getDomain(), realAction.getShot(), realAction.getMedia()));
        }
    }

    private void dispatcher(BaseTransferBean transferObject) {
        MsgContainerManager.getInstance().push(transferObject);
    }

}
