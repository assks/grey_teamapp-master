package in.technitab.teamapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.GuesthouseBooking;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GueshouseViewModel extends Observable {
    public ObservableInt guesthouseProgress;
    public ObservableInt guesthouseRecycler;
    public ObservableInt guesthouseLabel;
    public ObservableField<String> messageLabel;
    private String userId;
    private List<GuesthouseBooking> guesthouseList;
    private Context context;

    public GueshouseViewModel(@Nullable Context context,String userId) {
        this.context = context;
        this.userId = userId;
        this.guesthouseList = new ArrayList<>();
        guesthouseProgress = new ObservableInt(View.GONE);
        guesthouseRecycler = new ObservableInt(View.GONE);
        guesthouseLabel = new ObservableInt(View.VISIBLE);
        messageLabel = new ObservableField<>(context.getResources().getString(R.string.no_history_found));

        initializeViews();
        fetchGueshouseList();
    }

    private void initializeViews() {
        guesthouseLabel.set(View.GONE);
        guesthouseRecycler.set(View.GONE);
        guesthouseProgress.set(View.VISIBLE);
    }

    private void fetchGueshouseList() {
        RestApi api = APIClient.getClient().create(RestApi.class);

        Call<List<GuesthouseBooking>> call = api.fetchBookingList("self_booking", userId);
        call.enqueue(new Callback<List<GuesthouseBooking>>() {
            @Override
            public void onResponse(@NonNull Call<List<GuesthouseBooking>> call, @NonNull Response<List<GuesthouseBooking>> response) {
                if (response.isSuccessful()) {
                    List<GuesthouseBooking> stringResponse = response.body();
                    if (stringResponse != null) {
                        changePeopleDataSet(stringResponse);
                        guesthouseProgress.set(View.GONE);
                        guesthouseLabel.set(View.GONE);
                        guesthouseRecycler.set(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<GuesthouseBooking>> call, @NonNull Throwable t) {

                if (t instanceof SocketTimeoutException) {
                    messageLabel.set(context.getString(R.string.problem_to_connect));
                }
                guesthouseProgress.set(View.GONE);
                guesthouseLabel.set(View.VISIBLE);
                guesthouseRecycler.set(View.VISIBLE);
            }
        });
    }

    private void changePeopleDataSet(List<GuesthouseBooking> guesthouses) {
        guesthouseList.addAll(guesthouses);
        setChanged();
        notifyObservers();
    }

    public void refreshList(){
        guesthouseList.clear();
        fetchGueshouseList();
    }

    public List<GuesthouseBooking> getGuesthouseList() {
        return guesthouseList;
    }


    public void reset() {
        context = null;
    }

}
