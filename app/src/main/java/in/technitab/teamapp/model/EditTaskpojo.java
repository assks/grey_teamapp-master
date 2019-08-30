package in.technitab.teamapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditTaskpojo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("project_type_id")
    @Expose
    private String projectTypeId;
    @SerializedName("project_id")
    @Expose
    private int projectId;
    @SerializedName("activity_id")
    @Expose
    private int activityId;
    @SerializedName("assigned_on_date")
    @Expose
    private String assignedOnDate;
    @SerializedName("task_deliverables")
    @Expose
    private String taskDeliverables;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("estimated_hours")
    @Expose
    private String estimatedHours;
    @SerializedName("importance")
    @Expose
    private String importance;
    @SerializedName("urgency")
    @Expose
    private String urgency;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("imp")
    @Expose
    private String imp;

    public String getImp() {
        return imp;
    }

    public void setImp(String imp) {
        this.imp = imp;
    }

    public String getUrgcy() {
        return urgcy;
    }

    public void setUrgcy(String urgcy) {
        this.urgcy = urgcy;
    }

    @SerializedName("urgcy")
    @Expose
    private String urgcy;
    @SerializedName("deadlines")
    @Expose
    private String deadlines;
    @SerializedName("task_date")
    @Expose
    private Object taskDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("completion_date")
    @Expose
    private String completionDate;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("ti")
    @Expose
    private String ti;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("activity_name")
    @Expose
    private String activityName;
    @SerializedName("project_type")
    @Expose
    private String projectType;
    @SerializedName("pname")
    @Expose
    private String pname;
    @SerializedName("tname")
    @Expose
    private String tname;
    @SerializedName("usrname")
    @Expose
    private String usrname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(String projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getAssignedOnDate() {
        return assignedOnDate;
    }

    public void setAssignedOnDate(String assignedOnDate) {
        this.assignedOnDate = assignedOnDate;
    }

    public String getTaskDeliverables() {
        return taskDeliverables;
    }

    public void setTaskDeliverables(String taskDeliverables) {
        this.taskDeliverables = taskDeliverables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(String estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(String deadlines) {
        this.deadlines = deadlines;
    }

    public Object getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Object taskDate) {
        this.taskDate = taskDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getTi() {
        return ti;
    }

    public void setTi(String ti) {
        this.ti = ti;
    }

    public  String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getUsrname() {
        return usrname;
    }

    public void setUsrname(String usrname) {
        this.usrname = usrname;
    }

}