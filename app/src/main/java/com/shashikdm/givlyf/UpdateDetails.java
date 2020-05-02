package com.shashikdm.givlyf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.givlyf.api.BackendApi;
import com.shashikdm.givlyf.api.GeoJsonPoint;
import com.shashikdm.givlyf.api.UpdateUserRequest;
import com.shashikdm.givlyf.api.User;
import com.shashikdm.givlyf.utils.CustomSnackbar;
import com.shashikdm.givlyf.utils.TokenUtils;
import com.shashikdm.givlyf.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

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
    boolean fromMainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fromMainActivity = getIntent().getBooleanExtra("fromMainActivity", false);
        TextView phoneInput = findViewById(R.id.phoneInput);


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
                    EditText nameInput = findViewById(R.id.nameInput);
                    TextView phoneInput = findViewById(R.id.phoneInput);
                    phoneInput.setText(user.getPhoneNumber());
                    nameInput.setText(user.getName());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                    user.getHomeLocation().getY(),
                                    user.getHomeLocation().getX()),
                            15));
                    if (marker != null) {
                        marker.remove();
                    }
                    LatLng latLng = new LatLng(user.getHomeLocation().getY(), user.getHomeLocation().getX());
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Home"));

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplication());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    user.setToken(TokenUtils.getToken(activity).substring(7));
                    editor.putString("user", user.toString());
                    editor.apply();
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
            Toast.makeText(getApplicationContext(), "Please turn on location\nand give permission", Toast.LENGTH_SHORT).show();
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
            GeoJsonPoint homeLocation = new GeoJsonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                    if (response.isSuccessful()) {
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(getApplication());
                        String json = sharedPreferences.getString("user", null);
                        User user = User.fromJson(json);
                        user.setName(nameInput.getText().toString());
                        user.setHomeLocation(homeLocation);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        user.setToken(TokenUtils.getToken(activity).substring(7));
                        editor.putString("user", user.toString());
                        editor.apply();
                        Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show();
                        if (fromMainActivity) {
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(activity, "Profile was not updated", Toast.LENGTH_SHORT).show();
                    }
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