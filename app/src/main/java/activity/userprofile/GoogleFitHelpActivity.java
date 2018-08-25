package activity.userprofile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

import constants.Constants;
import database.SharedDatabase;


public class GoogleFitHelpActivity extends AppCompatActivity {
    public static final String TAG = "Help";
    ArrayList<String> packageArray = new ArrayList<String>();
    ImageView isGoogleFitInstalled;
    ImageView isLocationEnabled;
    LocationManager locationManager;
    String provider;
    Location location;
    Double lattitude = 0d;
    Double longitude = 0d;
    boolean isLocationEnable = false;
    Button gotoDashboard;
    SharedPreferences mState;
    boolean isAutheicatedtogoogleFit = false;
    boolean isGoogleFitAppInstalled = false;
    ImageView authenticationCheck;
    AlertDialog alertDialog;
    SharedDatabase sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlefithelp);
        isGoogleFitInstalled = (ImageView) findViewById(R.id.googlefitcheck);
        isLocationEnabled = (ImageView) findViewById(R.id.locationcheck);
        gotoDashboard = (Button) findViewById(R.id.gotodashboard);
        authenticationCheck = (ImageView) findViewById(R.id.autheticationcheck);
        sharedPrefs = new SharedDatabase(getApplicationContext());

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        init();

        if (sharedPrefs.getIsfirtstime() == false) {
            /*if(getIntent().getBooleanExtra("isfrom_loginactivity", false))
            {*/
            if (isGoogleFitAppInstalled && isLocationEnable) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);

                startActivity(intent);
                finish();
            }
            //}
        }

    }

    private boolean isGpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER) && service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void turnGPSOn() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.sendBroadcast(intent);

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }
    }


    public void raiseLocationAlertDialog(Context context) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GoogleFitHelpActivity.this);

        alertBuilder.setTitle("Use Location ?");
        alertBuilder.setMessage("Switch on location for accurate steps tracking and check-ins");

        alertBuilder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                isLocationEnable = true;

                if (isGoogleFitAppInstalled && isLocationEnable) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_NONE);
                    startActivity(intent);
                    finish();

                }

            }
        });
        alertBuilder.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });


        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void init() {
        mState = (SharedPreferences) getApplicationContext().getSharedPreferences("state", 0);
        isAutheicatedtogoogleFit = mState.getBoolean("isauthenticated", false);
        if (isAutheicatedtogoogleFit) {
            authenticationCheck.setBackgroundResource(R.drawable.yes);
        } else {
            authenticationCheck.setBackgroundResource(R.drawable.no);
        }
        gotoDashboard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isGoogleFitAppInstalled) {
                    sharedPrefs.setIsfirtstime(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_NONE);
                    startActivity(intent);
                    finish();
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.fitness&hl=en"));
                    startActivity(i);
                }
            }
        });
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            String packageName = packageInfo.packageName;
            packageArray.add(packageName);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }
        if (packageArray.contains("com.google.android.apps.fitness")) {
            isGoogleFitInstalled.setBackgroundResource(R.drawable.yes);
            isGoogleFitAppInstalled = true;
			/*isGoogleFitInstalled.setChecked(true);*/
        } else {
            isGoogleFitAppInstalled = false;
            if (getApplicationContext() != null) {
                connectivityMessage( "No Google Fit App Installed");
            }
        }
        boolean isGpsEnabled = isGpsEnabled();
        isLocationEnable = true;

        if (isGpsEnabled) {
            isLocationEnable = true;
            isLocationEnabled.setBackgroundResource(R.drawable.yes);
        } else {
            //isLocationEnable=false;
            isLocationEnabled.setBackgroundResource(R.drawable.no);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        raiseRuntimePermisionForLocation();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //DO OP WITH LOCATION SERVICE
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {

            lattitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            //isLocationEnable=false;
        }
        if (isGoogleFitAppInstalled) {
            gotoDashboard.setText("Go to Dashboard");
        } else {
            gotoDashboard.setText("Download Google fit App.");
        }
        System.out.println(packageArray);

    }
    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#FF9B30")), this);
            }
        } else if (msg.equals("Network Connected")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#4BCC1F")), this);
            }
        } else {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.RED), this);
            }
        }

    }

/* *******************Location Finding to given Run Time Permission in Marshmallow***************************/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                }
                return;
            }


        }
    }

    public void raiseRuntimePermisionForLocation() {
        if (ContextCompat.checkSelfPermission(GoogleFitHelpActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(GoogleFitHelpActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GoogleFitHelpActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(GoogleFitHelpActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            }
        }
    }
}