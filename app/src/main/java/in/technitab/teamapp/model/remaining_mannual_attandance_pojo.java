package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class remaining_mannual_attandance_pojo {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private boolean message;

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public void setManual_attandance(boolean manual_attandance) {
        this.manual_attandance = manual_attandance;
    }

    @SerializedName("manual_attandance")
    private boolean manual_attandance;

    public boolean isError() {
        return error;
    }

    public boolean isMessage() {
        return message;
    }

    public boolean isManual_attandance() {
        return manual_attandance;
    }

    public remaining_mannual_attandance_pojo(boolean error, boolean message, boolean manual_attandance) {
        this.error = error;
        this.message = message;
        this.manual_attandance = manual_attandance;
    }
}
