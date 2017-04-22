package com.rokid.cloudappclient.bean.request.requestinfo;

/**
 * In Request section,
 * the real request action will be illustrated including the request type and content.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class RequestBean {

    public static final String TYPE_INTENT = "INTENT";
    public static final String TYPE_EVENT = "EVENT";

    /**
     * ONLY type INTENT and EVENT are available currently
     */
    private String reqType;
    private String reqId;
    /**
     * indicates the last corresponding response id for the request.
     * It is *ONLY in effect when the reqType is EVENT.
     */
    private String currentReqId;
    private ContentBean content;

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getCurrentReqId() {
        return currentReqId;
    }

    public void setCurrentReqId(String currentReqId) {
        this.currentReqId = currentReqId;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class Builder<T> {

        private RequestBean request;

        public Builder() {
            request = new RequestBean();
        }

        public Builder reqType(String reqType) {
            request.setReqId(reqType);
            return this;
        }

        public Builder reqId(String reqId) {
            request.setReqId(reqId);
            return this;
        }

        public Builder currentReqId(String currentReqId) {
            request.setCurrentReqId(currentReqId);
            return this;
        }

        public Builder content(ContentBean content) {
            request.setContent(content);
            return this;
        }

        public Builder content_Domain(String domain) {
            checkContent();
            request.getContent().setDomain(domain);
            return this;
        }

        public Builder content_Intent(String intent) {
            checkContent();
            request.getContent().setIntent(intent);
            return this;
        }

        public Builder content_Slots(T slots) {
            checkContent();
            request.getContent().setSlots(slots);
            return this;
        }

        public Builder content_Event(String event) {
            checkContent();
            request.getContent().setEvent(event);
            return this;
        }

        public Builder content_Extra(String extra) {
            checkContent();
            request.getContent().setExtra(extra);
            return this;
        }

        private void checkContent() {
            if (null == request.getContent()) {
                request.setContent(new ContentBean());
            }
        }
    }

}
