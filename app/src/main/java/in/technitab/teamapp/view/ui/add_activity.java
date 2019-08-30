package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.database.ESSdb;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.CustomDate;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.SetTime;
import in.technitab.teamapp.util.UserPref;

public class add_activity extends AppCompatActivity {

    @BindView(R.id.activity_name)
    EditText activityname;
    @BindView(R.id.project_name)
    EditText project_name;
    @BindView(R.id.assignees)
    EditText assignees;
    @BindView(R.id.start_date)
    EditText startDate;
    @BindView(R.id.end_date)
    EditText endDate;
    @BindView(R.id.Hours)
    EditText hours;
    @BindView(R.id.rate_per_hour)
    EditText rate;
    @BindView(R.id.Billable_spinner)
    Spinner Billabe;
    @BindView(R.id.description)
    EditText Description;
    @BindView(R.id.submit_task)
    Button submit;

    private Activity mContext;
    private Resources resources;
    private RestApi api;
    private Dialog dialog;
    private NetConnection connection;
    private UserPref userPref;
    private ESSdb db;
    boolean projectTypeSpinnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);
        ButterKnife.bind(this);
        init();
        setupProjectType();

    }

    private void setupProjectType() {
        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.billableArray));
        projectTypeList.add("Select Billabe Type");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        Billabe.setAdapter(projectTypeAdapter);
        //  Billabe.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }

    private void init() {
        mContext = this;
        if (mContext != null) {
            resources = mContext.getResources();
            dialog = new Dialog(this);
            userPref = new UserPref(this);
            db = new ESSdb(this);
            api = APIClient.getClient().create(RestApi.class);
            new CustomDate(startDate, this, null, null);
            new CustomDate(endDate, this, null, null);
            new SetTime(hours, this);


        }
    }

    @OnClick(R.id.project_name)
    public void onPickProjectName() {
        Intent intent = new Intent(mContext, ProjectActivity.class);
        intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.PROJECT);
        this.startActivityForResult(intent, 1);
    }

    @OnClick(R.id.assignees)
    public void onassignes() {
        Intent intent = new Intent(mContext, assigneelist.class);
        intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.PROJECT);
        this.startActivityForResult(intent, 1);
    }

   /* private void fetchGoldLot() {
        dialog.showDialog();
        Call<ProductData> call = api.fetchGoldLot();
        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(@NonNull Call<ProductData> call, @NonNull Response<ProductData> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()) {

                    ProductData data = response.body();
                    if (data != null) {
                        if (!mCategotyList.isEmpty()) {
                            mCategotyList.clear();
                            mGoldLotList.clear();
                        }

                        setLotSpinner(data.getGoldLotArrayList());
                        setCategorySpinner(data.getCategoryArrayList());
                    }
                } else {
                    showToast(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductData> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showToast(NetworkError.getNetworkErrorMessage(t));
            }
        });


    }*/


    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}
