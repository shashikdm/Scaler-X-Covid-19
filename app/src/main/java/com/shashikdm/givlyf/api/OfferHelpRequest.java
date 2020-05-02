package com.shashikdm.givlyf.api;

public class OfferHelpRequest {
    private String message;
    private String postId;

    public OfferHelpRequest(String message, String postId) {
        this.message = message;
        this.postId = postId;
    }
}
