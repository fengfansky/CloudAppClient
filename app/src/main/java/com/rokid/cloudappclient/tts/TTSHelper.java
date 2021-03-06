package com.rokid.cloudappclient.tts;

import android.os.RemoteException;
import android.text.TextUtils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.WidgetUtils;
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
    private TTSCallback ttsCallback;

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

    public void setTTSCallback(TTSCallback ttsCallback) {
        this.ttsCallback = ttsCallback;
    }

    public int speakTTSError(String ttsContent, final TTSErrorCallback ttsCallback) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            ttsCallback.onTTSFinish();
            return STOP;
        }

        ttsId = rkTts.speak(ttsContent, new RKTTSCallback() {
            @Override
            public void onStart(int id) {
                super.onStart(id);
                Logger.i("TTS is onTTSStart - id: " + id);
                if (null != ttsCallback) {
                    ttsCallback.onTTSStart(id);
                }

                ttsId = id;
            }

            @Override
            public void onCancel(int id) {
                super.onCancel(id);
                Logger.i("TTS is onStop - id: " + id + ", current id: " + ttsId);

                if (id != ttsId) {
                    Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                    return;
                }

                if (null != ttsCallback) {
                    ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onComplete(int id) {
                super.onComplete(id);
                Logger.i("TTS is onComplete - id: " + id);
                if (null != ttsCallback) {
                    ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onError(int id, int err) {
                super.onError(id, err);
                Logger.i("tts onError - id: " + id + ", error: " + err);
                if (null != ttsCallback) {
                    ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }
        });

        return STOP;
    }


    public int speakTTS(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            this.ttsCallback.onTTSFinish();
            return STOP;
        }

        ttsId = rkTts.speak(ttsContent, new RKTTSCallback() {
            @Override
            public void onStart(int id) {
                super.onStart(id);
                Logger.i("TTS is onTTSStart - id: " + id);
                if (null != TTSHelper.this.ttsCallback) {
                    TTSHelper.this.ttsCallback.onTTSStart(id);
                }

                ttsId = id;
            }

            @Override
            public void onCancel(int id) {
                super.onCancel(id);
                Logger.i("TTS is onStop - id: " + id + ", current id: " + ttsId);

                if (id != ttsId) {
                    Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                    return;
                }

                if (null != TTSHelper.this.ttsCallback) {
                    TTSHelper.this.ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onComplete(int id) {
                super.onComplete(id);
                Logger.i("TTS is onComplete - id: " + id);
                if (null != TTSHelper.this.ttsCallback) {
                    TTSHelper.this.ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onError(int id, int err) {
                super.onError(id, err);
                Logger.i("tts onError - id: " + id + ", error: " + err);
                if (null != TTSHelper.this.ttsCallback) {
                    TTSHelper.this.ttsCallback.onTTSFinish();
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }
        });

        return STOP;
    }

    private int speakTTS(String ttsContent, boolean isNeedSendWidget, RKTTSCallback ttsCallback) throws RemoteException {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            ttsCallback.onError(STOP, STOP);
            return STOP;
        }

        Logger.i("start to speakTTS - ttsContent: " + ttsContent);

        if (rkTts == null) {
            Logger.i("TTSService is unbind, push the data to the buffer queue!!!");
            bufferQueue.add(new Node(ttsContent, isNeedSendWidget, ttsCallback));
            return WAIT;
        }

        rkTts.speak(ttsContent, ttsCallback);

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
            speakTTS(node.ttsContent, node.isNeedSendWidget, node.ttsCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class Node {
        String ttsContent;
        boolean isNeedSendWidget;
        RKTTSCallback ttsCallback;

        public Node(String ttsContent, boolean isNeedSendWidget, RKTTSCallback ttsCallback) {
            this.ttsContent = ttsContent;
            this.isNeedSendWidget = isNeedSendWidget;
            this.ttsCallback = ttsCallback;
        }

    }

    public interface TTSCallback {

        void onTTSStart(int id);

        void onTTSFinish();

    }

    public interface TTSErrorCallback extends TTSCallback {

    }

}
