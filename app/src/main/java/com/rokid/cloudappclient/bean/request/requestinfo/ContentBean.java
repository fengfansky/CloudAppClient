package com.rokid.cloudappclient.bean.request.requestinfo;

/**
 * This object may be one of the two kinds of requests: IntentRequest and EventRequest.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class ContentBean<T> {

    /**
     * sent when a voice interaction execution has been started.
     */
    private static final String EVENT_VOICE_STARTED = "Voice.STARTED";
    /**
     * sent when a voice interaction execution finished.
     */
    private static final String EVENT_VOICE_FINISHED = "Voice.FINISHED";
    /**
     * send when a display has been showed up.
     */
    private static final String EVENT_DISPLAY_SHOW = "Display.SHOW";
    /**
     * send when a display has been dismissed.
     */
    private static final String EVENT_DISPLAY_DISMISS = "Display.DISMISS";
    /**
     * sent when media player starts playing.
     */
    private static final String EVENT_MEDIA_START_PLAYING = "Media.START_PLAYING";
    /**
     * sent when media player stops playing.
     */
    private static final String EVENT_MEDIA_PAUSED = "Media.PAUSED";
    /**
     * sent when media player near the end. Typically, this event will be sent in 15 seconds before the end.
     */
    private static final String EVENT_MEDIA_NEAR_FINISH = "Media.NEAR_FINISH";
    /**
     * sent when media player completes playing the current media item.
     */
    private static final String EVENT_MEDIA_FINISHED = "Media.FINISHED";

    /**
     * IntentRequest is based on the result of NLP result.
     * Domain, Intent and Slots are indicated.
     * CloudApps with the same domain should handle the intent with slots to make a response.
     */
    private String domain;
    private String intent;
    private T slots;

    /**
     * EventRequest is event based.
     * Event handling is an optional request for CloudApps.
     * CloudApps can make related response on demand.
     */
    private String event;
    private String extra;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public T getSlots() {
        return slots;
    }

    public void setSlots(T slots) {
        this.slots = slots;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
