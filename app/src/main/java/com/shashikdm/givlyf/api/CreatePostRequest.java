package com.shashikdm.givlyf.api;
//{
//        "description": "string",
//        "fullAddress": "string",
//        "geoHash": "gbsuv",
//        "latitude": 48.669,
//        "locationDisplayName": "string",
//        "longitude": -4.329,
//        "title": "string",
//        "willingToPay": true
//}
public class CreatePostRequest {
    private String title, description;
    private String locationDisplayName, fullAddress;
    private Double latitude, longitude;
    private Boolean willingToPay;

    public CreatePostRequest(String title,
                             String description,
                             String locationDisplayName,
                             String fullAddress,
                             Double latitude,
                             Double longitude,
                             Boolean willingToPay) {
        this.title = title;
        this.description = description;
        this.locationDisplayName = locationDisplayName;
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.willingToPay = willingToPay;
    }
}
