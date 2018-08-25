package activity.facilitychecking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.event.MultipleAddressActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import adaptor.FacilityChackinCustomRecyclerView;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.GPSTracker;
import utils.Loader;


public class FacilityCheckinDetailViewActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView backgroundImage;
    JSONObject jsonObject = new JSONObject();
    TextView phone_number, website, descriptionText;
    TextView address;
    Double lattitude = 0d;
    Double longitude;
    public LocationManager locationManager;
    ImageView facilityWeblink;
    String provider;
    TextView termsandConditions;
    ImageView downArrow;
    AlertDialog alertDialog;
    Double facilityLatitude = 0d;
    Double facilityLongitude = 0d;
    LinearLayout dialNumber;

    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    JSONArray location_setJsonArray;
    TextView facilityName;
    LinearLayout back;
    Button checkin;
    String cordinates;
    ArrayList<String> addressArray = new ArrayList<>();
    HashMap<String, String> params = new HashMap<>();

    public Location location;
    public GoogleApiClient mGoogleApiClient;
    LayoutInflater layoutInflater;
    public String PACKAGE_NAME;
    public CheckInternetConnection checkInternetConnection;
    String step_activity_limit;
    public SharedDatabase sharedDatabase;
    public String token;

    GPSTracker gpsTracker;
    int thePK = 0;

    public FacilityCheckinDetailViewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_checkin_detail_view);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        step_activity_limit = sharedDatabase.getStepactivitylimit();

        gpsTracker = new GPSTracker(this);

        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
            if (mGoogleApiClient == null) {
                try {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        params.put("location", "2");
        backgroundImage = (ImageView) findViewById(R.id.backgroundimage);
        termsandConditions = (TextView) findViewById(R.id.terms_conditions);
        termsandConditions.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        dialNumber = (LinearLayout) findViewById(R.id.dial);
        facilityName = (TextView) findViewById(R.id.title);
        checkin = (Button) findViewById(R.id.checkin);
        facilityWeblink = (ImageView) findViewById(R.id.facility_weblink);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        downArrow = (ImageView) findViewById(R.id.down_arrow);


        checkin.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        address = (TextView) findViewById(R.id.address);
        address.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));


        phone_number = (TextView) findViewById(R.id.phone_number);
        phone_number.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        website = (TextView) findViewById(R.id.website);
        website.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        descriptionText = (TextView) findViewById(R.id.description_textview);
        descriptionText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        jsonObject = FacilityChackinCustomRecyclerView.selectedFaciltyCheckinItem;
        try {
            updateUI(jsonObject.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String imageUrl = Constants.BASEURL + jsonObject.getString("display_img_url");


            Glide.with(getApplicationContext()).load(imageUrl).into(backgroundImage);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone_number.getText().toString(), null));
                startActivity(intent);
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webLinkIntent = new Intent(Intent.ACTION_VIEW);
                webLinkIntent.setData(Uri.parse(website.getText().toString()));
                startActivity(webLinkIntent);
            }
        });
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                finish();
            }
        });
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (!isGpsEnabled()) {
                        try {
                            raiseLocationAlertDialog(getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            //   new BackgroundTaskAsync().execute("");
                            getHttpRequest();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressArray.size() > 1) {
                    Intent intent = new Intent(getApplicationContext(), MultipleAddressActivity.class);
                    intent.putStringArrayListExtra("address_array", addressArray);
                    startActivityForResult(intent, 100);
                }
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        try {
            raiseRuntimePermisionForLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {

            lattitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                }
                return;
            }


        }
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
    /* ******************finding location to given runntime permission in marshmallow devices****************************/

    public void raiseRuntimePermisionForLocation() {
        if (ContextCompat.checkSelfPermission(FacilityCheckinDetailViewActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FacilityCheckinDetailViewActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FacilityCheckinDetailViewActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(FacilityCheckinDetailViewActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FacilityCheckinDetailViewActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            }
        }
    }

    private boolean isGpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER) && service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /* ******************checking location if not found to show popup****************************/

    public void raiseLocationAlertDialog(Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(FacilityCheckinDetailViewActivity.this);

        View view = layoutInflater.inflate(R.layout.location_not_found_pop_up, null);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button OK = (Button) view.findViewById(R.id.ok);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(view);


        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            int selectedIndex = data.getIntExtra("position", 0);
            try {
                JSONObject locationJsonObject = location_setJsonArray.getJSONObject(selectedIndex);
                address.setText(locationJsonObject.getString("address"));
            } catch (JSONException ex) {
                ex.printStackTrace();

            }


        }
    }

    /* ******************parsing json data and set to layout****************************/

    public void updateUI(String jsonresponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresponse);
            System.out.println("json is " + jsonObject);

            facilityName.setText(jsonObject.getString("name"));
            String website1 = jsonObject.getString("website");
            int length = website1.length();
            if (!website1.equals("")) {
                if (length > 36) {
                    String firstSubstring = website1.substring(0, 35);
                    String secondSubstring = website1.substring(35, length);
                    String finaladdress = firstSubstring + "\n" + secondSubstring;
                    website.setText(finaladdress);
                } else {
                    website.setText(website1);
                }
            } else {
                website.setText("N/A");
            }
            String junkFreeString = jsonObject.getString("terms_and_conditions");
            byte ptext[] = junkFreeString.getBytes(ISO_8859_1);
            String value = new String(ptext, UTF_8);
            termsandConditions.setText(Html.fromHtml(value).toString());


            String desc = jsonObject.getString("description");
            byte ptextdesc[] = desc.getBytes(ISO_8859_1);
            String description = new String(ptextdesc, UTF_8);
            descriptionText.setText(Html.fromHtml(description).toString());

            if (jsonObject.has("locations")) {
                location_setJsonArray = jsonObject.getJSONArray("locations");
                JSONObject locationFirstJsonObject = location_setJsonArray.getJSONObject(0);
                address.setText(locationFirstJsonObject.getString("address"));

                thePK = locationFirstJsonObject.getInt("pk");
                params.put("location", thePK+"");
            }

            for (int i = 0; i < location_setJsonArray.length(); i++) {
                JSONObject locationFirstJsonObject = location_setJsonArray.getJSONObject(i);
                String address = locationFirstJsonObject.getString("address");
                addressArray.add(address);


            }
            JSONObject locationJsonObject = location_setJsonArray.getJSONObject(0);
            JSONArray cooprdinatesJsonArray = locationJsonObject.getJSONArray("coordinates");
            cordinates = cooprdinatesJsonArray.getString(0);
            cordinates = cordinates + "," + cooprdinatesJsonArray.getString(1);
//            params.put("location", jsonObject.getString("pk"));
            System.out.println(params);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
            try {
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                    }
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    protected void onStop() {
        EventBus.getDefault().unregister(this);

        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {

        } else {
            try {
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.disconnect();
                    }
                }

            } catch (NullPointerException n) {
                n.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void getHttpRequest() {
        Loader.showProgressDialog(this);
        params.put("coordinates", gpsTracker.getLongitude()+","+gpsTracker.getLatitude());

        StringEntity entity = null;
        JSONObject jsonObject11 = null;
        try {
            jsonObject11 = new JSONObject(params);
            entity = new StringEntity(jsonObject11.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.setTimeout(800000);

        final String url = Constants.BASEURL + Constants.CHECKIN;

        client.post(FacilityCheckinDetailViewActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Loader.dialogDissmiss(getApplicationContext());

                Log.v("check in URL ", url);

                if (statusCode == 200 || statusCode == 201) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Checked In Successfully.");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.v("FAILURE RES", res);
                System.out.println("hheehgjh fail" + res);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                }
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    String JsonString = jsonArray.getString(0);
                    if (getApplicationContext() != null) {
                        connectivityMessage("" + JsonString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(FacilityCheckinDetailViewActivity.this);
                }


                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                        Loader.dialogDissmiss(getApplicationContext());
                    }
                }

            }
        });
    }
    /* *********************get method to checking Api for detail page*************************/


    @Override
    public void onConnected(Bundle bundle) {
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (!isGpsEnabled()) {
                raiseLocationAlertDialog(getApplicationContext());
            }
            if (location != null) {
                facilityLatitude = location.getLatitude();
                facilityLongitude = location.getLongitude();
                params.put("coordinates", facilityLongitude.toString() + "," + facilityLatitude.toString());
            } else {
                params.put("coordinates", "0.8534757,0.645645");
                connectivityMessage("Location Not Found");
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            getHttpRequest();
        }else{
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }


}