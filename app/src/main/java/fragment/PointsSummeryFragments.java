package fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import InternetConnection.CheckInternetConnection;
import activity.history.ActivityHistoryActivity;
import activity.rewardz.RewardzDetailActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import adaptor.PointCategoryAdapter;
import bean.PointsCategory;
import bean.PointsCategoryResponse;
import constants.Constants;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import me.leolin.shortcutbadger.ShortcutBadger;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.GIFView;


public class PointsSummeryFragments extends Fragment {
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private static final String AUTH_PENDING = "auth_state_pending";
    public static ArrayList<String> stateArrayList = new ArrayList<>();
    public static GoogleApiClient mClient = null;
    public static Double totalPointsEraned, totalPointsEraned1;
    static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public GIFView animatedManGIF;
    public ImageView animatedManImageView;
    public SharedDatabase sharedDatabase;
    public String token;
    protected String[] mParties = new String[]{
            "Redeemed", "Remaining", "", "", "", "", "", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };
    PieChart mChart;
    TextView totalPointsEarned;
    TextView redeemedPoints;
    TextView remaining_points;
    TextView pointsEarnedToday;
    LinearLayout scrollLayout;
    TextView pointsStatus;
    TextView viewAllRewardz, viewdiscountrewardz;
    TextView redemLabel;
    TextView remainingLabel;
    TextView viewMore;
    int weight;
    TextView rewardzPointsTextView;
    ArrayList<Integer> pieChartValues = new ArrayList<>();
    String[] pieChartColors = {"#7f58c2", "#f8d34e"};
    CheckInternetConnection checkInternetConnection;
    TextView basicLabel, prolabel, eliteLabel;
    TextView featureRewardzPoints;
    ImageView featuredRewardzImageview;
    TextView fetauredRewardz;
    String pk = "";
    ImageView refresh;
    int screenWidth;
    String enableReferralPage = null;
    View view;
    ArrayList<String> stepsList = new ArrayList<>();
    JSONObject stepPostJsonObject;
    JSONArray stepDataJsonArray;
    int total = 0;
    StringEntity entity;
    String mEmail;
    CoordinatorLayout coordinatorLayout;
    long daysDiference;
    Activity activity;
    TextView searchcounter;
    String categoryName;
    int count = 0;
    AndroidDeviceNames deviceNames;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    TextView whatsOnTextView;
    TextView pointsSummeryTextView;
    String pklength = null;
    ImageView search;
    FrameLayout viewDiscountButton;
    boolean isViewpointsRewardz, isViewDiscountRewardz;
    private LinearLayout basicrelative;
    private String pointSummary = "initial";
    private boolean authInProgress = false;
    private String step_activity_limit;


    RecyclerView pointCategoryRv;
    private GridLayoutManager mLayoutManager;
    PointCategoryAdapter adapter;
    List<PointsCategory> pointsCategoryList = new ArrayList<PointsCategory>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        view = inflater.inflate(R.layout.fragment_points_summary, null);

        stepDataJsonArray = new JSONArray();
        stepPostJsonObject = new JSONObject();
        mChart = (PieChart) view.findViewById(R.id.chart1);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        step_activity_limit = sharedDatabase.getStepactivitylimit();
        enableReferralPage = sharedDatabase.getEnableReferralPage();
        isViewpointsRewardz = sharedDatabase.getHaspointcategories();
        isViewDiscountRewardz = sharedDatabase.getHasdiscountcategories();
        totalPointsEarned = (TextView) view.findViewById(R.id.total_points_earned);
        redeemedPoints = (TextView) view.findViewById(R.id.redeemed_points);
        basicrelative = (LinearLayout) view.findViewById(R.id.basicrelative);
        remaining_points = (TextView) view.findViewById(R.id.remaining_points);
        pointsEarnedToday = (TextView) view.findViewById(R.id.points_earned_today);
        scrollLayout = (LinearLayout) view.findViewById(R.id.scroll_layout);
        pointsStatus = (TextView) view.findViewById(R.id.points_descrition);
        viewAllRewardz = (TextView) view.findViewById(R.id.viewallrewardz);
        viewdiscountrewardz = (TextView) view.findViewById(R.id.viewdiscountrewardz);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        redemLabel = (TextView) view.findViewById(R.id.redeem_label);
        remainingLabel = (TextView) view.findViewById(R.id.remaining_label);
        fetauredRewardz = (TextView) view.findViewById(R.id.featured_rewardz_textview);
        rewardzPointsTextView = (TextView) view.findViewById(R.id.rewardz_points_textView);
        viewMore = (TextView) view.findViewById(R.id.view_morelable);
        viewDiscountButton = (FrameLayout) view.findViewById(R.id.viewdiscountrewardzButton);
//        animatedManGIF = (GIFView) view.findViewById(R.id.running_men);
        animatedManImageView = (ImageView) view.findViewById(R.id.fragment_points_summary_runningManImageView);
        searchcounter = (TextView) view.findViewById(R.id.searchcounter);

        pointCategoryRv = (RecyclerView) view.findViewById(R.id.rv_pointCategory);

        initRecyclerView();

        sharedDatabase.setPosition(1);
        sharedDatabase.setType("all");

        search = (ImageView) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                navigataionCallback.onNavigationDrawerItemSelected(5, "all");
            }
        });

        searchcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                navigataionCallback.onNavigationDrawerItemSelected(5, "all");
            }
        });
        basicLabel = (TextView) view.findViewById(R.id.basic_label);
        prolabel = (TextView) view.findViewById(R.id.pro_label);
        eliteLabel = (TextView) view.findViewById(R.id.elite_label);
        refresh = (ImageView) view.findViewById(R.id.refresh);
        featuredRewardzImageview = (ImageView) view.findViewById(R.id.featured_reward_image);
        featureRewardzPoints = (TextView) view.findViewById(R.id.featured_rewardz_pts);
        basicLabel.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        prolabel.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        eliteLabel.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        checkInternetConnection = new CheckInternetConnection(activity);
        rewardzPointsTextView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        deviceNames = new AndroidDeviceNames(getActivity());

        viewMore.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        redemLabel.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        remainingLabel.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        totalPointsEarned.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        redeemedPoints.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        remaining_points.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        pointsEarnedToday.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        pointsStatus.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        viewAllRewardz.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        viewdiscountrewardz.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        whatsOnTextView = (TextView) view.findViewById(R.id.whats_on_textview);
        whatsOnTextView.setClickable(false);
        whatsOnTextView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        pointsSummeryTextView = (TextView) view.findViewById(R.id.points_summery);

        whatsOnTextView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        whatsOnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    if (pointSummary.equals("succesfully")) {
                        whatsOnTextView.setBackgroundResource(R.drawable.text_bg);
                        pointsSummeryTextView.setBackgroundColor(Color.TRANSPARENT);
                        NavigationDrawerFragment.whatsOn = false;
                        NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                        navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                        navigataionCallback.onNavigationDrawerItemSelected(1, "all");
                    }
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }

                    //Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();

                }
            }
        });
        if (isViewDiscountRewardz) {
            viewdiscountrewardz.setVisibility(View.VISIBLE);
            viewDiscountButton.setVisibility(View.VISIBLE);
        } else {
            viewdiscountrewardz.setVisibility(View.GONE);
            viewDiscountButton.setVisibility(View.GONE);
        }
        if (isViewpointsRewardz) {
            viewAllRewardz.setVisibility(View.VISIBLE);
        } else {
            viewAllRewardz.setVisibility(View.GONE);
        }
        pointsSummeryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        LinearLayout panel = (LinearLayout) view.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        viewAllRewardz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    NavigationDrawerFragment.type = "point";
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                    navigataionCallback.onNavigationDrawerItemSelected(12, "point");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                    //  Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
        viewdiscountrewardz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    NavigationDrawerFragment.type = "discount";

                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                    navigataionCallback.onNavigationDrawerItemSelected(12, "discount");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    Intent intent = new Intent(activity, HistoryFragment.class);
                    startActivity(intent);
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        ImageView gotoCalender = (ImageView) view.findViewById(R.id.goto_calender);
        gotoCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                navigataionCallback.onNavigationDrawerItemSelected(3, "all");
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                            callTotalPointsSummeryApi();
                        } else {
                            if (mClient != null) {
                                if (mClient.isConnected()) {
                                    compareWithPreviousDate();
                                    new InsertAndVerifyDataTask().execute();
                                }
                            }
                        }
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
            }
        });

        return view;
    }

    private void initRecyclerView() {
        adapter = new PointCategoryAdapter(getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        pointCategoryRv.setLayoutManager(mLayoutManager);
        pointCategoryRv.setAdapter(adapter);
        pointCategoryRv.setNestedScrollingEnabled(false);
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }


    @Override
    public void onResume() {
        AppController.getInstance().getMixpanelAPI().track("PointsSummary");
        super.onResume();
        if (checkInternetConnection.isConnectingToInternet()) {
            if (count == 1) {
//                Loader.dialogDissmiss(getActivity());
                try {
                    callTotalPointsSummeryApi();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
           /* }*/
            }
        } else {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }

    }

    public String separatorPoints(Double balance) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);

        return format.format(balance).split("\\,")[0];
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                    callTotalPointsSummeryApi();
                } else {
                    buildFitnessClient();
                }
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
    }

    /* *******************Calling Point Summary Api For get Method***************************/

    public void callTotalPointsSummeryApi() {

//        Loader.showProgressDialog(getActivity());
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.get(Constants.BASEURL + Constants.TOTAL_POINTS_SUMMERY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

//                Loader.dialogDissmiss(getActivity());
                try {
                    JSONArray categoryJsonArray = jsonObject.getJSONArray("point_categories");
                    for (int i = 0; i < categoryJsonArray.length(); i++) {
                        PointsCategory pointsCategory = new PointsCategory(categoryJsonArray.getJSONObject(i));
                        pointsCategoryList.add(pointsCategory);
                    }

                    adapter.updateAdapter(pointsCategoryList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                count = 1;
                pointSummary = "succesfully";
                whatsOnTextView.setClickable(true);
                updateUI(jsonObject.toString());

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Loader.dialogDissmiss(getActivity());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }

                if (statusCode == 400) {
                    if (coordinatorLayout != null) {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
            }
        });
    }

    /* *****************Parsing dat And Set to Layout *****************************/

    public void updateUI(String response) {
        try {

            scrollLayout.removeAllViews();
            JSONObject jsonObject = new JSONObject(response);
            totalPointsEraned = jsonObject.getDouble("total_points");
            totalPointsEraned1 = jsonObject.getDouble("remaining_points");
            if (jsonObject.has("badge_counter")) {
                pklength = jsonObject.getString("badge_counter");

                if (!pklength.equals("null") && !pklength.equals("") && !pklength.equals("0")) {
                    searchcounter.setVisibility(View.VISIBLE);
                    searchcounter.setText(pklength);
                    if (coordinatorLayout != null) {
                        ShortcutBadger.applyCount(getActivity(), Integer.parseInt(jsonObject.getString("badge_counter")));
                    }

                } else {
                    searchcounter.setVisibility(View.GONE);
                }
            }
            totalPointsEarned.setText(separatorPoints(totalPointsEraned) + " PTS");
            redeemedPoints.setText(separatorPoints(jsonObject.getDouble("redeemed_points")) + " PTS");
            remaining_points.setText(separatorPoints(jsonObject.getDouble("remaining_points")) + " PTS");
            pointsEarnedToday.setText(jsonObject.getString("today_points") + " Points Earned on " + jsonObject.getString("today"));
            int redeemedPoints = jsonObject.getInt("redeemed_points");
            int remainingPoints = jsonObject.getInt("remaining_points");
            pieChartValues.add(redeemedPoints);
            pieChartValues.add(remainingPoints);
            JSONArray categoryJsonArray = jsonObject.getJSONArray("point_categories");

            final PointsCategoryResponse pointsCategoryResponse = new PointsCategoryResponse(categoryJsonArray);

//            for (int i = 0; i < pointsCategoryResponse.getPointsCategories().size(); i++) {
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("steps")) {
//                    stepsValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    stepsPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    stepsLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//
//                    totalPointsEarned.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//
//                }
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("healthy-activities")) {
//                    healthValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    healthPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    healthLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("check-in-events-facilities")) {
//                    checkInValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    checkInPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    checkInLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("corporate-program")) {
//                    programValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    programPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    programLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("Appreciation")) {
//                    appreciationValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    appreciationPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    appreciationLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//                if (pointsCategoryResponse.getPointsCategories().get(i).getSlug().equals("referral")) {
//                    referralValue.setText(pointsCategoryResponse.getPointsCategories().get(i).getValue());
//                    referralPoints.setText(pointsCategoryResponse.getPointsCategories().get(i).getPoints() + "PTS");
//
//                    final int finalI = i;
//                    referralLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String name = pointsCategoryResponse.getPointsCategories().get(finalI).getName();
//                            String color = pointsCategoryResponse.getPointsCategories().get(finalI).getColor();
//                            String imageUrl = pointsCategoryResponse.getPointsCategories().get(finalI).getIcon();
//                            String slug = pointsCategoryResponse.getPointsCategories().get(finalI).getSlug();
//                            Intent intent = new Intent(activity, ActivityHistoryActivity.class);
//                            intent.putExtra("name", name);
//                            intent.putExtra("color", color);
//                            intent.putExtra("icon", imageUrl);
//                            intent.putExtra("slug", slug);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//            }

            updateChart();
            fetauredRewardz.setText("N/A");

            int remainingpoint = Integer.parseInt(jsonObject.getString("remaining_points"));
            String styledText = separatorPoints(jsonObject.getDouble("points_to_next_tier")) + " more points to become <font color='red'>" + jsonObject.getString("next_tier").toUpperCase() + "</font>";
            pointsStatus.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            String currenttier = jsonObject.getString("current_tier");
            try {
                if (basicrelative != null) {
                    weight = basicrelative.getWidth();
                    if (currenttier.equals("Basic")) {
                        weight = weight / 3 - 50;
//                        animatedManGIF.animate().x(weight);
                        animatedManImageView.animate().x(weight);
                    } else if (currenttier.equals("Pro")) {
                        weight = weight / 2 - 50;
//                        animatedManGIF.animate().x(weight);
                        animatedManImageView.animate().x(weight);
                    } else {
                        weight = weight - 50;
//                        animatedManGIF.animate().x(weight);
                        animatedManImageView.animate().x(weight);
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }
            Object featuredRewardzjsonObject = jsonObject.get("featured_reward");
            if (featuredRewardzjsonObject instanceof JSONObject) {
                JSONObject featuredRewardzjson = jsonObject.getJSONObject("featured_reward");
                if (featuredRewardzjson != null) {

                    featureRewardzPoints.setText(featuredRewardzjson.getString("points") + " pts");
                    fetauredRewardz.setText(featuredRewardzjson.getString("name"));
                    int featuredTotalRewardzPoints = featuredRewardzjson.getInt("points");
                    int totalPointsEarned = jsonObject.getInt("total_points");
                    rewardzPointsTextView.setText(String.valueOf(featuredTotalRewardzPoints - remainingpoint) + " more points to get this reward");
                    pk = featuredRewardzjson.getString("pk");
                    if (activity != null) {
                        Glide.with(activity).load(Constants.BASEURL + featuredRewardzjson.getString("thumbnail_img_url")).into(featuredRewardzImageview);
                    }
                    fetauredRewardz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (activity != null) {
                                Intent intent = new Intent(activity, RewardzDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("pk", pk);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    /* ********************to checking connect to fitbit account or not**************************/

    private void buildFitnessClient() {
        // Create the Google API Client
        try {
            if (activity != null) {
                mClient = new GoogleApiClient.Builder(activity)
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
                                            int sdkVersion = Build.VERSION.SDK_INT;

                                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                                if (sdkVersion > 22) {
                                                    raiseRuntimePermisionForLocation();

                                                    int permissionCheck = ContextCompat.checkSelfPermission((Activity)getContext(),
                                                            Manifest.permission.GET_ACCOUNTS);
                                                    if (permissionCheck != 0) {
                                                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                                            mEmail = Plus.AccountApi.getAccountName(mClient);
                                                            sharedDatabase.setGfitaccount(mEmail);
                                                            sharedDatabase.setIsconnected(true);
                                                        }
                                                    }
                                                }
                                            } else {
                                                mEmail = Plus.AccountApi.getAccountName(mClient);
                                                sharedDatabase.setGfitaccount(mEmail);
                                                sharedDatabase.setIsconnected(true);
                                            }
                                        }
                                        compareWithPreviousDate();
                                        new InsertAndVerifyDataTask().execute();
                                    }

                                    @Override
                                    public void onConnectionSuspended(int i) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            try {
                                                callTotalPointsSummeryApi();
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
                                    // Called whenever the API client fails to connect.
                                    @Override
                                    public void onConnectionFailed(ConnectionResult result) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            try {

                                                callTotalPointsSummeryApi();
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
                                        Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                        if (!result.hasResolution()) {
                                            // Show the localized error dialog
                                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                                    activity, 0).show();
                                            return;
                                        }
                                        if (!authInProgress) {
                                            try {
                                                Log.i(TAG, "Attempting to resolve failed connection");
                                                authInProgress = true;
                                                result.startResolutionForResult(activity,
                                                        REQUEST_OAUTH);
                                            } catch (IntentSender.SendIntentException e) {
                                                Log.e(TAG,
                                                        "Exception while starting resolution activity", e);
                                            }
                                        }
                                    }
                                }
                        )
                        .build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

    /* ********************Location finding to given run time permission marshmallow**************************/

    public void raiseRuntimePermisionForLocation() {
        try {
            if (coordinatorLayout != null) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);


                    }
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        if (checkInternetConnection.isConnectingToInternet()) {
            if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                Log.i(TAG, "Connecting...");
            } else {
                new LoginAsyncTask().execute();
            }
        } else {
            if (getResources() != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
            // Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
       /* if (mClient.isConnected()) {
            mClient.disconnect();
        }*/
    }
    /* *********************sending steps in server  for calling post api*************************/

    public void callStepsPostAPI() {
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
//                Loader.showProgressDialog(getActivity());
                try {
                    entity = new StringEntity(stepPostJsonObject.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String authProvider = SettingsManager.getInstance().getAuthProvider();

                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                client.setTimeout(80000);
                client.addHeader("USER-AGENT", LoginActivity.useragent);
                client.addHeader("Authorization", authProvider + " " + token);
                client.addHeader("connection", "Keep-Alive");
                client.addHeader("Content-Type", "application/json");
                client.post(activity, Constants.BASEURL + Constants.STEPS_POST, entity, "application/json", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                                super.onSuccess(statusCode, headers, jsonArray);

//                                Loader.dialogDissmiss(getActivity());
                                sharedDatabase.setPoststepdate(mFormatter.format(new Date()));
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   if (checkInternetConnection.isConnectingToInternet()) {
                                                                       try {

                                                                           callTotalPointsSummeryApi();
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
                                                               }
                                                           }

                                    );
                                }


                            }

                            @Override
                            public void onFailure(int statusCode, cz.
                                    msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray
                                                          errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                if (activity != null) {
//                                    Loader.dialogDissmiss(getActivity());
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callTotalPointsSummeryApi();
                                        }
                                    });

                                    if (statusCode == 400) {
                                        if (coordinatorLayout != null) {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                                            View snackBarView = snackbar.getView();
                                            snackBarView.setBackgroundColor(Color.RED);
                                            snackbar.show();
                                        }

                                    }
                                    if (statusCode == 500) {
                                        if (coordinatorLayout != null) {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                            View snackBarView = snackbar.getView();
                                            snackBarView.setBackgroundColor(Color.RED);
                                            snackbar.show();
                                        }
                                    }
                                }
                            }
                        }

                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (getResources() != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
            //Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
        }
    }

    public void compareWithPreviousDate() {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


            String str1 = sharedDatabase.getPoststepdate();
            Date date1 = mFormatter.parse(str1);

            String str2 = mFormatter.format(new Date());
            Date date2 = mFormatter.parse(str2);
            int result = date1.compareTo(date2);
            if (date1.compareTo(date2) < 0) {
                //postBulkSteps();
                System.out.println("date2 is Greater than my date1");

            } else if (date1.compareTo(date2) > 0) {
                if (mClient.isConnected() || mClient.isConnecting()) {

                    PendingResult<Status> pendingResult = Fitness.ConfigApi.disableFit(mClient);

                    pendingResult.setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Google Fit disabled");
                            } else {
                                Log.e(TAG, "Google Fit wasn't disabled " + status);
                            }
                        }
                    });
                    mClient.disconnect();
                }
                sharedDatabase.setPoststepdate(mFormatter.format(new Date()));
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            if (mClient != null) {
                mClient.connect();
            }
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // Parsse response data
            return null;
        }

        protected void onPostExecute(Void result) {

            //move activity
            super.onPostExecute(result);
        }
    }

    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //First, create a new dataset and insertion request.
            DataSet dataSet = insertFitnessData();
            Log.i(TAG, "Inserting the dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mClient, dataSet)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                mClient.connect();
                Log.i(TAG, "There was a problem inserting the dataset.");
                // callTotalPointsSummeryApi();
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful!");
            DataReadRequest readRequest = queryFitnessData();
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            printData(dataReadResult);

            return null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }


    /**
     * Create and return a {@link DataSet} of step count data for the History API.
     */
    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(activity)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .setStreamName("user_input")


                .build();

        // Create a data set
        int stepCountDelta = 0;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.

        daysDiference = daysDifference(sharedDatabase.getPoststepdate());
        ++daysDiference;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        if (daysDiference != 0) {
            cal.add(Calendar.DAY_OF_YEAR, (int) -daysDiference);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
      /*  cal.add(Calendar.WEEK_OF_YEAR, -1);*/
        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

//Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        return readRequest;
    }


    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {

                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            readAllStep();
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
            readAllStep();

        }

    }

    public void readAllStep() {

        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
        DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            total = totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            //  stepsList.add(String.valueOf(total));
        } else {
            // handle failure
        }
        try {
            if (daysDiference == 1) {

                String dateFormat = formatter.format(new Date());
                stepDataJsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("value", total);
                jsonObject1.put("date_time", dateFormat);
                stepDataJsonArray.put(jsonObject1);
                stepPostJsonObject = new JSONObject();
                stepPostJsonObject.put("steps", stepDataJsonArray);
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        try {

                            callStepsPostAPI();
                        } catch (Exception e) {

                        }
                    } else {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
            });


        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // [START parse_dataset]
    private void dumpDataSet(DataSet dataSet) {
        try {
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


            for (DataPoint dp : dataSet.getDataPoints()) {

                DataSource dataSource = dp.getOriginalDataSource();
                if (dataSource != null) {

                    Log.i(TAG, "\tgetApplicationPackagename: " + dataSource.getAppPackageName());
                    Log.i(TAG, "Stream :" + dataSource.getStreamName());
                }

                //Log.i(TAG, "\tType: " + dataSource.getAppPackageName());
                Log.i(TAG, "\tType: " + dp.getDataType().getName());
                Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                for (Field field : dp.getDataType().getFields()) {
                    Log.i(TAG, "\tField: " + field.getName() +
                            " Value: " + dp.getValue(field));
                    stepsList.add(dp.getValue(field).toString());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                    String dateInString = formatter.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                    JSONObject jsonObject1 = new JSONObject();


                    jsonObject1.put("value", Integer.parseInt(dp.getValue(field).toString()));
                    jsonObject1.put("date_time", dateInString);
                    stepDataJsonArray.put(jsonObject1);

                }

                stepPostJsonObject.put("steps", stepDataJsonArray);

                Log.i(TAG, "\tstepList: " + stepsList);

            }


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    public long daysDifference(String previousDate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = previousDate;
        String inputString2 = mFormatter.format(new Date());


        long diff = 0;

        try {
            Date date1 = mFormatter.parse(inputString1);
            Date date2 = mFormatter.parse(inputString2);
            diff = date2.getTime() - date1.getTime();

            System.out.println("Days: YYYYYYYYYYYYYYYYYYYYYYYYY  " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    public void updateChart() {
        try {

            mChart.setUsePercentValues(false);
            mChart.setDescription("");

            mChart.setDragDecelerationFrictionCoef(0.95f);


            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColorTransparent(true);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(75f);
            mChart.setTransparentCircleRadius(80f);

            mChart.setDrawCenterText(false);

            mChart.setRotationAngle(0);

            // enable rotation of the chart by touch
            mChart.setRotationEnabled(false);
            setData(1, 100);

            mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();

            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

            l.setEnabled(false);
            l.setXEntrySpace(0);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setData(int count, float range) {

        float mult = 300;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (pieChartValues.get(i)), i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        int color1 = Color.parseColor(pieChartColors[0]);
        int color2 = Color.parseColor(pieChartColors[1]);
        colors.add(color1);
        colors.add(color2);

        dataSet.setColors(colors);
        ArrayList<String> xValues = new ArrayList<String>();
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0);
        data.setValueTextColor(Color.TRANSPARENT);
        mChart.setData(data);
        mChart.highlightValues(null);


        mChart.invalidate();
    }



    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            callTotalPointsSummeryApi();
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

}