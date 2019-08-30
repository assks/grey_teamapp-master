package in.technitab.teamapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class fetch_hotal_pojo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("contact_name")
    @Expose
    private String contactName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

}