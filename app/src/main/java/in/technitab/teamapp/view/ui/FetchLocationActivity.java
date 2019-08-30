package in.technitab.teamapp.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.Service;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.UserPref;



public class FetchLocationActivity extends AppCompatActivity {

    @BindView(R.id.fetch_location)
    ImageView fetchLocation;
    private String TAG = PunchInFragment.class.getSimpleName();

    private Activity mActivity;
    private Resources resources;
    Unbinder unbinder;

    String latLong, mAddressOutput, lat, lng;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    double lat3, lng3, lng1, lat1;

    private double dmeter;
    String meter;
  /*  private Activity mActivity;
    private Resources resources;*/

    double fivehundredmeter = 500;
    private UserPref userPref;


    //  private String BaseLocation;

    String aaccuracy = "0";
      String latitude="0",longitude="0",accuracy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_location);
        ButterKnife.bind(this);


        initialise();

      //  xfcxd();

        initLocation();
        //initialiseTimer();
        Glide.with(mActivity).load(resources.getDrawable(R.drawable.loading_location)).into(fetchLocation);
    }

    private void xfcxd() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                         latitude = intent.getStringExtra(Service.EXTRA_LATITUDE);
                         longitude = intent.getStringExtra(Service.EXTRA_LONGITUDE);
                         accuracy = intent.getStringExtra(Service.accuracy);


                        if (latitude != null && longitude != null && accuracy !=null) {
                            Log.d("pkts",latitude);
                            Log.d("pkts",accuracy);
                            Log.d("pktslongitude",longitude);
                           // mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude+ "\n Accuracy:"+accuracy);
                        }
                        else {
                            Toast.makeText(context, "no data found ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(Service.ACTION_LOCATION_BROADCAST)
        );
    }

    private void initialise() {
        mActivity = this;
        Activity mContext = this;
        resources = mActivity.getResources();
        userPref = new UserPref(mContext);
        UserPref userPref = new UserPref(mContext);
        //initialiseTimer();

    }
   /* private void initialiseTimer() {
        timerRunnable = new Runnable() {

            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                initLocation();
            }
        };
    }*/

    private void initLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mSettingsClient = LocationServices.getSettingsClient(mActivity);

        mLocationCallback = new LocationCallback() {

            public String BaseLocation(Location location) {
                return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);

            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();

                String accurac = String.format(Locale.ENGLISH, " %f", /*mAccuracyLabel,*/mCurrentLocation.getAccuracy());
                aaccuracy += accurac;
                Log.e("accuracy1", accurac);

                // String a= LocationManager.getA
                updateLocationUI();
                // mCurrentLocation = BaseLocation();

                Log.e("sds", String.valueOf(mCurrentLocation));


                // Toast.makeText(mActivity, "" + mCurrentLocation, Toast.LENGTH_SHORT).show();
            }
        };

        //  CompareLocation();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*mLocationRequest.setPriority(())*/


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    // private Location BaseLocation;

    private void CompareLocation() {

       /* BaseLocation = BaseLocation + m;
        Toast.makeText(mActivity, ""+BaseLocation, Toast.LENGTH_SHORT).show();*/

       /* if (mCurrentLocation == m) {
            Toast.makeText(mActivity, "Location compared", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void updateLocationUI() {
        Log.d(TAG, "fetching location");
        Log.d("mCurrentLocation", longitude);
        Log.d("mCurrentLocation", latitude);
        double latitude1=Double.parseDouble(latitude);
        double longitude1=Double.parseDouble(longitude);
        if (mCurrentLocation != null) {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(/*latitude1*/mCurrentLocation.getLatitude(), /*longitude1*/mCurrentLocation.getLongitude(), 1);
            } catch (IOException ioException) {

                mAddressOutput = getString(R.string.service_not_available);
                Log.d(TAG, getString(R.string.service_not_available));
            } catch (IllegalArgumentException illegalArgumentException) {
                mAddressOutput = getString(R.string.invalid_lat_long_used);
                Log.d(TAG, getString(R.string.invalid_lat_long_used));
            }

            if (addresses == null || addresses.size() == 0) {
                if (mAddressOutput.isEmpty()) {
                    mAddressOutput = getString(R.string.no_address_found);
                    Log.d(TAG, getString(R.string.no_address_found));
                }
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);
                Log.d(TAG, "address " + mAddressOutput);
               // stopLocationUpdates();
            }

            lat3 = mCurrentLocation.getLatitude();/*latitude1;*/
            lng3 = mCurrentLocation.getLongitude();/*longitude1;*/
            Log.d("currentllatitude", String.valueOf(lat3));
            Log.d("currentlatlongitude", String.valueOf(lng3));
            latLong = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();


            try {
                String lat = userPref.getlongitude() /*"28.4628486"*/;
                String lon = userPref.getlatitude()/*"77.4898092"*/;
                lat1 = Double.parseDouble(lat);
                lng1 = Double.parseDouble(lon);
                Log.v("sharedlng:", String.valueOf(lng1));
                Log.v("sharedlat:", String.valueOf(lat1));

            } catch (Exception ignored) {
            }

            if (lat1 == 0.0) {
                Intent i = new Intent(mActivity, seletct_location.class);
                String latitud = Double.toString(lat3);
                String longitude = Double.toString(lng3);
                Log.d("accuracy2", aaccuracy);

                i.putExtra("latitude", latitud);
                i.putExtra("longitude", longitude);
                i.putExtra("accurac", aaccuracy);
                i.putExtra("meters", meter);
                i.putExtra("address", mAddressOutput/*+city+state+country+postalCode+knownName*/);
                startActivity(i);
            } else {
                try {
                    if (distance(lat1, lng1, lat3, lng3) < 0.3) { // if distance < 0.3 miles we take locations as equal
                        //do what you want to do...
                        // Toast.makeText(mActivity, "fd", Toast.LENGTH_SHORT).show();

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


           /* Intent intent = new Intent(mActivity, MainActivity.class);
            intent.putExtra(ConstantVariable.Location.LOCATION, latLong);
            intent.putExtra(ConstantVariable.Location.LOCATION_ADDRESS, mAddressOutput);
            startActivity(intent);
            finish();*/

        }

    }

    private double distance(double lat1, double lng1, double lat3, double lng3) throws IOException {


        Log.d("asskslat1", String.valueOf(lat1));
        Log.d("asskslng1", String.valueOf(lng1));
        Log.d("asskslat3", String.valueOf(lat3));
        Log.d("asskslng3", String.valueOf(lng3));

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat1 - lat3);
        double dLng = Math.toRadians(lng1 - lng3);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        Log.d("assks", String.valueOf(sindLat));
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat3));
        Log.d("assks", String.valueOf(a));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Log.d("assks", String.valueOf(c));
        double kmeter = earthRadius * c;

        dmeter = kmeter * 1000;
        meter = Double.toString(dmeter);
        // Toast.makeText(this, "dist__" + meter, Toast.LENGTH_SHORT).show();
        Log.v("meter=", String.valueOf(dmeter));
        Log.v("500=", String.valueOf(fivehundredmeter));


        Log.v("addres", mAddressOutput);
        if (dmeter <= fivehundredmeter /*&& dmeter >0.0*/) {
            Log.d(TAG, "dmeter is greater then equals to 500m");
            Intent intent = new Intent(mActivity, MainActivity.class);
            intent.putExtra(ConstantVariable.Location.LOCATION, latLong);
            intent.putExtra(ConstantVariable.Location.LOCATION_ADDRESS, mAddressOutput);
            startActivity(intent);
            finish();
        } else {

            Intent i = new Intent(mActivity, Dialogbox.class);
            String latitud = Double.toString(lat3);
            String longitude = Double.toString(lng3);
            Log.d("latitude", latitud);
            String meter = Double.toString(kmeter);
            //   Toast.makeText(mActivity, "latitude" + mAddressOutput, Toast.LENGTH_SHORT).show();
            i.putExtra("latitude", latitud);
            i.putExtra("longitude", longitude);
            i.putExtra("meters", meter);
            i.putExtra("accuracy", aaccuracy);
            i.putExtra("address", mAddressOutput/*+city+state+country+postalCode+knownName*/);
            startActivity(i);
        }

        return dmeter;  /** output distance, in KILOMETERS */
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.d(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.d(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.d(TAG, errorMessage);

                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS)
            switch (resultCode) {
                case Activity.RESULT_OK:
                    break;
                case Activity.RESULT_CANCELED:
                    initLocation();
                    break;
            }
    }

    public void onResume() {
        super.onResume();

        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                startLocationPermissionRequest();
            }
        }
    }*/


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            startLocationPermissionRequest();
        } else {
            startLocationPermissionRequest();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}

