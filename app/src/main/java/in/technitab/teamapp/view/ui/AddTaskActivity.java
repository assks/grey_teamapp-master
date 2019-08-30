package in.technitab.teamapp.view.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.date)
    EditText date;
    @BindView(R.id.time)
    EditText time;
    @BindView(R.id.submit)
    Button submit;
    private String TAG = AddTaskActivity.class.getSimpleName();
    private UserPref userPref;
    private NetConnection connection;

    private Dialog dialog;
    private RestApi api;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        init();
        setToolbar();
    }

    private void init() {
        userPref = new UserPref(this);
        connection = new NetConnection();
        dialog = new Dialog(this);
        api = APIClient.getClient().create(RestApi.class);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this, year, month, day);
        timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, hour, minute,false);
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(R.string.add_task);
            actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

    }
    @OnClick(R.id.date)
    protected void onDatePicker() {
        datePickerDialog.show();
    }

    @OnClick(R.id.submit)
    protected void onSubmit() {
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        date.setText(getResources().getString(R.string.date_value,year,getValue(month+1),getValue(dayOfMonth)));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        time.setText(getResources().getString(R.string.time_text, getValue(hourOfDay), getValue(minute)));
    }

    private String getValue(int value) {
        return value < 10 ? "0" + value : "" + value;
    }
}
