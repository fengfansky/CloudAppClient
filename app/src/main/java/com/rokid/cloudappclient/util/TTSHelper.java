package com.rokid.cloudappclient.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.tts.ITts;
import com.rokid.tts.ITtsCallback;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This is a TTS tools, used to send the TTS, stop the TTS.
 *
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class TTSHelper {

    private static final int WAIT = -2;
    private static final int STOP = -1;
    private static final int QUEUE_CAPACITY = 5;
    private volatile static TTSHelper instance;
    private ITts tts;
    private int ttsId = STOP;
    private Queue<Node> bufferQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    /**
     * Private constructor, avoid this class wall be instantiated.
     */
    private TTSHelper() {
    }

    public void bindTTSService() {
        if (null == tts) {
            Intent intent = new Intent("com.com.rokid.tts.TtsService");
            intent.setPackage("com.com.rokid.tts");
            Boolean isBind = RKCloudAppApplication.getInstance().bindService(intent, conn, Context.BIND_AUTO_CREATE);
            Logger.d("Start to Bind : " + isBind);
        }
    }

    public static TTSHelper getInstance() {
        if (null == instance) {
            synchronized (TTSHelper.class) {
                if (null == instance) {
                    instance = new TTSHelper();
                }
            }
        }

        return instance;
    }

    public int speakTTS(String ttsContent, final TTSCallback callback) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            callback.onTTSFinish();
            return STOP;
        }

        try {
            Logger.d("TTSContent: " + ttsContent);
            return speakTTS(ttsContent, true, new ITtsCallback.Stub() {
                @Override
                public void onStart(int id) {
                    Logger.i("TTS is onStart - id: " + id);
                    if (null != callback) {
                        callback.onStart(id);
                    }

                    ttsId = id;
                }

                @Override
                public void onComplete(int id) {
                    Logger.i("TTS is onComplete - id: " + id);
                    if (null != callback) {
                        callback.onTTSFinish();
                    }

                    ttsId = STOP;
                    speakTTSFromBufferQueue();
                }

                @Override
                public void onStop(int id) {
                    Logger.i("TTS is onStop - id: " + id + ", current id: " + ttsId);

                    if (id != ttsId) {
                        Logger.i("The new tts is already speaking, previous tts stop should not callback");
                        return;
                    }

                    if (null != callback) {
                        callback.onTTSFinish();
                    }

                    ttsId = STOP;
                    speakTTSFromBufferQueue();
                }

                @Override
                public void onError(int id, int err) {
                    Logger.i("tts onError - id: " + id + ", error: " + err);
                    if (null != callback) {
                        callback.onTTSFinish();
                    }

                    ttsId = STOP;
                    speakTTSFromBufferQueue();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return STOP;
    }

    private int speakTTS(String ttsContent, boolean isNeedSendWidget, ITtsCallback callback) throws RemoteException {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            callback.onError(STOP, STOP);
            return STOP;
        }

        if (null == tts) {
            Logger.i("TTSService is unbind, push the data to the buffer queue!!!");
            bufferQueue.add(new Node(ttsContent, isNeedSendWidget, callback));
            return WAIT;
        }

        Logger.i("start to speakTTS - ttsContent: " + ttsContent);
        ttsId = tts.speak(ttsContent, callback);

        if (isNeedSendWidget) {
            Logger.i("is need send the widget");
            WidgetUtils.sendTxtWidget(ttsContent);
        }

        return ttsId;
    }

    public void stopTTS() {
        stopTTS(ttsId);
    }

    public void stopTTS(int ttsId) {
        if (null == tts || ttsId <= STOP) {
            Logger.e("TTSService is unbind");
            return;
        }

        try {
            tts.stop(ttsId);
            ttsId = STOP;
            Logger.i("ttsId: " + ttsId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void speakTTSFromBufferQueue() {
        if (bufferQueue.size() < 1) {
            Logger.i("Buffer queue is empty, don't play the TTS");
            return;
        }

        try {
            Logger.i("Start speak TTS from bufferQueue");
            Node node = bufferQueue.poll();
            speakTTS(node.ttsContent, node.isNeedSendWidget, node.callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.i("name : " + name);
            tts = ITts.Stub.asInterface(service);
            speakTTSFromBufferQueue();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.i("name : " + name);
            tts = null;
        }
    };

    private static class Node {
        String ttsContent;
        boolean isNeedSendWidget;
        ITtsCallback callback;

        public Node(String ttsContent, boolean isNeedSendWidget, ITtsCallback callback) {
            this.ttsContent = ttsContent;
            this.isNeedSendWidget = isNeedSendWidget;
            this.callback = callback;
        }

    }

    public interface TTSCallback {

        void onStart(int id);

        void onTTSFinish();

    }

}
