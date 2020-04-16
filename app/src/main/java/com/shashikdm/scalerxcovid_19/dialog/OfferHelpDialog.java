package com.shashikdm.scalerxcovid_19.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.api.BackendApi;
import com.shashikdm.scalerxcovid_19.api.OfferHelpRequest;
import com.shashikdm.scalerxcovid_19.api.Post;
import com.shashikdm.scalerxcovid_19.utils.CustomSnackbar;
import com.shashikdm.scalerxcovid_19.utils.TokenUtils;
import com.shashikdm.scalerxcovid_19.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfferHelpDialog extends DialogFragment
        implements OnMapReadyCallback {
    Post post;
    Activity activity;
    GoogleMap mMap;
    MapFragment mapFragment;
    OfferHelpDialog instance;
    public OfferHelpDialog() {

    }
    public static OfferHelpDialog newInstance(Post post) {
        final Bundle args = new Bundle(1);
        args.putSerializable("post", post);
        OfferHelpDialog fragment = new OfferHelpDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        instance = this;
        activity = getActivity();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.offer_help_dialog);
        post = (Post) getArguments().getSerializable("post");
        builder.setPositiveButton("Help", (dialog, which) -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebUtils.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BackendApi backendApi = retrofit.create(BackendApi.class);
            EditText messageInput = getDialog().findViewById(R.id.messageInput);
            String message = messageInput.getText().toString();
            OfferHelpRequest offerHelpRequest = new OfferHelpRequest(message, post.getPostId());
            Call<Void> call = backendApi.
                    offerHelp(TokenUtils.getToken(activity), offerHelpRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    if(response.isSuccessful()) {
                        CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                                "Help offered successfully", Snackbar.LENGTH_SHORT).show();
                    } else {
                        CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                                "Help wasn't sent. please try again later", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    CustomSnackbar.makeText(activity.findViewById(R.id.listRequestConstraintLayout),
                            "Could not connect to the server", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            EditText title = alertDialog.findViewById(R.id.titleInput);
            EditText description = alertDialog.findViewById(R.id.descriptionInput);
            EditText displayLocation = alertDialog.findViewById(R.id.displayLocationInput);
            EditText fullAddress = alertDialog.findViewById(R.id.fullAddressInput);
            Switch willingToPay = alertDialog.findViewById(R.id.willingToPayInput);
            title.setText(post.getTitle());
            description.setText(post.getDescription());
            displayLocation.setText(post.getLocationDisplayName());
            fullAddress.setText(post.getFullAddress());
            willingToPay.setChecked(post.getWillingToPay());
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.addressInput);
            mapFragment.getMapAsync(instance);
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        post.getLocation().getY(),
                        post.getLocation().getX()),
                15));
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception ignored) {
        }
        mMap.addMarker(new MarkerOptions().
                position(new LatLng(post.getLocation().getY(), post.getLocation().getX())));
    }
}
