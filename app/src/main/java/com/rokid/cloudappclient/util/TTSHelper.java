package com.rokid.cloudappclient.util;

import android.os.RemoteException;
import android.text.TextUtils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import rokid.os.RKTTS;
import rokid.os.RKTTSCallback;

/**
 * This is a TTS tools, used to send the TTS, stop the TTS.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class TTSHelper {

    private static final int WAIT = -2;
    private static final int STOP = -1;
    private static final int QUEUE_CAPACITY = 30;
    private volatile static TTSHelper instance;
    private RKTTS rkTts = new RKTTS();
    private int ttsId = STOP;
    private Queue<Node> bufferQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

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

        ttsId = rkTts.speak(ttsContent, new RKTTSCallback() {
            @Override
            public void onStart(int id) {
                super.onStart(id);
                Logger.i("TTS is onStart - id: " + id);
                if (null != callback) {
                    callback.onStart(id);
                }

                ttsId = id;
            }

            @Override
            public void onCancel(int id) {
                super.onCancel(id);
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
            public void onComplete(int id) {
                super.onComplete(id);
                Logger.i("TTS is onComplete - id: " + id);
                if (null != callback) {
                    callback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onError(int id, int err) {
                super.onError(id, err);
                Logger.i("tts onError - id: " + id + ", error: " + err);
                if (null != callback) {
                    callback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }
        });

        return STOP;
    }

    private int speakTTS(String ttsContent, boolean isNeedSendWidget, RKTTSCallback callback) throws RemoteException {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            callback.onError(STOP, STOP);
            return STOP;
        }

        Logger.i("start to speakTTS - ttsContent: " + ttsContent);

        rkTts.speak(ttsContent, callback);

        return ttsId;
    }

    public void stopTTS() {
        stopTTS(ttsId);
    }

    public void stopTTS(int ttsId) {
        if (ttsId <= STOP) {
            Logger.e("TTSService is unbind");
            return;
        }

        Logger.d("stopTTS tts ");
        rkTts.stop(ttsId);
        ttsId = STOP;
        Logger.i("ttsId: " + ttsId);
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

    private static class Node {
        String ttsContent;
        boolean isNeedSendWidget;
        RKTTSCallback callback;

        public Node(String ttsContent, boolean isNeedSendWidget, RKTTSCallback callback) {
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
