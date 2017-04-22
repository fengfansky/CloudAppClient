package com.rokid.cloudappclient.util;

/**
 * Description: TODO
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class WidgetUtils {

    /**
     * Private constructor, avoid this class wall be instantiated.
     */
    private WidgetUtils() {
    }

    /**
     * Send the text Widget to mobile client APP.
     */
    public static void sendTxtWidget(final String content) {
//        RKCloudAppApplication.getInstance().threadPoolExecute(new Runnable() {
//            @Override
//            public void run() {
//                IRKConnectionUtil rkConnection = RemoteServiceHelper.getService(RemoteServiceHelper.CONNECTION_SERVICE);
//                if (rkConnection == null) {
//                    return;
//                }
//                try {
//                    Logger.i("SendWidget: " + content);
//                    RKWidgetModelV1 rkWidgetModelV1 = new RKWidgetModelV1();
//                    RKWidgetModelTTS rkWidgetModelTTS = new RKWidgetModelTTS();
//                    rkWidgetModelTTS.setData(TextUtils.isEmpty(content) ? "" : content);
//                    rkWidgetModelTTS.setType(RKWidgetModelTTS.Type.TEXT);
//                    rkWidgetModelV1.setTts(rkWidgetModelTTS);
//                    String ttsWidgetMessage = new Gson().toJson(rkWidgetModelV1);
//                    rkConnection.sendWidget(SystemInfoUtils.getCurrentDomain(), ttsWidgetMessage);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

}
