package com.shashikdm.givlyf.api;
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
public class CreatePostResponse {
    private String postId, title, description;
    private String locationDisplayName, fullAddress;
    private String geoHash;
    private Boolean willingToPay;
    private GeoJsonPoint location;
}
