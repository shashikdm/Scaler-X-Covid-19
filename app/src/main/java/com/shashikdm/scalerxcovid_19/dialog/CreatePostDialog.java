package com.shashikdm.scalerxcovid_19.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.api.BackendApi;
import com.shashikdm.scalerxcovid_19.api.CreatePostRequest;
import com.shashikdm.scalerxcovid_19.api.CreatePostResponse;
import com.shashikdm.scalerxcovid_19.api.User;
import com.shashikdm.scalerxcovid_19.utils.CustomSnackbar;
import com.shashikdm.scalerxcovid_19.utils.MapUtils;
import com.shashikdm.scalerxcovid_19.utils.TokenUtils;
import com.shashikdm.scalerxcovid_19.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePostDialog extends DialogFragment
        implements OnMapReadyCallback {
    GoogleMap mMap;
    MapFragment mapFragment;
    Marker marker;
    Boolean permissionDenied;
    CreatePostDialog instance;
    AlertDialog alertDialog;
    private FusedLocationProviderClient fusedLocationClient;
    Activity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        instance = this;
        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.create_post_dialog);

        builder.setPositiveButton("Post", (dialog, which) -> {

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.addressInput);
            mapFragment.getMapAsync(instance);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            permissionDenied = false;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebUtils.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BackendApi backendApi = retrofit.create(BackendApi.class);
            Call<User> call = backendApi.getUserDetails(TokenUtils.getToken(activity));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                    if(response.isSuccessful()) {
                        User user = response.body();
                        if(Objects.requireNonNull(user).getDetailsUpdated()) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                            user.getHomeLocation().getY(),
                                            user.getHomeLocation().getX()),
                                    15));
                            if(marker != null) {
                                marker.remove();
                            }
                            LatLng latLng = new LatLng(user.getHomeLocation().getY(), user.getHomeLocation().getX());
                            placeMarkerAndFillDetails(latLng);
                        } else {
                            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                MapUtils.moveCameraToMyLocation(mMap, fusedLocationClient, activity);
                            } else if(!permissionDenied) {
                                // Show rationale and request permission.
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1998);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {

                }
            });


            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                if(marker == null) {
                    Toast.makeText(activity, "Please place the marker", Toast.LENGTH_SHORT).show();
                    return;
                }
                Retrofit retrofit1 = new Retrofit.Builder()
                        .baseUrl(WebUtils.getBaseUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BackendApi backendApi1 = retrofit1.create(BackendApi.class);
                CreatePostRequest createPostRequest;
                EditText titleInput = alertDialog.findViewById(R.id.titleInput);
                EditText descriptionInput = alertDialog.findViewById(R.id.descriptionInput);
                EditText displayLocationInput = alertDialog.findViewById(R.id.displayLocationInput);
                EditText fullAddressInput = alertDialog.findViewById(R.id.fullAddressInput);
                Switch willingToPayInput = alertDialog.findViewById(R.id.willingToPayInput);

                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String displayLocation = displayLocationInput.getText().toString();
                String fullAddress = fullAddressInput.getText().toString();
                Double latitude = marker.getPosition().latitude;
                Double longitude = marker.getPosition().longitude;
                Boolean willingToPay = willingToPayInput.isChecked();

                createPostRequest = new CreatePostRequest(title,
                        description, displayLocation, fullAddress, latitude, longitude, willingToPay);
                Call<CreatePostResponse> call1 = backendApi1.
                        createPost(TokenUtils.getToken(activity), createPostRequest);
                call1.enqueue(new Callback<CreatePostResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CreatePostResponse> call, @NotNull Response<CreatePostResponse> response) {
                        alertDialog.dismiss();
                        if(response.isSuccessful()) {
                            CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                                    "Post created successfully", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                                    "Post creation failed. please try again later", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CreatePostResponse> call, @NotNull Throwable t) {
                        CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                                "Could not connect to the server", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            });
        });
        return alertDialog;
    }

    @Override
    public void onResume() {
        if(mMap != null) {
            onMapReady(mMap);
        }
        super.onResume();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction();

        ft.remove( getFragmentManager()
                .findFragmentById(R.id.addressInput));
        ft.commit();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.getUiSettings().setCompassEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception ignored) {
        }
        mMap.setOnMapClickListener(this::placeMarkerAndFillDetails);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1998) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MapUtils.moveCameraToMyLocation(mMap, fusedLocationClient, activity);
            } else {
                permissionDenied = true;
            }
        }
    }

    void placeMarkerAndFillDetails(LatLng latLng) {
        if(marker != null) {
            marker.remove();
        }
        try {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Home"));
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            Address address = geocoder.
                    getFromLocation(marker.getPosition().latitude,
                            marker.getPosition().longitude, 1).get(0);
            EditText displayLocationInput = alertDialog.findViewById(R.id.displayLocationInput);
            EditText fullAddressInput = alertDialog.findViewById(R.id.fullAddressInput);
            displayLocationInput.setText(address.getLocality());
            fullAddressInput.setText(address.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
