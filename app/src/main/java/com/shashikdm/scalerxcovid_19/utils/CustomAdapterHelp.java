package com.shashikdm.scalerxcovid_19.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.api.Help;

import java.util.ArrayList;

public class CustomAdapterHelp extends RecyclerView.Adapter<CustomAdapterHelp.MyViewHolder> {
    private ArrayList<Help> dataSet;
    private View.OnClickListener myOnClickListener;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView helperName;
        TextView helperMessage;
        TextView helpId;

        MyViewHolder(View itemView) {
            super(itemView);
            this.helperName = itemView.findViewById(R.id.helperName);
            this.helperMessage = itemView.findViewById(R.id.helperMessage);
            this.helpId = itemView.findViewById(R.id.helpId);
        }
    }

    public CustomAdapterHelp(ArrayList<Help> data, View.OnClickListener onClickListener) {
        this.dataSet = data;
        this.myOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CustomAdapterHelp.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_helps_card_view, parent, false);

        view.setOnClickListener(myOnClickListener);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterHelp.MyViewHolder holder, int listPosition) {
        TextView helperName = holder.helperName;
        TextView helperMessage = holder.helperMessage;
        TextView helpId = holder.helpId;
        helperName.setText(dataSet.get(listPosition).getHelperName());
        helperMessage.setText(dataSet.get(listPosition).getMessage());
        helpId.setText(String.valueOf(listPosition));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
