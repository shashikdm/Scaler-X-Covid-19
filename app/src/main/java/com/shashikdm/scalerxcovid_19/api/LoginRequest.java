package com.shashikdm.scalerxcovid_19.api;

public class LoginRequest {
    private Integer otpLength;

    private String phoneNumber;

    public LoginRequest(Integer otpLength, String phoneNumber) {
        this.otpLength = otpLength;
        this.phoneNumber = phoneNumber;
    }
}
