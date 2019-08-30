package in.technitab.teamapp.view.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.EducationAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.database.ESSdb;
import in.technitab.teamapp.model.Education;
import in.technitab.teamapp.model.UserProfile;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.NetworkError;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.employee_designation)
    TextView employeeDesignation;
    @BindView(R.id.personal_detail)
    TextView personalDetail;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.marital_status)
    TextView maritalStatus;
    @BindView(R.id.nationality)
    TextView nationality;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.father)
    TextView father;
    @BindView(R.id.religion)
    TextView religion;
    @BindView(R.id.reporting_to)
    TextView reportingTo;
    @BindView(R.id.joining_date)
    TextView joiningDate;
    @BindView(R.id.appointment_date)
    TextView appointmentDate;
    @BindView(R.id.permanent_address)
    TextView permanentAddress;
    @BindView(R.id.current_address)
    TextView currentAddress;
    @BindView(R.id.official_email)
    TextView officialEmail;
    @BindView(R.id.personal_email)
    TextView personalEmail;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.emergency_number)
    TextView emergencyNumber;

    @BindView(R.id.profile_recycler_view)
    RecyclerView profileRecyclerView;

    @BindView(R.id.attachment_list)
    Button attachmentList;
    @BindView(R.id.pan_number)
    TextView panNumber;
    @BindView(R.id.passport_number)
    TextView passportNumber;
    @BindView(R.id.aadhar_number)
    TextView aadharNumber;
    @BindView(R.id.dl)
    TextView dl;
    @BindView(R.id.voter_id_number)
    TextView voterIdNumber;
    @BindView(R.id.bank_name)
    TextView bankName;
    @BindView(R.id.bank_address)
    TextView bankAddress;
    @BindView(R.id.account_number)
    TextView accountNumber;
    @BindView(R.id.ifsc_code)
    TextView ifscCode;

    private NetConnection connection;
    private Dialog dialog;
    private RestApi api;
    private UserPref userPref;
    private ESSdb esSdb;
    private ArrayList<Education> educationArrayList;
    private EducationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        init();
        setToolbar();
        initRecyclerView();
        getUserProfile();
    }

    private void initRecyclerView() {
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        profileRecyclerView.setNestedScrollingEnabled(false);
        profileRecyclerView.setHasFixedSize(false);
        adapter = new EducationAdapter(this, educationArrayList);
        profileRecyclerView.setAdapter(adapter);
    }

    private void init() {
        connection = new NetConnection();
        dialog = new Dialog(this);
        userPref = new UserPref(this);
        educationArrayList = new ArrayList<>();
        api = APIClient.getClient().create(RestApi.class);
        esSdb = new ESSdb(this);
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void getUserProfile() {
        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();
            Call<UserProfile> call = api.userProfile(userPref.getRoleId(), userPref.getRelatedTable());
            call.enqueue(new Callback<UserProfile>() {
                @Override
                public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                    if (response.isSuccessful()) {
                        dialog.dismissDialog();
                        UserProfile addTimesheet = response.body();
                        if (addTimesheet != null) {
                            showData(addTimesheet);
                        } else {
                            showMessage(getResources().getString(R.string.no_projet_assigned));
                        }
                    }else{
                        showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    showMessage(NetworkError.getNetworkErrorMessage(t));
                }
            });
        } else {
            showMessage(getResources().getString(R.string.internet_not_available));
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void showData(UserProfile profile) {
        name.setText(profile.getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.circleCrop();
        requestOptions.placeholder(getResources().getDrawable(R.drawable.circluler_bg));
        Glide.with(this).load(profile.getImagePath()).apply(requestOptions).into(userIcon);
        employeeDesignation.setText(getResources().getString(R.string.emp_designation, userPref.getRoleId(), profile.getDesignation()));
        father.setText(profile.getFather());
        dob.setText(profile.getBirthDate());
        nationality.setText(profile.getNationality());
        maritalStatus.setText(profile.getMaritalStatus());
        reportingTo.setText(profile.getReportingTo());
        gender.setText(profile.getGender());
        joiningDate.setText(profile.getJoiningDate());
        appointmentDate.setText(profile.getAppointmentDate());
        officialEmail.setText(profile.getOfficialEmailId());
        personalEmail.setText(profile.getPersonalEmailId());
        number.setText(profile.getMobileNumber());
        emergencyNumber.setText(profile.getEmergencyNumber());
        currentAddress.setText(profile.getCurrentFullAddress());
        permanentAddress.setText(profile.getPermanentFullAddress());
        aadharNumber.setText(profile.getAadharNumber());
        panNumber.setText(profile.getPanNumber());
        voterIdNumber.setText(profile.getVoterIdNumber());
        dl.setText(profile.getDrivingLicenseNumber());
        bankAddress.setText(profile.getBankAddress());
        bankName.setText(profile.getBankName());
        ifscCode.setText(profile.getIfscCode());
        passportNumber.setText(profile.getPassportNumber());
        accountNumber.setText(profile.getAccountNumer());

        educationArrayList.add(new Education("High School", profile.getTenSchool(), profile.getTenYear(), profile.getTenBoard(), profile.getTenPercentage()));
        educationArrayList.add(new Education("Intermediate", profile.getTwelveSchool(), profile.getTwelveYear(), profile.getTwelveBoard(), profile.getTwelvePercentage()));
        educationArrayList.add(new Education("Diploma", profile.getDiplomaSchool(), profile.getDiplomaYear(), profile.getDiplomaBoard(), profile.getDiplomaPercentage()));
        educationArrayList.add(new Education("Graduation", profile.getGradSchool(), profile.getGradYear(), profile.getGradBoard(), profile.getGradPercentage()));
        educationArrayList.add(new Education("Post Graduation", profile.getPostGradSchool(), profile.getPostGradYear(), profile.getPostGradBoard(), profile.getPostGradPercentage()));
        adapter.notifyDataSetChanged();

    }

    @OnClick(R.id.attachment_list)
    protected void onAttachment(){
        startActivity(new Intent(this,PolicyActivity.class).putExtra(ConstantVariable.MIX_ID.VIEW_TYPE,"profile"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance_leave,menu);
        MenuItem menuItem = menu.findItem(R.id.menu_leave);
        menuItem.setTitle(getResources().getString(R.string.logout));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_leave){
            esSdb.deleteALLData();
            userPref.resetLogin();
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
