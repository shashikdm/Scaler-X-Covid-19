package com.shashikdm.givlyf.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.shashikdm.givlyf.MainActivity;
import com.shashikdm.givlyf.R;
import com.shashikdm.givlyf.api.BackendApi;
import com.shashikdm.givlyf.utils.TokenUtils;
import com.shashikdm.givlyf.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogoutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.logout_dialog);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebUtils.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BackendApi backendApi = retrofit.create(BackendApi.class);
            Activity activity = getActivity();
            Call<Object> call = backendApi.
                    logOut(TokenUtils.getToken(activity));
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("user");
            Intent intent = new Intent(activity, MainActivity.class);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                    if(response.isSuccessful()) {
                        editor.apply();
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Something went wrong\nPlease try again later", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                    Toast.makeText(activity, "Could not connect to the server", Toast.LENGTH_SHORT).show();
                }
            });

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        return builder.create();
    }
}
