package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;


public class TaskSpinner {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private int value;

    public TaskSpinner(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
