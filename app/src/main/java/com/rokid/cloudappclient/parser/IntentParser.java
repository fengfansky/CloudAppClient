package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.DeviceInfoBean;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.bean.RealAction;
import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.action.MediaAction;
import com.rokid.cloudappclient.msg.action.VoiceAction;
import com.rokid.cloudappclient.msg.manager.MsgContainerManager;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.util.CommonResponseHelper;
import com.rokid.cloudappclient.util.DeviceInfoUtil;
import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;

import java.util.Map;

/**
 * Created by fanfeng on 2017/5/8.
 */

public class IntentParser {

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";
    private static final String KEY_DEVICE_INFO = "device";

    TTSSpeakInterface ttsSpeakInterface;

    public IntentParser(TTSSpeakInterface ttsSpeakInterface) {
        this.ttsSpeakInterface = ttsSpeakInterface;
    }

    public void startParse(Intent intent) {
        if (isIntentValidate(intent)) {
            Logger.d("startParse intent invalidate!");
        } else {
            CommonResponse commonResponse = parseIntent(intent);
            if (null == commonResponse) {
                Logger.d("parse common response failed");
                ttsSpeakInterface.speakNLPDateEmptyErrorTTS();
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

    private boolean isIntentValidate(Intent intent) {
        if (null == intent) {
            Logger.d("Intent is null!!!");
            return true;
        }
        return false;
    }

    private CommonResponse parseIntent(Intent intent) {
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            ttsSpeakInterface.speakNLPEmptyErrorTTS();
            return null;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = new Gson().fromJson(nlp, NLPBean.class);

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            ttsSpeakInterface.speakNLPDateEmptyErrorTTS();
            return null;
        }

        Map<String, String> slots = nlpBean.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            return null;
        }

        if (!slots.containsKey(KEY_DEVICE_INFO)) {
            Logger.i("NLP slots has no DEVICE_INFO");
            return null;
        }

        String deviceInfo = slots.get(KEY_DEVICE_INFO);

        Logger.d("device Info : " + deviceInfo);

        DeviceInfoBean deviceInfoBean = null;

        deviceInfoBean = new Gson().fromJson(deviceInfo, DeviceInfoBean.class);

        DeviceInfoUtil.setDeviceInfoBean(deviceInfoBean);

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

    /**
     * To process real action
     *
     * @param realAction the validated action
     */
    protected void processRealAction(RealAction realAction) {
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
            ttsSpeakInterface.finishActivity();
        } else if (ResponseBean.TYPE_INTENT.equals(responseType) && ActionBean.FORM_CUT.equals(shot)) {
            // when the response type is INTENT and the application shot is CUT, current action should be
            // stopped and the queue of cut should be cleared.

            MediaAction.getInstance().stopAction();
            VoiceAction.getInstance().stopAction();

            MsgContainerManager.getInstance().clear();
        }
    }

}
