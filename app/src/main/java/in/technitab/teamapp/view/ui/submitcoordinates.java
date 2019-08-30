package in.technitab.teamapp.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.technitab.teamapp.BuildConfig;
import in.technitab.teamapp.R;
import in.technitab.teamapp.adapter.SpinAdapter;
import in.technitab.teamapp.database.ESSdb;
import in.technitab.teamapp.networking.ApiConfig;
import in.technitab.teamapp.networking.AppConfig;
import in.technitab.teamapp.networking.ServerResponse;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.Dialog;
import in.technitab.teamapp.util.MyNotification;
import in.technitab.teamapp.util.NetConnection;
import in.technitab.teamapp.util.UserPref;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.technitab.teamapp.util.DateCal.getDate;
import static in.technitab.teamapp.util.DateCal.getDateTime;

public class submitcoordinates extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static String timeStamp;
    @BindView(R.id.preview)
    ImageView preview;
    @BindView(R.id.pickImage)
    TextView pickImage;
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.bookingMode)
    TextView bookingMode;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.headerLayout)
    LinearLayout headerLayout;
    @BindView(R.id.view)
    View view;
    /*    @BindView(R.id.relative)
        RelativeLayout relative;*/
/*    @BindView(R.id.attachment)
    TextView attachment;*/
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.purpose)
    Spinner purpose;
    @BindView(R.id.accuracy)
    TextView accuracytext;
    private Activity mContext;
    private Uri mMediaUri;
    private ESSdb db;
    private static final int CAMERA_PIC_REQUEST = 1111;

    private static final String TAG = submitcoordinates.class.getSimpleName();

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private UserPref userPref;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    ProgressDialog pDialog;
    Resources resources;
    int purpose_id ,upSpeed;
    private File mFile = null;
    String location;
    String time1;
    private NetConnection connection;
    private Dialog dialog;
    String punchOutTime;
    private String postPath, latitude, longitude, addresss, id, typename, vender, projectname, accuracy;
    String types, type_id, imeiNumber;
    Paint paint;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        ButterKnife.bind(this);


        mContext = this;
        userPref = new UserPref(mContext);
        dialog = new Dialog(this);
        connection = new NetConnection();
        db = new ESSdb(mContext);
        //initDialog();
        InternetSpeed();
        String a = userPref.getUserId();
        Activity mActivity = this;
        resources = mActivity.getResources();
        punchOutTime = getDateTime(System.currentTimeMillis());
        getdatafromsqlite();
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                latitude = extras.getString("latitude");
                longitude = extras.getString("longitude");
                addresss = extras.getString("address");
                id = extras.getString("id");
                accuracy = extras.getString("accuracy");
                typename = extras.getString("typename");
                vender = extras.getString("vender");
                projectname = extras.getString("projectname");
            } else {
                Toast.makeText(mActivity, "Data Not Found", Toast.LENGTH_SHORT).show();

            }

            try {

                Log.d("projectnam", projectname);
                Log.d("pktlatitude", vender);
                Log.d("pktlatitude", typename);
                Log.d("pktlatitude", id);
                Log.d("pktlatitude", latitude);
                Log.d("pktlatitude", addresss);
                Log.d("pktlatitude", vender);
                Log.d("pktlatituded", accuracy);
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        if (projectname != null || vender != null) {
            description.setVisibility(View.VISIBLE);
            description.setText(projectname + vender);
        }

        if (postPath != null) {
            //  attachment.setText(postPath);
            Log.d("postpath", postPath);
        }
        try {
            StringTokenizer tokens = new StringTokenizer(accuracy, "\\ ");
            String b = tokens.nextToken();
            String second = tokens.nextToken();
            Log.d("first", b);
            Log.d("first", second);
            double value = Double.parseDouble(second);

            double meters = value / 3.2808;


            String numberAsString = Double.toString(meters);

            if (second.length() > 3) {
                numberAsString = second.substring(0, 6);
            }  // mtr = second;


            bookingMode.setText(addresss);
            if (projectname != null) {
                description.setText(projectname /*+ vender*/);
            } else if (vender != null) {
                description.setText(vender);
            }
            String text = "Accuracy :  ";
            String meters1 = " meters";
            type.setText(typename);
            accuracytext.setText(text + numberAsString + meters1);
        } catch (Exception ignored) {
            Toast.makeText(mActivity, "Data not Available", Toast.LENGTH_SHORT).show();
        }

        switch (typename) {
            case "I am in Travel":
                types = "Travel";
                break;
            case "I am at Base Location":
                types = "Base";
               // Toast.makeText(mActivity, "dfdd", Toast.LENGTH_SHORT).show();
                break;
            case "I am at Site Location":
                types = "Site";
                break;
            case "I am at Hotel":
                types = "Hotel";
                break;
        }




      /*  StringTokenizer tokens = new StringTokenizer(accuracy, "\\:");
       String km = tokens.nextToken();
        String second = tokens.nextToken();
        Log.d("first", km);
        Log.d("first", second);
        Toast.makeText(mActivity, "accuracy"+second, Toast.LENGTH_SHORT).show();*/
        // attachment.setText(mediaPath);

        setToolbar();
        spinner();

        getCurrentTime();
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            }
            imeiNumber = telephonyManager.getDeviceId();
            Log.d("emeino", imeiNumber);
        } catch (Exception ignored) {
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(submitcoordinates.this, seletct_location.class);
        startActivity(i);
        return true;
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resources.getString(R.string.submit_location));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void spinner() {
        List<Object> projectTypeList = new ArrayList<>();
        List<String> list = Arrays.asList(resources.getStringArray(R.array.purpose));
        projectTypeList.add("- -Select purpose- -");
        projectTypeList.addAll(list);
        SpinAdapter projectTypeAdapter = new SpinAdapter(mContext, android.R.layout.simple_list_item_1, projectTypeList);
        purpose.setAdapter(projectTypeAdapter);
        purpose.setOnItemSelectedListener(new ProjectLocation() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                purpose_id = position;
                Log.d("purpose_id", String.valueOf(purpose_id));
                Object item = parent.getItemAtPosition(position);
                type_id = item.toString();
                Log.d("type", type_id);
                try {
                    if (purpose_id == 0 || addresss.equals("Service not available")) {
                        upload.setVisibility(View.GONE);
                    } else if (purpose_id == 1 || purpose_id == 2) {
                        upload.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ignored) {
                }


            }
        });
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {


                if (data != null) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    Log.d("cursor", String.valueOf(cursor));
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    String path = mediaPath;
                    setupFile(path);
                    Bitmap b = BitmapFactory.decodeFile(mediaPath);

                    Bitmap scaled = Bitmap.createScaledBitmap(b, 80, 100, true);

                    preview.setImageBitmap(scaled);
                    // preview.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                    cursor.close();


                    postPath = mediaPath;

                }


            } else if (requestCode == CAMERA_PIC_REQUEST) {
                if (Build.VERSION.SDK_INT > 21) {
                    // String path=mImageFileLocation;
                    //  setupFile( path);
                    Glide
                            .with(mContext)
                            .load(mImageFileLocation)
                            .apply(new RequestOptions().override(200, 200))
                            .into(preview);

                    preview.buildDrawingCache();
                    Bitmap bmap = preview.getDrawingCache();

                    // Glide.with(this).load(mImageFileLocation).into(preview);
                    postPath = mImageFileLocation;

                } else {
                    Glide.with(this).load(fileUri).into(preview);
                    postPath = fileUri.getPath();

                }

            }

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }

    private void setupFile(String path) {
        mFile = new File(path);
        if (mFile.exists()) {
            long fileSize = mFile.length() / 1024;
            if (fileSize > 2048) {
                showMessage("File Size error");
                pickImage.setTextColor(Color.RED);
                pickImage.setText(resources.getString(R.string.invalid_file_size));
                mFile = null;
            }
        } else {
            showMessage("Unknown file. Please move file internal storage");
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

    }


    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            // We give some instruction to the intent to save the image
            File photoFile = null;

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("Exception error in generating the file");
                e.printStackTrace();
            }

            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in
            // AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Logger.getAnonymousLogger().info("Calling the camera App by intent");

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }


    }

    File createImageFile() throws IOException {
        Logger.getAnonymousLogger().info("Generating the image - method started");


        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;
        // Here we specify the environment location and the exact path where we want to save the so-created file
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Logger.getAnonymousLogger().info("Storage directory set");

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set");

        mImageFileLocation = image.getAbsolutePath();


        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes

        outState.putParcelable("file_uri", fileUri);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("file_uri");


    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + ".png");
        } else {
            return null;
        }

        return mediaFile;

    }

    String present = "present full day", path;

    private void getCurrentTime() {

        java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(System.currentTimeMillis());
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void   InternetSpeed() {
         ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
         upSpeed = nc.getLinkUpstreamBandwidthKbps();
        Log.d("DownSpeed", String.valueOf(downSpeed));
        Log.d("upSpeed", String.valueOf(upSpeed));
        Log.d("netInfo", String.valueOf(netInfo));
    }

    private void uploadFile() {

        Date d = new Date();
        CharSequence date = DateFormat.format("MMMM d, yyyy ", d.getTime());

        if (postPath == null || postPath.equals("")) {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
        } else {
            if (connection.isNetworkAvailable(this)) {

                dialog.showDialog();


                // Map is used to multipart the file using okhttp3.RequestBody
                Map<String, RequestBody> map = new HashMap<>();
                File file = new File(postPath);
                path = file.getPath();

                DisplayMetrics displaymetrics = new DisplayMetrics();

                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

                //Paint on image
                paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setAntiAlias(true);
                paint.setUnderlineText(true);
                paint.setAlpha(100);
                paint.setTextSize(100);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                    Bitmap image = bitmap.copy(Bitmap.Config.RGB_565, true);

                    Canvas canvas = new Canvas(image);

                    float scale = getResources().getDisplayMetrics().density;
                    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paintText.setColor(Color.rgb(0, 0, 0));
                    paintText.setTextSize((int) (14 * scale));
                    paintText.setShadowLayer(1f, 0f, 1f, Color.WHITE);

                    Rect bounds = new Rect();
                    paintText.getTextBounds(userPref.getName(), 0, userPref.getName().length(), bounds);

                    int x = (bitmap.getWidth() - bounds.width()) / 2;
                    int y = (bitmap.getHeight() + bounds.height()) / 2;

                    canvas.drawText(addresss + "", 10, y, paint);
                    canvas.drawText("type : " + types, 110, 200, paint);
                    canvas.drawText("User Name :" + userPref.getName(), 110, 300, paint);
                    canvas.drawText("purpose : " + type_id, 110, 500, paint);
                    canvas.drawText("Date : " + date, 110, 400, paint);
                    canvas.drawText("IMEI No: " + imeiNumber, 110, 5200, paint);
                    canvas.drawText("Version Code: " + Build.VERSION.RELEASE, 110, 5100, paint);
                    canvas.drawText("MODEL: " + Build.MODEL, 110, 5000, paint);
                    canvas.drawText("Manufacture: " + Build.MANUFACTURER, 110, 4900, paint);
                    canvas.drawText("Board: " + Build.BOARD, 110, 4800, paint);

                    if (projectname != null) {
                        canvas.drawText("projectName: " + projectname, 110, 100, paint);
                    } else if (vender != null) {
                        canvas.drawText("Vender : " + vender, 110, 100, paint);
                    }
                    // canvas.drawRect(0, 0, 200, 200, paint);

                    //Scaling the Bitmap Image
                    int height1 = displaymetrics.heightPixels;
                    int width1 = displaymetrics.widthPixels;
                    // image =Bitmap.createScaledBitmap(image, height1, width1, false);
                    Bitmap scaled = Bitmap.createScaledBitmap(image, 900, 750, true);
                    // Converting  Bitmap to String
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(postPath);
                    fos.write(b);

                } catch (Throwable t) {
                    Log.e("ERROR", "Error compressing file." + t.toString());
                    t.printStackTrace();
                }

                try {
                    // Parsing any Media type file
                    if (upSpeed>1000000) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                        map.put("base_img\"; filename=\"" + file.getName() + "\"", requestBody);
                    }
                    RequestBody rbAction = RequestBody.create(MediaType.parse("text/plain"), id);
                    map.put("value", rbAction);
                    RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), userPref.getUserId());
                    map.put("user_id", userid);
                    RequestBody longi = RequestBody.create(MediaType.parse("text/plain"), latitude);
                    map.put("base_long", longi);
                    RequestBody latitud = RequestBody.create(MediaType.parse("text/plain"), longitude);
                    map.put("base_lat", latitud);
                    RequestBody address = RequestBody.create(MediaType.parse("text/plain"), addresss);
                    map.put("base_address", address);
                    RequestBody type = RequestBody.create(MediaType.parse("text/plain"), types);
                    map.put("type", type);


                    location = latitude + longitude;
                } catch (Exception ignored) {
                    Toast.makeText(mContext, "Current location not found", Toast.LENGTH_SHORT).show();
                }
                ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
                Call<ServerResponse> call = getResponse.submitlocation(purpose_id,/*"25.213456", "44.2541236", "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA", "I am at Site Location",*//*"xcx",*/ map);
                call.enqueue(new Callback<ServerResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                dialog.dismissDialog();
                                ServerResponse serverResponse = response.body();

                                Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                if (!serverResponse.getSuccess()) {
                                    String responses = serverResponse.getMessage();
                                    if (responses.equals("Succssfully submitted") || responses.equals("Data submitted Succssfully")) {
                                        addPunchInLocalDatabase();

                                        MyNotification notification = new MyNotification(mContext);
                                        if (purpose_id == 1) {
                                            notification.displayNotification((ConstantVariable.punchin), userPref.getName(), submitcoordinates.class);
                                        } else if (purpose_id == 2) {
                                            notification.displayNotification((ConstantVariable.punchout), userPref.getName(), submitcoordinates.class);
                                        }
                                    }

                                    Intent i = new Intent(submitcoordinates.this, MainActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(submitcoordinates.this, "" + serverResponse.getSuccess(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            dialog.dismissDialog();
                            showMessage(resources.getString(R.string.problem_to_connect));
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
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
    }




    @OnClick({R.id.pickImage, R.id.upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pickImage:
                captureImage();
                break;

            case R.id.upload:
                uploadFile();
                break;
        }
    }

    private void addPunchInLocalDatabase() /*throws ParseException*/ {
        if (purpose_id == 1) {
            boolean result = db.addPunchIN(present, punchOutTime, path, location, addresss, 1);
            if (result) {
                Toast.makeText(this, "punchin ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            String second = null;
            String spend_hours = "";
            long a = 0;


            try {
                String punchInTime = db.getPunchInTime(getDate(System.currentTimeMillis()));
                StringTokenizer tokens = new StringTokenizer(punchInTime, "\\ ");
                String km = tokens.nextToken();
                second = tokens.nextToken();
                Log.d("punch_in", second);

                StringTokenizer punchout = new StringTokenizer(punchOutTime, "\\ ");
                String one = punchout.nextToken();
                String seconds = punchout.nextToken();

                Log.d("punch_in", seconds);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date s1 = null, s2 = null;
                long difference = 0;
                try {
                    s1 = format.parse(second);
                    s2 = format.parse(seconds);
                    difference = s2.getTime() - s1.getTime();


                    spend_hours = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                            TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)),
                            TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference)));
                    System.out.println(spend_hours);
                    Log.d("time", spend_hours);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //  Log.d("punch_diff", String.valueOf(a));

                //  spend_hours = Long.toString(difference);

            } catch (Exception ignored) {
            }

            boolean result = db.addPunchOUT(punchOutTime, "", path, location, addresss, "", spend_hours, 2);
            if (result) {
                Toast.makeText(this, "punchout", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void getdatafromsqlite() {
     String   punchInTime = db.getpunchindata(getDate(System.currentTimeMillis()));
     Log.d("pun",punchInTime);
    }


}
