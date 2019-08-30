package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.ExistProjectAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.listener.OnBottomReachedListener;
import in.technitab.teamapp.model.Project;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExistingProjectActivity extends AppCompatActivity implements OnBottomReachedListener {

    @BindView(R.id.projectRecyclerView)
    RecyclerView projectRecyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    private NetConnection connection;
    private Dialog dialog;
    private RestApi api;
    private UserPref userPref;
    private Resources resources;
    private ExistProjectAdapter adapter;
    private ArrayList<Project> mProjectArrayList;
    private String action = "";
    private static final int  USER_EDIT_RC = 2, RC_ADD_PROJECT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_project);
        ButterKnife.bind(this);

        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            action = bundle.getString(ConstantVariable.MIX_ID.ACTION);
        }
        setToolbar();
        initRecylcerView();
        fetchProjects(mProjectArrayList.size());
    }

    private void init() {
        connection = new NetConnection();
        resources = getResources();
        userPref = new UserPref(this);
        dialog = new Dialog(this);
        api = APIClient.getClient().create(RestApi.class);
        mProjectArrayList = new ArrayList<>();
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(action.equalsIgnoreCase(resources.getString(R.string.approve)) ? resources.getString(R.string.approve_project) : action.equalsIgnoreCase(ConstantVariable.MIX_ID.SUBMIT) ? resources.getString(R.string.submitted) : resources.getString(R.string.added));
            actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initRecylcerView() {
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        projectRecyclerView.setHasFixedSize(false);
        projectRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ExistProjectAdapter(this, action, mProjectArrayList, projectRecyclerView);
        projectRecyclerView.setAdapter(adapter);
        adapter.setOnBottomReachedListener(this);

    }

    @Override
    public void onLoadMore() {
        /*if (mProjectArrayList.size() >= 20) {
            mProjectArrayList.add(null);
            adapter.notifyItemInserted(mProjectArrayList.size() - 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProjectArrayList.remove(mProjectArrayList.size() - 1);
                    adapter.notifyItemRemoved(mProjectArrayList.size());
                    int index = mProjectArrayList.size();
                    fetchProjects(index);
                }
            }, 3000);
        }*/
    }

    private void fetchProjects(int index) {
        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();

            Call<ArrayList<Project>> call = api.fetchProjects(index, userPref.getAccessControlId(), userPref.getUserId(), action);
            call.enqueue(new Callback<ArrayList<Project>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Project>> call, @NonNull Response<ArrayList<Project>> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        ArrayList<Project> stringResponse = response.body();
                        if (stringResponse != null) {
                            if (!mProjectArrayList.isEmpty() && stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.no_more_project));
                                emptyTextView.setVisibility(View.GONE);
                            } else if (stringResponse.isEmpty()) {
                                showMessage(getResources().getString(R.string.project_not_found));
                                emptyTextView.setText(getResources().getString(R.string.no_history_found));
                                emptyTextView.setVisibility(View.GONE);
                            } else {
                                mProjectArrayList.addAll(stringResponse);
                                emptyTextView.setVisibility(View.GONE);
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
                        emptyTextView.setVisibility(View.GONE);
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
    public void onRootClickListener(RecyclerView.ViewHolder viewHolder, int position) {

        Intent intent = new Intent(this, AddProjectActivity.class);
        intent.putExtra(resources.getString(R.string.project), mProjectArrayList.get(position));
        intent.putExtra(ConstantVariable.MIX_ID.ID, position);
        intent.putExtra(ConstantVariable.MIX_ID.ACTION, resources.getString(R.string.update));
        startActivityForResult(intent, USER_EDIT_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == USER_EDIT_RC && resultCode == Activity.RESULT_OK && data != null) {
            Project project = data.getParcelableExtra(resources.getString(R.string.project));
            int position = data.getIntExtra(ConstantVariable.MIX_ID.ID, -1);
            if (position != -1) {
                mProjectArrayList.set(position, project);
                adapter.notifyItemChanged(position);
                showMessage(resources.getString(R.string.successfully_updated));
            }
        } else if (requestCode == RC_ADD_PROJECT && resultCode == Activity.RESULT_OK && data != null) {
            Project project = data.getParcelableExtra(resources.getString(R.string.project));
            mProjectArrayList.add(0, project);
            showMessage(resources.getString(R.string.successfully_saved));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance_leave, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_leave);
        menuItem.setTitle(resources.getString(R.string.submit_project));
        menuItem.setIcon(resources.getDrawable(R.drawable.ic_add));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_leave) {
            Intent intent = new Intent(this, FindCustomerActivity.class);
            intent.putExtra(ConstantVariable.MIX_ID.ACTION_VIEW_TYPE, getResources().getString(R.string.customer_name));
            intent.putExtra(ConstantVariable.MIX_ID.ACTION, action);
            startActivityForResult(intent, RC_ADD_PROJECT);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
