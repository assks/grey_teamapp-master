package in.technitab.teamapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class userproject {
    @SerializedName("project_id")
    @Expose
    private int getId;
    @SerializedName("project_name")
    @Expose
    private String projectName;

    public int getId() {
        return getId;
    }

    public void setProjectId(int projectId) {
        this.getId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}