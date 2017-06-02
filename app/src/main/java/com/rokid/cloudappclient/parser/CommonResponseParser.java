package com.rokid.cloudappclient.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.RealAction;
import com.rokid.cloudappclient.bean.response.CloudAppResponse;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.msg.action.MediaAction;
import com.rokid.cloudappclient.msg.action.VoiceAction;
import com.rokid.cloudappclient.msg.manager.MsgContainerManager;
import com.rokid.cloudappclient.msg.manager.StateManager;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.CommonResponseHelper;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by fanfeng on 2017/6/1.
 */

public class CommonResponseParser {

    private static CommonResponseParser parser;

    public static CommonResponseParser getInstance() {
        if (parser == null) {
            synchronized (CommonResponseParser.class) {
                if (parser == null)
                    parser = new CommonResponseParser();
            }
        }
        return parser;
    }

    TTSSpeakInterface mTtsSpeakInterface;

    public void setTTSSpeakInterface(TTSSpeakInterface ttsSpeakInterface) {
        mTtsSpeakInterface = ttsSpeakInterface;
    }

    public void parseCommonResponse(CommonResponse commonResponse) {

        RealAction realAction = CommonResponseHelper.validateCommonResponse(commonResponse);
        if (null == realAction || !realAction.isValid()) {

            Logger.d("action for cloud app is illegal");
            // TODO: check app state. if app is running well, ignore this exception. Otherwise, show exception.
            return;
        }

        // update current application info for further use. App info consists: DOMAIN and SHOT
        StateManager.getInstance().setCurrentAppInfo(realAction.getDomain(), realAction.getShot());

        preHandlingForRealAction(realAction);

        processRealAction(realAction);
    }

    public void parseSendEventResponse(Response response) {

        SendEvent.SendEventResponse eventResponse = null;
        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (eventResponse != null) {
            Logger.d(" eventResponse : " + eventResponse.getResponse());
            CloudAppResponse cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudAppResponse.class);
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setAction(cloudResponse);
            CommonResponseParser commonResponseParser = CommonResponseParser.getInstance();
            commonResponseParser.parseCommonResponse(commonResponse);
        }
    }

    /**
     * To process real action
     *
     * @param realAction the validated action
     */
    private void processRealAction(RealAction realAction) {
        if (realAction == null)
            return;

        if (realAction.getVoice() != null) {
            MsgContainerManager.getInstance().push(new TransferVoiceBean(realAction.getDomain(), realAction.getShot(), realAction.getVoice()));
        }
        if (realAction.getMedia() != null) {
            MsgContainerManager.getInstance().push(new TransferMediaBean(realAction.getDomain(), realAction.getShot(), realAction.getMedia()));
        }
    }

    private void preHandlingForRealAction(RealAction realAction) {

        String responseType = realAction.getResType();
        String actionType = realAction.getActionType();
        String shot = realAction.getShot();

        Logger.d("primaryReadActionHandling - responseType: " + responseType + ", actionType: " + actionType + ", shot: " + shot);

        if (!TextUtils.isEmpty(actionType) && ActionBean.TYPE_EXIT.equals(actionType)) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            if (mTtsSpeakInterface != null) {
                mTtsSpeakInterface.finishActivity();
            }
        } else if (ResponseBean.TYPE_INTENT.equals(responseType) && ActionBean.FORM_CUT.equals(shot)) {
            // when the response type is INTENT and the application shot is CUT, current action should be
            // stopped and the queue of cut should be cleared.

            MediaAction.getInstance().stopAction();
            VoiceAction.getInstance().stopAction();

            MsgContainerManager.getInstance().clear();
        }
    }
}
