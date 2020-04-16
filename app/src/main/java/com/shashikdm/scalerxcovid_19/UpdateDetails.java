package com.shashikdm.scalerxcovid_19;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.shashikdm.scalerxcovid_19.api.BackendApi;
import com.shashikdm.scalerxcovid_19.api.UpdateUserRequest;
import com.shashikdm.scalerxcovid_19.api.User;
import com.shashikdm.scalerxcovid_19.utils.CustomSnackbar;
import com.shashikdm.scalerxcovid_19.utils.MapUtils;
import com.shashikdm.scalerxcovid_19.utils.TokenUtils;
import com.shashikdm.scalerxcovid_19.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateDetails extends AppCompatActivity
        implements OnMapReadyCallback {
    GoogleMap mMap;
    MapFragment mapFragment;
    Marker marker;
    Boolean permissionDenied;
    Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        permissionDenied = false;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String json= sharedPreferences.getString("user", null);

        User user = User.fromJson(json);
        EditText phoneInput = findViewById(R.id.phoneInput);
        phoneInput.setText(user.getPhoneNumber());
        phoneInput.setFocusable(false);
        phoneInput.setClickable(false);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebUtils.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackendApi backendApi = retrofit.create(BackendApi.class);
        Call<User> call = backendApi.getUserDetails(TokenUtils.getToken(this));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if(response.isSuccessful()) {
                    User user = response.body();
                    if(Objects.requireNonNull(user).getDetailsUpdated()) {
                        EditText nameInput = findViewById(R.id.nameInput);
                        nameInput.setText(user.getName());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                user.getHomeLocation().getY(),
                                user.getHomeLocation().getX()),
                                15));
                        if(marker != null) {
                            marker.remove();
                        }
                        LatLng latLng = new LatLng(user.getHomeLocation().getY(), user.getHomeLocation().getX());
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Home"));

                    } else {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                            MapUtils.moveCameraToMyLocation(mMap, fusedLocationClient, activity);
                            mMap.setMyLocationEnabled(true);

                        } else if(!permissionDenied) {
                            // Show rationale and request permission.
                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1998);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                CustomSnackbar.makeText(findViewById(R.id.registerConstraintLayout),
                        "Could not connect to the server", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }
    @Override
    protected void onResume() {
        if(mMap != null) {
            onMapReady(mMap);
        }
        super.onResume();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.getUiSettings().setCompassEnabled(true);
        mMap.setOnMapClickListener(latLng -> {
            if(marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Home"));
        });
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception ignored) {
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1998) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MapUtils.moveCameraToMyLocation(mMap, fusedLocationClient, this);
                mMap.setMyLocationEnabled(true);

            } else {
                permissionDenied = true;
            }
        }
    }
    public void save(View view) {
        Intent intent = new Intent(this, ListRequests.class);
        EditText nameInput = findViewById(R.id.nameInput);
        if(nameInput.getText().toString().isEmpty()) {
            CustomSnackbar.makeText(findViewById(R.id.registerConstraintLayout), "Please enter your name", Snackbar.LENGTH_LONG).show();
        } else if(marker == null) {
            CustomSnackbar.makeText(findViewById(R.id.registerConstraintLayout), "Please choose your location", Snackbar.LENGTH_LONG).show();
        } else {
            UpdateUserRequest updateUserRequest =
                    new UpdateUserRequest(nameInput.getText().toString(),
                            marker.getPosition().longitude,
                            marker.getPosition().latitude);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebUtils.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BackendApi backendApi = retrofit.create(BackendApi.class);

            Call<Object> call = backendApi.
                    updateUser(TokenUtils.getToken(this), updateUserRequest);

            Button button = findViewById(R.id.updateButton);
            button.setClickable(false);
            ProgressBar progressBar = findViewById(R.id.loadingBar);
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                    startActivity(intent);
                    button.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    activity.finish();
                }

                @Override
                public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                    CustomSnackbar.makeText(findViewById(R.id.registerConstraintLayout),
                            "Could not connect to the server", Snackbar.LENGTH_LONG)
                            .show();
                    button.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });


        }
    }

}