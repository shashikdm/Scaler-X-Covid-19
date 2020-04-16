package com.shashikdm.scalerxcovid_19;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.shashikdm.scalerxcovid_19.api.BackendApi;
import com.shashikdm.scalerxcovid_19.api.Post;
import com.shashikdm.scalerxcovid_19.dialog.CreatePostDialog;
import com.shashikdm.scalerxcovid_19.dialog.FiltersDialog;
import com.shashikdm.scalerxcovid_19.dialog.LogoutDialog;
import com.shashikdm.scalerxcovid_19.dialog.OfferHelpDialog;
import com.shashikdm.scalerxcovid_19.utils.CustomAdapter;
import com.shashikdm.scalerxcovid_19.utils.CustomSnackbar;
import com.shashikdm.scalerxcovid_19.utils.Filters;
import com.shashikdm.scalerxcovid_19.utils.TokenUtils;
import com.shashikdm.scalerxcovid_19.utils.WebUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListRequests extends AppCompatActivity
implements FiltersDialog.NoticeDialogListener {
    DialogFragment addPostDialog;
    SwipeRefreshLayout swipeContainer;
    ArrayList<Post> postList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    int itemsSeen, pageNo, pageSize;
    Filters filters;
    public static View.OnClickListener myOnClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_requests);
        itemsSeen = 0;
        pageNo = 0;
        pageSize = Integer.parseInt(getString(R.string.page_size));
        filters = new Filters();
        filters.setRadius(Integer.parseInt(getString(R.string.default_radius)));
        myOnClickListener = new MyOnClickListener();
        addPostDialog = new CreatePostDialog();
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            postList.clear();
            pageNo = 0;
            fetchPostsAsync(pageNo);
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.scalerdark,
                R.color.scalermedium,
                R.color.scalerlight);
        swipeContainer.setRefreshing(true);
        fetchPostsAsync(pageNo);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        adapter = new CustomAdapter(postList, myOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !swipeContainer.isRefreshing()) {
                    pageNo++;
                    swipeContainer.setRefreshing(true);
                    fetchPostsAsync(pageNo);
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        swipeContainer.setRefreshing(true);
        pageNo = 0;
        postList.clear();
        fetchPostsAsync(pageNo);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView postIdText = v.findViewById(R.id.postId);
            DialogFragment dialogFragment = OfferHelpDialog.newInstance(
                    postList.get(Integer.parseInt(postIdText.getText().toString())));
            dialogFragment.show(getFragmentManager(), null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_SHORT).show();
                //Todo search
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.update:
                intent = new Intent(this, UpdateDetails.class);
                startActivity(intent);
                return true;
            case R.id.myPosts:
                intent = new Intent(this, ListMyPosts.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                DialogFragment dialog = new LogoutDialog();
                dialog.show(getFragmentManager(), "Logout");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void createPost(View view) {
        addPostDialog.show(getFragmentManager(), "createPost");
    }

    public void applyFilters(View view) {
        DialogFragment dialogFragment = FiltersDialog.newInstance(filters);
        dialogFragment.show(getFragmentManager(), null);
    }

    public void fetchPostsAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        Integer pageNo = page;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebUtils.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackendApi backendApi = retrofit.create(BackendApi.class);
        Call<List<Post>> call = backendApi.getPostsNearUser(TokenUtils.getToken(this), pageNo, pageSize, filters.getRadius());

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NotNull Call<List<Post>> call, @NotNull Response<List<Post>> response) {
                if(response.isSuccessful()) {
                    for(Post post : Objects.requireNonNull(response.body())) {
                        postList.add(post);
                        adapter.notifyItemInserted(postList.size()-1);
                    }
                    if(response.body().size() == 0) {
                        if(postList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "No posts", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No more posts", Toast.LENGTH_SHORT).show();
                        }
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
                    CustomSnackbar.makeText(findViewById(R.id.listRequestConstraintLayout),
                            "Error code: "+response.code() + "\nMessage: "+ response.message(), Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(@NotNull Call<List<Post>> call, @NotNull Throwable t) {
                CustomSnackbar.makeText(findViewById(R.id.listRequestConstraintLayout),
                        "Could not connect to the server", Snackbar.LENGTH_LONG)
                        .show();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
