package com.rokid.cloudappclient.msg.queue;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;

/**
 * Created by fanfeng on 2017/4/20.
 */

public interface IMsgQueue<T extends BaseTransferBean>{

    void push(T transferBean);

    T poll(T transferBean);

    void clear();

}
