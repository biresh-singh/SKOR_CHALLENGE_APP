package fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.model.QBRosterEntry;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import InternetConnection.CheckInternetConnection;
import activity.skorchat.PreviewActivity;
import activity.userprofile.MainActivity;
import activity.userprofile.MyProfile;
import activity.userprofile.ResetPasswordActivity;
import activity.userprofile.SplashPage;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import gcm.MyfireBaseIntentService;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.CircleImageView;
import utils.Loader;


public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {


    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    public static boolean whatsOn = false;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    LinearLayout bookmark_layout;
    LinearLayout dashboard_layout;
    LinearLayout calender_layout;
    LinearLayout addActivity_layout;
    LinearLayout facilityCheckin_layout;
    LinearLayout referral_layout;
    LinearLayout rewards_layout;
    LinearLayout history_layout;
    LinearLayout wallet;
    LinearLayout setting_layout;
    public LinearLayout surveyPollLayout;
    public LinearLayout skorchat_layout;
    LinearLayout leaderboard;
    LinearLayout driveaxalayout;
    RelativeLayout lo;
    LinearLayout axaDrive, myAXAHealth;
    LinearLayout emergencyServices;
    LinearLayout linearProfile;
    TextView profileName;
    LinearLayout notificationslayout;
    String enableReferralPage;
    public static boolean isSubRewardzopen = false;
    CircleImageView profileImage;
    LinearLayout pointRewardz;
    LinearLayout discountRewardz;
    public static String type = "";
    CoordinatorLayout coordinatorLayout;
    String PACKAGE_NAME;
    String firstname;
    String profilepicurl;
    AndroidDeviceNames deviceNames;
    String enablefacilitycheckin1;
    TextView pointsRewardzLabel;
    TextView discountRewardzLabel;
    TextView dashboardtext, calandertext,
            leaderboardtext, notificationstext, facilityCheckstext,
            referraltext, historytext, emergency_serveicestext, rewardztext, skorchattext, bookmarktext, walletText, surveyPollText;
    LinearLayout openOrCloseReawrdzlayout;
    LinearLayout subCategoryRewardzLayout, lo1;
    CheckInternetConnection checkInternetConnection;
    public SharedDatabase sharedDatabase;
    public String token, email;
    String valueType;
    public static int counter = 0;
    int position;

    QBUser user;
    QBChatService qbChatService;

    private QBRoster сhatRoster;
    private QBRosterListener rosterListener;
    private QBSubscriptionListener subscriptionListener;


    public NavigationDrawerFragment() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedDatabase = new SharedDatabase(getActivity());
        mUserLearnedDrawer = sharedDatabase.getNavigationdrawerlearned();

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        String intentAction = getActivity().getIntent().getStringExtra("action");
        if (ResetPasswordActivity.istrue == true) {
            Constants.BASEURL = ResetPasswordActivity.baseurl;
        } else {
            final String baseUrl = getResources().getString(R.string.base_url);
            Constants.BASEURL = baseUrl;
        }
        if (SplashPage.pushnotificationisfalse == false) {
            if (!intentAction.equals("point_summery")) {
                if (intentAction != null && MyfireBaseIntentService.gcmintent1 != false) {
                    if (MyProfile.isEditprofile == false) {

                        if (MyfireBaseIntentService.gcmintent == false) {
                            whatsOn = true;
                            MyfireBaseIntentService.gcmintent = true;
                        } else {
                            whatsOn = true;
                            MyfireBaseIntentService.gcmintent = true;
                        }


                    } else {
                        whatsOn = false;
                        MyfireBaseIntentService.gcmintent = false;
                        MyProfile.isEditprofile = false;

                    }

                } else {
                    sharedDatabase.setMessage("");
                    sharedDatabase.setObjectid("");
                    sharedDatabase.setObjecttype("");
                    sharedDatabase.setFrom("");
                    sharedDatabase.setImage("");
                    sharedDatabase.setPk("");
                    whatsOn = false;
                }
            }

        } else {
            SplashPage.pushnotificationisfalse = false;
            MyfireBaseIntentService.gcmintent = false;
            whatsOn = false;
        }


        if (intentAction.equals(Constants.ACTION_NONE)) {
            selectItem(1);
        } else if (intentAction.equals(Constants.ACTION_CONNECTED_APPS)) {
            selectItem(7);
        } else if (intentAction.equals(Constants.ACTION_REFER)) {
            selectItem(10);
        } else if (intentAction.equals(Constants.ACTION_FACILITY_CHECKIN)) {
            selectItem(6);
        } else if (intentAction.equals(Constants.ACTION_POINT_SUMMERY)) {
            selectItem(2);
            whatsOn = false;

        } else if (intentAction.equals(Constants.ACTION_EVENT_CHECKIN)) {
            NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
            navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
            navigataionCallback.onNavigationDrawerItemSelected(3, "events");
        }else if(intentAction.equals(Constants.ACTION_WALLET)){
            selectItem(20);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, null);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        email = sharedDatabase.getEmail();
        dashboard_layout = (LinearLayout) view.findViewById(R.id.dashboard);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        calender_layout = (LinearLayout) view.findViewById(R.id.calander);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        addActivity_layout = (LinearLayout) view.findViewById(R.id.addActivity);
        facilityCheckin_layout = (LinearLayout) view.findViewById(R.id.facilityCheck);
        referral_layout = (LinearLayout) view.findViewById(R.id.referral);
        rewards_layout = (LinearLayout) view.findViewById(R.id.rewards1);
        history_layout = (LinearLayout) view.findViewById(R.id.history);
        setting_layout = (LinearLayout) view.findViewById(R.id.setting);
        skorchat_layout = (LinearLayout) view.findViewById(R.id.skorchat);
        bookmark_layout = (LinearLayout) view.findViewById(R.id.bookmark);
        surveyPollLayout = (LinearLayout) view.findViewById(R.id.surveyPolling);
        lo1 = (LinearLayout) view.findViewById(R.id.lo1);
        emergencyServices = (LinearLayout) view.findViewById(R.id.emergency_serveices);
        notificationslayout = (LinearLayout) view.findViewById(R.id.notifications);
        leaderboard = (LinearLayout) view.findViewById(R.id.leaderboard);
        driveaxalayout = (LinearLayout) view.findViewById(R.id.driveaxalayout);
        checkInternetConnection = new CheckInternetConnection(getActivity());
        linearProfile = (LinearLayout) view.findViewById(R.id.linearProfile);
        pointRewardz = (LinearLayout) view.findViewById(R.id.point_rewardz);
        lo = (RelativeLayout) view.findViewById(R.id.lo);
        axaDrive = (LinearLayout) view.findViewById(R.id.axaDrive);
        myAXAHealth = (LinearLayout) view.findViewById(R.id.myAxaHealth);
        discountRewardz = (LinearLayout) view.findViewById(R.id.discount_rewardz);
        openOrCloseReawrdzlayout = (LinearLayout) view.findViewById(R.id.open_or_close_layout);
        subCategoryRewardzLayout = (LinearLayout) view.findViewById(R.id.sub_rewardz_layout);
        wallet = (LinearLayout) view.findViewById(R.id.wallet);
        pointsRewardzLabel = (TextView) view.findViewById(R.id.point_rewardz_label);
        discountRewardzLabel = (TextView) view.findViewById(R.id.discount_rewardz_label);
        dashboardtext = (TextView) view.findViewById(R.id.dashboardtext);
        calandertext = (TextView) view.findViewById(R.id.calandertext);
        leaderboardtext = (TextView) view.findViewById(R.id.leaderboardtext);
        bookmarktext = (TextView) view.findViewById(R.id.bookmarktext);
        notificationstext = (TextView) view.findViewById(R.id.notificationstext);
        facilityCheckstext = (TextView) view.findViewById(R.id.facilityCheckstext);
        referraltext = (TextView) view.findViewById(R.id.referraltext);
        historytext = (TextView) view.findViewById(R.id.historytext);
        walletText = (TextView) view.findViewById(R.id.wallet_label);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        emergency_serveicestext = (TextView) view.findViewById(R.id.emergency_serveicestext);
        skorchattext = (TextView) view.findViewById(R.id.skorchattext);
        surveyPollText = (TextView) view.findViewById(R.id.surveyPollingText);
//        settingtext = (TextView) view.findViewById(R.id.settingtext);
        rewardztext = (TextView) view.findViewById(R.id.rewardztext);
        deviceNames = new AndroidDeviceNames(getActivity());
        dashboard_layout.setOnClickListener(this);
        calender_layout.setOnClickListener(this);
        addActivity_layout.setOnClickListener(this);
        facilityCheckin_layout.setOnClickListener(this);
        referral_layout.setOnClickListener(this);
        rewards_layout.setOnClickListener(this);
        history_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
        skorchat_layout.setOnClickListener(this);
        bookmark_layout.setOnClickListener(this);
        wallet.setOnClickListener(this);
        surveyPollLayout.setOnClickListener(this);
        PACKAGE_NAME = getActivity().getPackageName();

        if (PACKAGE_NAME.equals("com.root.axa")) {
            MainActivity.isInternetConnection = false;
            lo1.setVisibility(View.GONE);
            driveaxalayout.setVisibility(View.VISIBLE);
            myAXAHealth.setVisibility(View.VISIBLE);
            axaDrive.setVisibility(View.VISIBLE);
        } else if (PACKAGE_NAME.equals("com.root.skorcarrier")) {
            MainActivity.isInternetConnection = false;
            myAXAHealth.setVisibility(View.GONE);
            lo1.setVisibility(View.GONE);
            axaDrive.setClickable(false);
            driveaxalayout.setVisibility(View.VISIBLE);
            driveaxalayout.setClickable(false);
        } else {
            lo1.setVisibility(View.GONE);
            driveaxalayout.setVisibility(View.GONE);
            lo.setVisibility(View.VISIBLE);
        }
        axaDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                if (!PACKAGE_NAME.equals("com.root.skorcarrier")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.axa.drive2&hl=en"));
                    startActivity(i);
                }
            }
        });
        myAXAHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.tcs.mobility.axa&hl=en"));
                startActivity(i);
            }
        });
        lo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PACKAGE_NAME.equals("com.root.unilever.ae")) {
                    MainActivity.isInternetConnection = false;
                    PackageManager packageManager = getActivity().getPackageManager();
                    boolean isInstalled = isPackageInstalled("com.ionicframework.uengage647310", packageManager);
                    if (isInstalled) {
                        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.ionicframework.uengage647310");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ionicframework.uengage647310&hl=en"));
                        startActivity(i);
                    }
                }

            }
        });
        leaderboard.setOnClickListener(this);
        linearProfile.setOnClickListener(this);
        emergencyServices.setOnClickListener(this);
        pointRewardz.setOnClickListener(this);
        notificationslayout.setOnClickListener(this);
        discountRewardz.setOnClickListener(this);
        firstname = sharedDatabase.getFirstName();
        profilepicurl = sharedDatabase.getProfilePic();
        profileName.setText(firstname);
        final String profileImageNavigation = Constants.BASEURL + profilepicurl;
        Glide.with(getActivity()).load(profileImageNavigation).into(profileImage);

        checkInternetConnection = new CheckInternetConnection(getActivity());
        boolean isPointRewardzAvailable = sharedDatabase.getHaspointcategories();
        boolean isDiscountRewardzAvailable = sharedDatabase.getHasdiscountcategories();
        enablefacilitycheckin1 = sharedDatabase.getEnablefacilitycheckin();
        enableReferralPage = sharedDatabase.getEnableReferralPage();
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callProfileAPI();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }
        if (enablefacilitycheckin1.equals("0")) {
            facilityCheckin_layout.setVisibility(View.GONE);
        }
        if (enableReferralPage.equals("0")) {
            referral_layout.setVisibility(View.GONE);
        }

        if (!isPointRewardzAvailable) {
            pointRewardz.setVisibility(View.GONE);
        }
        if (!isDiscountRewardzAvailable) {
            discountRewardz.setVisibility(View.GONE);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra("url", profileImageNavigation);
                intent.putExtra("name", "profile");
                intent.putExtra("type", "image");
                startActivity(intent);
            }
        });


        openOrCloseReawrdzlayout.setOnClickListener(this);
        return view;
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* ******************calling Edit Profile Get Method to Setting Image ****************************/

    public void callProfileAPI() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String userAgent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();
        client.addHeader("USER-AGENT", userAgent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.EDITPROFILE;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getActivity() != null) {
                    Loader.dialogDissmiss(getActivity());
                    try {

                        profileName.setText(jsonObject.getString("first_name"));
                        String profileImageNavigation = Constants.BASEURL + jsonObject.getString("profile_pic_url");
                        Glide.with(getActivity()).load(profileImageNavigation).into(profileImage);
                        sharedDatabase.setFirstName(jsonObject.getString("first_name"));
                        sharedDatabase.setProfilePic(jsonObject.getString("profile_pic_url"));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (getActivity() != null) {
                    Loader.dialogDissmiss(getActivity());

                    if (statusCode == 401) {
                        UserManager.getInstance().logOut(getActivity());
                    }


                    if (statusCode == 400) {
                        Toast.makeText(getActivity(), "" + errorResponse, Toast.LENGTH_LONG).show();
                    }
                    if (statusCode == 500) {
                        Toast.makeText(getActivity(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                valueType = sharedDatabase.getType();
                position = sharedDatabase.getPosition();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    sharedDatabase.setNavigationdrawerlearned(true);

                }
                InputMethodManager inputManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                getActivity().supportInvalidateOptionsMenu();

                if (valueType.equals("all") && position == 1) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    dashboardtext.setTextColor(Color.parseColor("#f59505"));
                }
                if (valueType.equals("point") && position == 12) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.VISIBLE);
                    pointsRewardzLabel.setTextColor(Color.parseColor("#f59505"));
                }
                if (valueType.equals("discount") && position == 12) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.VISIBLE);
                    discountRewardzLabel.setTextColor(Color.parseColor("#f59505"));
                }

                if (valueType.equals("all") && position == 3) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    calandertext.setTextColor(Color.parseColor("#f59505"));
                }

                if (valueType.equals("all") && position == 5) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    notificationstext.setTextColor(Color.parseColor("#f59505"));
                }

                if (valueType.equals("all") && position == 15) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    historytext.setTextColor(Color.parseColor("#f59505"));
                }

                if (valueType.equals("all") && position == 10) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    referraltext.setTextColor(Color.parseColor("#f59505"));
                }

                if (valueType.equals("all") && position == 6) {
                    resetView();
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                    facilityCheckstext.setTextColor(Color.parseColor("#f59505"));
                }

            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void resetView() {
        dashboardtext.setTextColor(Color.parseColor("#8A8A8A"));
        calandertext.setTextColor(Color.parseColor("#8A8A8A"));
        skorchattext.setTextColor(Color.parseColor("#8A8A8A"));
        leaderboardtext.setTextColor(Color.parseColor("#8A8A8A"));
        notificationstext.setTextColor(Color.parseColor("#8A8A8A"));
        facilityCheckstext.setTextColor(Color.parseColor("#8A8A8A"));
        referraltext.setTextColor(Color.parseColor("#8A8A8A"));
        rewardztext.setTextColor(Color.parseColor("#8A8A8A"));
        discountRewardzLabel.setTextColor(Color.parseColor("#8A8A8A"));
        pointsRewardzLabel.setTextColor(Color.parseColor("#8A8A8A"));
        historytext.setTextColor(Color.parseColor("#8A8A8A"));
        emergency_serveicestext.setTextColor(Color.parseColor("#8A8A8A"));
        bookmarktext.setTextColor(Color.parseColor("#8A8A8A"));
//        settingtext.setTextColor(Color.parseColor("#8A8A8A"));
    }

    private void selectItem(int position) {

        // Defer code dependent on restoration of previous instance state.


        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position, "all");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, String preSelectedItemForCalender);
    }

    @Override
    public void onClick(View v) {
        counter = 1;
        switch (v.getId()) {

            case R.id.dashboard: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                whatsOn = false;
                dashboardtext.setTextColor(Color.parseColor("#f59505"));
                if (checkInternetConnection.isConnectingToInternet()) {
                    selectItem(1);
                    sharedDatabase.setPosition(1);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.calander: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                calandertext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(3);
                    selectItem(3);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }

            case R.id.facilityCheck: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                facilityCheckstext.setTextColor(Color.parseColor("#f59505"));
                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(6);
                    selectItem(6);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.referral: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                referraltext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(10);
                    selectItem(10);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.rewards1: {
                resetView();
                resetViewColor();
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (isSubRewardzopen == false) {
                        isSubRewardzopen = true;
                        subCategoryRewardzLayout.setVisibility(View.VISIBLE);
                    } else {
                        isSubRewardzopen = false;
                        subCategoryRewardzLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }

                break;
            }
            case R.id.history: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                historytext.setTextColor(Color.parseColor("#f59505"));
                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(15);
                    selectItem(15);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.leaderboard: {
                resetView();
                resetViewColor();
                subCategoryRewardzLayout.setVisibility(View.GONE);
                leaderboardtext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(16);
                    selectItem(16);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.setting: {
                resetView();
                resetViewColor();
//                settingtext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(7);
                    selectItem(7);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.emergency_serveices: {
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();
                emergency_serveicestext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(9);
                    selectItem(9);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.linearProfile: {
                AppController.getInstance().getMixpanelAPI().track("ViewProfile");
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();

                if (checkInternetConnection.isConnectingToInternet()) {
                    Intent intent = new Intent(getActivity(), MyProfile.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.point_rewardz: {
                type = "point";
                resetViewColor();
                pointsRewardzLabel.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(12);
                    selectItem(12);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.notifications: {
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();
                notificationstext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(5);
                    selectItem(5);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.discount_rewardz: {
                type = "discount";
                resetViewColor();
                discountRewardzLabel.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(12);
                    selectItem(12);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.skorchat: {
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();
                skorchattext.setTextColor(Color.parseColor("#f59505"));
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (sharedDatabase.getAuthorizedToQuickblox()) {
                        sharedDatabase.setPosition(18);
                        selectItem(18);
                    } else {
                        Toast.makeText(getContext(), "Not authorized to quickblox", Toast.LENGTH_SHORT).show();
                    }

                    //moved to mainactivity
//                    user = new QBUser(email, token);
//                    loginQuickblox();
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }

                break;
            }
            case R.id.surveyPolling: {
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();
                surveyPollText.setTextColor(Color.parseColor("#f59505"));
                if (checkInternetConnection.isConnectingToInternet()) {
                    if(getActivity().getPackageName().contains("com.root.axa")){
                        selectItem(21);
                    }else{
                        selectItem(21);
                    }
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }

                break;
            }
            case R.id.bookmark: {
                subCategoryRewardzLayout.setVisibility(View.GONE);
                resetViewColor();
                bookmarktext.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(19);
                    selectItem(19);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }

                break;
            }
            case R.id.open_or_close_layout: {
                if (isSubRewardzopen == false) {
                    isSubRewardzopen = true;
                    subCategoryRewardzLayout.setVisibility(View.VISIBLE);
                } else {
                    isSubRewardzopen = false;
                    subCategoryRewardzLayout.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.wallet: {
                resetViewColor();
                walletText.setTextColor(Color.parseColor("#f59505"));

                if (checkInternetConnection.isConnectingToInternet()) {
                    sharedDatabase.setPosition(20);
                    selectItem(20);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            default:
                break;
        }


    }

    private void loginQuickblox() {
        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle args) {

                user.setId(qbUser.getId());
                loginToChat(user);
                qbUser.getCustomData();
            }

            @Override
            public void onError(QBResponseException error) {
                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loginToChat(QBUser user) {
        QBChatService.setDebugEnabled(true);
        QBChatService.setConfigurationBuilder(buildChatConfigs());

        qbChatService = QBChatService.getInstance();
        qbChatService.setUseStreamManagement(true);

        if (qbChatService.isLoggedIn()) {
            return;
        }

        qbChatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                initSubscriptionListener();
                initRosterListener();
                initRoster();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getActivity(), "failed login chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();

        configurationBuilder.setSocketTimeout(60);
        configurationBuilder.setUseTls(true);
        configurationBuilder.setKeepAlive(true);
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
        сhatRoster = qbChatService.getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
        сhatRoster.addRosterListener(rosterListener);

        Collection<QBRosterEntry> entries = сhatRoster.getEntries();
        List<QBRosterEntry> entriesList = new ArrayList<QBRosterEntry>(entries);

        for (int i = 0; i < entriesList.size(); i++) {
            String currentEntry = entriesList.get(i).getRosterEntry().getUser();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            callProfileAPI();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }

    private void resetViewColor(){
        walletText.setTextColor(Color.parseColor("#8A8A8A"));
        bookmarktext.setTextColor(Color.parseColor("#8A8A8A"));
        pointsRewardzLabel.setTextColor(Color.parseColor("#8A8A8A"));
        discountRewardzLabel.setTextColor(Color.parseColor("#8A8A8A"));
        dashboardtext.setTextColor(Color.parseColor("#8A8A8A"));
        historytext.setTextColor(Color.parseColor("#8A8A8A"));
        notificationstext.setTextColor(Color.parseColor("#8A8A8A"));
        calandertext.setTextColor(Color.parseColor("#8A8A8A"));
        facilityCheckstext.setTextColor(Color.parseColor("#8A8A8A"));
        referraltext.setTextColor(Color.parseColor("#8A8A8A"));
        emergency_serveicestext.setTextColor(Color.parseColor("#8A8A8A"));
        leaderboardtext.setTextColor(Color.parseColor("#8A8A8A"));
        skorchattext.setTextColor(Color.parseColor("#8A8A8A"));
        surveyPollText.setTextColor(Color.parseColor("#8A8A8A"));
    }
}