package com.shashikdm.scalerxcovid_19.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shashikdm.scalerxcovid_19.R;
import com.shashikdm.scalerxcovid_19.api.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//import com.shashikdm.scalerxcovid_19.ListRequests;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<Post> dataSet;
    private View.OnClickListener myOnClickListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView displayLocation;
        ImageView willingToPay;
        TextView postId;

        MyViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.titlePost);
            this.displayLocation = itemView.findViewById(R.id.displayLocationPost);
            this.willingToPay = itemView.findViewById(R.id.monetization);
            this.postId = itemView.findViewById(R.id.postId);
        }
    }

    public CustomAdapter(ArrayList<Post> data, View.OnClickListener onClickListener) {
        this.dataSet = data;
        this.myOnClickListener = onClickListener;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_requests_card_view, parent, false);

        view.setOnClickListener(myOnClickListener);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView title = holder.title;
        TextView displayLocation = holder.displayLocation;
        ImageView willingToPay = holder.willingToPay;
        TextView postId = holder.postId;
        title.setText(dataSet.get(listPosition).getTitle());
        displayLocation.setText(dataSet.get(listPosition).getLocationDisplayName());
        if(dataSet.get(listPosition).getWillingToPay()) {
            willingToPay.setImageResource(R.drawable.ic_attach_money_black_24dp);
        } else {
            willingToPay.setImageResource(R.drawable.ic_money_off_black_24dp);
        }
        postId.setText(String.valueOf(listPosition));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
