package in.technitab.teamapp.model;


import com.google.gson.annotations.SerializedName;

public class assignee {
    @SerializedName("name")
    private String name;

    public assignee(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
