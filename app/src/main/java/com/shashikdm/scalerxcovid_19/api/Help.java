package com.shashikdm.scalerxcovid_19.api;

import java.io.Serializable;
/*
{
        "helpId": "string",
        "helperLocation": {
        "coordinates": [
        0
        ],
        "type": "string",
        "x": 0,
        "y": 0
        },
        "helperName": "string",
        "helperPhoneNumber": "string",
        "message": "string",
        "postId": "string"
}
*/

public class Help implements Serializable {
    private String helpId;
    private GeoJsonPoint helperLocation;
    private String helperName;
    private String helperPhoneNumber;
    private String message;
    private String postId;

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public GeoJsonPoint getHelperLocation() {
        return helperLocation;
    }

    public void setHelperLocation(GeoJsonPoint helperLocation) {
        this.helperLocation = helperLocation;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperPhoneNumber() {
        return helperPhoneNumber;
    }

    public void setHelperPhoneNumber(String helperPhoneNumber) {
        this.helperPhoneNumber = helperPhoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
