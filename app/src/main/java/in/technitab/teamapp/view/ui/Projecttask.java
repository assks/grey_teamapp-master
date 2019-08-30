package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.AddResponse;
import in.technitab.teamapp.model.TaskSpinner;
import in.technitab.teamapp.model.TaskSpinnerResponse;

import in.technitab.teamapp.model.User;

import in.technitab.teamapp.model.userproject;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.CustomDate;
import in.technitab.teamapp.util.DateCal;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetworkError;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Projecttask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Unbinder unbinder;
    @BindView(R.id.task_name)
    EditText taskName;
    /*@BindView(R.id.project_name)
    CustomEditText projectName;*/
    @BindView(R.id.estimated_hours)
    EditText estimatedHours;
    @BindView(R.id.urgency_spinner)
    Spinner urgencySpinner;
    @BindView(R.id.dead_line)
    EditText deadLine;
    @BindView(R.id.assigned_date)
    EditText assignedDate;
    @BindView(R.id.Notice)
    EditText note;
    @BindView(R.id.project_type_spinner)
    Spinner projectTypeSpinner;
    @BindView(R.id.activity_spinner)
    Spinner activityNameSpinner;
    @BindView(R.id.task_deliverables)
    EditText taskDeliverables;
    /*@BindView(R.id.assignees_spinner)
    Spinner assigneesSpinner;*/
    @BindView(R.id.importance_spinner)
    Spinner importanceSpinner;
    @BindView(R.id.priority_spinner)
    Spinner prioritySpinner;
    @BindView(R.id.status_spinner)
    Spinner statusSpinner;

    private RestApi api;
    private Dialog dialog;
    private Activity mContext;
    private String action = "";
    SpinAdapter activityAdapter,  projectadapte;
    List<Object> projectlist, activityList, importanceList, urgencyList, priorityList, statusList, assigneesList;
    private Resources resources;
    int projectId = 0;
    private UserPref userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_projecttask);
        ButterKnife.bind(this);
        init();
        setToolbar();
        getProjectActivitie();

        getSpinnerData();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(action.equalsIgnoreCase(resources.getString(R.string.approve)) ? resources.getString(R.string.approve_project) : action.equalsIgnoreCase(ConstantVariable.MIX_ID.SUBMIT) ? resources.getString(R.string.submitted) : resources.getString(R.string.create_task));
            actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
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
        if (projectadapte != null) {
            projectTypeSpinner.setAdapter(null);
            projectlist.clear();
        }

        projectlist.add(resources.getString(R.string.your_Project));
        projectlist.addAll(name);
        projectadapte = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectlist);
        projectTypeSpinner.setAdapter(projectadapte);
        projectTypeSpinner.setOnItemSelectedListener(this);

    }


    private void init() {
        mContext = this;
        if (mContext != null) {
            resources = mContext.getResources();
            dialog = new Dialog(this);
            //    ((MainActivity) mContext).setToolbar(resources.getString(R.string.addtask));
            api = APIClient.getClient().create(RestApi.class);
            activityList = new ArrayList<>();
            projectlist = new ArrayList<>();
            urgencyList = new ArrayList<>();
            importanceList = new ArrayList<>();
            priorityList = new ArrayList<>();
            statusList = new ArrayList<>();
            assigneesList = new ArrayList<>();
            userPref = new UserPref(mContext);

            new CustomDate(deadLine, mContext, DateCal.getDate(System.currentTimeMillis()), null);
            new CustomDate(assignedDate, mContext, DateCal.getDate(System.currentTimeMillis()), null);

        }
    }


    @OnClick(R.id.submit_task)
    public void onSubmitTask() {
        if (!invalidate()) {

            String strTaskName = taskName.getText().toString().trim();
            String strAssignedDate = assignedDate.getText().toString().trim();
            int projectTypeId = projectTypeSpinner.getSelectedItemPosition();
            int activityId = 0;
            if (activityNameSpinner.getSelectedItemPosition() > 0) {
                in.technitab.teamapp.model.ProjectActivity activity = (in.technitab.teamapp.model.ProjectActivity) activityList.get(activityNameSpinner.getSelectedItemPosition());
                activityId = activity.getId();
            }

            int assigneesId = 0;
           /* if (assigneesSpinner.getSelectedItemPosition() >0){
                User user = (User) assigneesList.get(assigneesSpinner.getSelectedItemPosition());
                assigneesId = user.getId();
            }*/

            int importanceId = 0, importanceValue = 0;
            if (importanceSpinner.getSelectedItemPosition() > 0) {
                TaskSpinner importance = (TaskSpinner) importanceList.get(importanceSpinner.getSelectedItemPosition());
                importanceId = importance.getId();
                importanceValue = importance.getValue();
            }

            int urgencyId = 0, urgencyValue = 0;
            if (urgencySpinner.getSelectedItemPosition() > 0) {
                TaskSpinner urgency = (TaskSpinner) urgencyList.get(urgencySpinner.getSelectedItemPosition());
                urgencyId = urgency.getId();
                urgencyValue = urgency.getValue();
            }

            int scoreValue = importanceValue * urgencyValue;

            String strEstimatedHour = estimatedHours.getText().toString().trim();

            int priorityId = 0;
            if (prioritySpinner.getSelectedItemPosition() > 0) {
                TaskSpinner priority = (TaskSpinner) priorityList.get(prioritySpinner.getSelectedItemPosition());
                priorityId = priority.getId();
            }

            String strDeadLine = deadLine.getText().toString().trim();
            String strNote = note.getText().toString().trim();
            String strTaskDeliverable = taskDeliverables.getText().toString().trim();

            int statusId = 0;
            if (statusSpinner.getSelectedItemPosition() > 0) {
                TaskSpinner status = (TaskSpinner) statusList.get(statusSpinner.getSelectedItemPosition());
                statusId = status.getId();
            }

            uploadData(strTaskName, projectTypeId, projectId, activityId, assigneesId, strEstimatedHour, urgencyId, importanceId, scoreValue, priorityId, strDeadLine, strAssignedDate, strNote, strTaskDeliverable, statusId);
        }
    }

    private void uploadData(String strTaskName, int projectTypeId, int projectId, int activityId,
                            int assigneesId, String strEstimatedHour, int urgencyId, int importanceId, int scoreValue,
                            int priorityId, String strDeadLine, String strAssignedDate, String strNote, String strTaskDeliverable,
                            int statusId) {
        dialog.showDialog();
        Call<AddResponse> call = api.addTask(strTaskName, projectTypeId, projectId, activityId, assigneesId,
                strEstimatedHour, urgencyId, importanceId, priorityId, scoreValue, strDeadLine, strAssignedDate, strNote, strTaskDeliverable, statusId, userPref.getUserId());
        call.enqueue(new Callback<AddResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddResponse> call,
                                   @NonNull Response<AddResponse> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    AddResponse addResponse = response.body();
                    if (addResponse != null) {
                        showMessage(addResponse.getMessage());
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

    private boolean invalidate() {
        boolean error = false;
        if (TextUtils.isEmpty(taskName.getText().toString().trim())) {
            showMessage("Task name is required");
            error = true;
        } else if (projectTypeSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Project type is required");
            error = true;
      /*  }else if(projectId == 0){
            showMessage("Project name is required");
            error = true;*/
        } else if (activityNameSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Activity name is required");
            error = true;
       /* }else if(assigneesSpinner.getSelectedItemPosition() <= 0){
            showMessage("Project type is required");
            error = true;*/
        } else if (TextUtils.isEmpty(estimatedHours.getText().toString().trim())) {
            showMessage("Estimated hour is required");
            error = true;
        } else if (TextUtils.isEmpty(estimatedHours.getText().toString().trim())) {
            showMessage("Estimated hour is required");
            error = true;
        } else if (urgencySpinner.getSelectedItemPosition() <= 0) {
            showMessage("Urgency is required");
            error = true;
        } else if (importanceSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Importance hour is required");
            error = true;
        } else if (TextUtils.isEmpty(assignedDate.getText().toString().trim())) {
            showMessage("Assigned date is required");
            error = true;
        } else if (TextUtils.isEmpty(deadLine.getText().toString().trim())) {
            showMessage("dead line is required");
            error = true;
        } else if (TextUtils.isEmpty(taskDeliverables.getText().toString().trim())) {
            showMessage("Task deliverable is required");
            error = true;
        } else if (statusSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Status is required");
            error = true;
        }
        return error;
    }

    /*@OnClick(R.id.project_name)
    public void onPickProjectName(){

       // int projectTypeSpinnerId =1;
        Intent intent = new Intent(mContext, ProjectActivity.class);
        intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.PROJECT);
        //  intent.putExtra(ConstantVariable.MIX_ID.ID, projectTypeSpinnerId);
        this.startActivityForResult(intent, 1);


        *//*int projectTypeSpinnerId = projectTypeSpinner.getSelectedItemPosition();
        if (projectTypeSpinnerId != 0) {
            Intent intent = new Intent(mContext, ProjectActivity.class);
            intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.PROJECT);
            intent.putExtra(ConstantVariable.MIX_ID.ID,projectTypeSpinnerId);
            this.startActivityForResult(intent,1);
        }*//*



    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                projectId = bundle.getInt(ConstantVariable.MIX_ID.ID );
                String strProjectid = bundle.getString(mContext.getResources().getString(R.string.project));
                String strProjectName = bundle.getString(mContext.getResources().getString(R.string.project));
                //projectName.setText(strProjectName);
                Log.e("projectid", String.valueOf(projectId));
                Log.e("projectid", String.valueOf(strProjectid));
                 getProjectActivities();
                // Next();
            }
        }

    }*/

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
            activityNameSpinner.setAdapter(null);
            activityList.clear();
        }

        activityList.add(resources.getString(R.string.select_activity));
        activityList.addAll(projectActivities);
        activityAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, activityList);
        activityNameSpinner.setAdapter(activityAdapter);
        activityNameSpinner.setOnItemSelectedListener(this);
    }

    private void getActivityAssignees(int activityI) {
        dialog.showDialog();
        Call<ArrayList<User>> call = api.fetchActivityAssignees(activityI);
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call,
                                   @NonNull Response<ArrayList<User>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<User> users = response.body();
                    //setAssigneesSpinner(users);
                } else {
                    //showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }

   /* private void setAssigneesSpinner(ArrayList<User> projectActivities) {
        if (assigneesAdapter != null){
            assigneesSpinner.setAdapter(null);
            assigneesList.clear();
        }

        assigneesList.add(resources.getString(R.string.select_assignees));
        assigneesList.addAll(projectActivities);
        assigneesAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, assigneesList);
        assigneesSpinner.setAdapter(assigneesAdapter);
    }*/

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

    private void
    setUrgencySpinner(TaskSpinnerResponse taskSpinner) {

        urgencyList.add(resources.getString(R.string.select_urgency));
        urgencyList.addAll(taskSpinner.getUrgency());
        SpinAdapter urgencyAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, urgencyList);
        urgencySpinner.setAdapter(urgencyAdapter);
        urgencySpinner.setOnItemSelectedListener(this);

        importanceList.add(resources.getString(R.string.select_importance));
        importanceList.addAll(taskSpinner.getImportance());
        SpinAdapter importanceAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, importanceList);
        importanceSpinner.setAdapter(importanceAdapter);
        importanceSpinner.setOnItemSelectedListener(this);

        statusList.add(resources.getString(R.string.select_status));
        statusList.addAll(taskSpinner.getStatus());
        SpinAdapter statusAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, statusList);
        statusSpinner.setAdapter(statusAdapter);

        priorityList.addAll(taskSpinner.getPriority());
        SpinAdapter priorityAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, priorityList);
        prioritySpinner.setAdapter(priorityAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


        switch (adapterView.getId()) {

            case R.id.project_type_spinner:
                if (projectlist.get(position) instanceof in.technitab.teamapp.model.userproject) {
                    getProjectActivities(((in.technitab.teamapp.model.userproject) projectlist.get(position)).getId());
                    projectId = ((userproject) projectlist.get(position)).getId();
                }
                break;

          /*  case R.id.activity_spinner:
                if (activityList.get(position) instanceof in.technitab.teamapp.model.ProjectActivity){
                    getActivityAssignees(((in.technitab.teamapp.model.ProjectActivity) activityList.get(position)).getId());
                }
                break;*/

            case R.id.importance_spinner:
                if (importanceList.get(position) instanceof TaskSpinner) {
                    updatePrioritySpinner();
                }
                break;

            case R.id.urgency_spinner:
                if (urgencyList.get(position) instanceof TaskSpinner) {
                    updatePrioritySpinner();
                }
                break;


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updatePrioritySpinner() {
        int importanceValue = 0;
        if (importanceSpinner.getSelectedItemPosition() > 0) {
            TaskSpinner importance = (TaskSpinner) importanceList.get(importanceSpinner.getSelectedItemPosition());
            importanceValue = importance.getValue();
        }

        int urgencyValue = 0;
        if (urgencySpinner.getSelectedItemPosition() > 0) {
            TaskSpinner urgency = (TaskSpinner) urgencyList.get(urgencySpinner.getSelectedItemPosition());
            urgencyValue = urgency.getValue();
        }

        int priorityValue = urgencyValue * importanceValue;
        int prioritySpinnerSelectionId = 0;
        if (priorityValue >= 1 && priorityValue <= 6) {
            prioritySpinnerSelectionId = 1;
        } else if (priorityValue >= 7 && priorityValue <= 15) {
            prioritySpinnerSelectionId = 2;
        } else if (priorityValue >= 16 && priorityValue <= 30) {
            prioritySpinnerSelectionId = 3;
        } else if (priorityValue >= 31 && priorityValue <= 130) {
            prioritySpinnerSelectionId = 4;
        }

        prioritySpinner.setSelection(prioritySpinnerSelectionId, true);
        prioritySpinner.setEnabled(false);
    }

   /* @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }*/
}
