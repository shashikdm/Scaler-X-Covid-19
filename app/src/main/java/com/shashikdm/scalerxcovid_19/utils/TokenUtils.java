package com.shashikdm.scalerxcovid_19.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shashikdm.scalerxcovid_19.api.User;

public class TokenUtils {
    static public String getToken(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        String json= sharedPreferences.getString("user", null);

        User user = User.fromJson(json);
        return "Bearer "+user.getToken();
    }
}
