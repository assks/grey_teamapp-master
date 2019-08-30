package in.technitab.teamapp.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class fetch_coordinate {

    @SerializedName("base_img")
    @Expose
    private String baseImg;
    @SerializedName("base_lat")
    @Expose
    private String baseLat;
    @SerializedName("base_long")
    @Expose
    private String baseLong;
    @SerializedName("base_address")
    @Expose
    private String baseAddress;

    public String getBaseImg() {
        return baseImg;
    }

    public void setBaseImg(String baseImg) {
        this.baseImg = baseImg;
    }

    public String getBaseLat() {
        return baseLat;
    }

    public void setBaseLat(String baseLat) {
        this.baseLat = baseLat;
    }

    public String getBaseLong() {
        return baseLong;
    }

    public void setBaseLong(String baseLong) {
        this.baseLong = baseLong;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }


}