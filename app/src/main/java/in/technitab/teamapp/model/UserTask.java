package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class UserTask {

@SerializedName("task_deliverables")
private String task_deliverables;
@SerializedName("deadlines")
private String deadlines;

@SerializedName("pname")
private String priority;

@SerializedName("tname")
private String status;


@SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserTask(String task_deliverables, String name) {
        this.task_deliverables = task_deliverables;
        this.name = name;
    }

    public String getTask_deliverables() {
        return task_deliverables;
    }

    public void setTask_deliverables(String task_deliverables) {
        this.task_deliverables = task_deliverables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(String deadlines) {
        this.deadlines = deadlines;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

