package com.shashikdm.scalerxcovid_19.api;

import java.io.Serializable;

public class LoginResponse implements Serializable {

    private String requestId;
    private Long expiry;
    private Integer attemptsAllowed;

    public LoginResponse(String requestId, Long expiry, Integer attemptsAllowed) {
        this.requestId = requestId;
        this.expiry = expiry;
        this.attemptsAllowed = attemptsAllowed;
    }

    public Long getExpiry() {
        return expiry;
    }

    public String getRequestId() {
        return requestId;
    }

    public Integer getAttemptsAllowed() {
        return attemptsAllowed;
    }

}
