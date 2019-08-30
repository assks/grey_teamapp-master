package in.technitab.teamapp.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.email)
    EditText email;
    private UserPref userPref;
    private NetConnection connection;
    private Dialog dialog;
    RestApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        userPref = new UserPref(this);
        connection = new NetConnection();
        dialog = new Dialog(this);
        api = APIClient.getClient().create(RestApi.class);
    }

    @OnClick(R.id.submit)
    protected void onSubmit(){

        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if (invalidate(strEmail,strPassword)){
            if (connection.isNetworkAvailable(this)){
                login(strEmail,strPassword);
            }else{

                showMessage(getResources().getString(R.string.internet_not_available));
            }
        }
    }

    private void login(final String strNumber, String strPassword) {
        dialog.showDialog();

        Call<String> call = api.login(strNumber,strPassword,userPref.getFcmToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dialog.dismissDialog();
                if (response.isSuccessful()){
                    try {
                        JSONObject mainObject = new JSONObject(response.body());
                        boolean errorResponse = mainObject.getBoolean("error");
                        String message = mainObject.getString("message");

                        if (!errorResponse){
                            String user_id = mainObject.getString(ConstantVariable.UserPrefVar.USER_ID);
                            int roleId = mainObject.getInt(ConstantVariable.UserPrefVar.ROLE_ID);
                            int accessControlId = mainObject.getInt(ConstantVariable.UserPrefVar.ACCESS_CONTROL_ID);
                            String email = mainObject.getString(ConstantVariable.UserPrefVar.EMAIL);
                            String name = mainObject.getString(ConstantVariable.UserPrefVar.NAME);
                            String image = mainObject.getString(ConstantVariable.UserPrefVar.IMAGE);
                            String relatedTable = mainObject.getString(ConstantVariable.UserPrefVar.RELATED_TABLE);
                            String mobile_number = mainObject.getString(ConstantVariable.UserPrefVar.NUMBER);
                            String role = mainObject.getString(ConstantVariable.UserPrefVar.USER_ROLE);
                            String baseLocation = mainObject.getString(ConstantVariable.UserPrefVar.BASE_OFFICE_LOCATION);
                            userPref.storeData(user_id,accessControlId,roleId,role,relatedTable,email,name,image,mobile_number,baseLocation);
                            startHomeActivity();
                        }
                        showMessage(message);
                    }
                     catch (JSONException e) {
                         Toast.makeText(LoginActivity.this, "You Are Not Authorised ", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                if (t instanceof SocketTimeoutException){
                    showMessage(getResources().getString(R.string.internet_not_available));
                }
            }
        });
    }

    private void startHomeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,RestoreDataActivity.class));
                finish();
            }
        },1000);
    }

    private boolean invalidate(String strEmail, String strPassword) {
        boolean valid = true;

        if (strEmail.isEmpty()){
            showMessage("Email is required");
            valid = false;

        }else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
            showMessage("Email is not valid");
            valid = false;

        }else if (strPassword.isEmpty()){
            showMessage("Password is required");
            valid = false;
        }

        return valid;
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
    }


}
