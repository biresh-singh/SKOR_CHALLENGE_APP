package activity.userprofile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.BuildConfig;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.newsfeed.NewsDetailActivity;
import activity.rewardz.EligibleRewardzActivity;
import activity.skorchat.ChattingActivity;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import fragment.ConnectedAppsandDevicesFragment;
import fragment.CreateSurveyPollingFragment;
import fragment.DashboardFragment;
import fragment.FacilityCheckinFragment;
import fragment.NavigationDrawerFragment;
import fragment.WalletFragment;
import gcm.MyfireBaseIntentService;
import utils.AppController;
import utils.Loader;
import utils.VMRuntime;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    //for chat
    QBUser user;
    QBChatService qbChatService;
    private QBRoster сhatRoster;
    private QBRosterListener rosterListener;
    private QBSubscriptionListener subscriptionListener;
    public String token, email;

    public static DrawerLayout mDrawerLayout;
    public static final String TAG = "BasicHistoryApi";
    public static GoogleApiClient mClient = null;
    private static final int REQUEST_OAUTH = 1;
    public static boolean isInternetConnection = true;
    JSONObject stepPostJsonObject;
    JSONArray stepDataJsonArray;
    String mEmail;
    static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    boolean doubleBackToExitPressedOnce = false;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private boolean isFromNotif = false;
    private String step_activity_limit;
    RelativeLayout headerLayout;
    CheckInternetConnection checkInternetConnection;
    public NavigationDrawerFragment mNavigationDrawerFragment;
    public SharedDatabase sharedDatabase;
    private String FromChallenge="";
    public String PACKAGE_NAME;
    String versionName1 = "";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        PACKAGE_NAME = getApplicationContext().getPackageName();

        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        VMRuntime.getRuntime().setMinimumHeapSize(4096 * 1024 * 1024);
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.v("onCreate", "maxMemory:" + Long.toString(maxMemory));
        step_activity_limit = sharedDatabase.getStepactivitylimit();
        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
            Log.d("hello", "hello");
        } else {
            buildFitnessClient();
        }
        stepDataJsonArray = new JSONArray();
        stepPostJsonObject = new JSONObject();

        //buildFitnessClient();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        headerLayout = (RelativeLayout) findViewById(R.id.headerlayout);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mDrawerLayout.closeDrawer(GravityCompat.START);

        versionName1 = BuildConfig.VERSION_NAME;
        isFromNotif = getIntent().getBooleanExtra("isFromNotif", false);

        if(getIntent()!=null)
        {
            Bundle bundle=getIntent().getExtras();
            if(bundle!=null)
            {

                if(bundle.containsKey("target"))
                {
                    FromChallenge=bundle.getString("target");

                    Fragment fragment = new DashboardFragment();
                    Bundle bundlea = new Bundle();
                    bundlea.putString("target", "challengeFragment");
                    bundlea.putString("pre_selected_item", "");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragment.setArguments(bundlea);
                    SplashPage.pushnotificationisfalse = false;
                    MyfireBaseIntentService.gcmintent = false;
                    NavigationDrawerFragment.whatsOn = false;
                    fragmentTransaction.commit();
                }

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* ******************Fragment Panel Clicking To Open Classes and Fragment****************************/

    @Override
    public void onNavigationDrawerItemSelected(int position, String preselectedItem) {
        try {

            if(!TextUtils.isEmpty(FromChallenge))
            {
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "challengeFragment");
                bundle.putString("pre_selected_item", "");
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                NavigationDrawerFragment.whatsOn = false;
                fragmentTransaction.commit();
            }
            else
            {
            if (position == 1) {

                if (MyfireBaseIntentService.gcmintent1 != false && NavigationDrawerFragment.whatsOn != false) {
                    MyfireBaseIntentService.gcmintent = true;
                    NavigationDrawerFragment.whatsOn = false;
                } else {
                    MyfireBaseIntentService.gcmintent = false;
                    NavigationDrawerFragment.whatsOn = true;
                }
                Fragment fragment = new DashboardFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

            } else if (position == 2) {
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromHistory", true);
                bundle.putString("target", "pointsSummaryFragment");
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                MyfireBaseIntentService.gcmintent = false;
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                fragmentTransaction.commit();

            } else if (position == 3) {
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "calendarFragment");
                bundle.putString("pre_selected_item", preselectedItem);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                NavigationDrawerFragment.whatsOn = false;
                fragmentTransaction.commit();

            } else if (position == 5) {
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", NavigationDrawerFragment.type);
                bundle.putString("target", "pushNotificationListFragment");
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                fragmentTransaction.commit();

            } else if (position == 6) {
                Fragment fragment = new FacilityCheckinFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;

            } else if (position == 7) {
                Fragment fragment = new ConnectedAppsandDevicesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;

            } else if (position == 8) {
                Intent intent = new Intent(getApplicationContext(), EligibleRewardzActivity.class);
                intent.putExtra("iseligible_rewardz_screen", false);
                intent.putExtra("category_type", "all");
                intent.putExtra("category", "");
                startActivity(intent);

            } else if (position == 9) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "emergencyServicesFragment");
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

            } else if (position == 10) {
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                SplashPage.pushnotificationisfalse = false;
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "referralClientFragment");
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

            } else if (position == 11) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "referralEmployeeFragment");
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

            } else if (position == 12) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", NavigationDrawerFragment.type);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();

            } else if (position == 15) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);

                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "historyFragment");
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();

            } else if (position == 16) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "leaderboardIndividualFragment");
                bundle.putString("type", NavigationDrawerFragment.type);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();

            } else if (position == 17) {
                NavigationDrawerFragment.whatsOn = false;
                SplashPage.pushnotificationisfalse = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "leaderboardBusinessFragment");
                bundle.putString("type", NavigationDrawerFragment.type);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();
            } else if (position == 18) {
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "skorChatFragment");
                bundle.putString("type", NavigationDrawerFragment.type);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();
            } else if (position == 19) {
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new DashboardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", NavigationDrawerFragment.type);
                bundle.putString("target", "bookmarkFragment");
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();
            } else if (position == 20) {
                if (sharedDatabase == null) {
                    sharedDatabase = new SharedDatabase(getApplicationContext());
                }
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new WalletFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", NavigationDrawerFragment.type);
                bundle.putString("target", "walletFragment");
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();
            } else if (position == 21) {
                if (sharedDatabase == null) {
                    sharedDatabase = new SharedDatabase(getApplicationContext());
                }
                SplashPage.pushnotificationisfalse = false;
                NavigationDrawerFragment.whatsOn = false;
                MyfireBaseIntentService.gcmintent = false;
                sharedDatabase.setPosition(position);
                sharedDatabase.setType(NavigationDrawerFragment.type);
                Fragment fragment = new CreateSurveyPollingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("target", "surveyPollFragment");
                if (sharedDatabase.getIsStaff()) {
                    bundle.putInt(activity.surveypolling.Constants.PARAM_USER_TYPE, activity.surveypolling.Constants.TYPE_ADMIN);
                } else {
                    bundle.putInt(activity.surveypolling.Constants.PARAM_USER_TYPE, activity.surveypolling.Constants.TYPE_USER);
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragment.setArguments(bundle);
                fragmentTransaction.commit();
            }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/* *******************checking fitbit Email connected or not connect***************************/

    private void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                if (mClient.isConnected()) {
                                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.GET_ACCOUNTS);
                                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                        mEmail = Plus.AccountApi.getAccountName(mClient);
                                        sharedDatabase.setGfitaccount(mEmail);
                                        sharedDatabase.setIsconnected(true);
                                    }
                                }


                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                try {
                                    Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                    if (!result.hasResolution()) {
                                        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                                MainActivity.this, 0).show();
                                        return;
                                    }
                                    if (!authInProgress) {
                                        try {
                                            Log.i(TAG, "Attempting to resolve failed connection");
                                            authInProgress = true;
                                            result.startResolutionForResult(MainActivity.this,
                                                    REQUEST_OAUTH);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e(TAG,
                                                    "Exception while starting resolution activity", e);
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                ).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "Connecting...");
        if (checkInternetConnection.isConnectingToInternet()) {
            if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                Log.d("hello", "hello");
            } else {
                mClient.connect();
            }
        } else {
            if (getApplicationContext() != null) {

                SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Waiting for network!").textColor(Color.WHITE).color(Color.RED), this);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Loader.dialogDissmiss(this);

        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
            sharedDatabase.setPosition(0);
        } else {
            if (mClient.isConnected()) {
                mClient.disconnect();
                sharedDatabase.setPosition(0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Loader.dialogDissmiss(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            // restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        connectivityMessage("Please click back again to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                sharedDatabase.setPosition(0);
            }
        }, 2000);
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

    @Override
    protected void onResume() {
        super.onResume();

        AppController.getInstance().initQuickblox(sharedDatabase.getQuckblox_appId(), sharedDatabase.getQuckblox_authKey(), sharedDatabase.getQuickblox_authSecret());

        hideSoftKeyboard();

        if (checkInternetConnection.isConnectingToInternet()) {
//            Loader.dialogDissmiss(this);
            if (isInternetConnection) {
                connectivityMessage("Network Connecting....");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mobileApiPlatform();
                        connectivityMessage("Network Connected");
                    }
                }, 3000);
            } else {
                isInternetConnection = true;
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }

        if (sharedDatabase.getOrganizationChat().equals("True")) {
            mNavigationDrawerFragment.skorchat_layout.setVisibility(View.VISIBLE);
        } else {
            mNavigationDrawerFragment.skorchat_layout.setVisibility(View.GONE);
        }

        token = sharedDatabase.getToken();
        email = sharedDatabase.getEmail();
        user = new QBUser(email, token);
        loginQuickblox();
    }

    public void mobileApiPlatform() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        if (PACKAGE_NAME.equals("com.root.axa")) {
            url = Constants.BASEURL + "mobile/api/platforms/3/";
        } else if (PACKAGE_NAME.equals("com.root.skor")) {
            url = Constants.BASEURL + "mobile/api/platforms/1/";
        }
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
//                try {
//                        JSONObject newObj = jsonObject.getJSONObject("latest_version");
//                        Boolean isImportant = newObj.getBoolean("is_important");
//                        String versionNumber = newObj.getString("")
//
//                        if(isImportant) {
//
//                        }else {
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                try {
//                    Loader.dialogDissmiss(getApplicationContext());
                    String versionNumberFromServer = response.getJSONObject("latest_version").getString("version_number");
                    Boolean isImportant = response.getJSONObject("latest_version").getBoolean("is_important");
                    if (!versionNumberFromServer.equals(versionName1)) {
                        if (isImportant) {
                            final AlertDialog alertDialog;
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            View view = layoutInflater.inflate(R.layout.activity_updateplaystore, null);
                            FrameLayout update = (FrameLayout) view.findViewById(R.id.update);
                            FrameLayout notnowFrameLayout = (FrameLayout) view.findViewById(R.id.notnowFrameLayout);
                            Button notNow = (Button) view.findViewById(R.id.notNowButton);
                            FrameLayout mustUpdate = (FrameLayout) view.findViewById(R.id.mustUpdate);
                            mustUpdate.setVisibility(View.VISIBLE);
                            update.setVisibility(View.GONE);
                            notnowFrameLayout.setVisibility(View.GONE);

                            builder.setView(view);
                            alertDialog = builder.create();
                            if (!isFinishing()) {
                                alertDialog.show();
                            }

                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });

                            mustUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));
                                    }
                                }
                            });
                        } else {
                            final AlertDialog alertDialog;
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            View view = layoutInflater.inflate(R.layout.activity_updateplaystore, null);
                            FrameLayout update = (FrameLayout) view.findViewById(R.id.update);
                            FrameLayout notnowFrameLayout = (FrameLayout) view.findViewById(R.id.notnowFrameLayout);
                            Button notNow = (Button) view.findViewById(R.id.notNowButton);
                            FrameLayout mustUpdate = (FrameLayout) view.findViewById(R.id.mustUpdate);
                            mustUpdate.setVisibility(View.GONE);
                            update.setVisibility(View.VISIBLE);
                            notnowFrameLayout.setVisibility(View.VISIBLE);

                            builder.setView(view);
                            alertDialog = builder.create();
                            if (!isFinishing()) {
                                alertDialog.show();
                            }

                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
//                                    Loader.dialogDissmiss(getApplicationContext());
                                }
                            });
                            update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));
                                    }
                                }
                            });

                            notNow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("" + errorResponse);
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
            }
        });

    }

    private void loginQuickblox() {
        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle args) {
                user.setId(qbUser.getId());
                sharedDatabase.setUserId(qbUser.getId());
                loginToChat(user);
                sharedDatabase.setAuthorizedToQuickblox(true);
            }

            @Override
            public void onError(QBResponseException error) {
//                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                sharedDatabase.setAuthorizedToQuickblox(false);
            }
        });

    }

    public void loginToChat(QBUser user) {
        user.setId(sharedDatabase.getUserId());
        QBChatService.setDebugEnabled(true);
        QBChatService.setConfigurationBuilder(buildChatConfigs());
        QBChatService.getInstance().login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                initSubscriptionListener();
                initRosterListener();
                initRoster();

                pushNotification();
            }

            @Override
            public void onError(QBResponseException e) {
                pushNotification();
//                Toast.makeText(getApplicationContext(), "failed login chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();

        configurationBuilder.setSocketTimeout(60);
        configurationBuilder.setUseTls(true);
        configurationBuilder.setKeepAlive(true);
        configurationBuilder.setUseStreamManagement(true);
        configurationBuilder.setAutojoinEnabled(true);
        configurationBuilder.setAutoMarkDelivered(true);
        configurationBuilder.setReconnectionAllowed(true);
        configurationBuilder.setAllowListenNetwork(true);

        return configurationBuilder;
    }

    private void initRosterListener() {
        rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> collection) {

            }

            @Override
            public void entriesAdded(Collection<Integer> collection) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> collection) {

            }

            @Override
            public void presenceChanged(QBPresence qbPresence) {

            }
        };
    }

    private void initSubscriptionListener() {
        subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userId) {

            }
        };
    }

    private void initRoster() {
        сhatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                сhatRoster.addRosterListener(rosterListener);
            }
        }, 3000);


//        Collection<QBRosterEntry> entries = сhatRoster.getEntries();
//        List<QBRosterEntry> entriesList = new ArrayList<QBRosterEntry>(entries);
//
//        for(int i = 0; i < entriesList.size(); i++) {
//            String currentEntry =  entriesList.get(i).getRosterEntry().getUser();
//        }
    }

    private boolean checkSignIn() {
        return QBSessionManager.getInstance().getSessionParameters() != null;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    void pushNotification() {
        if (sharedDatabase.getPushNotifReceived()) {
            if (isFromNotif) {
                String objectType = sharedDatabase.getObjecttype();
                String dialogId = sharedDatabase.getDialogId();
                if (objectType.equalsIgnoreCase("feed")) {
                    Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                    intent.putExtra("feedId", sharedDatabase.getObjectid());
                    startActivity(intent);
                } else {
                    if (dialogId.equalsIgnoreCase("")) {
                        Fragment fragment = new DashboardFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("target", "skorChatFragment");
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commit();
                    } else {
                        Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
                        intent.putExtra("qbChatDialogId", dialogId);
                        startActivity(intent);
                    }
                }
                isFromNotif = false;
            }
        }
    }

}
