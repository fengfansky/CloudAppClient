package com.rokid.cloudappclient.msg.queue;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by fanfeng on 2017/4/21.
 */

public abstract class BaseMsgQueue<T extends BaseTransferBean> {

    private static final int QUEUE_CAPACITY = 30;

    public String domain;
    public Queue<T> msgQueue;

    public BaseMsgQueue() {
        msgQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    }

    public abstract void push(T transferBean);

    public T poll() {
        return msgQueue.poll();
    }

    public void clear() {
        msgQueue.clear();
    }

}
