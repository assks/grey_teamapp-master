package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Button;
import android.widget.RelativeLayout;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.NewProjectAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.model.Project;

import in.technitab.teamapp.model.Trip;
import in.technitab.teamapp.model.assignee;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;

import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class assigneelist extends AppCompatActivity implements RecyclerViewItemClickListener {

    @BindView(R.id.userProjectRecyclerView)
    RecyclerView userProjectRecyclerView;
    @BindView(R.id.retry)
    Button retry;
    @BindView(R.id.empty_layout)
    RelativeLayout emptyLayout;
    @BindView(R.id.rootLayout)
    CoordinatorLayout rootLayout;
    Unbinder unbinder;
    private Activity mActivity;
    private Resources resources;
    private UserPref userPref;
    private NetConnection connection;
    private Dialog dialog;
    RestApi api;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 25;
    private int pageNumber = 1, projectTypeId = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Object> mProjectArrayList;
    private NewProjectAdapter adapter;
    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);

        init();
        setToolbar();
        setRecyclerView();

       /* Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            action = bundle.getString(ConstantVariable.MIX_ID.ACTION);
            if (action.equalsIgnoreCase(resources.getString(R.string.tec))) {
                getProjectListBasedOnType();
            } else if (action.equalsIgnoreCase(ConstantVariable.MIX_ID.PROJECT)) {
                projectTypeId = bundle.getInt(ConstantVariable.MIX_ID.ID);
                getTripTec();
                setupScrolListener();
            } else {
                getProjectList();
            }
        }*/
    }

    private void setupScrolListener() {
        userProjectRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = userProjectRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading && dy > 0) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {

                 //   loadMoreProject(++pageNumber);
                    loading = true;
                }
            }
        });
    }

   /* private void loadMoreProject(int pageNumber) {
        getProjectListBasedOnType(projectTypeId,pageNumber);
    }*/

    private void getProjectListBasedOnType() {
        dialog.showDialog();
        Call<ArrayList<assignee>> call = api.getassignee();
        call.enqueue(new Callback<ArrayList<assignee>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<assignee>> call,
                                   @NonNull Response<ArrayList<assignee>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<assignee> stringResponse = response.body();
                    if (stringResponse != null) {
                        if (!mProjectArrayList.isEmpty() && stringResponse.isEmpty()) {
                            showMessage(getResources().getString(R.string.no_more_project));
                        } else if (stringResponse.isEmpty()) {
                            showMessage(getResources().getString(R.string.no_history_found));
                        } else {
                            mProjectArrayList.addAll(stringResponse);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    showMessage(resources.getString(R.string.problem_to_connect));
                }
            }


            @Override
            public void onFailure(@NonNull Call<ArrayList<assignee>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                if (t instanceof SocketTimeoutException) {
                    showMessage(getResources().getString(R.string.slow_internet_connection));
                }
            }
        });

    }


    private void getTripTec() {
        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();

            Call<ArrayList<Trip>> call = api.fetchTripForTEC(Integer.parseInt(userPref.getUserId()));
            call.enqueue(new Callback<ArrayList<Trip>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Trip>> call, @NonNull Response<ArrayList<Trip>> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        ArrayList<Trip> stringResponse = response.body();
                        if (stringResponse != null) {
                            if (!mProjectArrayList.isEmpty() && stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.no_more_project));
                            } else if (stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.no_history_found));
                            } else {
                                mProjectArrayList.addAll(stringResponse);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Trip>> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(getResources().getString(R.string.slow_internet_connection));
                    }
                }
            });
        }
    }

    private void setRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        userProjectRecyclerView.setLayoutManager(mLayoutManager);
        userProjectRecyclerView.setHasFixedSize(false);
        userProjectRecyclerView.setNestedScrollingEnabled(false);
        userProjectRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        adapter = new NewProjectAdapter(this, 1, mProjectArrayList);
        userProjectRecyclerView.setAdapter(adapter);
        adapter.setTaskListener(this);
    }

    private void init() {
        mActivity = this;
        resources = getResources();
        mProjectArrayList = new ArrayList<>();
        userPref = new UserPref(mActivity);
        connection = new NetConnection();
        dialog = new Dialog(mActivity);
        api = APIClient.getClient().create(RestApi.class);

    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resources.getString(R.string.choose_project));
            actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    private void getProjectList() {
        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();

            Call<ArrayList<Project>> call = api.fetchUserProject(userPref.getUserId());
            call.enqueue(new Callback<ArrayList<Project>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Project>> call, @NonNull Response<ArrayList<Project>> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        ArrayList<Project> stringResponse = response.body();
                        if (stringResponse != null) {
                            if (!mProjectArrayList.isEmpty() && stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.no_more_project));
                            } else if (stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.no_history_found));
                            } else {
                                mProjectArrayList.addAll(stringResponse);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Project>> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(getResources().getString(R.string.slow_internet_connection));
                    }
                }
            });

        } else {
            showMessage(getResources().getString(R.string.internet_not_available));
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClickListener(RecyclerView.ViewHolder viewHolder, int position) {
        if (mProjectArrayList.get(position) instanceof Trip) {
            Trip project = (Trip) mProjectArrayList.get(position);
            Intent intent = new Intent(mActivity, GenerateTecActivity.class);
            intent.putExtra(ConstantVariable.Tec.ID, project.getId());
            intent.putExtra(ConstantVariable.Tec.PROJECT_ID, project.getProjectId());
            intent.putExtra(getResources().getString(R.string.project), project.getProjectName());
            intent.putExtra(ConstantVariable.Project.SITE_LOCATION, project.getDestination());
            intent.putExtra(ConstantVariable.Trip.CREATED_DATE, project.getStartDate());
            startActivityForResult(intent, 1);
        } else if (mProjectArrayList.get(position) instanceof Project) {
            Intent intent = new Intent();
            Project project = (Project) mProjectArrayList.get(position);
            intent.putExtra(ConstantVariable.MIX_ID.ID, project.getId());
            intent.putExtra(getResources().getString(R.string.project), project.getProjectName());
            intent.putExtra(ConstantVariable.Project.SITE_LOCATION, project.getLocation());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
