package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.TeamTaskAdapter;
import in.technitab.teamapp.adapter.TripAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.UserTask;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamTask extends Fragment implements TripAdapter.TripListener {

    @BindView(R.id.userProjectRecyclerView)
    RecyclerView userProjectRecyclerView;
    private Resources resources;
    private UserPref userPref;
    private NetConnection connection;
    private Dialog dialog;
    RestApi api;
    private TeamTaskAdapter adapter;
    private ArrayList<Object> mProjectArrayList=null;
    private LinearLayoutManager mLayoutManager;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int previousTotal = 0;
    private int pageNumber = 1;
    private String id = "39";
    private Activity activity;
    private int visibleThreshold = 25;
    Unbinder unbinder;
    private Activity mActivity;
    private static final int RC_ADD = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_team_task, menu);
        MenuItem leaveItem = menu.findItem(R.id.menu_leave);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchViewAction.setIconifiedByDefault(true);
        EditText searchEditText = (EditText)searchViewAction.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));

        leaveItem.setTitle(resources.getString(R.string.submit_trip));

        leaveItem.setIcon(resources.getDrawable(R.drawable.ic_add));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_leave) {
            Intent intent = new Intent(getActivity(),Projecttask.class);
            intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.SUBMIT);
            startActivityForResult(intent, RC_ADD);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_team_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();
        ((MainActivity) activity).setToolbar(resources.getString(R.string.Task));
        ((MainActivity) activity).setToolBarSubtitle(null);


        return view;

    }
    @Override
    public void onResume(){
        super.onResume();

        setRecyclerView();
        getProjectListBasedOnType(pageNumber);

    }

    private void setRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        userProjectRecyclerView.setLayoutManager(mLayoutManager);
        userProjectRecyclerView.setHasFixedSize(false);
        userProjectRecyclerView.setNestedScrollingEnabled(false);
        userProjectRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new TeamTaskAdapter(getContext(), 1, mProjectArrayList);
        userProjectRecyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(this);
        setupScrolListener();
    }

    private void init() {
        activity = getActivity();
        resources =  getResources();
        mProjectArrayList = new ArrayList<>();
        userPref = new UserPref(activity);
        connection = new NetConnection();
        dialog = new Dialog(getContext());
        api = APIClient.getClient().create(RestApi.class);
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
                    loadMoreProject(++pageNumber);
                    loading = true;
                }
            }
        });
    }

    private void loadMoreProject(int page) {
        getProjectListBasedOnType(page);
    }

    private void getProjectListBasedOnType(int page) {
        if (connection.isNetworkAvailable(getContext())) {
        mProjectArrayList.clear();
        dialog.showDialog();
        Call<ArrayList<UserTask>> call = api.task(  /*"39"*/userPref.getUserId()/*, page*/);
        call.enqueue(new Callback<ArrayList<UserTask>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<UserTask>> call, @NonNull Response<ArrayList<UserTask>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<UserTask> stringResponse = response.body();
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
            public void onFailure(Call<ArrayList<UserTask>> call, Throwable t) {
                dialog.dismissDialog();
                if (t instanceof SocketTimeoutException) {
                    showMessage(getResources().getString(R.string.slow_internet_connection));
                }
            }
        });

        } else {
            showMessage(resources.getString(R.string.internet_not_available));
        }
    }

    private void showMessage(String message) {
        //  Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRootClick(RecyclerView.ViewHolder viewHolder, int headerPosition, int position) {

    }

    @Override
    public void onActionClick(RecyclerView.ViewHolder viewHolder, int headerPosition, int position) {

    }
}
