package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.history.FeedBackSupportActivity;
import activity.userprofile.ChangePassword;
import activity.userprofile.MainActivity;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;


public class ConnectedAppsandDevicesFragment extends android.support.v4.app.Fragment implements OnClickListener {

    TextView connectRunkeeper;
    WebView webView;
    TextView connectToFitBit;
    CheckInternetConnection checkInternet;
    String fitbitService, runkeeperService, strevasService, jawbonesService;
    String fitBitUrl, runkeeperUrl, jawbones, strevas;
    ImageView jawbone, strava;
    LinearLayout backButton, panel, activity_tracker, step_limits_layout, connected_appslayout, step_tracker;
    TextView toolbar_title;
    TextView changePassword, feedbacksupport;
    TextView googleFitAccount, googleFitDisconnect;
    String statusText;
    CoordinatorLayout coordinatorLayout;
    TextView connectStrava, connectJawbone;
    CheckInternetConnection checkInternetConnection;
    private String step_activity_limit, enable_activity_tracker;
    boolean isConnected, isFinishedCallingApi;
    TextView fitlabel;
    TextView connectrkDisconnectedtext, connectstrvaDisconnectedtext, connectjawboneDisconnectedtext, connectfitbitDisconnectedtext;
    ImageView changePasswordRightArrow;
    ImageView feedbackRightArrow, runkeeperImage, fitbitImage;
    public SharedDatabase sharedDatabase;
    public String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Display display = ((MainActivity) getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int height = size.y;
        View view;
        if (screenWidth > 600) {
            view = inflater.inflate(R.layout.activity_setting, null);
        } else {
            view = inflater.inflate(R.layout.activity_setting_smaller, null);
        }

        backButton = (LinearLayout) view.findViewById(R.id.backButton);
        step_tracker = (LinearLayout) view.findViewById(R.id.step_tracker);
        activity_tracker = (LinearLayout) view.findViewById(R.id.activity_tracker);
        step_limits_layout = (LinearLayout) view.findViewById(R.id.step_limits_layout);
        connected_appslayout = (LinearLayout) view.findViewById(R.id.connected_appslayout);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);

        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        isFinishedCallingApi = false;

        checkInternetConnection = new CheckInternetConnection(getActivity());
        try {
            if (checkInternetConnection.isConnectingToInternet()) {
                try {
                    getConnectingServices();
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        panel = (LinearLayout) view.findViewById(R.id.menupanel);
        connectRunkeeper = (TextView) view.findViewById(R.id.connect_rk);
        googleFitAccount = (TextView) view.findViewById(R.id.google_fit_account);
        googleFitDisconnect = (TextView) view.findViewById(R.id.google_fit_connect);
        fitlabel = (TextView) view.findViewById(R.id.fitlabel);
        toolbar_title = (TextView) view.findViewById(R.id.toolbar_title);
        connectrkDisconnectedtext = (TextView) view.findViewById(R.id.connect_rk_text);
        connectjawboneDisconnectedtext = (TextView) view.findViewById(R.id.connect_jawbone_text);
        connectstrvaDisconnectedtext = (TextView) view.findViewById(R.id.connect_strva_text);
        connectfitbitDisconnectedtext = (TextView) view.findViewById(R.id.connect_fitbit_text);
        connectJawbone = (TextView) view.findViewById(R.id.jawbone);
        connectStrava = (TextView) view.findViewById(R.id.strava);
        connectToFitBit = (TextView) view.findViewById(R.id.connect_fitbit);
        jawbone = (ImageView) view.findViewById(R.id.jawboneImage);
        strava = (ImageView) view.findViewById(R.id.streva);
        changePasswordRightArrow = (ImageView) view.findViewById(R.id.changepaasbutton);
        feedbackRightArrow = (ImageView) view.findViewById(R.id.feedback_arrow);
        runkeeperImage = (ImageView) view.findViewById(R.id.runkeeperImage);
        fitbitImage = (ImageView) view.findViewById(R.id.fitbitImage);
        webView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearCache(true);
//        Loader.dialogDissmiss(getActivity());
        step_activity_limit = sharedDatabase.getStepactivitylimit();
        enable_activity_tracker = sharedDatabase.getEnableactivitytracker();
        if (step_activity_limit.equals("0") && enable_activity_tracker.equals("0")) {
            connected_appslayout.setVisibility(View.GONE);
        }
        if (step_activity_limit.equals("0")) {
            step_tracker.setVisibility(View.GONE);
            step_limits_layout.setVisibility(View.GONE);

        } else {
            Log.d("hello", "hello");
        }
        if (step_activity_limit.equals("2")) {
            step_tracker.setVisibility(View.GONE);
            step_limits_layout.setVisibility(View.GONE);
        }
        if (enable_activity_tracker.equals("0")) {
            activity_tracker.setVisibility(View.GONE);


        } else {
            Log.d("hello", "hello");
        }
        if (enable_activity_tracker.equals("2")) {
            activity_tracker.setVisibility(View.GONE);
        }
        String connectedGmailAccount = sharedDatabase.getGfitaccount();
        isConnected = sharedDatabase.getIsconnected();
        if (isConnected) {
            googleFitAccount.setText(connectedGmailAccount);
        } else {
            googleFitAccount.setText("You are not connected with Google Fit.");
        }

        try {
            connectRunkeeper.setOnClickListener(this);
            connectStrava.setOnClickListener(this);
            connectToFitBit.setOnClickListener(this);
            connectJawbone.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        changePassword = (TextView) view.findViewById(R.id.changePassword);
        feedbacksupport = (TextView) view.findViewById(R.id.feedbacksupport);
        googleFitAccount.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        changePassword.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        feedbacksupport.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        feedbacksupport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedBackSupportActivity.class);
                startActivity(intent);
            }
        });
        changePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });

        checkInternet = new CheckInternetConnection(getActivity());
        panel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar_title.setText("SETTINGS");
                webView.setVisibility(View.GONE);
                panel.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
            }
        });

        googleFitDisconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = sharedDatabase.getIsconnected();
                if (isConnected) {
                    if (checkInternetConnection.isConnectingToInternet()) {

                        if (!step_activity_limit.equals("0")) {

                            try {
                                new AsyncTaskRunnerClient().execute("");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {

                            Log.d("hello", "hello");

                        }
                    } else {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                        //  Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
                    navigataionCallback.onNavigationDrawerItemSelected(2, "all");
                }
            }
        });
        changePasswordRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });
        feedbackRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedBackSupportActivity.class);
                startActivity(intent);
            }
        });

         /* ********************Clear Cookie**************************/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Log.d(C.TAG, "Using ClearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            //Log.d(C.TAG, "Using ClearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(getActivity());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }


        return view;
    }

  /* ********************Connected App and Device Api Parsing get Method**************************/

    public void getConnectingServices() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

//        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.addHeader("connection", "Keep-Alive");
        String url = Constants.BASEURL + Constants.CONNECT_SERVICE;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
//                Loader.dialogDissmiss(getContext());
                updateUI(jsonObject.toString());
            }

            @Override
            public void onFailure(final int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Loader.dialogDissmiss(getActivity());

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }


                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (statusCode == 500) {
                                if (getActivity() != null) {
                                    if (coordinatorLayout != null) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(Color.RED);
                                        snackbar.show();
                                    }
                                }
                            }

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /* *******************set Data on Layout to Parse Data***************************/

    public void updateUI(String jsonresponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresponse);

            JSONObject jsonObjectUrl = jsonObject.getJSONObject("urls");
            if (!jsonObjectUrl.has("step_counter") && !jsonObjectUrl.has("activity_tracker")) {
                connected_appslayout.setVisibility(View.GONE);
                sharedDatabase.setStepactivitylimit("0");
                sharedDatabase.setEnableactivitytracker("0");

            }
            if (jsonObjectUrl.has("step_counter")) {
                sharedDatabase.setStepactivitylimit("1");
                step_limits_layout.setVisibility(View.VISIBLE);
                JSONArray jsonArraystepCounter = jsonObjectUrl.getJSONArray("step_counter");

                for (int i = 0; i < jsonArraystepCounter.length(); i++) {
                    JSONObject jsonObjectJawbone = jsonArraystepCounter.getJSONObject(0);
                    Glide.with(getActivity()).load(Constants.BASEURL + jsonObjectJawbone.getString("logo")).into(jawbone);

                    jawbones = jsonObjectJawbone.getString("url");
                    jawbonesService = jsonObjectJawbone.getString("service_name");
                    if (jawbones.equals("false")) {
                        connectJawbone.setText("Disconnect");
                        connectjawboneDisconnectedtext.setVisibility(View.VISIBLE);
                    }
                    JSONObject jsonObjectsteps = jsonArraystepCounter.getJSONObject(1);
                    fitbitService = jsonObjectsteps.getString("service_name");
                    Glide.with(getActivity()).load(Constants.BASEURL + jsonObjectsteps.getString("logo")).into(fitbitImage);
                    fitBitUrl = jsonObjectsteps.getString("url");
                    if (fitBitUrl.equals("false")) {
                        connectToFitBit.setText("Disconnect");
                        connectfitbitDisconnectedtext.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                sharedDatabase.setStepactivitylimit("0");
            }
            if (jsonObjectUrl.has("activity_tracker")) {
                sharedDatabase.setEnableactivitytracker("1");
                activity_tracker.setVisibility(View.VISIBLE);
                JSONArray jsonArrayactivityTracker = jsonObjectUrl.getJSONArray("activity_tracker");
                for (int i = 0; i < jsonArrayactivityTracker.length(); i++) {
                    JSONObject jsonObjectstreva = jsonArrayactivityTracker.getJSONObject(0);
                    String stravalogo = jsonObjectstreva.getString("logo");
                    Glide.with(getActivity()).load(Constants.BASEURL + stravalogo).into(strava);
                    strevas = jsonObjectstreva.getString("url");

                    strevasService = jsonObjectstreva.getString("service_name");
                    if (strevas.equals("false")) {
                        connectStrava.setText("Disconnect");
                        connectstrvaDisconnectedtext.setVisibility(View.VISIBLE);
                    }

                    JSONObject jsonObjectRunkeeper = jsonArrayactivityTracker.getJSONObject(1);
                    runkeeperService = jsonObjectRunkeeper.getString("service_name");
                    String runkeeperImagelogo = jsonObjectRunkeeper.getString("logo");
                    Glide.with(getActivity()).load(Constants.BASEURL + runkeeperImagelogo).into(runkeeperImage);
                    runkeeperUrl = jsonObjectRunkeeper.getString("url");
                    if (runkeeperUrl.equals("false")) {
                        connectRunkeeper.setText("Disconnect");
                        connectrkDisconnectedtext.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                sharedDatabase.setEnableactivitytracker("0");
            }

            isFinishedCallingApi = true;
        } catch (JSONException e) {
            isFinishedCallingApi = false;
            e.printStackTrace();
        }
    }
    /* ********************checking connect or disconnect to fitbit**************************/

    public void disconnectfit() {
        // TODO Auto-generated method stub
        if (MainActivity.mClient.hasConnectedApi(Fitness.CONFIG_API)) {

            PendingResult<Status> pendingResult = Fitness.ConfigApi.disableFit(MainActivity.mClient);

            pendingResult.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                    if (status.isSuccess()) {

                    } else {
                        //Log.e(TAG, "Google Fit wasn't disabled " + status);
                        statusText = "Google Fit wasn't Disabled";
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "Google Fit wasn't Disabled", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Google Fit wasn't Disabled", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            MainActivity.mClient.disconnect();
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Google Fit Not Connected", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }


    }
    /* *********************fitbit calling in background via asyncTask*************************/

    private class AsyncTaskRunnerClient extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            disconnectfit();
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    googleFitAccount.setText("You are not connected with Google Fit.");
                    googleFitDisconnect.setText("Connect");
                    sharedDatabase.setIsconnected(false);
                    statusText = "Google Fit disabled";
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Google Fit disabled", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            });
//            Loader.dialogDissmiss(getActivity());
        }


        @Override
        protected void onPreExecute() {
//            Loader.showProgressDialog(getActivity());
        }
    }
    /* ********************Activity Fragment Services calling on Start Fragment or Activity**************************/

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // Connect to the Fitness API
        step_activity_limit = sharedDatabase.getStepactivitylimit();

        if (checkInternetConnection.isConnectingToInternet()) {
            if (!step_activity_limit.equals("0")) {
                step_tracker.setVisibility(View.VISIBLE);
                if (MainActivity.mClient != null) {
                    MainActivity.mClient.connect();
                } else {
                    step_tracker.setVisibility(View.GONE);
                }
            } else {
                if (getActivity() != null) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }
        }
    }
/* *********************Calling Api when we want to disconnect service*************************/

    public void callDissConnectServicesApi(String serviceType, final String fitbitServiceName) {
        if (checkInternet.isConnectingToInternet()) {
//            Loader.showProgressDialog(getActivity());
            String authProvider = SettingsManager.getInstance().getAuthProvider();

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.post(serviceType, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            // called when response HTTP status is "200 OK"
//                            Loader.dialogDissmiss(getActivity());

                            if (fitbitService != null) {
                                if (fitbitServiceName.equals("runkeeper")) {
                                    connectRunkeeper.setText("Connect");
                                    connectrkDisconnectedtext.setVisibility(View.GONE);
                                }
                            }
                            if (runkeeperService != null) {
                                if (fitbitServiceName.equals("fitbit")) {
                                    connectToFitBit.setText("Connect");
                                    connectfitbitDisconnectedtext.setVisibility(View.GONE);
                                }
                            }
                            if (jawbonesService != null) {
                                if (jawbonesService.equals("jawbone")) {
                                    connectJawbone.setText("Connect");
                                    connectjawboneDisconnectedtext.setVisibility(View.GONE);
                                }
                            }
                            if (strevasService != null) {
                                if (strevasService.equals("strava")) {
                                    connectStrava.setText("Connect");
                                    connectstrvaDisconnectedtext.setVisibility(View.GONE);
                                }
                            }
                            try {
                                getConnectingServices();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "Successfully Disconnected", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
//                            Loader.dialogDissmiss(getActivity());
                            if (getActivity() != null) {
                            }
                            if (statusCode == 500) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
            );
        }


    }

    /***********************Clicking Listener to perform connect and Disconnect Api Calling************************/

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if(isFinishedCallingApi) {
            switch (view.getId()) {
                case R.id.connect_rk:
                    AppController.getInstance().getMixpanelAPI().track("RunkeeperConnect");
                {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        if (runkeeperUrl.equals("false")) {
                            try {
                                callDissConnectServicesApi(Constants.BASEURL + "services/api/disconnect/runkeeper/", runkeeperService);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            webView.setVisibility(View.VISIBLE);
                            toolbar_title.setText("Runkeeper");
                            panel.setVisibility(View.GONE);
                            backButton.setVisibility(View.VISIBLE);
                            openCallbackURL(runkeeperUrl);
                        }
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }

                    break;
                }
                case R.id.strava:
                    AppController.getInstance().getMixpanelAPI().track("StravaConnect");
                {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        if (strevas.equals("false")) {
                            try {
                                callDissConnectServicesApi(Constants.BASEURL + "services/api/disconnect/strava/", strevasService);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            toolbar_title.setText("Strava");
                            webView.setVisibility(View.VISIBLE);
                            panel.setVisibility(View.GONE);
                            backButton.setVisibility(View.VISIBLE);
                            openCallbackURL(strevas);
                        }
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }

                    break;
                }
                case R.id.jawbone:
                    AppController.getInstance().getMixpanelAPI().track("JawboneConnect");
                {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        if (jawbones.equals("false")) {
                            try {
                                callDissConnectServicesApi(Constants.BASEURL + "services/api/disconnect/jawbone/", jawbonesService);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {


                            if (!fitBitUrl.equals("false")) {

                                webView.setVisibility(View.VISIBLE);
                                toolbar_title.setText("Jawbone");
                                panel.setVisibility(View.GONE);
                                backButton.setVisibility(View.VISIBLE);
                                openCallbackURL(jawbones);
                            } else {
                                if (coordinatorLayout != null) {
                                    Toast.makeText(getActivity(), "Please Disconnect FitBit First", Toast.LENGTH_LONG).show();

                                }
                            }
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

                case R.id.connect_fitbit:
                    AppController.getInstance().getMixpanelAPI().track("FitbitConnect");
                {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        if (fitBitUrl.equals("false")) {
                            try {
                                callDissConnectServicesApi(Constants.BASEURL + "services/api/disconnect/fitbit/", fitbitService);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {


                            if (!jawbones.equals("false")) {
                                toolbar_title.setText("Fitbit");
                                webView.setVisibility(View.VISIBLE);
                                panel.setVisibility(View.GONE);
                                backButton.setVisibility(View.VISIBLE);
                                openCallbackURL(fitBitUrl);

                            } else {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "Please Disconnect Jawbone First", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }

                }

                default:
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Please wait..", Toast.LENGTH_SHORT).show();
        }
    }

    /* *******************clearing cookie***************************/
    @SuppressWarnings("deprecation")
    public void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    /* ********************Url Loading to webview**************************/

    private void openCallbackURL(String serviceurl) {
        String authorizationUrl = serviceurl;
        final String CALLBACK_URL = Uri.parse(authorizationUrl).getQueryParameter("redirect_uri");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {

                    String authCode = Uri.parse(url).getQueryParameter("code");
                    webView.setVisibility(View.GONE);

                    if (checkInternetConnection.isConnectingToInternet()) {
                        clearCookies(getActivity());
                        try {
                            postAccessTokenApi(url);
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
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        clearCookies(getActivity());
        webView.clearView();
        webView.loadUrl(authorizationUrl);
    }

    /* *********************callback url sending to Api to Connect the services Post method*************************/
    public void postAccessTokenApi(String authCode) {

        if (checkInternet.isConnectingToInternet()) {
//            Loader.showProgressDialog(getActivity());
            String authProvider = SettingsManager.getInstance().getAuthProvider();
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.post(authCode, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            // called when response HTTP status is "200 OK"
//                            Loader.dialogDissmiss(getActivity());
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Successfully Connected", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                            try {
                                getConnectingServices();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            toolbar_title.setText("SETTING");

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
//                            Loader.dialogDissmiss(getActivity());
                            if (statusCode == 400) {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Already Connected With This Account", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            } else if (statusCode == 302) {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Successfully Connected", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            } else if (statusCode == 500) {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }
                            if (getActivity() != null) {
//                                Loader.dialogDissmiss(getActivity());
                            }
                            getConnectingServices();
                            toolbar_title.setText("SETTING");
                        }

                    }
            );
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("SettingsPage");
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            getConnectingServices();
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
