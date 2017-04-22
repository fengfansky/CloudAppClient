package com.rokid.rkaudioplayer.entities;

import java.io.Serializable;

/**
 * This Class is the play list item of AudioPlayer。
 * Created by Bassam on 2016/10/31.
 */
public class RKPlayerItemObject implements Serializable {
    private String url;//The audio file's Adress of Web Service
    private String token;
    private String expectedPreviousToken;
    private int offsetInMilliseconds;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpectedPreviousToken() {
        return expectedPreviousToken;
    }

    public void setExpectedPreviousToken(String expectedPreviousToken) {
        this.expectedPreviousToken = expectedPreviousToken;
    }

    public int getOffsetInMilliseconds() {
        return offsetInMilliseconds;
    }

    public void setOffsetInMilliseconds(int offsetInMilliseconds) {
        this.offsetInMilliseconds = offsetInMilliseconds;
    }
    @Override
    public String toString() {
        return getUrl()+getToken();
    }


}
