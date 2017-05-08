package com.rokid.cloudappclient.tts;


/**
 * Created by fanfeng on 2017/5/8.
 */

public interface TTSSpeakInterface {

    void speakIntentEmptyErrorTTS();

    void speakNLPEmptyErrorTTS();

    void speakNLPDateEmptyErrorTTS();

    void speakNLPInvalidErrorTTS();

    void finishActivity();

}
