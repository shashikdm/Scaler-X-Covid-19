package com.shashikdm.scalerxcovid_19.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.scalerxcovid_19.ListRequests;
import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.UpdateDetails;
import com.shashikdm.scalerxcovid_19.api.BackendApi;
import com.shashikdm.scalerxcovid_19.api.LoginResponse;
import com.shashikdm.scalerxcovid_19.api.LoginVerifyRequest;
import com.shashikdm.scalerxcovid_19.api.LoginVerifyResponse;
import com.shashikdm.scalerxcovid_19.api.User;
import com.shashikdm.scalerxcovid_19.utils.CustomSnackbar;
import com.shashikdm.scalerxcovid_19.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPDialog extends DialogFragment {
    Integer attemptsAllowed;
    public OTPDialog() {
        // Empty constructor required for DialogFragment
    }
    public static OTPDialog newInstance(LoginResponse loginResponse) {
        final Bundle args = new Bundle(1);
        args.putSerializable("loginResponse", loginResponse);
        OTPDialog fragment = new OTPDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LoginResponse loginResponse = (LoginResponse) getArguments().getSerializable("loginResponse");
        builder.setTitle("Enter OTP");
        builder.setView(R.layout.send_otp_dialog);
        builder.setPositiveButton("Submit", (dialog, which) -> {
            //Do nothing here because we override this button later to change the close behaviour.
            //However, we still need this because on older versions of Android unless we
            //pass a handler the button doesn't get instantiated
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Date expiry = new Date(Objects.requireNonNull(loginResponse).getExpiry()), now = new Date();
            long timeOut = expiry.getTime()-now.getTime();
            TextView countDown = alertDialog.findViewById(R.id.timerView);
            countDown.setText(String.valueOf(timeOut/1000));
            attemptsAllowed = loginResponse.getAttemptsAllowed();
            new CountDownTimer(timeOut, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextView countDown = alertDialog.findViewById(R.id.timerView);
                    int curTime = Integer.parseInt(countDown.getText().toString());
                    countDown.setText(String.valueOf(curTime-1));
                }
                public void onFinish() {
                    dialog.dismiss();
                }
            }.start();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(WebUtils.getBaseUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BackendApi backendApi = retrofit.create(BackendApi.class);
                EditText otpInput = getDialog().findViewById(R.id.otpInput);
                String otpCode = otpInput.getText().toString(), requestId = loginResponse.getRequestId();
                LoginVerifyRequest loginVerifyRequest = new LoginVerifyRequest(otpCode, requestId);
                Call<LoginVerifyResponse> call = backendApi.
                        loginVerify(loginVerifyRequest);
                call.enqueue(new Callback<LoginVerifyResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<LoginVerifyResponse> call, @NotNull Response<LoginVerifyResponse> response) {
                        if(response.isSuccessful()) {
                            String token = Objects.requireNonNull(response.body()).getToken();
                            EditText phoneInput = getActivity().findViewById(R.id.phoneInput);
                            String phoneNumber = phoneInput.getText().toString();
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", new User(phoneNumber, token).toString());
                            editor.apply();
                            Intent intent;
                            if(!response.body().getDetailsUpdated()) {
                                intent = new Intent(getActivity(), UpdateDetails.class);
                            } else {
                                intent = new Intent(getActivity(), ListRequests.class);
                            }
                            startActivity(intent);
                            getActivity().finish();
                        } else if(response.code() == 400) {
                            if(attemptsAllowed > 1) {
                                attemptsAllowed--;
                                Toast.makeText(getActivity(), "OTP incorrect\nRemaining attempts: "+attemptsAllowed, Toast.LENGTH_LONG).show();
                            } else {
                                getDialog().dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error code: "+response.code() + "\nMessage: "+ response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<LoginVerifyResponse> call, @NotNull Throwable t) {
                        CustomSnackbar.makeText(getActivity().findViewById(R.id.mainAcitivityConstraintLayout),
                                "Could not connect to the server", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            });
        });
        return alertDialog;
    }
}