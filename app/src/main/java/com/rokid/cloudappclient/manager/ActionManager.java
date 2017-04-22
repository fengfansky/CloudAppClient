package com.rokid.cloudappclient.manager;

import com.rokid.cloudappclient.bean.RealAction;
import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.manager.queue.MsgContainerManager;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by showingcp on 3/14/17.
 */
public class ActionManager {

    private static volatile ActionManager mInstance;

    private ActionManager() {
    }

    public static ActionManager getInstance() {
        if (null == mInstance) {
            synchronized (ActionManager.class) {
                if (null == mInstance) {
                    mInstance = new ActionManager();
                }
            }
        }

        return mInstance;
    }

    public void handleAction(RealAction realAction) {
        if (null == realAction) {
            Logger.e("realAction can't be empty!!!");
            return;
        }

        // TODO
        if (realAction.getResType().equals(ResponseBean.TYPE_INTENT)) {
            if (realAction.getActionType().equals(ActionBean.TYPE_EXIT)) {
                if (ResponseBean.SHOT_CUT.equals(realAction.getShot())) {
                    // TODO: stop current voice and media; clear voice and media queue;
                }
            }
        } else {

        }

        // TODO
        dispatcher(new TransferVoiceBean(realAction.getDomain(), realAction.getShot(), realAction.getVoice()));
        dispatcher(new TransferMediaBean(realAction.getDomain(), realAction.getShot(), realAction.getMedia()));
    }

    private void dispatcher(BaseTransferBean transferObject) {
        // TODO
        MsgContainerManager.getInstance().push(transferObject);
    }

}
