package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class Urgency {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



}
