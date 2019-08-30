package in.technitab.teamapp.view.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;
import in.technitab.teamapp.api.Service;
import in.technitab.teamapp.util.UserPref;

public class Dialogbox extends AppCompatActivity {

    private static final String TAG = "";

    AlertDialog.Builder builder;
    private boolean mAlreadyStartedService = false;
    String latitude, longitude, meter1, addresss, dmeters, km, mtr = "", accuracy, accuracy3, latitude1, longitude1, accuracy1;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.Continue)
    Button Continue;
    private UserPref userPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        ButterKnife.bind(this);



        userPref = new UserPref(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            latitude = extras.getString("latitude");
            longitude = extras.getString("longitude");
            addresss = extras.getString("address");
            dmeters = extras.getString("meters");
            accuracy = extras.getString("accuracy");
            Log.d("latlongitude", latitude + longitude + addresss);
            Log.v("dmeter=", String.valueOf(dmeters));

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = new Intent(getApplicationContext(), Service.class);
        startService(i);
        xfcxd();
    }


    private void xfcxd() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude1 = intent.getStringExtra(Service.EXTRA_LATITUDE);
                        longitude1 = intent.getStringExtra(Service.EXTRA_LONGITUDE);
                        accuracy1 = intent.getStringExtra(Service.accuracy);

                        if (latitude1 != null && longitude1 != null && accuracy1 != null) {
                            Log.d("pktsd", latitude1);
                            Log.d("pktsd", accuracy1);
                            Log.d("pktslongituded", longitude1);


                            final double latitude2 = Double.parseDouble(latitude1/*"28.462899"*/);
                            final double longitude2 = Double.parseDouble(longitude1/*"70.25412"*/);
                            final double longitude3 = Double.parseDouble(userPref.getlongitude()/*"70.25412"*/);
                            final double latitude3 = Double.parseDouble(userPref.getlatitude()/*"20.25412"*/);
                            Log.d("mnblat2", String.valueOf(latitude2));
                            Log.d("mnblong2", String.valueOf(longitude2));
                            Log.d("mnblat3", String.valueOf(latitude3));
                            Log.d("mnblong3", String.valueOf(longitude3));
                            try {
                                distance(latitude2, longitude2, latitude3, longitude3);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "no data found ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(Service.ACTION_LOCATION_BROADCAST)
        );
    }



    private void distance(double lat1, double lng1, double lat3, double lng3) throws IOException {


        Log.d("asskslat11", String.valueOf(lat1));
        Log.d("asskslng11", String.valueOf(lng1));
        Log.d("asskslat33", String.valueOf(lat3));
        Log.d("asskslng33", String.valueOf(lng3));

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

        double dmeter = kmeter * 1000;
        meter1 = Double.toString(dmeter);
        String kms = Double.toString(kmeter);
        Log.v("500=", String.valueOf(dmeter));
        Log.d("meter=", String.valueOf(kmeter));

        StringTokenizer tokens = new StringTokenizer(kms, "\\.");
        String kmss = tokens.nextToken();
        String second = tokens.nextToken();
        Log.d("meterkm", kmss);
        Log.d("second2", second);

        if (second.length() > 3) {
            mtr = second.substring(0, 3);
        } else {
            mtr = second;
        }
        Log.d("metermtr", mtr);
        String s = "0";
        if (!s.equals(accuracy1) && accuracy1 != null) {
            s = accuracy1;

            try {
                // dialog1(kmss);
                text.setText("You are  " + kmss + "." + mtr + " kmeters (" + latitude1 + " / " + longitude1 + ")" +
                        " away from the location chosen by you (" + userPref.getlongitude() + " / " + userPref.getlatitude() + ")" +
                        " and your GPS accuracy is " + accuracy1 + " meters . You can CONTINUE for either apply for 'Manual Attendance' or Update your 'Set Location' . or you can CANCEL to wait for 15 secs to retry with your accurate location .  ");
            } catch (Exception ignored) {
            }
        }

        /** output distance, in KILOMETERS/Meter */
    }

    private void dialog1(String kmss) {

        builder = new AlertDialog.Builder(this);


        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        Log.v("ttt", mtr);
        builder.setMessage("You are  " + kmss + "." + mtr + " kmeters (" + latitude1 + " / " + longitude1 + ")" +
                " away from the location chosen by you (" + userPref.getlongitude() + " / " + userPref.getlatitude() + ")" +
                " and your GPS accuracy is " + accuracy1 + " meters . You can CONTINUE for either apply for 'Manual Attendance' or Update your 'Set Location' . or you can CANCEL to wait for 15 secs to retry with your accurate location .  ")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    Intent i = new Intent(Dialogbox.this, Manual_punchin_Reminder.class);

                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(Dialogbox.this, seletct_location.class);

                        //   Toast.makeText(mActivity, "latitude" + mAddressOutput, Toast.LENGTH_SHORT).show();
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        i.putExtra("accurac", accuracy);
                        i.putExtra("address", addresss/*+city+state+country+postalCode+knownName*/);
                        startActivity(i);
                        Log.d("latitud", latitude);
                        /*finish();*/
                                /*Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                        Toast.LENGTH_SHORT).show();*/
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Intent i = new Intent(Dialogbox.this, MainActivity.class);
                        startActivity(i);
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("You are far from your 'Set Location'.");
        alert.show();
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.v(TAG, "I shouldn't be here");
      //  stopService(new Intent(this, Service.class));

    }

    @Override
    protected void onStop() {

        //Stop location sharing service to app server.........

      //  stopService(new Intent(this, Service.class));
        mAlreadyStartedService = false;
        //Ends................................................

        super.onStop();


    }


    public void cancel(View view) {

      /*  Intent j = new Intent(getApplicationContext(), Service.class);
        stopService(j);*/
        Intent i = new Intent(Dialogbox.this, MainActivity.class);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {

        Intent intent =new Intent(Dialogbox.this,MainActivity.class);
        startActivity(intent);
    }

    public void Continue(View view) {


       /* Intent j = new Intent(getApplicationContext(), Service.class);
        stopService(j);*/
        Intent i = new Intent(Dialogbox.this, seletct_location.class);
        //   Toast.makeText(mActivity, "latitude" + mAddressOutput, Toast.LENGTH_SHORT).show();
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        i.putExtra("accurac", accuracy);
        i.putExtra("address", addresss/*+city+state+country+postalCode+knownName*/);
        startActivity(i);
        Log.d("latitud", latitude);
        /*finish();*/
                                /*Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                        Toast.LENGTH_SHORT).show();*/

    }




}
