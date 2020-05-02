package com.shashikdm.givlyf;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.givlyf.api.BackendApi;
import com.shashikdm.givlyf.api.Help;
import com.shashikdm.givlyf.api.Post;
import com.shashikdm.givlyf.dialog.ShowHelpDialog;
import com.shashikdm.givlyf.utils.CustomAdapterHelp;
import com.shashikdm.givlyf.utils.CustomSnackbar;
import com.shashikdm.givlyf.utils.TokenUtils;
import com.shashikdm.givlyf.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListHelpActivity extends AppCompatActivity {
    Post post;
    DialogFragment dialogFragment;
    SwipeRefreshLayout swipeContainer;
    ArrayList<Help> helpList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    public static View.OnClickListener myOnClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_help);
        post = (Post) getIntent().getSerializableExtra("post");
        myOnClickListener = new ListHelpActivity.MyOnClickListener();
        swipeContainer = findViewById(R.id.swipeContainerHelps);
        // Setup refresh listener which triggers new data loading
        helpList = new ArrayList<>();
        // Your code to refresh the list here.
        // Make sure you call swipeContainer.setRefreshing(false)
        // once the network request has completed successfully.
        swipeContainer.setOnRefreshListener(this::fetchHelpsAsync);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.scalerdark,
                R.color.scalermedium,
                R.color.scalerlight);
        swipeContainer.setRefreshing(true);
        fetchHelpsAsync();
        recyclerView = findViewById(R.id.recyclerViewHelps);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapterHelp(helpList, myOnClickListener);
        recyclerView.setAdapter(adapter);

        EditText title = findViewById(R.id.titleInput);
        EditText description = findViewById(R.id.descriptionInput);
        EditText fullAddress = findViewById(R.id.fullAddressInput);
        Switch willingToPay = findViewById(R.id.willingToPayInput);
        title.setText(post.getTitle());
        description.setText(post.getDescription());
        fullAddress.setText(post.getFullAddress());
        willingToPay.setChecked(post.getWillingToPay());
    }

    class MyOnClickListener implements View.OnClickListener {

        private MyOnClickListener() {
        }

        @Override
        public void onClick(View v) {
            //show dialog with details
            if (!(dialogFragment != null
                    && dialogFragment.getDialog() != null
                    && dialogFragment.getDialog().isShowing()
                    && !dialogFragment.isRemoving())) {
                //dialog is not showing
                TextView helpId = v.findViewById(R.id.helpId);
                dialogFragment = ShowHelpDialog.
                        newInstance(helpList.get(Integer.parseInt(helpId.getText().toString())));
                dialogFragment.show(getFragmentManager(), null);
            }

        }
    }

    public void fetchHelpsAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebUtils.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackendApi backendApi = retrofit.create(BackendApi.class);
        Call<List<Help>> call = backendApi.getHelps(TokenUtils.getToken(this), post.getPostId());
        call.enqueue(new Callback<List<Help>>() {
            @Override
            public void onResponse(@NotNull Call<List<Help>> call, @NotNull Response<List<Help>> response) {
                if(response.isSuccessful()) {
                    helpList.clear();
                    helpList.addAll(Objects.requireNonNull(response.body()));
                    adapter.notifyDataSetChanged();
                    if(response.body().size() == 0) {
                        Toast.makeText(getApplicationContext(), "No helps yet", Toast.LENGTH_SHORT).show();
                    }
                } else if(response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Please login again", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("user");
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    CustomSnackbar.makeText(findViewById(R.id.listHelpConstraintLayout),
                            "Error code: "+response.code() + "\nMessage: "+ response.message(), Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(@NotNull Call<List<Help>> call, @NotNull Throwable t) {
                CustomSnackbar.makeText(findViewById(R.id.listHelpConstraintLayout),
                        "Could not connect to the server", Snackbar.LENGTH_LONG)
                        .show();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
