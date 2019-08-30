package in.technitab.teamapp.model;

import com.google.gson.annotations.SerializedName;

public class Policy {
    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("base_url")
    private String baseUrl;
    @SerializedName("path")
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @SerializedName("file_url")
    private String fileUrl;
    @SerializedName("download_file_url")
    private String downloadFileUrl;

    public Policy(boolean error, String message, String baseUrl, String fileUrl, String downloadFileUrl,String path) {
        this.error = error;
        this.path=path;
        this.message = message;
        this.baseUrl = baseUrl;
        this.fileUrl = fileUrl;
        this.downloadFileUrl = downloadFileUrl;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    public String getFileUrl() {
        return fileUrl;
    }

    public String getDownloadFileUrl() {
        return downloadFileUrl;
    }
}
