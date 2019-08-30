package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class StringResponse {
    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("manual_attendance")
    private String manual_attendance;

    @SerializedName("version")
    private int version;

    @SerializedName("version_code")
    private int version_code;

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    @SerializedName("status")
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @SerializedName("base_lat")
    private String base_lat;

    @SerializedName("base_long")
    private String base_long;

    @SerializedName("base_address")
    private String base_address;





    public StringResponse(boolean error, String message) {
        this.error = error;
        this.message = message;

    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getManual_attendance() {
        return manual_attendance;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getBase_lat() {
        return base_lat;
    }

    public void setBase_lat(String base_lat) {
        this.base_lat = base_lat;
    }

    public String getBase_long() {
        return base_long;
    }

    public void setBase_long(String base_long) {
        this.base_long = base_long;
    }

    public String getBase_address() {
        return base_address;
    }

    public void setBase_address(String base_address) {
        this.base_address = base_address;
    }
}
