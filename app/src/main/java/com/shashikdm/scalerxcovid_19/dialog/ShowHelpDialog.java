package com.shashikdm.scalerxcovid_19.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.api.Help;

public class ShowHelpDialog extends DialogFragment
        implements OnMapReadyCallback {
    Help help;
    Activity activity;
    GoogleMap mMap;
    MapFragment mapFragment;
    ShowHelpDialog instance;

    public ShowHelpDialog() {
    }

    public static ShowHelpDialog newInstance(Help help) {
        final Bundle args = new Bundle(1);
        args.putSerializable("help", help);
        ShowHelpDialog fragment = new ShowHelpDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        instance = this;
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.show_help_dialog);
        help = (Help) getArguments().getSerializable("help");
        activity = getActivity();
        builder.setPositiveButton("Close", (dialog, which) -> {

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            EditText helperName = alertDialog.findViewById(R.id.helperNameInput);
            EditText helperMessage = alertDialog.findViewById(R.id.helperMessageInput);
            TextView phoneName = alertDialog.findViewById(R.id.phoneName);
            TextView phoneNumber = alertDialog.findViewById(R.id.phoneNo);
            phoneName.setText(help.getHelperName());
            phoneNumber.setText(help.getHelperPhoneNumber());
            helperName.setText(help.getHelperName());
            helperMessage.setText(help.getMessage());
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.addressInput);
            mapFragment.getMapAsync(instance);
            LinearLayout phoneAddButton = alertDialog.findViewById(R.id.contactHolderLayout);
            phoneAddButton.setOnClickListener(v -> {
                String name = help.getHelperName();
                String phoneNo = help.getHelperPhoneNumber();
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNo);
                startActivity(intent);
            });
        });
        return alertDialog;
    }

    @Override
    public void onResume() {
        if (mMap != null) {
            onMapReady(mMap);
        }
        super.onResume();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction();

        ft.remove(getFragmentManager()
                .findFragmentById(R.id.addressInput));
        ft.commit();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.getUiSettings().setCompassEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        help.getHelperLocation().getY(),
                        help.getHelperLocation().getX()),
                15));
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception ignored) {
        }
        mMap.addMarker(new MarkerOptions().
                position(new LatLng(help.getHelperLocation().getY(), help.getHelperLocation().getX())));
    }
}
