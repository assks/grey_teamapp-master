package in.technitab.teamapp.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.AlarmReceiver;
import in.technitab.teamapp.BuildConfig;
import in.technitab.teamapp.NotificationScheduler;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.api.Service;
import in.technitab.teamapp.model.versionpojo;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.ReminderData;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private UserPref userPref;

    private NetConnection connection;
    private Dialog dialog;
    private RestApi api;
    private Resources resources;
    int version;
    String versionName, version_codes, version_name;
    int appversionCode, server_code;
    TelephonyManager telephonyManager;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        userPref = new UserPref(this);
        setSupportActionBar(toolbar);

       /* Intent i = new Intent(getApplicationContext(), Service.class);
        startService(i);*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        View view = navView.getHeaderView(0);
        ImageView imageView = view.findViewById(R.id.user_icon);
        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(userPref.getName());
        TextView appVersion = view.findViewById(R.id.app_version);
        appVersion.setText(getResources().getString(R.string.app_version, getVersionInfo()));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.circleCrop();
        requestOptions.placeholder(getResources().getDrawable(R.drawable.circluler_bg));
        Glide.with(this).load(userPref.getUserImage()).apply(requestOptions).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            displaySelectedView(R.id.nav_attendance_leave);
        else {
            startPunchInFragment();
            navView.setCheckedItem(R.id.nav_punch_in_out);
        }

        setPunchReminder();

        connection = new NetConnection();
        resources = getResources();
        dialog = new Dialog(this);
        userPref = new UserPref(this);
        api = APIClient.getClient().create(RestApi.class);
        onProceed();






    }

    private String getVersionInfo() {
        appversionCode = BuildConfig.VERSION_CODE;
        versionName = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;

    }


    private void startPunchInFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new PunchInFragment());
        ft.commit();

        navView.setCheckedItem(R.id.nav_attendance_leave);
    }


    public void setToolbar(String title) {
        toolbar.setTitle(title);
    }

    public void setToolBarSubtitle(String subtitle) {
        if (subtitle != null) {
            toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorSecondary));
            toolbar.setSubtitle(subtitle);
        } else {
            toolbar.setSubtitle(null);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedView(item.getItemId());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void setPunchReminder() {
        ReminderData localData = new ReminderData(this);
        NotificationScheduler.setPunchInReminder(this, AlarmReceiver.class, localData.getPunchInHour(), localData.getPunchInMin());
        NotificationScheduler.setPunchOutReminder(this, AlarmReceiver.class, localData.getPunchOutHour(), localData.getPunchOutMin());
    }

    private void displaySelectedView(int id) {
        navView.setCheckedItem(id);
        Fragment mFragment = null;
        switch (id) {

            case R.id.nav_attendance_leave:
                mFragment = new AttendanceLeaveFragment();
                break;

            case R.id.nav_punch_in_out:
                break;

            case R.id.nav_timesheet:
                mFragment = new TimeSheetFragment();
                break;

            case R.id.nav_task_todo_list:
                mFragment = new TeamTask();
                break;

            case R.id.nav_vendor:
                mFragment = new AddVendorFragment();
                break;

            case R.id.nav_expense:
                mFragment = new ExpenseFragment();
                break;

            case R.id.nav_travel:
                mFragment = new TravelBookingFragment();
                break;

            case R.id.nav_payroll:
                mFragment = new PayrollFragment();
                break;

            case R.id.nav_project_task:
                mFragment = new ProjectTaskFragment();
                break;

           /* case R.id.activity:
                mFragment = new ActivityFragmnet();
                break;*/
            case R.id.nav_resource:
                mFragment = new ResourceFragment();
        }

        if (mFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, mFragment);
            ft.commit();
        } else {

            startActivity(new Intent(this, FetchLocationActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void onProceed() {
        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();
            Call<versionpojo> call = api.appversion("version");
            call.enqueue(new Callback<versionpojo>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<versionpojo> call, @NonNull Response<versionpojo> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        versionpojo assignProject = response.body();
                        if (assignProject != null) {

                            version_codes = assignProject.getVersionCode();
                            version_name = assignProject.getVersion();
                            server_code = Integer.valueOf(version_codes);
                            Log.e("version_code", String.valueOf(version_name));
                            //showMessage(assignProject.getVersion());
                        }
                    } else {
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }
                    Log.e("server_code", String.valueOf(server_code));
                    Log.e("appversionCode", String.valueOf(appversionCode));

                    if (appversionCode < server_code) {

                        Log.d("versionid", String.valueOf(appversionCode));
                        Intent i = new Intent(MainActivity.this, Versiondialog.class);
                        i.putExtra("Sversion", version_name);
                        i.putExtra("appversion", versionName);
                        startActivity(i);

                    } else {
                        Log.e("version_name", versionName);
                        // Toast.makeText(MainActivity.this, "appversioncode>=server_code", Toast.LENGTH_SHORT).show();

                        Log.d("versionid", String.valueOf(appversionCode));


                    }

                }

                @Override
                public void onFailure(@NonNull Call<versionpojo> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(resources.getString(R.string.slow_internet_connection));
                    }

                }
            });
        } else {
            Toast toast = Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            v.setTextSize(20);
            View view = toast.getView();
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            view.setBackgroundColor(Color.TRANSPARENT);
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toast.setGravity(Gravity.BOTTOM, 0, 0);

            toast.show();

        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onResume() {
        super.onResume();

        // onProceed();
    }

    @Override
    public void onBackPressed() {
        // finish();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
