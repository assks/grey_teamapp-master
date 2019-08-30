package in.technitab.teamapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.Tec;
import in.technitab.teamapp.util.NetworkError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecViewModel extends Observable {
    public ObservableInt tecProgress;
    public ObservableInt tecRecycler;
    public ObservableInt tecLabel;
    public ObservableInt tecStatusFilter;
    public ObservableField<String> messageLabel;

    private List<Tec> tecList;
    private Context context;
    private int visible, invisible;
    private String searchText, filterBy;
    private String userId;

    public TecViewModel(@Nullable Context context, int pageNumber,String userId) {
        this.context = context;
        this.userId = userId;
        this.tecList = new ArrayList<>();
        tecProgress = new ObservableInt(View.GONE);
        tecRecycler = new ObservableInt(View.GONE);
        tecLabel = new ObservableInt(View.VISIBLE);
        tecStatusFilter = new ObservableInt(View.GONE);
        messageLabel = new ObservableField<>(this.context.getResources().getString(R.string.no_history_found));

        initializeViews();
        fetchTecList(pageNumber,userId, "", "");
    }

    private void initializeViews() {
        visible = View.VISIBLE;
        invisible = View.GONE;
        searchText = "";
        filterBy = "";
        viewVisibility(visible, invisible, invisible);
    }

    public void hideShowFilter() {
        filterBy = "";
        tecStatusFilter.set(tecStatusFilter.get() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void setStatus(String status) {
        this.filterBy = status;
    }


    public void fetchFilterTec(int pageNumber, String filterBy) {
        tecProgress.set(visible);
        tecList.clear();
        this.filterBy = filterBy;
        fetchTecList(pageNumber,userId, this.filterBy, searchText);
    }

    public void fetchSearchTec(int pageNumber, String searchText) {
        tecProgress.set(visible);
        tecList.clear();
        this.searchText = searchText;
        fetchTecList(pageNumber,userId, filterBy, this.searchText);
    }

    public void loadMoreTec(int pageNumber) {
        tecProgress.set(visible);
        fetchTecList(pageNumber,userId, filterBy, searchText);
    }


    private void fetchTecList(int pageNumber,String userId, String filterBy, String searchText) {
        RestApi api = APIClient.getClient().create(RestApi.class);

        Call<List<Tec>> call = api.fetchTecList("fetch_user_tec", pageNumber,userId, filterBy, searchText);
        call.enqueue(new Callback<List<Tec>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tec>> call, @NonNull Response<List<Tec>> response) {
                if (response.isSuccessful()) {
                    List<Tec> stringResponse = response.body();
                    tecProgress.set(invisible);
                    if (stringResponse != null) {
                        if (stringResponse.isEmpty()) {
                            if (tecList.isEmpty()) {
                                messageLabel.set("Tec not found");
                                viewVisibility(invisible, invisible, visible);
                            } else {
                                Toast.makeText(context, "No more tec", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            changeTecDataSet(stringResponse);
                            viewVisibility(invisible, visible, invisible);
                        }
                    }
                } else {
                    viewVisibility(invisible, invisible, visible);
                    String errorMessage = NetworkError.unsuccessfulResponseMessage(response.code());
                    messageLabel.set(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tec>> call, @NonNull Throwable t) {
                String errorMessage = NetworkError.getNetworkErrorMessage(t);
                messageLabel.set(errorMessage);
                viewVisibility(invisible, invisible, visible);
            }
        });
    }

    private void viewVisibility(int progress, int recyclerView, int label) {
        tecProgress.set(progress);
        tecLabel.set(label);
        tecRecycler.set(recyclerView);
    }


    private void changeTecDataSet(List<Tec> tecs) {
        tecList.addAll(tecs);
        setChanged();
        notifyObservers();
    }

    public List<Tec> getTecList() {
        return tecList;
    }


    public void reset() {
        context = null;
    }


    public void refreshTec(int pageNumber) {
        tecList.clear();
        fetchTecList(pageNumber,userId,filterBy,searchText);
    }
}
