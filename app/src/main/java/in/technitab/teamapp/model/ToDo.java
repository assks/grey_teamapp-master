package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class ToDo {
    @SerializedName("name")
    private String name;
    @SerializedName("reminder_date")
    private String reminderDate;
    @SerializedName("is_repeat")
    private int isRepeat;
    @SerializedName("is_custom")
    private int isCustom;
    @SerializedName("repeat_value")
    private String repeatValue;
    @SerializedName("activity_id")
    private int activityId;
    @SerializedName("activity_name")
    private String activityName;
    @SerializedName("created_by_id")
    private int createdById;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("assign_to_id")
    private String assignToId;
    @SerializedName("assign_to")
    private String assingTo;
    @SerializedName("modified_by_to")
    private String modifedById;
    @SerializedName("modifed_date")
    private String modifiedDate;

    public ToDo(String name, String reminderDate, int isRepeat, int isCustom, String repeatValue, int activityId, String activityName, int createdById, String createdBy, String createdDate, String assignToId, String assingTo, String modifedById, String modifiedDate) {
        this.name = name;
        this.reminderDate = reminderDate;
        this.isRepeat = isRepeat;
        this.isCustom = isCustom;
        this.repeatValue = repeatValue;
        this.activityId = activityId;
        this.activityName = activityName;
        this.createdById = createdById;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.assignToId = assignToId;
        this.assingTo = assingTo;
        this.modifedById = modifedById;
        this.modifiedDate = modifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(int isRepeat) {
        this.isRepeat = isRepeat;
    }

    public int getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(int isCustom) {
        this.isCustom = isCustom;
    }

    public String getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(String repeatValue) {
        this.repeatValue = repeatValue;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAssignToId() {
        return assignToId;
    }

    public void setAssignToId(String assignToId) {
        this.assignToId = assignToId;
    }

    public String getAssingTo() {
        return assingTo;
    }

    public void setAssingTo(String assingTo) {
        this.assingTo = assingTo;
    }

    public String getModifedById() {
        return modifedById;
    }

    public void setModifedById(String modifedById) {
        this.modifedById = modifedById;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
