package activity.userprofile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.BuildConfig;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import singleton.SettingsManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.GetDeviceInfo;
import utils.Loader;

public class LoginActivity extends Activity{
    AndroidDeviceNames deviceNames;
    String versionName1 = "";
    EditText userName;
    TextView versionName;
    EditText password;
    public static String useragent = null;
    String usernameValue;
    Animation shake;
    GetDeviceInfo getDeviceId;
    String deviceId;
    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    String enableReferralPage;
    public static String mobileNumberJsonObject;
    String passwordValue;
    private String gcmRegId = "";
    public static String PACKAGE_NAME;
    String step_activity_limit;
    String GCM_SENDER_ID = "";
    CheckInternetConnection checkInternet;
    public String token;
    SharedDatabase sharedDatabase;

    AsyncHttpClient client;
    RequestParams params;
    String urlLogin;

    //for sso login
    Button ssoLoginButton;
    LinearLayout rootLinearLayout;
    WebView ssoLoginWebView;
    String ssoURLCallback="";
    Boolean haveCode;
    Boolean isSsoLoginWebViewAppear;
    AlertDialog alertDialog;


    private int REQUEST_PERMISSION = 0;
    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        deviceNames = new AndroidDeviceNames(LoginActivity.this);
        versionName = (TextView) findViewById(R.id.version_name);
        ssoLoginButton = (Button) findViewById(R.id.ssoLogin);
        rootLinearLayout = (LinearLayout) findViewById(R.id.root_linear_layout);
        ssoLoginWebView = (WebView) findViewById(R.id.ssoLoginWebView);
        checkInternet = new CheckInternetConnection(LoginActivity.this);
        getDeviceId = new GetDeviceInfo(getApplicationContext());
        deviceId = getDeviceId.getDeviceId();
        sharedDatabase = new SharedDatabase(this);

        client = new AsyncHttpClient(true, 80, 443);

        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();
        GCM_SENDER_ID = getResources().getString(R.string.gcm_product_id);
        versionName1 = BuildConfig.VERSION_NAME;
        PACKAGE_NAME = getApplicationContext().getPackageName();
        versionName.setText("Version " + versionName1);
        token = sharedDatabase.getToken();
        step_activity_limit = sharedDatabase.getStepactivitylimit();
        if (isGoogelPlayInstalled()) {
            if (checkInternet.isConnectingToInternet()) {
                try {
                    new GCMRegistrationTask().execute();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                connectivityMessage("Waiting for Network!");
            }

        } else {
            if (getApplicationContext() != null) {
                connectivityMessage("Google Play Services is not Installed");
            }
        }

        haveCode = false;
        isSsoLoginWebViewAppear = false;
        rootLinearLayout.setVisibility(View.VISIBLE);
        ssoLoginWebView.setVisibility(View.GONE);

        //check permission
        if (!hasAllPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        }

        ssoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loader.showProgressDialog(LoginActivity.this);

                isSsoLoginWebViewAppear = true;
                rootLinearLayout.setVisibility(View.GONE);
                ssoLoginWebView.setVisibility(View.VISIBLE);
                versionName.setVisibility(View.GONE);

                //android webView
                ssoLoginWebView.getSettings().setJavaScriptEnabled(true);
                ssoLoginWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                ssoLoginWebView.getSettings().setSupportMultipleWindows(true);
//                ssoLoginWebView.setWebViewClient(new WebViewClient());
//                ssoLoginWebView.setWebChromeClient(new WebChromeClient());
                ssoLoginWebView.getSettings().setUseWideViewPort(true);
                ssoLoginWebView.getSettings().setLoadWithOverviewMode(true);
                ssoLoginWebView.getSettings().setAllowContentAccess(true);
                ssoLoginWebView.getSettings().setAllowFileAccess(true);
                ssoLoginWebView.getSettings().setDatabaseEnabled(true);
                ssoLoginWebView.getSettings().setDomStorageEnabled(true);
                ssoLoginWebView.getSettings().setAppCacheEnabled(false);
                ssoLoginWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//                ssoLoginWebView.loadUrl("https://maam-dev.axa.com/dev/authorize?scope=profile+openid+axa_id_weAXA&state=V6QKMrsNW1ZB&redirect_uri=http%3A%2F%2Fweaxa.skorpoints.com%2Faccounts%2Faxa%2Flogin%2Fcallback%2F&response_type=code&client_id=b1759e71-6d63-4d3b-b59b-ba938800d385");
                ssoLoginWebView.loadUrl("https://maam.axa.com/authorize?scope=profile+openid+axa_id_weAXA&state=Lkkie8HXGcbj&redirect_uri=https%3A%2F%2Fweaxa.skorpoints.com%2Faccounts%2Faxa%2Flogin%2Fcallback%2F&response_type=code&client_id=b1759e71-6d63-4d3b-b59b-ba938800d385");
//                ssoLoginWebView.loadUrl("https://maam.axa.com/authorize?scope=profile+openid+axa_id_weAXA&state=Lkkie8HXGcbj&redirect_uri=https%3A%2F%2Fweaxa.skorpoints.com%2Faccounts%2Faxa%2Flogin%2Fcallback%2F&response_type=code&client_id=b1759e71-6d63-4d3b-b59b-ba938800d385");

                ssoLoginWebView.setWebViewClient(new SSLTolerentWebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        System.out.println("your current url when webpage loading.." + url);

                        if (url.contains("code=")) {
                            if (!haveCode) {
                                ssoLoginWebView.setVisibility(View.GONE);
//                                Loader.dialogDissmiss(LoginActivity.this);
                                ssoURLCallback = url;
                                ssoLoginWebView.loadUrl("about:blank");
                                callLoginAPI("Oauth");
                                ssoLoginWebView.clearCache(true);
                                ssoLoginWebView.clearHistory();
                                haveCode = true;
                            }
                        }

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        System.out.println("your current url when webpage loading.. finish" + url);
                        super.onPageFinished(view, url);
                        Loader.dialogDissmiss(LoginActivity.this);

                    }

                    @Override
                    public void onLoadResource(WebView view, String url) {
                        // TODO Auto-generated method stub
                        super.onLoadResource(view, url);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }

                });
            }
        });


/* ********************checking Token Find To sending MainActivity**************************/

        if (!token.equals("")) {
            if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                SplashPage.pushnotificationisfalse = true;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                startActivity(intent);
                finish();
            } else {
                boolean isFirstTime = sharedDatabase.getIsfirtstime();
                if (isFirstTime) {
                    Intent intent = new Intent(getApplicationContext(), GoogleFitHelpActivity.class);
                    startActivity(intent);
                } else {
                    SplashPage.pushnotificationisfalse = true;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_NONE);
                    startActivity(intent);
                    finish();
                }
            }
        }
        userName = (EditText) findViewById(R.id.edit1);
        password = (EditText) findViewById(R.id.edit2);

        shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
        password.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                        userName.startAnimation(shake);
                        password.startAnimation(shake);
                        if (getApplicationContext() != null) {
                            connectivityMessage("Enter Valid Username and Password.");
                        }

                    } else if (userName.getText().toString().equals("")) {
                        userName.startAnimation(shake);
                        connectivityMessage("Enter Valid username.");
                    } else if (password.getText().toString().equals("")) {
                        password.startAnimation(shake);
                        if (getApplicationContext() != null) {
                            connectivityMessage("Enter Valid Password.");
                        }
                    } else {
                        usernameValue = userName.getText().toString().trim();
                        passwordValue = password.getText().toString().trim();
                        if (checkInternet.isConnectingToInternet()) {
                            try {
                                callLoginAPI("Auth");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            connectivityMessage("Waiting for Network!");
                        }
                    }
                    return false;
                }

                return false;
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        TextView forfot_pass = (TextView) findViewById(R.id._forgot_pass);

        forfot_pass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button loginButton = (Button) findViewById(R.id._login);
        loginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                    userName.startAnimation(shake);
                    password.startAnimation(shake);
                    if (getApplicationContext() != null) {
                        connectivityMessage(
                                "Enter Valid username and Password.");
                    }

                } else if (userName.getText().toString().equals("")) {
                    userName.startAnimation(shake);
                    if (getApplicationContext() != null) {
                        connectivityMessage(
                                "Enter Valid username.");
                    }
                } else if (password.getText().toString().equals("")) {
                    userName.startAnimation(shake);
                    if (getApplicationContext() != null) {
                        connectivityMessage(
                                "Enter Valid Password.");
                    }
                } else {
                    usernameValue = userName.getText().toString().trim();
                    passwordValue = password.getText().toString().trim();

                    if (checkInternet.isConnectingToInternet()) {
                        try {
                            callLoginAPI("Auth");
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        connectivityMessage("Waiting for Network!");
                    }
                }

            }
        });


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
    /* *******************Referesh Token Api For Push Notification***************************/

    public void refereshTokenApi() {
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;
        try {
            jsonObject.put("device_id", deviceId);
            jsonObject.put("registration_id", gcmRegId);
            entity = new StringEntity(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        token = sharedDatabase.getToken();
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);

        client.post(LoginActivity.this, Constants.BASEURL + Constants.PUSH_NOTIFICATION_REFERESH, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {

                        System.out.println("Referesh Toaken Api" + res);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        sharedDatabase.userToken("");
                    }

                }
        );

    }

    /* *******************checking is Installed google play services***************************/

    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                connectivityMessage(
                        "Google Play Service is not installed");

                finish();
            }
            return false;
        }
        return true;

    }

    /* *******************Registered Gcm for Push Notification AsyncTask Services***************************/

    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {


            if (gcmRegId.equals("") && isGoogelPlayInstalled()) {
                gcmRegId = FirebaseInstanceId.getInstance().getToken();
                System.out.print("tokentoken" + gcmRegId);
            }
            try {
                sharedDatabase.setGcmRegId(gcmRegId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return gcmRegId;


        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {


            }
        }
    }
    /* **********************registered Gcm For Push Notifcation************************/

    public void registerGcm() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("registration_id", gcmRegId);
        paramMap.put("device_id", deviceId);
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("connection", deviceId);
        token = sharedDatabase.getToken();
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        // client.addHeader("Authorization", "Token " + token);
        client.post(Constants.BASEURL + Constants.GCM_REGISTER, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 400) {
                    //  Toast.makeText(getApplicationContext(), "Unable to log in with provided credentials.", Toast.LENGTH_LONG).show();
                }
                Loader.dialogDissmiss(getApplicationContext());
                step_activity_limit = sharedDatabase.getStepactivitylimit();

                if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                    SplashPage.pushnotificationisfalse = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_NONE);
                    startActivity(intent);
                    finish();
                } else {
                    boolean isFirstTime = sharedDatabase.getIsfirtstime();
                    if (isFirstTime) {
                        SplashPage.pushnotificationisfalse = true;
                        Intent intent = new Intent(getApplicationContext(), GoogleFitHelpActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        SplashPage.pushnotificationisfalse = true;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("action", Constants.ACTION_NONE);
                        startActivity(intent);
                        finish();
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Loader.dialogDissmiss(getApplicationContext());
                try {
                    refereshTokenApi();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                step_activity_limit = sharedDatabase.getStepactivitylimit();
                if (step_activity_limit.equals("0") || step_activity_limit.equals("")) {
                    SplashPage.pushnotificationisfalse = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_NONE);
                    startActivity(intent);
                    finish();
                } else {
                    boolean isFirstTime = sharedDatabase.getIsfirtstime();
                    if (isFirstTime) {
                        SplashPage.pushnotificationisfalse = true;
                        Intent intent = new Intent(getApplicationContext(), GoogleFitHelpActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        SplashPage.pushnotificationisfalse = true;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("action", Constants.ACTION_NONE);
                        startActivity(intent);
                        finish();

                    }
                }

            }
        });
    }
    /* ************************ Calling Loging Api **********************/

    public void callLoginAPI(final String loginType) {
        Loader.showProgressDialog(this);
        if (loginType.equals("Auth")) {
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("username", usernameValue);
            paramMap.put("password", passwordValue);
            params = new RequestParams(paramMap);

            client.setTimeout(800000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", useragent);
            urlLogin = Constants.BASEURL + Constants.LOGIN_AUTH;
        } else {
            String code = ssoURLCallback.substring(ssoURLCallback.indexOf("=") + 1, ssoURLCallback.indexOf("&"));
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("code", code);
            paramMap.put("redirect_uri", "https://weaxa.skorpoints.com/accounts/axa/login/callback/");
            paramMap.put("provider", "axa");
            params = new RequestParams(paramMap);

            client.setTimeout(800000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", useragent);
            urlLogin = Constants.BASEURL + Constants.LOGIN_OAUTH;
        }

//        RequestParams params = new RequestParams(params);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        client.setTimeout(800000);
//        client.addHeader("connection", "Keep-Alive");
//        client.addHeader("USER-AGENT", useragent);
//        String url = Constants.BASEURL + Constants.LOGIN_AUTH;
//        client.post(Constants.BASEURL + Constants.LOGIN_AUTH, params, new TextHttpResponseHandler() {

        client.post(urlLogin, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Loader.dialogDissmiss(getApplicationContext());

                if (statusCode == 404) {
                    if (getApplicationContext() != null) {
                        haveCode = false;
                        isSsoLoginWebViewAppear = false;
                        ssoLoginWebView.clearCache(true);
                        ssoLoginWebView.clearHistory();
                        clearCookies(LoginActivity.this);
                        ssoLoginWebView.setVisibility(View.GONE);
                        rootLinearLayout.setVisibility(View.VISIBLE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Warning");
                        builder.setMessage("Your user ID are not registered in WeAXA please contact WeAXA administrator");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                }


                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        haveCode = false;
                        ssoLoginWebView.setVisibility(View.GONE);
                        rootLinearLayout.setVisibility(View.VISIBLE);
                        clearCookies(LoginActivity.this);
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        haveCode = false;
                        ssoLoginWebView.setVisibility(View.GONE);
                        rootLinearLayout.setVisibility(View.VISIBLE);
                        clearCookies(LoginActivity.this);
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Loader.dialogDissmiss(getApplicationContext());

                ssoLoginWebView.clearCache(true);
                ssoLoginWebView.clearHistory();
                clearCookies(LoginActivity.this);

                AppController.getInstance().getMixpanelAPI().track("login");
                try {

                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject organizationsettings = jsonObject.getJSONObject("organization_settings");
                    try {
                        sharedDatabase.userToken(jsonObject.getString("token"));
                        if (organizationsettings.has("step_activity_limit")) {
                            sharedDatabase.setStepactivitylimit(organizationsettings.getString("step_activity_limit"));
                        } else {
                            sharedDatabase.setStepactivitylimit("2");
                        }
                        if (organizationsettings.has("enable_activity_tracker")) {
                            sharedDatabase.setEnableactivitytracker(organizationsettings.getString("enable_activity_tracker"));
                        } else {
                            sharedDatabase.setEnableactivitytracker("2");
                        }
                        sharedDatabase.setOrganizationChat(jsonObject.getString("organization_chat"));

                        JSONObject userDetailJsonObject = jsonObject.getJSONObject("user_details");
                        sharedDatabase.setFirstName(userDetailJsonObject.getString("first_name"));
                        sharedDatabase.setLastName(userDetailJsonObject.getString("last_name"));
                        sharedDatabase.setEmail(userDetailJsonObject.getString("email"));

                        if (userDetailJsonObject.getString("email").equals("livesupport@skorpoints.com") ||
                                userDetailJsonObject.getString("email").equals("admin@skorpoints.com")) {
                            quickbloxCustomerSupportAPI(sharedDatabase.getToken());
                        } else {
                            quickbloxAPI(sharedDatabase.getToken());
                        }

                        if (jsonObject.has("auth_provider")) {
                            SettingsManager.getInstance().setAuthProvider(jsonObject.getString("auth_provider"));
                        }

                        sharedDatabase.setHaspointcategories(jsonObject.getBoolean("has_point_categories"));
                        sharedDatabase.setHasdiscountcategories(jsonObject.getBoolean("has_discount_categories"));
                        sharedDatabase.setProfilePic(userDetailJsonObject.getString("profile_pic_url"));
                        String mobileNumberJsonObject1 = userDetailJsonObject.getString("phone_number");
                        mobileNumberJsonObject = mobileNumberJsonObject1;
                        sharedDatabase.setOrganizationLogo(jsonObject.getString("organization_logo"));
                        sharedDatabase.setEnablefacilitycheckin(organizationsettings.getString("enable_facility_checkin"));
                        sharedDatabase.setMobileNumber(userDetailJsonObject.getString("phone_number"));
                        sharedDatabase.setDepartmentName(userDetailJsonObject.getString("department_name"));
                        sharedDatabase.setEnableReferralPage(organizationsettings.getString("enable_referral_page"));
                        if (loginType.equals("Auth")) {
                            byte[] password = Base64.encode(passwordValue.getBytes(), Base64.NO_PADDING);
                            sharedDatabase.setUserPassword(Arrays.toString(password));
                        }
                        byte[] userEmail = Base64.encode(userDetailJsonObject.getString("email").getBytes(), Base64.DEFAULT);
                        sharedDatabase.setUserName(Arrays.toString(userEmail));
                        if (getPackageName().equals("com.root.xskordbs")) {
                            boolean defaultPassword = userDetailJsonObject.getBoolean("using_default_password");
                            if (defaultPassword) {
                                openPasswordChangeDialog();
                            } else {
                                if (gcmRegId != null) {
                                    registerGcm();
                                }
                            }
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    if (!getApplicationContext().getPackageName().equals("com.root.skordbs")) {
                        if (gcmRegId != null) {
                            registerGcm();
                        }
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void quickbloxAPI(String token) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("USER-AGENT", useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.get(Constants.BASEURL + "quickblox/api/quickblox_settings/", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                Loader.dialogDissmiss(getApplicationContext());

                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Loader.dialogDissmiss(getApplicationContext());
                try {

                    JSONObject jsonObject = new JSONObject(responseString);
                    try {

                        JSONObject userChatJSONObject = jsonObject.getJSONObject("user_chat");
                        sharedDatabase.setQuickblox_appId(userChatJSONObject.getString("application_id"));
                        sharedDatabase.setQuickblox_authKey(userChatJSONObject.getString("authorization_key"));
                        sharedDatabase.setQuickblox_authSecret(userChatJSONObject.getString("authorization_secret"));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void quickbloxCustomerSupportAPI(String token) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("USER-AGENT", useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.get(Constants.BASEURL + "quickblox/api/quickblox_customer_support/", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                Loader.dialogDissmiss(getApplicationContext());

                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Loader.dialogDissmiss(getApplicationContext());
                try {

                    JSONObject jsonObject = new JSONObject(responseString);
                    try {
                        sharedDatabase.setQuickblox_appId(jsonObject.getString("application_id"));
                        sharedDatabase.setQuickblox_authKey(jsonObject.getString("authorization_key"));
                        sharedDatabase.setQuickblox_authSecret(jsonObject.getString("authorization_secret"));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void openPasswordChangeDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please change password.");
        alertDialogBuilder.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(LoginActivity.this, ChangePassword.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (gcmRegId != null) {
                    registerGcm();
                }
            }

        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName1 = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private boolean hasAllPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
//            Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSsoLoginWebViewAppear) {
            rootLinearLayout.setVisibility(View.VISIBLE);
            ssoLoginWebView.setVisibility(View.GONE);
            versionName.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }

        isSsoLoginWebViewAppear = false;
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += " Do you want to continue anyway?";

            builder.setTitle("SSL Certificate Error");
            builder.setMessage(message);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }}