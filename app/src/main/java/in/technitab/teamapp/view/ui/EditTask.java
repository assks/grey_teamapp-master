package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
import in.technitab.teamapp.model.ProjectActivity;
import in.technitab.teamapp.model.TaskSpinner;
import in.technitab.teamapp.model.TaskSpinnerResponse;
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

public class EditTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Unbinder unbinder;
    @BindView(R.id.task_name)
    EditText taskName;
    /* @BindView(R.id.project_name)
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
    public EditText note;
    @BindView(R.id.project_type_spinner)
    Spinner projectTypeSpinner;
    @BindView(R.id.activity_spinner)
    Spinner activityNameSpinner;
    @BindView(R.id.task_deliverables)
    EditText taskDeliverables;
    /* @BindView(R.id.assignees_spinner)
     Spinner assigneesSpinner;*/
    @BindView(R.id.importance_spinner)
    Spinner importanceSpinner;
    @BindView(R.id.priority_spinner)
    Spinner prioritySpinner;
    @BindView(R.id.status_spinner)
    public Spinner statusSpinner;
    public String ote, taskname, estimatehours, assigndate, deadline, taskdeliverables, Projecttype;
    @BindView(R.id.submit_task)
    Button submitTask;

    private RestApi api;
    private Dialog dialog;
    private Activity mContext;
    SpinAdapter activityAdapter, projectadapter;
    List<Object> activityList, projectlist, importanceList, urgencyList, priorityList, statusList, assigneesList;
    private Resources resources;
    int projectId = 0,userprojectId;
    private UserPref userPref;
    public String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_projecttask);
        ButterKnife.bind(this);
        value = Objects.requireNonNull(getIntent().getExtras()).getString("message");
        submitTask.setText(getString(R.string.update));

        init();
        getProjectListBasedOnType();
        //setupProjectType();


        getSpinnerData();

    }

    private void getProjectListBasedOnType() {
        dialog.showDialog();
        Call<ArrayList<EditTaskpojo>> call = api.editTask(/*"44", "91" */ userPref.getUserId(), value);
        call.enqueue(new Callback<ArrayList<EditTaskpojo>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<EditTaskpojo>> call, @NonNull Response<ArrayList<EditTaskpojo>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<EditTaskpojo> stringResponse = response.body();
                    if (stringResponse != null) {
                        Toast.makeText(EditTask.this, "" + stringResponse, Toast.LENGTH_SHORT).show();
                        if (stringResponse.isEmpty()) {
                            showMessage(getResources().getString(R.string.no_history_found));
                        } else {
                            ote = stringResponse.get(0).getNotes();
                            taskname = stringResponse.get(0).getName();
                            Projecttype = stringResponse.get(0).getProjectType();
                            estimatehours = stringResponse.get(0).getEstimatedHours();
                            assigndate = stringResponse.get(0).getAssignedOnDate();
                            deadline = stringResponse.get(0).getDeadlines();
                            taskdeliverables = stringResponse.get(0).getTaskDeliverables();
                            userprojectId = stringResponse.get(0).getProjectId();
                            getProjectActivitie();
                            Log.d("projectid", String.valueOf(userprojectId));

                        }
                    }
                } else {
                    showMessage(resources.getString(R.string.problem_to_connect));
                }

                note.setText(ote);
                taskName.setText(taskname);
                //projectTypeSpinner.setAdapter(Projecttype);
                estimatedHours.setText(estimatehours);
                assignedDate.setText(assigndate);
                deadLine.setText(deadline);
                taskDeliverables.setText(taskdeliverables);
                //projectName.setText(projectname);

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


    private void getProjectActivitie() {

        Call<ArrayList<userproject>> call = api.fetchProjectListOnuser(userPref.getUserId(), 1);
        call.enqueue(new Callback<ArrayList<userproject>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<userproject>> call,
                                   @NonNull Response<ArrayList<userproject>> response) {
                    dialog.dismissDialog();
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
                  dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }

    private
    void setActivitySpinne(ArrayList<userproject> name) {
        if (projectadapter != null) {
            projectTypeSpinner.setAdapter(null);
            projectlist.clear();
        }

       /* String billingPosition = priorityList.indexOf(EditTaskpojo.getProjectName());
        projectTypeSpinner.setSelection(billingPosition < 0 ? 0 : billingPosition);*/
        projectlist.add(resources.getString(R.string.your_Project));
        projectlist.addAll(name);
        projectadapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectlist);
        projectTypeSpinner.setAdapter(projectadapter);
        projectTypeSpinner.setOnItemSelectedListener(this);

    }

    private void init() {
        mContext = this;
        if (mContext != null) {
            resources = mContext.getResources();
            dialog = new Dialog(this);
            api = APIClient.getClient().create(RestApi.class);
            activityList = new ArrayList<>();
            urgencyList = new ArrayList<>();
            importanceList = new ArrayList<>();
            priorityList = new ArrayList<>();
            projectlist = new ArrayList<>();
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
            // int projectTypeId = projectTypeSpinner.getSelectedItemPosition();
            int activityId = 0;

            if (activityNameSpinner.getSelectedItemPosition() > 0) {
                ProjectActivity activity = (ProjectActivity) activityList.get(activityNameSpinner.getSelectedItemPosition());
                activityId = activity.getId();
            }

            /*int assigneesId = 0;
            if (assigneesSpinner.getSelectedItemPosition() > 0) {
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

            uploadData(strTaskName, /*projectTypeId,*/ projectId, activityId, /*assigneesId,*/ strEstimatedHour, urgencyId, importanceId, scoreValue, priorityId, strDeadLine, strAssignedDate, strNote, strTaskDeliverable, statusId);
        }
    }

    private void uploadData(String strTaskName, /*int projectTypeId,*/ int projectId, int activityId,
            /* int assigneesId,*/ String strEstimatedHour, int urgencyId, int importanceId, int scoreValue,
                            int priorityId, String strDeadLine, String strAssignedDate, String strNote, String strTaskDeliverable,
                            int statusId) {

        dialog.showDialog();

        Call<AddResponse> call = api.updatetask(strTaskName, /*projectTypeId,*/ projectId, activityId,/* assigneesId,*/
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
                        Toast.makeText(mContext, "Successfully Updated", Toast.LENGTH_SHORT).show();
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
        /*} else if (projectTypeSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Project type is required");
            error = true;*/
        } else if (projectId == 0) {
            showMessage("Project name is required");
            error = true;
        } else if (activityNameSpinner.getSelectedItemPosition() <= 0) {
            showMessage("Activity name is required");
            error = true;
        /*} else if (assigneesSpinner.getSelectedItemPosition() <= 0) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                projectId = bundle.getInt(ConstantVariable.MIX_ID.ID, 0);
                String strProjectName = bundle.getString(mContext.getResources().getString(R.string.project));
                //projectName.setText(strProjectName);
                getProjectActivities(projectId);
            }
        }
    }

    private void getProjectActivities(int projectId) {
        dialog.showDialog();
        Call<ArrayList<ProjectActivity>> call = api.fetchProjectActivities(projectId, 0);
        call.enqueue(new Callback<ArrayList<ProjectActivity>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ProjectActivity>> call,
                                   @NonNull Response<ArrayList<ProjectActivity>> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<ProjectActivity> projectActivities = response.body();
                    if (projectActivities != null) {
                        setActivitySpinner(projectActivities);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ProjectActivity>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }


    private void setActivitySpinner(ArrayList<ProjectActivity> projectActivities) {
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
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
                if (projectlist.get(position) instanceof userproject) {
                    getProjectActivities(((userproject) projectlist.get(position)).getId());
                    projectId = ((userproject) projectlist.get(position)).getId();
                }
                break;

          /*  case R.id.activity_spinner:
                if (activityList.get(position) instanceof in.technitab.teamapp.model.ProjectActivity) {
                    //getActivityAssignees(((in.technitab.teamapp.model.ProjectActivity) activityList.get(position)).getId());
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


}
