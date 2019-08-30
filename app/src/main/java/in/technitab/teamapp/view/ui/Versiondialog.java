package in.technitab.teamapp.view.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import in.technitab.teamapp.R;

public class Versiondialog extends Activity {
    Button closeButton;
    AlertDialog.Builder builder;
    String  S_version="0",app_version="0",addresss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogbox);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            S_version = extras.getString("Sversion");
            app_version = extras.getString("appversion");

//            Log.d("latlongitude",S_version);
        }

        builder = new AlertDialog.Builder(this);

       /* closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {*/

        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage(" You are using the older version ("+app_version+") and the Latest Version is ("+S_version+") to update the app click Continue   ")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    Intent i=new Intent(Versiondialog.this,Manual_punchin_Reminder.class);

                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=in.technitab.teamapp&hl=en"));

                        startActivity(i);

                                /*Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                        Toast.LENGTH_SHORT).show();*/
                    }
                });
               /* .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Intent i=new Intent(Versiondialog.this,MainActivity.class);
                        startActivity(i);
                    }
                });*/

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("UPDATE");
        alert.show();

        //    }
        //  });
    }
}


