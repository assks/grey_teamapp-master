package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class ProjectActivity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("activity_type_id")
    private int activityTypeId;
    @SerializedName("is_selected")
    private boolean isSelected;

    public ProjectActivity(int id, String activityType, int activityTypeId, boolean isSelected) {
        this.id = id;
        this.name = activityType;
        this.activityTypeId = activityTypeId;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public String getActivityType() {
        return name;
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActivityType(String activityType) {
        this.name = activityType;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
