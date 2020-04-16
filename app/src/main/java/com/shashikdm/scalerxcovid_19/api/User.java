package com.shashikdm.scalerxcovid_19.api;


import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class User {
    private String name;
    private String phoneNumber;
    private String token;
    private Boolean detailsUpdated;
    private GeoJsonPoint homeLocation;
    private String userId;

    public User(String phoneNumber, String token) {
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    static public User fromJson(String json) {
        return new Gson().fromJson(json, User.class);
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDetailsUpdated() {
        return detailsUpdated;
    }

    public void setDetailsUpdated(Boolean detailsUpdated) {
        this.detailsUpdated = detailsUpdated;
    }

    public GeoJsonPoint getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(GeoJsonPoint homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
