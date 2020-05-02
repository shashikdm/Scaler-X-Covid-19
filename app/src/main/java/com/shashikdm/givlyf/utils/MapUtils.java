package com.shashikdm.givlyf.utils;

import android.app.Activity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapUtils {
    public static void moveCameraToMyLocation(final GoogleMap map, FusedLocationProviderClient fusedLocationClient, final Activity activity) {
        map.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    } else {
                        moveCameraToMyLocation(map, fusedLocationClient, activity);
                    }
                });
    }
}
