package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripUser {
    @SerializedName("name")
    private String name;
    @SerializedName("user_tecs")
    private ArrayList<Tec> tecUserArrayList;

    public TripUser(String name, ArrayList<Tec> tecUserArrayList) {
        this.name = name;
        this.tecUserArrayList = tecUserArrayList;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Tec> getTecUserArrayList() {
        return tecUserArrayList;
    }
}
