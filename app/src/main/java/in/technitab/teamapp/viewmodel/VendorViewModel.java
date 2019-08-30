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
import in.technitab.teamapp.model.Vendor;
import in.technitab.teamapp.util.NetworkError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorViewModel extends Observable {
    public ObservableInt vendorProgress;
    public ObservableInt vendorRecycler;
    public ObservableInt vendorLabel;
    public ObservableField<String> messageLabel;

    private List<Vendor> vendorList;
    private Context context;
    private String searchText;
    private int visible, invisible;

    public VendorViewModel(@Nullable Context context, int pageNumber) {
        this.context = context;
        this.vendorList = new ArrayList<>();
        vendorProgress = new ObservableInt(View.GONE);
        vendorRecycler = new ObservableInt(View.GONE);
        vendorLabel = new ObservableInt(View.VISIBLE);
        messageLabel = new ObservableField<>(context.getResources().getString(R.string.no_history_found));

        initializeViews();
        fetchVendorList(pageNumber,searchText);
    }

    private void initializeViews() {
        visible = View.VISIBLE;
        invisible = View.GONE;
        viewVisibility(visible, invisible, invisible);
        searchText = "";
    }

    private void fetchVendorList(int page,String searchText) {
        RestApi api = APIClient.getClient().create(RestApi.class);

        Call<List<Vendor>> call = api.fetchApproveVendorList("fetch_vendor", page,searchText);
        call.enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vendor>> call, @NonNull Response<List<Vendor>> response) {
                vendorProgress.set(invisible);
                if (response.isSuccessful()) {
                    List<Vendor> stringResponse = response.body();
                    if (stringResponse != null) {
                        if (stringResponse.isEmpty()) {
                            if (vendorList.size() < 1) {
                                messageLabel.set("Vendor not found");
                                viewVisibility(invisible, invisible, visible);
                            } else {
                                Toast.makeText(context, "No more data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            changeVendorDataSet(stringResponse);
                            viewVisibility(invisible, visible, invisible);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Vendor>> call, @NonNull Throwable t) {
                String errorMessage = NetworkError.getNetworkErrorMessage(t);
                messageLabel.set(errorMessage);
                viewVisibility(invisible, invisible, visible);
            }
        });
    }

    private void viewVisibility(int progress, int recyclerView, int label) {
        vendorProgress.set(progress);
        vendorLabel.set(label);
        vendorRecycler.set(recyclerView);

    }


    private void changeVendorDataSet(List<Vendor> vendors) {
        vendorList.addAll(vendors);
        setChanged();
        notifyObservers();
    }

    public void loadMoreVendor(int pageNumber) {
        vendorProgress.set(visible);
        fetchVendorList(pageNumber,searchText);
    }

    public List<Vendor> getVendorList() {
        return vendorList;
    }


    public void reset() {
        context = null;
    }


    public void fetchSearchVendor(int pageNumber, String query) {
        vendorList.clear();
        searchText = query;
        fetchVendorList(pageNumber,searchText);
    }
}
