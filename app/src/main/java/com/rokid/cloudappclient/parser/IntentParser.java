package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.DeviceInfoBean;
import com.rokid.cloudappclient.bean.NLPBean;
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

    public void parseIntent(Intent intent) {
        if (intent == null) {
            Logger.d("intent null !");
            return;
        }
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp) && ttsSpeakInterface != null) {
            Logger.d("NLP is empty!!!");
            ttsSpeakInterface.speakNLPEmptyErrorTTS();
            return;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = new Gson().fromJson(nlp, NLPBean.class);

        if (null == nlpBean && ttsSpeakInterface != null) {
            Logger.d("NLPData is empty!!!");
            ttsSpeakInterface.speakNLPDateEmptyErrorTTS();
            return;
        }

        Map<String, String> slots = nlpBean.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            return;
        }

        if (!slots.containsKey(KEY_DEVICE_INFO)) {
            Logger.i("NLP slots has no DEVICE_INFO");
            return;
        }

        String deviceInfo = slots.get(KEY_DEVICE_INFO);

        Logger.d("device Info : " + deviceInfo);

        DeviceInfoBean deviceInfoBean = null;

        deviceInfoBean = new Gson().fromJson(deviceInfo, DeviceInfoBean.class);

        DeviceInfoUtil.setDeviceInfoBean(deviceInfoBean);

        if (!slots.containsKey(KEY_COMMON_RESPONSE)) {
            Logger.i("NLP slots has no COMMON_RESPONSE info");
            return;
        }

        String extraString = slots.get(KEY_COMMON_RESPONSE);

        if (TextUtils.isEmpty(extraString)) {
            Logger.i("COMMON_RESPONSE info is invalid");
            return;
        }

        CommonResponse commonResponse = null;

        try {
            commonResponse = new Gson().fromJson(extraString, CommonResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == commonResponse && ttsSpeakInterface != null) {
            Logger.d("parse common response failed");
            ttsSpeakInterface.speakNLPDateEmptyErrorTTS();
            return;
        }

        CommonResponseParser.getInstance().parseCommonResponse(commonResponse);
    }

}
