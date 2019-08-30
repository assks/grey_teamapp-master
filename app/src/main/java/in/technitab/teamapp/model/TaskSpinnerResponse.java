package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskSpinnerResponse {
    @SerializedName("importance")
    private List<TaskSpinner> importance;
    @SerializedName("urgency")
    private List<TaskSpinner> urgency;
    @SerializedName("status")
    private List<TaskSpinner> status;
    @SerializedName("priority")
    private List<TaskSpinner> priority;

    public TaskSpinnerResponse(List<TaskSpinner> importance, List<TaskSpinner> urgency, List<TaskSpinner> status, List<TaskSpinner> priority) {
        this.importance = importance;
        this.urgency = urgency;
        this.status = status;
        this.priority = priority;
    }

    public List<TaskSpinner> getImportance() {
        return importance;
    }

    public List<TaskSpinner> getUrgency() {
        return urgency;
    }

    public List<TaskSpinner> getStatus() {
        return status;
    }

    public List<TaskSpinner> getPriority() {
        return priority;
    }
}
