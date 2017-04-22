package com.rokid.cloudappclient.util;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.RealAction;
import com.rokid.cloudappclient.bean.response.CloudAppResponse;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;

/**
 * Created by showingcp on 3/13/17.
 */

public class CommonResponseHelper {


    private static boolean checkCloudAppAction(CloudAppResponse action) {
        if (action == null) {
            Logger.i("checkCloudAppResponse: action is null");
            return false;
        }

        // check version
        String version = action.getVersion();
        if (TextUtils.isEmpty(version) || !version.equals(CommonConfig.PROTOCOL_VERSION)) {
            Logger.i("checkCloudAppAction: given protocol version: " + version + " is invalid");
            return false;
        }

        // check response
        if (action.getResponse() == null) {
            Logger.i("checkAction: response of action is null");
            return false;
        }

        // check response domain
        if (TextUtils.isEmpty(action.getResponse().getDomain())) {
            Logger.i("checkCloudAppAction: domain for response is invalid");
            return false;
        }

        // check response shot
        String shot = action.getResponse().getShot();

        if (TextUtils.isEmpty(shot)) {
            Logger.i("checkCloudAppAction: shot for response is invalid");
            return false;
        }

        if (!shot.equals(ResponseBean.SHOT_CUT)
                && !shot.equals(ResponseBean.SHOT_SCENE)) {
            Logger.i("checkCloudAppAction: ignore for unknown shot type: " + shot);
            return false;
        }

        // TODO check response id
//        if (TextUtils.isEmpty(action.getResponse().getRespId())) {
//            Logger.i( "checkCloudAppAction: respId is invalid");
//            return false;
//        }

        // check response type
        String resType = action.getResponse().getResType();
        if (TextUtils.isEmpty(resType)) {
            Logger.i("checkCloudAppAction: resType is invalid");
            return false;
        }

        if (!resType.equals(ResponseBean.TYPE_INTENT)
                && !resType.equals(ResponseBean.TYPE_EVENT)) {
            Logger.i("checkCloudAppAction: ignore for unknown resType: " + resType);
            return false;
        }

        // check response action
        ActionBean responseAction = action.getResponse().getAction();
        if (responseAction == null) {
            Logger.i("checkCloudAppAction: response action is null");
            return false;
        }

        // check response action type
        String responseActionType = responseAction.getType();
        if (TextUtils.isEmpty(responseActionType)) {
            Logger.i("checkCloudAppAction: response action type is invalid");
            return false;
        }

        if (!responseActionType.equals(ActionBean.TYPE_NORMAL)
                && !responseActionType.equals(ActionBean.TYPE_EXIT)) {
            Logger.i("checkCloudAppAction: ignore unknown response action type: " + responseActionType);
            return false;
        }

        // check response action elements
        if (!checkActionElements(action.getResponse())) {
            Logger.i("checkCloudAppAction: elements are invalid");
            return false;
        }

        return true;
    }


    /**
     * Method to validate the whole common response including voice and media and generate the real action object.
     *
     * @param commonResponse the given common response {@link CommonResponse}
     * @return the real action object. {@link RealAction}
     */
    public static RealAction validateCommonResponse(final CommonResponse commonResponse) {
        RealAction realAction = new RealAction();

        if (null == commonResponse) {
            Logger.i("common response is null");
            return null;
        }

        CloudAppResponse cloudAppResponse = commonResponse.getAction();

        if (!checkCloudAppAction(cloudAppResponse)) {
            Logger.i("cloud app response is invalid");
            return null;
        }

        realAction.setValid(true);
        realAction.setDomain(cloudAppResponse.getResponse().getDomain());
        realAction.setActionType(cloudAppResponse.getResponse().getAction().getType());
        realAction.setAsr(commonResponse.getAsr());
        realAction.setNlp(commonResponse.getNlp());
        realAction.setRespId(cloudAppResponse.getResponse().getRespId());
        realAction.setResType(cloudAppResponse.getResponse().getResType());
        realAction.setShot(cloudAppResponse.getResponse().getShot());
        realAction.setShouldEndSession(cloudAppResponse.getResponse().getAction().isShouldEndSession());
        realAction.setVoice(cloudAppResponse.getResponse().getAction().getVoice());
        realAction.setMedia(cloudAppResponse.getResponse().getAction().getMedia());

        return realAction;
    }

    /**
     * Private method to check voice, media and display
     *
     * @param responseBean
     * @return
     */
    private static boolean checkActionElements(ResponseBean responseBean) {
        ActionBean responseAction = responseBean.getAction();
        String responseActionType = responseAction.getType();

        MediaBean mediaBean = responseAction.getMedia();
        VoiceBean voiceBean = responseAction.getVoice();

        if (mediaBean == null
                && voiceBean == null
                && responseAction.getDisplay() == null) {
            if (!responseActionType.equals(ActionBean.TYPE_EXIT)) {
                Logger.i("checkCloudAppAction: media, voice and display cannot be null when response action type is not EXIT");
                return false;
            } else {
                return true;
            }
        } else {
            if (mediaBean != null && !mediaBean.isValid()) {
                Logger.i("media is invalid");
                return false;
            }

            if (voiceBean != null && !voiceBean.isValid()) {
                Logger.i("voice is invalid");
                return false;
            }
        }

        return true;
    }

}
