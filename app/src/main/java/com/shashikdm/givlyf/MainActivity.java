package com.shashikdm.givlyf;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.givlyf.api.BackendApi;
import com.shashikdm.givlyf.api.LoginRequest;
import com.shashikdm.givlyf.api.LoginResponse;
import com.shashikdm.givlyf.dialog.OTPDialog;
import com.shashikdm.givlyf.utils.CustomSnackbar;
import com.shashikdm.givlyf.utils.WebUtils;
import com.shashikdm.givlyf.validation.PhoneValidator;
import com.shashikdm.givlyf.validation.Validator;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebUtils.setBaseUrl(getString(R.string.base_url));
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String json= sharedPreferences.getString("user", null);
        if(json != null) {
            Intent intent = new Intent(getApplication(), ListRequests.class);
            startActivity(intent);
            finish();
        }
        requestPermission();
    }

    void requestPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }

    public void sendOtp(View view) {
        EditText editText = findViewById(R.id.phoneInput);
        String input = editText.getText().toString();
        Validator validator = new PhoneValidator();
        if(validator.validate(input)) {
            EditText baseUrlInput = findViewById(R.id.baseUrlInput);
            String baseUrl = baseUrlInput.getText().toString();
            if(!baseUrl.isEmpty()) {
                WebUtils.setBaseUrl(baseUrl);
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebUtils.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BackendApi backendApi = retrofit.create(BackendApi.class);
            LoginRequest loginRequest = new LoginRequest(Integer.parseInt(getString(R.string.otp_length)), "+91"+input);
            Call<LoginResponse> call = backendApi.
                    login(loginRequest);
            Button button = findViewById(R.id.loginButton);
            button.setClickable(false);
            ProgressBar progressBar = findViewById(R.id.loadingBar);
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                    if(response.isSuccessful()) {
                        DialogFragment dialog = OTPDialog.newInstance(response.body());
                        dialog.show(getFragmentManager(), "OTP");
                    } else if(response.code() == 400) {
                        CustomSnackbar.makeText(findViewById(R.id.mainAcitivityConstraintLayout),
                                "Please input valid phone number", Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        CustomSnackbar.makeText(findViewById(R.id.mainAcitivityConstraintLayout),
                                "Error code: "+response.code() + "\nMessage: "+ response.message(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                    button.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                    CustomSnackbar.makeText(findViewById(R.id.mainAcitivityConstraintLayout),
                            "Could not connect to the server", Snackbar.LENGTH_LONG)
                            .show();
                    button.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            CustomSnackbar.makeText(findViewById(R.id.mainAcitivityConstraintLayout),
                    "Please input valid phone number", Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
/*
Todo:
1) change login screen - done
2) onboarding - will not do
3) select language
4) change edittext to textview - done
5) auto otp
*/
