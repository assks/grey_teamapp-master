package in.technitab.teamapp.view.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.database.ESSdb;
import in.technitab.teamapp.util.CameraPreview;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.Permissions;
import in.technitab.teamapp.util.UserPref;

public class ProjectLocation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int RC_PERMISSIONS = 1;

    @BindView(R.id.camera_preview)
    FrameLayout cameraView;
   /* @BindView(R.id.project_name)
    TextView projectName;*/
    @BindView(R.id.address)
    TextView address;
  /*  @BindView(R.id.spinner)
    Spinner spinner;*/
    @BindView(R.id.select_image)
    TextView selectImage;
    @BindView(R.id.image)
    ImageView image;
   /* @BindView(R.id.state)
    Spinner state;*/
 /*   @BindView(R.id.hotel)
    Spinner hotel;*/
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.type_name)
    TextView typeName;
    @BindView(R.id.name)
    TextView name;
    /*@BindView(R.id.addres)
    TextView addres;*/

   /* @BindView(R.id.vender_name)
    TextView venderName;*/

    private Resources resources;
    private Activity mContext;
    private Dialog dialog;
    private String latitude, longitude, addresss;
    private static final int CAMERA_REQUEST = 1888;
    Unbinder unbinder;
    int projectId = 0;
    private RestApi api;
    private int Location_type = 0;
    protected String states;
    SpinAdapter activityAdapter;
    List<Object> activityList;
    private UserPref userPref;
    private String type_id;
    String temp, id, names,typename;
    String hotel_id;
    String strProjectName = null;
    String vender;
    String In_Travel = "In_Travel";

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    Camera.Parameters parameters;

    private Activity mActivity;
  //  private Resources resources;
  //  Unbinder unbinder;

    private File mFile;
    private int mCameraFaceId = 1, punchStatus = 0, RC_TIME = 2;
    private ESSdb db;
    String latLong = "", punchInTime = "", punchOutTime = "", mAddressOutput;
    Handler timerHandler = new Handler();
    Runnable timerRunnable;
   // private UserPref userPref;
  //  private Dialog dialog;
    private NetConnection connection;
  //  private RestApi api;

    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    public ProjectLocation() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_location);
        ButterKnife.bind(this);
        setToolbar();
        inti();
      //  spinner();
      //  state();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }

    String viewType = "";

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewType.equalsIgnoreCase(getResources().getString(R.string.policies)) ? getResources().getString(R.string.policies) : getResources().getString(R.string.pick_coordinates));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /*private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }*/

    private void createCameraPreview() {
        mCameraPreview = new CameraPreview(mActivity, mCamera, mCameraFaceId);
        cameraView.addView(mCameraPreview);

    }

    private void startCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* if (checkCameraHardware(mActivity)) {
                    mCamera = getCameraFacingId();
                    createCameraPreview();
                }*/
            }
        }, 300);

    }

    public void onResume() {
        super.onResume();

        if (Permissions.hasPermissions(mActivity, PERMISSIONS)) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, RC_PERMISSIONS);
        }
    }

    private Camera getCameraFacingId() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = Camera.open(1);
            mCamera.startPreview();
            parameters = mCamera.getParameters();
        } catch (Exception e) {

        }

        return mCamera;
    }

    @SuppressLint("SetTextI18n")
    private void inti() {
        mContext = this;
        Activity mActivity = this;
        resources = mActivity.getResources();
        api = APIClient.getClient().create(RestApi.class);
        activityList = new ArrayList<>();
        userPref = new UserPref(mContext);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getString("latitude");
            longitude = extras.getString("longitude");
            addresss = extras.getString("address");
             id = extras.getString("id");
             typename = extras.getString("typename");
            String vender="_";
            String projectname ="_";
             vender = extras.getString("vender");
             projectname = extras.getString("projectname");
            typeName.setText(typename);
            name.setText(projectname+vender);

            try {

                Log.d("projectnam", projectname);
                Log.d("pktlatitude", vender);
                Log.d("pktlatitude", typename);
                Log.d("pktlatitude", id);
            } catch (Exception ignored) {
            }
        }

        Log.v("latitude", latitude + longitude);
        address.setText(latitude + longitude);
        address.setTextColor(this.getResources().getColor(R.color.ic_launcher_background));
        address.setText(addresss);

      /*  mActivity = getActivity();
        userPref = new UserPref(mActivity);
        connection = new NetConnection();
        dialog = new Dialog(mActivity);
        api = APIClient.getClient().create(RestApi.class);
        resources = mActivity.getResources();
        ((MainActivity) mActivity).setToolbar(resources.getString(R.string.punch_in_out));
        db = new ESSdb(mActivity);
        mCameraFaceId = resources.getInteger(R.integer.cameraFaceId);*/

    }

   /* private void spinner() {
        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.location_base));
        projectTypeList.add("Select Location Type");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        spinner.setAdapter(projectTypeAdapter);
        spinner.setOnItemSelectedListener(new ProjectLocation() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location_type = position;
                Object item = parent.getItemAtPosition(position);
                type_id = item.toString();
                sd();
                //  Toast.makeText(mContext, "" + Location_type, Toast.LENGTH_SHORT).show();
            }

        });

    }*/

   /* private void sd() {
        if (Location_type == 1) {
            projectName.setVisibility(View.VISIBLE);
            //   Toast.makeText(mContext, "Visiable", Toast.LENGTH_SHORT).show();
        } else {
            projectName.setVisibility(View.GONE);
            //   Toast.makeText(mContext, "invisiable", Toast.LENGTH_SHORT).show();
        }
        if (Location_type == 2) {
            state.setVisibility(View.VISIBLE);
            hotel.setVisibility(View.VISIBLE);

        } else {
            state.setVisibility(View.GONE);
            hotel.setVisibility(View.GONE);
        }
    }*/

   /* @OnClick(R.id.project_name)
    public void onPickProjectName() {

        int projectTypeSpinnerId = Location_type;
        if (projectTypeSpinnerId != 0) {

            Intent intent = new Intent(mContext, ProjectActivity.class);
            intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.PROJECT);
            intent.putExtra(ConstantVariable.MIX_ID.ID, projectTypeSpinnerId);
            this.startActivityForResult(intent, 1);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                projectId = bundle.getInt(ConstantVariable.MIX_ID.ID, 0);
                strProjectName = bundle.getString(mContext.getResources().getString(R.string.project));
                projectName.setText(strProjectName);
                // getProjectActivities(null);
            }
        }*/
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        temp = Base64.encodeToString(b, Base64.DEFAULT);
        // Toast.makeText(mContext, "" + temp, Toast.LENGTH_SHORT).show();
        return temp;

    }

   /* private void state() {

        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.stateArray));
        projectTypeList.add("Select State");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        state.setAdapter(projectTypeAdapter);
        state.setOnItemSelectedListener(new ProjectLocation() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // states += parent;
                Object item = parent.getItemAtPosition(position);
                states = item.toString();
                //  Toast.makeText(mContext, "" + states, Toast.LENGTH_SHORT).show();
                getProjectActivities(states);
            }

        });

    }*/

   /* private void getProjectActivities(String states) {
        // dialog.showDialog();
        Call<ArrayList<fetch_hotal_pojo>> call = api.Fetch_hotal(states);
        call.enqueue(new Callback<ArrayList<fetch_hotal_pojo>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<fetch_hotal_pojo>> call,
                                   @NonNull Response<ArrayList<fetch_hotal_pojo>> response) {
                //    dialog.dismissDialog();
                if (response.isSuccessful()) {
                    ArrayList<fetch_hotal_pojo> fetch_hotal_pojo = response.body();
                    if (fetch_hotal_pojo != null) {

                        //   Toast.makeText(ProjectLocation.this, "" + fetch_hotal_pojo, Toast.LENGTH_SHORT).show();
                        setActivitySpinner(fetch_hotal_pojo);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<fetch_hotal_pojo>> call, @NonNull Throwable t) {
                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }
        });
    }
*/
    /*private void setActivitySpinner(ArrayList<fetch_hotal_pojo> projectActivities) {
        if (activityAdapter != null) {
            hotel.setAdapter(null);
            activityList.clear();
        }

        activityList.add(resources.getString(R.string.select_Hotel));
        activityList.addAll(projectActivities);
        activityAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, activityList);
        hotel.setAdapter(activityAdapter);
        hotel.setOnItemSelectedListener(this);


    }*/


    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

   /* @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (activityList.get(position) instanceof fetch_hotal_pojo) {
            hotel_id = (((fetch_hotal_pojo) activityList.get(position)).getId());
            vender = (((fetch_hotal_pojo) activityList.get(position)).getContactName());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

    @OnClick(R.id.select_image)
    public void onViewClicked() {
/*        Intent i = new Intent(ProjectLocation.this, seletct_location.class);
        startActivity(i);*/
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    
  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        if (requestCode == RC_PERMISSIONS && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED)
                    valid = true;
                else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    valid = false;
                }
            }

            if (valid) {
                showImageCaptureDialog();
            }
        }
    }*/


    @OnClick(R.id.submit)
    public void onViewClick() {

/*        int hotel = 0;

        try {
            hotel = Integer.parseInt(hotel_id);
        } catch (Exception ignored) {
        }
        if (projectId != 0) {
            id = Integer.toString(projectId);
        } else if

        (hotel != 0) {
            id = hotel_id;
        } else {
            id = userPref.getUserId();
        }


        if (strProjectName != null) {
            names = strProjectName;
        } else if (vender != null) {
            names = vender;
        } else {
            names = null;
        }*/
        //sf();

       /* if (type_id.equals(In_Travel)) {
            sf();
            Intent intent = new Intent(ProjectLocation.this, MainActivity.class);
            intent.putExtra(ConstantVariable.Location.LOCATION, latitude);
            intent.putExtra(ConstantVariable.Location.LOCATION_ADDRESS, addresss);
            startActivity(intent);
            finish();
        }
*/
    }

   /* private void sf() {
        //userPref.storeLocation(latitude, longitude, addresss, type_id, id, names);
        Call<StringResponse> call = api.submitlocation(userPref.getUserId(), latitude, longitude, addresss, typename, *//*image,*//* id);
        call.enqueue(new Callback<StringResponse>() {

            @Override
            public void onResponse(@NonNull Call<StringResponse> call,
                                   @NonNull Response<StringResponse> response) {
                //    dialog.dismissDialog();
                if (response.isSuccessful()) {
                    StringResponse StringResponse = response.body();
                    if (StringResponse != null) {
                    //    Toast.makeText(ProjectLocation.this, "" + StringResponse, Toast.LENGTH_SHORT).show();
                        showMessage(StringResponse.getMessage());

                        Intent i = new Intent(ProjectLocation.this, MainActivity.class);
                        startActivity(i);
                    }
                } else {
                    showMessage(NetworkError.unsuccessfulResponseMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StringResponse> call, @NonNull Throwable t) {
//                dialog.dismissDialog();
                showMessage(NetworkError.getNetworkErrorMessage(t));
            }


        });

    }*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}