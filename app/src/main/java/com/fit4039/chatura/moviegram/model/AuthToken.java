package com.fit4039.chatura.moviegram.model;

/**
 * Created by hp on 6/3/2015.
 */

// this class stores Twitter authentication information
public class AuthToken {
    String tokenType;
    String accessToken;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
