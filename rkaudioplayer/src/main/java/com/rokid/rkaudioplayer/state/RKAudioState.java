package com.rokid.rkaudioplayer.state;

import com.rokid.rkaudioplayer.constants.RKMediaPlayerConstants;
import com.rokid.rkaudioplayer.entities.RKPlayerItemObject;

import java.io.Serializable;

/**
 * Created by Bassam on 2016/11/1.
 */

public class RKAudioState implements Serializable {
    public String token;
    private String url;
    private String domain;
    
    public int offsetInMilliseconds;
    private String state;
    private long timestamp;

    public RKAudioState(String domain) {
        this.domain = domain;
        initState();
    }

    public void initState() {
        initState(null);
    }

    public void initState(RKPlayerItemObject rkPlayerObject) {
        if (rkPlayerObject == null) {
            token = null;
            offsetInMilliseconds = 0;
            state = RKMediaPlayerConstants.PlayActivity.PLAYER_ACTIVITY_IDLE;
            url = null;
            timestamp = System.currentTimeMillis();
        } else {
            token = rkPlayerObject.getToken();
            offsetInMilliseconds = rkPlayerObject.getOffsetInMilliseconds();
            url = rkPlayerObject.getUrl();
        }
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOffsetInMilliseconds() {
        return offsetInMilliseconds;
    }

    public void setOffsetInMilliseconds(int offsetInMilliseconds) {
        this.offsetInMilliseconds = offsetInMilliseconds;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void upTimestamp() {
        timestamp = System.currentTimeMillis();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "RKAudioState{" +
                "token='" + token + '\'' +
                ", url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", offsetInMilliseconds=" + offsetInMilliseconds +
                ", state='" + state + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
