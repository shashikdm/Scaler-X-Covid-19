package com.shashikdm.scalerxcovid_19.api;

import java.io.Serializable;

//{
//        "postId": "5e8caf4212dbf061cd23a03d",
//        "title": "string",
//        "description": "string",
//        "willingToPay": true,
//        "location": {
//        "x": 48.669,
//        "y": -4.329,
//        "type": "Point",
//        "coordinates": [
//        48.669,
//        -4.329
//        ]
//        },
//        "locationDisplayName": "string",
//        "fullAddress": "string",
//        "geoHash": "gbsuv"
//}
public class Post implements Serializable {
    private String postId, title, description;
    private String locationDisplayName, fullAddress;
    private String geoHash;
    private Boolean willingToPay;
    private GeoJsonPoint location;

    public Post(String postId, String title, String description, String locationDisplayName, String fullAddress, String geoHash, Boolean willingToPay, GeoJsonPoint location) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.locationDisplayName = locationDisplayName;
        this.fullAddress = fullAddress;
        this.geoHash = geoHash;
        this.willingToPay = willingToPay;
        this.location = location;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationDisplayName() {
        return locationDisplayName;
    }

    public void setLocationDisplayName(String locationDisplayName) {
        this.locationDisplayName = locationDisplayName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public Boolean getWillingToPay() {
        return willingToPay;
    }

    public void setWillingToPay(Boolean willingToPay) {
        this.willingToPay = willingToPay;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }
}
