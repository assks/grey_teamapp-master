package in.technitab.teamapp.view.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.APIClient;
import in.technitab.teamapp.api.RestApi;
import in.technitab.teamapp.database.ESSdb;
import in.technitab.teamapp.model.StringResponse;
import in.technitab.teamapp.util.CameraPreview;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.MyNotification;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.Permissions;
import in.technitab.teamapp.util.UserPref;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.technitab.teamapp.util.DateCal.getDate;
import static in.technitab.teamapp.util.DateCal.getDateTime;
import static in.technitab.teamapp.util.DateCal.getHMFromData;
import static in.technitab.teamapp.util.DateCal.getHMSFromDates;
import static in.technitab.teamapp.util.DateCal.getHours;


public class PunchInFragment extends Fragment {

    @BindView(R.id.camera_preview)
    FrameLayout cameraView;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.location)
    TextView siteLocation;
    @BindView(R.id.punch_in)
    Button punchIn;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.displayLayout)
    RelativeLayout displayLayout;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    Camera.Parameters parameters;

    private Activity mActivity;
    private Resources resources;
    Unbinder unbinder;

    private File mFile;
    private int mCameraFaceId = 1, punchStatu, RC_TIME = 2;
    private ESSdb db;
    String latLong = "", punchInTime = "", punchOutTime = "", mAddressOutput;
    Handler timerHandler = new Handler();
    Runnable timerRunnable;
    private UserPref userPref;
    private Dialog dialog;
    private NetConnection connection;
    private RestApi api;

    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private int RC_PERMISSIONS = 1;

    private void initialiseTimer() {
        timerRunnable = new Runnable() {

            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String endDate = getDateTime(System.currentTimeMillis());
                String diff = getHMSFromDates(punchInTime, endDate);
                if (timer != null)
                    timer.setText(diff);
                timerHandler.post(this);
            }
        };
    }

    private Drawable pickDrawable(int color) {
        Drawable drawable = resources.getDrawable(R.drawable.punch_in_out_bg);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public PunchInFragment() {
        setHasOptionsMenu(true);
    }


    private void onProceed() {

        if (connection.isNetworkAvailable(mActivity)) {
            dialog.showDialog();
            Call<StringResponse> call = api.FetchAttandanceStatus(userPref.getUserId());
            call.enqueue(new Callback<StringResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<StringResponse> call, @NonNull Response<StringResponse> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        StringResponse assignProject = response.body();
                        if (assignProject != null) {
                            if (!assignProject.isError()) {
                                //  Toast.makeText(mActivity, "status__" + assignProject.getStatus(), Toast.LENGTH_SHORT).show();
                                updateButtonUI();
                                punchStatu = assignProject.getStatus();

                            }
                            showMessage(assignProject.getMessage());
                        }
                    } else {
                        showMessage(resources.getString(R.string.problem_to_connect));
                    }
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


    private void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_punch_in, container, false);
        unbinder = ButterKnife.bind(this, view);


        initialise();

        onProceed();
        Bundle userLocationBundle = mActivity.getIntent().getExtras();
        if (userLocationBundle != null) {
            mAddressOutput = userLocationBundle.getString(ConstantVariable.Location.LOCATION_ADDRESS);
            latLong = userLocationBundle.getString(ConstantVariable.Location.LOCATION);
            siteLocation.setText(mAddressOutput);
        }

        return view;
    }

    private void updateButtonUI(/*int punchStatus*/) {

        if (mAddressOutput.equals("Service not available")) {
            punchIn.setVisibility(View.GONE);
            timer.setVisibility(View.GONE);

        }

        switch (punchStatu) {
            case 0:
                punchIn.setText(resources.getString(R.string.in));
                punchIn.setVisibility(View.VISIBLE);
                punchIn.setBackground(pickDrawable(Color.parseColor("#17931d")));

                break;

            case 1:
                punchInTime = db.getPunchInTime(getDate(System.currentTimeMillis()));
                initialiseTimer();
                timerHandler.post(timerRunnable);

                punchIn.setText(resources.getString(R.string.out));
                punchIn.setVisibility(View.VISIBLE);
                punchIn.setBackground(pickDrawable(Color.parseColor("#FFDF3D28")));
                 timer.setVisibility(View.VISIBLE);
                break;

            case 2:
                punchIn.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                break;

        }
    }

    String types = null;

    private void initialise() {
        mActivity = getActivity();
        userPref = new UserPref(mActivity);
        connection = new NetConnection();
        dialog = new Dialog(mActivity);
        api = APIClient.getClient().create(RestApi.class);
        resources = mActivity.getResources();
        ((MainActivity) mActivity).setToolbar(resources.getString(R.string.punch_in_out));
        db = new ESSdb(mActivity);
        mCameraFaceId = resources.getInteger(R.integer.cameraFaceId);
        punchIn.setVisibility(View.GONE);



        types = null;
        String type = userPref.gettype();
        Log.d("type", type);
        if (type.equals("In_Travel")) {
            types = "Travel";
            // Toast.makeText(mActivity, "dfd", Toast.LENGTH_SHORT).show();
        } else if (type.equals("Base_Location")) {
            types = "Base";
        } else if (type.equals("Site_Location")) {
            types = "Site";
        } else if (type.equals("At_Hotel")) {
            types = "Hotel";
        }


    }


    private String getCurrentTime() {

        return DateFormat.getTimeInstance(DateFormat.SHORT).format(System.currentTimeMillis());
    }

    /*private String*/

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(resources.getString(R.string.time));
        builder.setMessage(resources.getString(R.string.time_dialog_message));
        builder.setCancelable(false);
        builder.setPositiveButton(resources.getString(R.string.enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), RC_TIME);
            }
        });
        builder.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void createCameraPreview() {
        mCameraPreview = new CameraPreview(mActivity, mCamera, mCameraFaceId);
        cameraView.addView(mCameraPreview);

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
            //
        }

        return mCamera;
    }

    public static boolean isTimeAutomatic(Context c) {
        return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_TIME) {
            time.setText(getCurrentTime());
        }
    }

    @Override
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
                startCamera();
            }
        }
    }

    private void startCamera() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkCameraHardware(mActivity)) {
                    mCamera = getCameraFacingId();
                    createCameraPreview();
                }
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
        onProceed();
        // punchStatus = db.getPunchStatus();
       // updateButtonUI(/*punchStatus*/);
        if (isTimeAutomatic(mActivity)) {
            time.setText(getCurrentTime());
        } else {
            showTimeDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        if (mCameraPreview != null) {
            cameraView.removeView(mCameraPreview);
            mCameraPreview = null;
        }
    }

    @OnClick(R.id.punch_in)
    protected void onPunchIn() {
        mCamera.takePicture(null, null, mPicture);
        //  Toast.makeText(mActivity, "" + mCamera, Toast.LENGTH_SHORT).show();


    }


    @SuppressLint("DefaultLocale")
    private void punchOutPerform(final int punchStatus) {


        timerHandler.removeCallbacks(timerRunnable);
        punchOutTime = getDateTime(System.currentTimeMillis());
        final String spentHours = getHMFromData(mActivity, punchInTime, punchOutTime);
        final String attendance = getAttendanceValue(spentHours, punchInTime, punchOutTime);

        if (connection.isNetworkAvailable(mActivity)) {
            dialog.showDialog();

            RequestBody employeeId = RequestBody.create(MediaType.parse("text/plain"), userPref.getUserId());
            RequestBody attendanceValue = RequestBody.create(MediaType.parse("text/plain"), attendance);
            RequestBody punchInLocation = RequestBody.create(MediaType.parse("text/plain"), latLong);
            final RequestBody punchInStatus = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(punchStatus));
            final RequestBody spentTime = RequestBody.create(MediaType.parse("text/plain"), spentHours);
            RequestBody punchInAddress = RequestBody.create(MediaType.parse("text/plain"), mAddressOutput);
            RequestBody InTime = RequestBody.create(MediaType.parse("text/plain"), punchOutTime);
            RequestBody rbAction = RequestBody.create(MediaType.parse("text/plain"), "punch_out");
            RequestBody outlocationtype = RequestBody.create(MediaType.parse("text/plain"), types/*userPref.gettype()*/);
            RequestBody outlocationvalue = RequestBody.create(MediaType.parse("text/plain"), userPref.getids());

            Map<String, RequestBody> myMap = new HashMap<>();
            myMap.put(ConstantVariable.UserPrefVar.USER_ID, employeeId);
            myMap.put(ConstantVariable.Tbl_Attendance.OUT_LOCATION, punchInLocation);
            myMap.put(ConstantVariable.Tbl_Attendance.PUNCH_STATUS, punchInStatus);
            myMap.put(ConstantVariable.Tbl_Attendance.OUT_ADDRESS, punchInAddress);
            myMap.put(ConstantVariable.Tbl_Attendance.SPENT_TIME, spentTime);
            myMap.put(ConstantVariable.Tbl_Attendance.PUNCH_OUT, InTime);
            myMap.put(ConstantVariable.Tbl_Attendance.ATTENDANCE, attendanceValue);
            myMap.put(ConstantVariable.MIX_ID.ACTION, rbAction);
            myMap.put("out_location_type", outlocationtype);
            myMap.put("out_location_value", outlocationvalue);

            Log.d("out_location_value", String.valueOf(outlocationvalue));

            if (mFile != null) {
                String extension = mFile.getName().substring(mFile.getName().lastIndexOf(".") + 1);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/" + extension), mFile);
                myMap.put("punchout_img\"; filename=\"" + mFile.getName(), fileBody);
            }


            Call<StringResponse> call = api.addPunchOut(myMap);
            call.enqueue(new Callback<StringResponse>() {


                @Override
                public void onResponse(@NonNull Call<StringResponse> call, @NonNull Response<StringResponse> response) {

                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        StringResponse stringResponse = response.body();
                        if (!Objects.requireNonNull(stringResponse).isError()) {
                            addPunchOutLocalDatabase(attendance, punchStatus, spentHours);
                            updateButtonUI(/*punchStatus*/);
                            //  addNotification(1);

                            MyNotification notification = new MyNotification(mActivity);
                            notification.displayNotification((ConstantVariable.punchout), userPref.getName(), MainActivity.class);
                        }
                    } else {
                        showToast(resources.getString(R.string.problem_to_connect));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StringResponse> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showToast(mActivity.getResources().getString(R.string.slow_internet_connection));
                    }
                }
            });

        } else {
            showToast(mActivity.getResources().getString(R.string.internet_not_available));
        }

    }

    private String getAttendanceValue(String spentHours, String punchInTime, String punchOutTime) {
        String attendance;
        String[] parts = spentHours.split(":");
        int hours = Integer.parseInt(parts[0]);
        if (hours > 4) {
            attendance = resources.getString(R.string.full_day_present);
        } else {
            int in = getHours(punchInTime);

            if (in < 13) {
                attendance = resources.getString(R.string.first_half);
            } else {
                attendance = resources.getString(R.string.second_half);
            }

        }
        return attendance;
    }

    private void addPunchOutLocalDatabase(String attendance, int punchStatus, String spentHours) {
        boolean result = db.addPunchOUT(punchOutTime, attendance, mFile.getPath(), latLong, mAddressOutput, "", spentHours, punchStatus);
        if (result) {
            Toast.makeText(mActivity, "Successfully Saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void punchInPerform(final String present, final int punchStatus) {
        timerHandler.postDelayed(timerRunnable, 0);
        punchInTime = getDateTime(System.currentTimeMillis());

        if (connection.isNetworkAvailable(mActivity)) {
            dialog.showDialog();
            RequestBody employeeId = RequestBody.create(MediaType.parse("text/plain"), userPref.getUserId());
            RequestBody punchInLocation = RequestBody.create(MediaType.parse("text/plain"), latLong);
            RequestBody attendance = RequestBody.create(MediaType.parse("text/plain"), present);
            final RequestBody punchInStatus = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(punchStatus));
            RequestBody punchInAddress = RequestBody.create(MediaType.parse("text/plain"), mAddressOutput);
            RequestBody InTime = RequestBody.create(MediaType.parse("text/plain"), punchInTime);
            RequestBody rbAction = RequestBody.create(MediaType.parse("text/plain"), "punch_in");
            RequestBody inlocationtype = RequestBody.create(MediaType.parse("text/plain"), types/*userPref.gettype()*/);
            RequestBody inlocationvalue = RequestBody.create(MediaType.parse("text/plain"), userPref.getids());
            Map<String, RequestBody> myMap = new HashMap<>();
            myMap.put(ConstantVariable.UserPrefVar.USER_ID, employeeId);
            myMap.put(ConstantVariable.Tbl_Attendance.IN_LOCATION, punchInLocation);
            myMap.put(ConstantVariable.Tbl_Attendance.ATTENDANCE, attendance);
            myMap.put(ConstantVariable.Tbl_Attendance.PUNCH_STATUS, punchInStatus);
            myMap.put(ConstantVariable.Tbl_Attendance.IN_ADDRESS, punchInAddress);
            myMap.put(ConstantVariable.Tbl_Attendance.PUNCH_IN, InTime);
            myMap.put(ConstantVariable.MIX_ID.ACTION, rbAction);
            myMap.put("in_location_type", inlocationtype);
            myMap.put("in_location_value", inlocationvalue);


            if (mFile != null) {
                String extension = mFile.getName().substring(mFile.getName().lastIndexOf(".") + 1);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/" + extension), mFile);
                myMap.put("punchin_img\"; filename=\"" + mFile.getName(), fileBody);
            }

            Call<StringResponse> call = api.addPunchIn(myMap);
            call.enqueue(new Callback<StringResponse>() {
                @Override
                public void onResponse(@NonNull Call<StringResponse> call, @NonNull Response<StringResponse> response) {
                    dialog.dismissDialog();
                    if (response.isSuccessful()) {
                        StringResponse stringResponse = response.body();
                        if (stringResponse != null) {
                            if (!stringResponse.isError()) {
                                addPunchInLocalDatabase(present, punchStatus);
                                updateButtonUI(/*punchStatus*/);
                                //  addNotification(2);

                                MyNotification notification = new MyNotification(mActivity);
                                notification.displayNotification((ConstantVariable.punchin), userPref.getName(), MainActivity.class);

                            } else {
                                showToast(stringResponse.getMessage());
                            }
                        }
                    } else {
                        showToast(resources.getString(R.string.problem_to_connect));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StringResponse> call, @NonNull Throwable t) {
                    dialog.dismissDialog();
                    if (t instanceof SocketTimeoutException) {
                        showToast(mActivity.getResources().getString(R.string.slow_internet_connection));
                    }
                }
            });
        } else {
            showToast(mActivity.getResources().getString(R.string.internet_not_available));
        }
    }

    private void addPunchInLocalDatabase(String present, int punchStatus) {
        boolean result = db.addPunchIN(present, punchInTime, mFile.getPath(), latLong, mAddressOutput, punchStatus);
        if (result) {
            Toast.makeText(mActivity, "Successfully Saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            mFile = pictureFile;
            if (pictureFile == null) {
                return;
            }

            try {
                Bitmap bitmapImage = getRotationImage(data);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
               // Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 250, 200, true);

                bitmapImage/*scaled*/.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] b = baos.toByteArray();
                FileOutputStream fos = new FileOutputStream(pictureFile);

                fos.write(b);

                fos.close();

                mCamera.startPreview();

                String present = resources.getString(R.string.full_day_present);

                if (punchStatu == 1 && punchIn.getText().toString().equalsIgnoreCase(resources.getString(R.string.out))) {
                    punchStatu = 2;
                    punchOutPerform(punchStatu);
                } else if (punchStatu == 0) {
                    punchStatu = 1;
                    punchInPerform(present, punchStatu);
                }


            } catch (IOException e) {
                Toast.makeText(mActivity, "File Not exp " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Ess");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }


    private Bitmap getRotationImage(byte[] data) {

        Matrix matrix = new Matrix();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);
            matrix.postConcat(matrixMirrorY);
        }

        matrix.postRotate(270);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void showToast(String text) {
        Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_attendance_leave, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_leave);
        menuItem.setIcon(resources.getDrawable(R.drawable.ic_add_timesheet));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_leave) {
            startActivity(new Intent(getActivity(), Manual_punchin_Reminder.class));
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {

        startActivity(new Intent(getActivity(), MainActivity.class));

    }


}
