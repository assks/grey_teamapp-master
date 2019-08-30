package in.technitab.teamapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class versionpojo {
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("version_code")
    @Expose
    private String versionCode;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

}

