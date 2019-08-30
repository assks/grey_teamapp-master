package in.technitab.teamapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.technitab.teamapp.util.ConstantVariable;

public class Taskpojo {

    @SerializedName("project_name")
    private String project_name;
    @SerializedName("activity_name")
    private  String activity_name;
    @SerializedName("task_deliverables")
    private  String task_deliverables;
    @SerializedName("estimated_hours")
    private  String estimated_hours;
    @SerializedName("notes")
    private  String notes;
    @SerializedName(ConstantVariable.Tbl_TimeSheet.DESCRIPTION)
    private String descriptio;
    @SerializedName(ConstantVariable.Tbl_TimeSheet.IS_BILLABLE)
    private int isBillable;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("activity_id")
    @Expose
    private String activityId;
    @SerializedName("task_id")
    @Expose
    private String taskId;
    @SerializedName("hrs_spend")
    @Expose
    private String hrsSpend;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("created_by_id")
    @Expose
    private String createdById;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("updated_by_id")
    @Expose
    private String updatedById;
    @SerializedName("task_name")
    @Expose
    private String taskName;
    @SerializedName("status_name")
    @Expose
    private String statusName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getHrsSpend() {
        return hrsSpend;
    }

    public void setHrsSpend(String hrsSpend) {
        this.hrsSpend = hrsSpend;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(String updatedById) {
        this.updatedById = updatedById;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }


    public String getDescriptio() {
        return descriptio;
    }

    public void setDescriptio(String descriptio) {
        this.descriptio = descriptio;
    }

    public int getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(int isBillable) {
        this.isBillable = isBillable;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getTask_deliverables() {
        return task_deliverables;
    }

    public void setTask_deliverables(String task_deliverables) {
        this.task_deliverables = task_deliverables;
    }

    public String getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(String estimated_hours) {
        this.estimated_hours = estimated_hours;
    }
}
