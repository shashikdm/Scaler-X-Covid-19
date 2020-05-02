package com.shashikdm.givlyf.api;

public class LoginVerifyRequest {
    private String otpCode, requestId;
    public LoginVerifyRequest(String otpCode, String requestId) {
        this.otpCode = otpCode;
        this.requestId = requestId;
    }
}
