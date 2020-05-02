package com.shashikdm.givlyf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.shashikdm.givlyf.R;
import com.shashikdm.givlyf.utils.Filters;
import com.shashikdm.givlyf.validation.RadiusValidator;
import com.shashikdm.givlyf.validation.Validator;

public class FiltersDialog extends DialogFragment {
    Filters filters;

    public FiltersDialog() {

    }

    public static FiltersDialog newInstance(Filters filters) {
        final Bundle args = new Bundle(1);
        FiltersDialog fragment = new FiltersDialog();
        fragment.filters = filters;
        fragment.setArguments(args);
        return fragment;
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            listener = (NoticeDialogListener) context;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement NoticeDialogListener");
//        }
        listener = (NoticeDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (NoticeDialogListener) getActivity();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.filters_dialog);
        builder.setPositiveButton("Apply", (dialog, which) -> {

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> listener.onDialogNegativeClick(FiltersDialog.this));
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            EditText radius =  alertDialog.findViewById(R.id.radiusInput);
            radius.setText(String.valueOf(filters.getRadius()));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                Validator radiusValidator = new RadiusValidator();
                if(radiusValidator.validate(radius.getText().toString())) {
                    filters.setRadius(Integer.parseInt(radius.getText().toString()));
                    listener.onDialogPositiveClick(FiltersDialog.this);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Radius is invalid", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return alertDialog;
    }

}
