package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.AssignProjectAdapter;
import in.technitab.teamapp.adapter.NewProjectAdapter;
import in.technitab.teamapp.adapter.activityAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;

import in.technitab.teamapp.model.AssignProject;
import in.technitab.teamapp.model.User_Activity;
import in.technitab.teamapp.model.Vendor;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddToDoFragment extends AppCompatActivity {

    @BindView(R.id.userProjectRecyclerView)
    RecyclerView userProjectRecyclerView;
    @BindView(R.id.create_project)
    Button createProject;
    @BindView(R.id.empty_layout)
    RelativeLayout emptyLayout;
    Unbinder unbinder;
    @BindView(R.id.rootLayout)
    CoordinatorLayout rootLayout;

    private Activity mActivity;
    private Resources resources;
    private UserPref userPref;
    private NetConnection connection;
    private Dialog dialog;
    RestApi api;

    private ArrayList<User_Activity> projectTaskArrayList;
    private activityAdapter adapter;
    String project_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_project_task);
        unbinder = ButterKnife.bind(this);
        init();

        setRcyclerView();
        fetchProjectTask();
        setToolbar();
    }

    private void init() {
        mActivity = this;
        resources = getResources();
        projectTaskArrayList = new ArrayList<>();
        userPref = new UserPref(mActivity);
        connection = new NetConnection();
        dialog = new Dialog(mActivity);
        api = APIClient.getClient().create(RestApi.class);
        /*((MainActivity) mActivity).setToolbar(resources.getString(R.string.project_task));
        ((MainActivity) mActivity).setToolBarSubtitle(null);*/

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            project_Id = extras.getString("id");
           // Log.d("project_id",project_Id);
        }
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resources.getString(R.string.user_activity));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void fetchProjectTask() {
        if (connection.isNetworkAvailable(mActivity)) {
            dialog.showDialog();
            Call<ArrayList<User_Activity>> call = api.user_activity(userPref.getUserId(), project_Id);
            call.enqueue(new Callback<ArrayList<User_Activity>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<User_Activity>> call, @NonNull Response<ArrayList<User_Activity>> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        ArrayList<User_Activity> User_Activity = response.body();
                        if (User_Activity != null) {
                            projectTaskArrayList.addAll(User_Activity);
                            adapter.notifyDataSetChanged();
                            if (projectTaskArrayList.isEmpty()) {
                                emptyLayout.setVisibility(View.VISIBLE);
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<User_Activity>> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(resources.getString(R.string.slow_internet_connection));
                    }

                }
            });
        } else {
            showMessage(resources.getString(R.string.internet_not_available));
        }
    }

    private void showMessage(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }


    private void setRcyclerView() {
        userProjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userProjectRecyclerView.setHasFixedSize(false);
        userProjectRecyclerView.setNestedScrollingEnabled(false);
        adapter = new activityAdapter(this, projectTaskArrayList);
        userProjectRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

}
