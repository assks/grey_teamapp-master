package in.technitab.teamapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TecResponse implements Parcelable {
    @SerializedName("status")
    private String status;
    @SerializedName("response")
    private ArrayList<Tec> tecArrayList;

    public TecResponse(String status, ArrayList<Tec> tecArrayList) {
        this.status = status;
        this.tecArrayList = tecArrayList;
    }

    protected TecResponse(Parcel in) {
        status = in.readString();
        tecArrayList = in.createTypedArrayList(Tec.CREATOR);
    }

    public static final Creator<TecResponse> CREATOR = new Creator<TecResponse>() {
        @Override
        public TecResponse createFromParcel(Parcel in) {
            return new TecResponse(in);
        }

        @Override
        public TecResponse[] newArray(int size) {
            return new TecResponse[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public ArrayList<Tec> getTecArrayList() {
        return tecArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeTypedList(tecArrayList);
    }
}
