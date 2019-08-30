package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.AddResponse;
import in.technitab.teamapp.model.EditTaskpojo;

import in.technitab.teamapp.model.TaskSpinner;
import in.technitab.teamapp.model.TaskSpinnerResponse;
import in.technitab.teamapp.model.User;
import in.technitab.teamapp.util.ConstantVariable;

import in.technitab.teamapp.util.CustomDate;
import in.technitab.teamapp.util.CustomEditText;

import in.technitab.teamapp.util.DateCal;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetworkError;
import in.technitab.teamapp.util.SetTime;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class updateTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener /*implements AdapterView.OnItemSelectedListener*/ {

    Unbinder unbinder;
    @BindView(R.id.task_name)
    TextView taskName;
    @BindView(R.id.project_name)
    CustomEditText projectName;
    @BindView(R.id.estimated_hours)
    EditText estimatedHours;
    @BindView(R.id.urgency_spinner)
    TextView urgencySpinner;
    @BindView(R.id.dead_line)
    EditText deadLine;
    @BindView(R.id.start_date)
    CustomEditText startDate;
    @BindView(R.id.assigned_date)
    EditText assignedDate;
    @BindView(R.id.Notice)
    public EditText note;
   /* @BindView(R.id.project_type_spinner)
    TextView projectTypeSpinner;*/
    @BindView(R.id.activity_spinner)
    TextView activityNameSpinner;
    @BindView(R.id.task_deliverables)
    EditText taskDeliverables;
   /* @BindView(R.id.assignees_spinner)
    TextView assigneesSpinner;*/
    @BindView(R.id.importance_spinner)
    TextView importanceSpinner;
    @BindView(R.id.priority_spinner)
    TextView prioritySpinner;
    @BindView(R.id.status_spinner)
    Spinner statusSpinner;
    @BindView(R.id.time)
    CustomEditText time;

    public String ote;
    public String projecttype;
    public int project;
    public String taskname;
    public String estimatehours;
    public String assigndate;
    public String deadline;
    public String taskdeliverables;
    public String projectname;
    public String status;
    public String activityname;
    public String assigne;
    public String urgency;
    public String importance;
    public String priorty;
    public int project_id,activity_id;
    private RestApi api;
    private Dialog dialog;
    private Activity mContext;
    SpinAdapter activityAdapter;
    SpinAdapter assigneesAdapter;
    private DatePickerDialog startDataPicker;
    List<Object> activityList, importanceList, urgencyList, priorityList, statusList, assigneesList;
    private Resources resources;
    int projectId = 0;
    private UserPref userPref;
    private int activity;
    public String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task1);
        ButterKnife.bind(this);
        value = Objects.requireNonNull(getIntent().getExtras()).getString("taskid");

        init();
       // setupProjectType();
        getSpinnerData();
        getProjectListBasedOnType();

        new CustomDate(startDate, this, null, DateCal.getDate(System.currentTimeMillis()));
        initialiseDatePicker();
        new SetTime(time, this);

    }

    private void initialiseDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        startDataPicker = new DatePickerDialog(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this, year, month, day);
        startDate.setText(getResources().getString(R.string.set_date_value, year, month + 1, day));

    }

    private void getProjectListBasedOnType() {
        dialog.showDialog();
        Call<ArrayList<EditTaskpojo>> call = api.editTask(/*"44", "91" */ userPref.getUserId(),value);
        call.enqueue(new Callback<ArrayList<EditTaskpojo>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<EditTaskpojo>> call, @NonNull Response<ArrayList<EditTaskpojo>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<EditTaskpojo> stringResponse = response.body();
                    if (stringResponse != null) {

                        if (stringResponse.isEmpty()) {
                            showMessage(getResources().getString(R.string.no_history_found));
                             } else {
                            ote = stringResponse.get(0).getNotes();
                            taskname =  stringResponse.get(0).getName();
                            projecttype = stringResponse.get(0).getProjectType();
                            estimatehours = stringResponse.get(0).getEstimatedHours();
                            assigndate=stringResponse.get(0).getAssignedOnDate();
                            deadline=stringResponse.get(0).getDeadlines();
                            taskdeliverables=stringResponse.get(0).getTaskDeliverables();
                            projectname=stringResponse.get(0).getProjectName();
                            status=  stringResponse.get(0).getStatus();
                            activityname=stringResponse.get(0).getActivityName();
                            urgency=stringResponse.get(0).getUrgcy();
                            importance=stringResponse.get(0).getImp();
                            priorty=stringResponse.get(0).getPname();
                            assigne=stringResponse.get(0).getUsrname();
                            project=stringResponse.get(0).getProjectId();
                             activity = stringResponse.get(0).getActivityId();

                        }
                    }
                } else {
                    showMessage(resources.getString(R.string.problem_to_connect));
                }

                note.setText(ote);
                taskName.setText(taskname);
                // projectTypeSpinner.setText(projecttype);
                estimatedHours.setText(estimatehours);
                assignedDate.setText(assigndate);
                deadLine.setText(deadline);
                taskDeliverables.setText(taskdeliverables);
                projectName.setText(projectname);
               // assigneesSpinner.setText(assigne);
                // statusSpinner.setText(status);
                activityNameSpinner.setText(activityname);
                urgencySpinner.setText(urgency);
                importanceSpinner.setText(importance);
                prioritySpinner.setText(priorty);

            }
            @Override
            public void onFailure(Call<ArrayList<EditTaskpojo>> call, Throwable t) {
                dialog.dismissDialog();
                if (t instanceof SocketTimeoutException) {
                    showMessage(getResources().getString(R.string.slow_internet_connection));
                }
            }
        });
    }


    private void init() {
        mContext = this;
        if (mContext != null) {
            resources = mContext.getResources();
            dialog = new Dialog(this);
            //    ((MainActivity) mContext).setToolbar(resources.getString(R.string.addtask));
            api = APIClient.getClient().create(RestApi.class);
            activityList = new ArrayList<>();
            urgencyList = new ArrayList<>();
            importanceList = new ArrayList<>();
            priorityList = new ArrayList<>();
            statusList = new ArrayList<>();
            assigneesList = new ArrayList<>();
            userPref = new UserPref(mContext);


        }
        setToolbar();
    }

    @OnClick(R.id.start_date)
    protected void OnDate() {
        startDataPicker.show();
            }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resources.getString(R.string.update_task));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    @OnClick(R.id.submit_task)
  public void Submit(View view) {

      String strTime = time.getText().toString().trim();
      final String strDate = startDate.getText().toString().trim();
         String statusName="";
         int statusId = 0;
      if (statusSpinner.getSelectedItemPosition() > 0) {
          TaskSpinner status = (TaskSpinner) statusList.get(statusSpinner.getSelectedItemPosition());
          statusId = status.getId();
            statusName=status.getName().toString();


      }

      uploadData(statusName,strTime,strDate,statusId);
  }

    private void uploadData(String statusName,String strTime,String strDate,int statusId ) {

        dialog.showDialog();

        Call<AddResponse> call = api.UpdateTaskHours(project,activity,value,strTime,strDate,statusId, userPref.getUserId());
        call.enqueue(new Callback<AddResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddResponse> call,
                                   @NonNull Response<AddResponse> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    AddResponse addResponse = response.body();
                    if (addResponse != null) {
                        showMessage(addResponse.getMessage());
                       /* Intent i=new Intent(updateTask.this,TeamTask.class);
                        startActivity(i);*/
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddResponse> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                projectId = bundle.getInt(ConstantVariable.MIX_ID.ID, 0);
                String strProjectName = bundle.getString(mContext.getResources().getString(R.string.project));
                projectName.setText(strProjectName);
                getProjectActivities(projectId);
            }
        }
    }

    private void getProjectActivities(int projectId) {
        dialog.showDialog();
        Call<ArrayList<in.technitab.teamapp.model.ProjectActivity>> call = api.fetchProjectActivities(projectId, 0);
        call.enqueue(new Callback<ArrayList<in.technitab.teamapp.model.ProjectActivity>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<in.technitab.teamapp.model.ProjectActivity>> call,
                                   @NonNull Response<ArrayList<in.technitab.teamapp.model.ProjectActivity>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<in.technitab.teamapp.model.ProjectActivity> projectActivities = response.body();
                    if (projectActivities != null) {
                        setActivitySpinner(projectActivities);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<in.technitab.teamapp.model.ProjectActivity>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }


    private void setActivitySpinner(ArrayList<in.technitab.teamapp.model.ProjectActivity> projectActivities) {
        if (activityAdapter != null) {
           // activityNameSpinner.setAdapter(null);
            activityList.clear();
        }

        activityList.add(resources.getString(R.string.select_activity));
        activityList.addAll(projectActivities);
        activityAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, activityList);
       /* activityNameSpinner.setAdapter(activityAdapter);
        activityNameSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);*/
    }

    private void getActivityAssignees(int activityId) {
        dialog.showDialog();
        Call<ArrayList<User>> call = api.fetchActivityAssignees(activityId);
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call,
                                   @NonNull Response<ArrayList<User>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<User> users = response.body();
                    if (users != null) {
                        //setAssigneesSpinner(users);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }
    // importance, urgency, status spinner data
    private void getSpinnerData() {
        dialog.showDialog();
        Call<TaskSpinnerResponse> call = api.getTaskSpinnerData();
        call.enqueue(new Callback<TaskSpinnerResponse>() {
            @Override
            public void onResponse(@NonNull Call<TaskSpinnerResponse> call,
                                   @NonNull Response<TaskSpinnerResponse> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {

                    TaskSpinnerResponse urgencies = response.body();
                    if (urgencies != null) {
                        setUrgencySpinner(urgencies);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TaskSpinnerResponse> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }


    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int monthOfDay) {
        startDate.setText(getResources().getString(R.string.set_date_value, year, month + 1, monthOfDay));
       // updateUI();
    }

    private void
    setUrgencySpinner(TaskSpinnerResponse taskSpinner) {

        statusList.add(resources.getString(R.string.select_status));
        statusList.addAll(taskSpinner.getStatus());
        SpinAdapter statusAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, statusList);
        statusSpinner.setAdapter(statusAdapter);

    }
}
