package in.technitab.teamapp.view.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.fetch_coordinate;
import in.technitab.teamapp.model.fetch_hotal_pojo;
import in.technitab.teamapp.model.userproject;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.NetworkError;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class seletct_location extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.type_id)
    TextView typeId;
    @BindView(R.id.addres)
    TextView addres;
    @BindView(R.id.vender_name)
    TextView venderName;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.project_name)
    Spinner projectName;
    @BindView(R.id.state)
    Spinner state;
    @BindView(R.id.hotel)
    Spinner hotel;

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.getlocation)
    TextView getlocation;
    @BindView(R.id.latlong)
    TextView latlong;
    @BindView(R.id.fetch_address)
    Button fetchAddress;
    @BindView(R.id.state1)
    LinearLayout state1;
    @BindView(R.id.lodging1)
    LinearLayout lodging1;
    /*@BindView(R.id.project)
    TextInputLayout project;*/
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.projectlayout)
    LinearLayout projectlayout;

    private RestApi api;
    Resources resources;
    private Activity mContext;
    private int Location_type = 0;
    private String type_id,hotel_name;
    int projectId = 0;
    String strProjectName,projectname;
    SpinAdapter activityAdapter, projectlocation;
    private Dialog dialog;
    List<Object> activityList, projectlist;
    String hotel_id;
    private UserPref userPref;
    String latitude;
    String longitude;
    String addresss;
    String pkt = null;
    private NetConnection connection;
    String vender;
    String latitude1 = null, longitude1, id, names, accuracy;
    private String status = "";
    private String Site_Location = "Site_Location";
    private String athotel = "At_Hotel";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seletct_location);
        ButterKnife.bind(this);
        setToolbar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getString("latitude");
            longitude = extras.getString("longitude");
            addresss = extras.getString("address");
            accuracy = extras.getString("accurac");
//            Log.d("accurac", accuracy);
        }
        init();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("data", "Data you want to send");
        // Your fragment
        pick_coordinates obj = new pick_coordinates();
        obj.setArguments(bundle);

    }

    private void init() {
        mContext = this;
        Activity mActivity = this;
        connection = new NetConnection();
        resources = mActivity.getResources();
        api = APIClient.getClient().create(RestApi.class);
        userPref = new UserPref(mContext);
        activityList = new ArrayList<>();
        projectlist = new ArrayList<>();
        UserPref userPref = new UserPref(mContext);


        add.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);

        Log.v("lati123", latitude + "/" + longitude);
        // Toast.makeText(mActivity, "type_id" + type_id, Toast.LENGTH_SHORT).show();
        spinner();
        state();
        addres.setText(userPref.getaddress());
        typeId.setText(userPref.gettype());
        venderName.setText(userPref.getnames());
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(seletct_location.this, MainActivity.class);
        startActivity(i);
        return true;
    }


    String viewType = "";

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewType.equalsIgnoreCase(getResources().getString(R.string.policies)) ? getResources().getString(R.string.policies) : getResources().getString(R.string.my_location));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance_leave, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_leave);
        menuItem.setTitle(resources.getString(R.string.submit_project));
        menuItem.setIcon(resources.getDrawable(R.drawable.ic_add_timesheet));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_leave) {
            startActivity(new Intent(seletct_location.this, Manual_punchin_Reminder.class));
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void spinner() {
        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.location_base));
        projectTypeList.add("- -Select New Location Type- -");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        spinner.setAdapter(projectTypeAdapter);
        spinner.setOnItemSelectedListener(new ProjectLocation() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location_type = position;
                Log.d("location_type", String.valueOf(Location_type));
                Object item = parent.getItemAtPosition(position);
                type_id = item.toString();
                Log.d("type", String.valueOf(type_id));

                sd();
            }
        });
    }

    private void sd() {
        if (Location_type == 1) {
            getProjectActivitie();
            projectName.setVisibility(View.VISIBLE);
            state1.setVisibility(View.GONE);
            lodging1.setVisibility(View.GONE);
            projectlayout.setVisibility(View.VISIBLE);
            type = Site_Location;

            add.setVisibility(View.GONE);

        } else if (Location_type == 2) {
            state1.setVisibility(View.VISIBLE);
            lodging1.setVisibility(View.VISIBLE);
            projectName.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            projectlayout.setVisibility(View.GONE);
            type = athotel;

        } else if (Location_type == 3) {
            type = "Base_Location";
            state1.setVisibility(View.GONE);
            lodging1.setVisibility(View.GONE);
            projectName.setVisibility(View.GONE);
            projectlayout.setVisibility(View.GONE);
        } else if (Location_type == 4) {
            type = "In_Travel";
            state1.setVisibility(View.GONE);
            lodging1.setVisibility(View.GONE);
            projectName.setVisibility(View.GONE);
            projectlayout.setVisibility(View.GONE);

        } else if (Location_type == 5) {
            type = "Client_Location";
            state1.setVisibility(View.GONE);
            lodging1.setVisibility(View.GONE);
            projectName.setVisibility(View.GONE);
            projectlayout.setVisibility(View.GONE);


        } else {
            add.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            projectlayout.setVisibility(View.GONE);
        }

        Log.d("sdss", type);
    }

    private void getProjectActivitie() {

        Call<ArrayList<userproject>> call = api.fetchProjectListOnuser(userPref.getUserId(), 1);
        call.enqueue(new Callback<ArrayList<userproject>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<userproject>> call,
                                   @NonNull Response<ArrayList<userproject>> response) {
                //    dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<userproject> projectName = response.body();
                    if (projectName != null) {
                      //  Log.d("projectname",projectName);
                        // Toast.makeText(Projecttask.this, "dfd"+projectName, Toast.LENGTH_SHORT).show();
                        setActivitySpinne(projectName);
                        // getProjectActivities(projectId);

                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<userproject>> call, @NonNull Throwable t) {
                //  dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }

    private void setActivitySpinne(ArrayList<userproject> name) {
        if (projectlocation != null) {
            projectName.setAdapter(null);
            projectlist.clear();
        }

        projectlist.add(resources.getString(R.string.your_Project));
        projectlist.addAll(name);
        projectlocation = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectlist);
        projectName.setAdapter(projectlocation);
        projectName.setOnItemSelectedListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                projectId = bundle.getInt(ConstantVariable.MIX_ID.ID, 0);
                strProjectName = bundle.getString(mContext.getResources().getString(R.string.project));
                //projectName.setText(strProjectName);
                Log.e("projectname", String.valueOf(projectId));
                // getProjectActivities(null);
                // Next();
            }
        }

    }

    private void state() {
        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.StateArray));
        projectTypeList.add("- -Select State- -");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        state.setAdapter(projectTypeAdapter);
        state.setOnItemSelectedListener(new ProjectLocation() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // states += parent;
                Object item = parent.getItemAtPosition(position);
                states = item.toString();
                //  Toast.makeText(mContext, "" + states, Toast.LENGTH_SHORT).show();
                getProjectActivities(states);
            }
        });
    }

    private void getProjectActivities(String states) {

        Call<ArrayList<fetch_hotal_pojo>> call = api.Fetch_hotal(states);
        call.enqueue(new Callback<ArrayList<fetch_hotal_pojo>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<fetch_hotal_pojo>> call,
                                   @NonNull Response<ArrayList<fetch_hotal_pojo>> response) {
                //    dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<fetch_hotal_pojo> fetch_hotal_pojo = response.body();
                    if (fetch_hotal_pojo != null) {

                        //   Toast.makeText(ProjectLocation.this, "" + fetch_hotal_pojo, Toast.LENGTH_SHORT).show();
                        setActivitySpinner(fetch_hotal_pojo);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<fetch_hotal_pojo>> call, @NonNull Throwable t) {
                //  dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }

    private void setActivitySpinner(ArrayList<fetch_hotal_pojo> projectActivities) {
        if (activityAdapter != null) {
            hotel.setAdapter(null);
            activityList.clear();
        }

        activityList.add(resources.getString(R.string.select_Hotel));
        activityList.addAll(projectActivities);
        activityAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, activityList);
        hotel.setAdapter(activityAdapter);
        hotel.setOnItemSelectedListener(this);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.project_name:
                if (projectlist.get(position) instanceof userproject) {
                    // getProjectActivities(((in.technitab.teamapp.model.userproject) projectlist.get(position)).getId());
                    projectId = ((userproject) projectlist.get(position)).getId();
                     projectname= ((userproject) projectlist.get(position)).getProjectName();
                    Log.d("projectid", String.valueOf(projectId));
                    Log.d("projectname",projectname);
                }
                break;

            case R.id.hotel:
                if (activityList.get(position) instanceof fetch_hotal_pojo) {
                    // getActivityAssignees(((in.technitab.teamapp.model.ProjectActivity) activityList.get(position)).getId());
                    hotel_id = ((fetch_hotal_pojo) activityList.get(position)).getId();
                     hotel_name = ((fetch_hotal_pojo) activityList.get(position)).getContactName();
                    Log.d("hsds", hotel_name);

                }
                break;
        }
    }

    @OnClick(R.id.submit)
    public void Next() {
        // apis();
        userPref.storeLocation(latitude1, longitude1, pkt, type, id, names);
        Intent i = new Intent(seletct_location.this, FetchLocationActivity.class);
        startActivity(i);
    }

    private void apis() {
        int hotels = 0;

        try {
            hotels = Integer.parseInt(hotel_id);

        } catch (Exception ignored) {
        }

        if (projectId != 0) {
            String projectid = Integer.toString(projectId);
            id = projectid;

        } else if
        (hotels != 0) {
            id = hotel_id;

        } else {
            id = userPref.getUserId();

        }
        if (strProjectName != null) {
            names = strProjectName;

        } else if (vender != null) {
            names = vender;

        } else {
            names = null;
        }

//        Log.d("asdasdasdf" , latitud );

        Log.d("sdss", type);
        //userPref.storeLocation(latitude1, longitude1, pkt, type_id, id, names);
        if (connection.isNetworkAvailable(this)) {
//            dialog.showDialog();
            Call<fetch_coordinate> call = api.fetchcoordinate(userPref.getUserId(), type, id);
            call.enqueue(new Callback<fetch_coordinate>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<fetch_coordinate> call, @NonNull Response<fetch_coordinate> response) {
                    //  dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        fetch_coordinate assignProject = response.body();
                        if (assignProject != null) {
                            pkt = assignProject.getBaseAddress();
                            latitude1 = assignProject.getBaseLat();
                            longitude1 = assignProject.getBaseLong();
                            getlocation.setText(pkt);

                            if (pkt == null || pkt.equals("")) {
                                add.setVisibility(View.VISIBLE);
                                submit.setVisibility(View.GONE);
                            } else {
                                add.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                            }
                            Log.e("pkt", String.valueOf(pkt));
                            Log.e("pkt", String.valueOf(longitude1));
                            //   Toast.makeText(seletct_location.this, "dfd" + pkt + "sfsd", Toast.LENGTH_SHORT).show();
                            // Log.e("pkt", String.valueOf(assignProject));



                        }
                    } else {
                        /*add.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);*/
                        // Toast.makeText(seletct_location.this, "sds", Toast.LENGTH_SHORT).show();
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }

                }

                @Override
                public void onFailure(@NonNull Call<fetch_coordinate> call, @NonNull Throwable t) {
//                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(resources.getString(R.string.slow_internet_connection));
                    }
                }

            });
        } else {
            showMessage(resources.getString(R.string.internet_not_available));

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

   /* private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }*/

    @OnClick(R.id.add)
    public void onViewClicked() {

        Intent i = new Intent(seletct_location.this, submitcoordinates.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        i.putExtra("address", addresss);
        i.putExtra("id", id);
        i.putExtra("typename", type_id);
        i.putExtra("vender", hotel_name);
        i.putExtra("projectname", projectname);
        i.putExtra("accuracy", accuracy);

        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(seletct_location.this, MainActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.fetch_address)
    public void fetchaddress() {
        apis();
    }

    private String myString = "hello";

    public String getMyData() {
        return myString;
    }

}
