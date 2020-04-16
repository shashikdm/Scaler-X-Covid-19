package com.shashikdm.scalerxcovid_19.api;

public class UpdateUserRequest {
    private String name;
    private Point homeLocation;

    public UpdateUserRequest(String name, Double x, Double y) {
        this.name = name;
        this.homeLocation = new Point(x, y);
    }
}
