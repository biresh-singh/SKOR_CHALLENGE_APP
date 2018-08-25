package activity.history;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class FeedBackSupportActivity extends Activity {
    private LinearLayout back;
    private TextView supprtPhoneNumber;
    private TextView supportEmailAddress;
    private TextView faqtutorialvideolink;
    private VideoView faqtutorialvideolinkwebView;
    private RelativeLayout faqs, tutoriallayout, pointCollection;
    private String PACKAGE_NAME;
    private CheckInternetConnection checkInternetConnection;
    private String baseurl;
    private TextView phoneLabel, emailLabel, faqlabel, faqAddress;
    TextView collectionPoint;
    public SharedDatabase sharedDatabase;
    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        int screenWidth = size.x;
        int height = size.y;
        if (screenWidth > 600) {
            setContentView(R.layout.activity_feedback_support);
        } else {
            setContentView(R.layout.activity_feedback_support_smaller);

        }
        pointCollection = (RelativeLayout) findViewById(R.id.pointCollectionLayout);
        collectionPoint = (TextView) findViewById(R.id.point_point);
        back = (LinearLayout) findViewById(R.id.menupanel);
        supportEmailAddress = (TextView) findViewById(R.id.support_email);
        supprtPhoneNumber = (TextView) findViewById(R.id.supprt_phone);
        faqAddress = (TextView) findViewById(R.id.faq_address);
        faqlabel = (TextView) findViewById(R.id.faq_label);
        phoneLabel = (TextView) findViewById(R.id.phone_label);
        emailLabel = (TextView) findViewById(R.id.email_label);
        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.titlebar);
        faqs = (RelativeLayout) findViewById(R.id.faqs);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        tutoriallayout = (RelativeLayout) findViewById(R.id.tutoriallayout);
        faqtutorialvideolinkwebView = (VideoView) findViewById(R.id.faq_tutorial_video_link_videoView);
        faqtutorialvideolink = (TextView) findViewById(R.id.faq_tutorial_video_link);
        faqtutorialvideolink.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        faqAddress.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        faqlabel.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        phoneLabel.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        emailLabel.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        supportEmailAddress.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        supprtPhoneNumber.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        baseurl = Constants.BASEURL;

        if (checkInternetConnection.isConnectingToInternet()) {
            try {

                getFeedBackAndSupport();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tutoriallayout.setVisibility(View.GONE);
            faqs.setVisibility(View.GONE);
            connectivityMessage("Waiting for Network!");
        }
        supprtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!supprtPhoneNumber.getText().toString().equals("N/A")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supprtPhoneNumber.getText().toString(), null));
                    startActivity(intent);
                }
            }
        });
        PACKAGE_NAME = getApplicationContext().getPackageName();
        supportEmailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!supportEmailAddress.getText().toString().equals("N/A")) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmailAddress.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT, "body of email");
                    i.putExtra(Intent.EXTRA_TEXT, R.string.intent_message);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("There are no email clients installed.");
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                finish();
            }
        });
        final Uri uri = Uri.parse(faqtutorialvideolink.getText().toString());
        faqtutorialvideolink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!faqtutorialvideolink.getText().toString().equals("N/A")) {
                    faqtutorialvideolinkwebView.setVisibility(View.VISIBLE);
                    faqtutorialvideolinkwebView.setVideoURI(uri);
                    faqtutorialvideolinkwebView.start();
                }
            }
        });
//        collectionPoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(FeedBackSupport.this, WebViewPDFActivity.class);
//                i.putExtra("pdf_url", collectionPoint.getText().toString());
//                startActivity(i);
//            }
//        });


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

    public void getFeedBackAndSupport() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(8000);
        client.setMaxRetriesAndTimeout(1, 8000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.CUSTOMER_SUPPORT;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                Loader.dialogDissmiss(getApplicationContext());

                updateUI(jsonObject.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                Loader.dialogDissmiss(getApplicationContext());
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(FeedBackSupportActivity.this);
                }

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
    }/*{
        "count": 1,
                "next": null,
                "previous": null,
                "results": [
        {
            "pk": 1,
                "organization": 1,
                "email": "suport@skordev.com",
                "phone": "1234567890",
                "faq": "http://skordev.com/faq",
                "tutorial": "http://skordev.com/tutorial"
        }
        ]
    }*/

    public void updateUI(String jsonresponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresponse);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject resultJsonObject = jsonArray.getJSONObject(0);
            String email = resultJsonObject.getString("email");
            String phone = resultJsonObject.getString("phone");
            if (phone.equals("")) {

            } else {
                supprtPhoneNumber.setText("" + phone);
            }
            if (email.equals("")) {

            } else {
                supportEmailAddress.setText("" + email);
            }

            if (resultJsonObject.has("faq")) {
                String faq = resultJsonObject.getString("faq");
                if (faq.equals("")) {
                    faqs.setVisibility(View.GONE);
                } else {
                    faqAddress.setText("" + faq);
                }
            } else {
                faqs.setVisibility(View.GONE);
            }
            if (resultJsonObject.has("tutorial")) {
                String tutorial = resultJsonObject.getString("tutorial");
                if (tutorial.equals("")) {
                    tutoriallayout.setVisibility(View.GONE);
                } else {
                    faqtutorialvideolink.setText("" + tutorial);
                }
            } else {
                tutoriallayout.setVisibility(View.GONE);
            }
            if (resultJsonObject.has("point_collation")) {
                String pointCollection = resultJsonObject.getString("point_collation");
                SpannableString content = new SpannableString(pointCollection + "");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                collectionPoint.setText(content);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("Support");
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
            getFeedBackAndSupport();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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
