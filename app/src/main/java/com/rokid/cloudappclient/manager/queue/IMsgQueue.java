package com.rokid.cloudappclient.manager.queue;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;

/**
 * Created by fanfeng on 2017/4/20.
 */

public interface IMsgQueue<T extends BaseTransferBean>{

    public void push(T transferBean);

    public T poll(T transferBean);

    public void clear();

}
