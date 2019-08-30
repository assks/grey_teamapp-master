package in.technitab.teamapp.view.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.model.StringResponse;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Manual_punchin_Reminder extends AppCompatActivity {

    @BindView(R.id.text)
    TextView TEXT;

    private NetConnection connection;
    private Dialog dialog;
    private RestApi api;
    private UserPref userPref;
    private Resources resources;
    private String mannual,cmonth;
    int year;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogbox);
       //  ButterKnife.bind(this);
        builder = new AlertDialog.Builder(this);
        init();
        onProceed();

    }

    private void init() {
        connection = new NetConnection();
        resources = getResources();
        dialog = new Dialog(this);
        userPref = new UserPref(this);
       /* claimCategoryList = new ArrayList<>();
        travelCategoryList = new ArrayList<>();
       */
        api = APIClient.getClient().create(RestApi.class);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
         cmonth = month_date.format(c.getTime());
         year= c.get(Calendar.YEAR);
    }

    private void onProceed() {

        if (connection.isNetworkAvailable(this)) {
            dialog.showDialog();
            Call<StringResponse> call = api.attendancecheck(userPref.getUserId());
            call.enqueue(new Callback<StringResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<StringResponse> call, @NonNull Response<StringResponse> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        StringResponse assignProject = response.body();
                        if (assignProject != null) {
                            if (!assignProject.isError()) {
                                mannual = assignProject.getManual_attendance();
                                if (mannual == null){
                                    mannual ="0";
                                    dialogbox(mannual);
                                }
                            }dialogbox(mannual);
                            showMessage(assignProject.getManual_attendance());
                        }
                    } else {
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }
                    //TEXT.setText("You have utilized" + mannual + "out of 3  manual attendance allowed per month for the month of" + cmonth +year +".After 3 Manual attendance you have to contact HR/Admin");
                }

                @Override
                public void onFailure(@NonNull Call<StringResponse> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showMessage(resources.getString(R.string.slow_internet_connection));
                    }

                }
            });
        } else {
            showMessage(resources.getString(R.string.internet_not_available));
        }
    }

    private void dialogbox(String mannual) {

        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage("You have utilized " + mannual + " out of 3  manual attendance allowed per month for the month of" + cmonth +" "+year +" . After 3 Manual attendance you have to contact HR/Admin")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    Intent i = new Intent(Manual_punchin_Reminder.this, Manual_punchin_Reminder.class);

                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(Manual_punchin_Reminder.this, ManualPunchActivity.class);
                        startActivity(i);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Intent i = new Intent(Manual_punchin_Reminder.this, seletct_location.class);
                        startActivity(i);
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("You have utilized "+mannual+" out of 3  manual attendance allowed");
        alert.show();


    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }

    public void ok(View view) {
        Intent i = new Intent(Manual_punchin_Reminder.this, ManualPunchActivity.class);
        startActivity(i);
    }
}
