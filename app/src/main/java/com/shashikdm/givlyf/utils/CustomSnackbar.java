package com.shashikdm.givlyf.utils;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.givlyf.R;

public class CustomSnackbar {
    public static Snackbar makeText(View view, String message, Integer duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        return snackbar;
    }
}
