package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.databinding.ActivityGuesthouseBookingRequestBinding;
import in.technitab.teamapp.model.AddResponse;
import in.technitab.teamapp.model.GuesthouseBooking;
import in.technitab.teamapp.model.StringResponse;
import in.technitab.teamapp.util.CustomDate;
import in.technitab.teamapp.util.DateCal;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.NetworkError;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuesthouseBookingRequestActivity extends AppCompatActivity {

    ActivityGuesthouseBookingRequestBinding binding;
    private NetConnection connection;
    private Dialog dialog;
    private RestApi api;
    private UserPref userPref;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guesthouse_booking_request);

        init();
        setToolbar();

    }

    private void init() {
        userPref = new UserPref(this);
        connection = new NetConnection();
        dialog = new Dialog(this);
        resources = getResources();
        api = APIClient.getClient().create(RestApi.class);


        new CustomDate(binding.checkIn, this, null, null);
        new CustomDate(binding.checkOut, this, null, null);
        binding.checkIn.addTextChangedListener(new MyWatcher(binding.checkIn));
        binding.checkOut.addTextChangedListener(new MyWatcher(binding.checkOut));

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.paidViaArray));
        List<Object> mList = new ArrayList<>();
        mList.addAll(list);
        SpinAdapter spinAdapter = new SpinAdapter(this, android.R.layout.simple_list_item_1, mList);
        binding.paidViaSpinner.setAdapter(spinAdapter);
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Guesthouse Booking");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }


        public void onSubmit(View view) {
            String strCheckIn = binding.checkIn.getText().toString().trim();
            String strCheckOut = binding.checkOut.getText().toString().trim();
            String strRefernceNum = binding.referenceNumber.getText().toString().trim();
            String strPaidVia = binding.paidViaSpinner.getSelectedItem().toString();
            boolean isRoomCheck = binding.availabiltyCheck.isChecked();
            boolean isRuleCheck = binding.guesthouseRuleCheck.isChecked();
            String strAmount = binding.totalAmount.getText().toString().trim();
            strAmount = strAmount.replaceAll("[^0-9]","");

            int rentAmount = strAmount.isEmpty()? 0:Integer.parseInt(strAmount);
            GuesthouseBooking guesthouseBooking = new GuesthouseBooking(strCheckIn, strCheckOut,rentAmount, strPaidVia, strRefernceNum, isRoomCheck, isRuleCheck);

            StringResponse validateResponse = guesthouseBooking.validate(guesthouseBooking);
            if (!validateResponse.isError()) {
                guesthouseBooking.setCreatedById(Integer.parseInt(userPref.getUserId()));
                Gson gson = new Gson();
                String bookingJson = gson.toJson(guesthouseBooking);
                if (connection.isNetworkAvailable(this)) {
                    dialog.showDialog();
                    Call<AddResponse> call = api.guesthouseBooking("booking", bookingJson);
                    call.enqueue(new Callback<AddResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<AddResponse> call, @NonNull Response<AddResponse> response) {
                            dialog.dismissDialog();
                            if (response.isSuccessful()) {
                                AddResponse stringResponse = response.body();
                                if (stringResponse != null) {
                                    showMessage(stringResponse.getMessage());
                                    startPreviousActivity();
                                }
                            } else {
                                showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<AddResponse> call, @NonNull Throwable t) {
                            dialog.dismissDialog();
                            showMessage(NetworkError.getNetworkErrorMessage(t));
                        }
                    });
                } else {
                    showMessage(resources.getString(R.string.internet_not_available));
                }


            } else {
                showMessage(validateResponse.getMessage());
            }


        }

    private void startPreviousActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        },500);
    }


    private class MyWatcher implements TextWatcher {

        private View view;

        private MyWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            switch (view.getId()) {

                case R.id.checkIn:
                    String date = binding.checkIn.getText().toString().trim();
                    if (!date.isEmpty())
                        new CustomDate(binding.checkOut, GuesthouseBookingRequestActivity.this, date, null);
                    calculateNights();
                    break;


                case R.id.checkOut:
                    calculateNights();
                    break;

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    private void calculateNights() {
        String strHotelFirst = binding.checkIn.getText().toString().trim();
        String strHotelLast = binding.checkOut.getText().toString().trim();
        if (strHotelFirst.isEmpty() || strHotelLast.isEmpty()) {
            binding.totalAmount.setVisibility(View.GONE);
        } else {
            binding.totalAmount.setVisibility(View.VISIBLE);
            String strTotalAmount = "Rent amount " + String.valueOf(100 * DateCal.getDays(strHotelFirst, strHotelLast));
            binding.totalAmount.setText(strTotalAmount);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
