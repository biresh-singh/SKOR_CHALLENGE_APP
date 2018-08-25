package activity.history;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import activity.skorchat.PreviewNewActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;


public class AnnouncementDetailActivity extends AppCompatActivity {
    private static final String TAG = "AnnouncementDetActivity";
    private ImageView userImage;
    private TextView announcementFrom;
    private TextView announcementDetail;
    private String selectedAnnouncementjson;
    private TextView readMore;
    private String link = "";
    private RelativeLayout relativeLayout;
    private String url;
    private String readMoreButtonBackGroundColor = "#FF5722";
    private String[] colors = {"#FF5722", "#42d583"};
    private Dialog dialog;
    private String token;
    private SharedDatabase sharedDatabase;
    WebView webView;
    String announcementId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcement_detail);
        userImage = (ImageView) findViewById(R.id.imageurl);
        announcementFrom = (TextView) findViewById(R.id.announcement_from);
        announcementDetail = (TextView) findViewById(R.id.desc_text);
        readMore = (TextView) findViewById(R.id.readmore);
        LinearLayout back = (LinearLayout) findViewById(R.id.back);
        relativeLayout = (RelativeLayout) findViewById(R.id.titlebar);
        webView = (WebView) findViewById(R.id.webView);
        sharedDatabase = new SharedDatabase(this);
        token = sharedDatabase.getToken();

        if(getIntent().hasExtra("announcementId")) {
            announcementId = getIntent().getStringExtra("announcementId");
            getAnnouncementDetailFromServer(announcementId);
        }else {
            selectedAnnouncementjson = getIntent().getStringExtra("selected_announcement_json");
            populateData(selectedAnnouncementjson);
        }

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JSONObject props = new JSONObject();
        try {
            props.put("announcementId", announcementId);
            AppController.getInstance().getMixpanelAPI().track("annoucementDetail", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateData(String announcementJSONString) {
        try {
            final JSONObject jsonObject = new JSONObject(announcementJSONString);
            String imageUrl = jsonObject.getString("display");
            if (imageUrl.equals("")) {
                relativeLayout.setBackgroundColor(Color.parseColor("#42d583"));
                readMore.setBackgroundResource(R.drawable.announce_readmore1);
                readMore.setTextColor(Color.WHITE);
                userImage.setBackgroundResource(R.drawable.announcements);
            } else {
                readMore.setBackgroundResource(R.drawable.announce_readmore2);
                readMore.setTextColor(Color.WHITE);
                Glide.with(getApplicationContext()).load(Constants.BASEURL + jsonObject.getString("display")).into(userImage);

                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AnnouncementDetailActivity.this, PreviewNewActivity.class);
                        ArrayList<String> urls = new ArrayList<String>();
                        try {
                            urls.add(Constants.BASEURL + jsonObject.getString("display"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putStringArrayListExtra("urls", urls);
                        intent.putExtra("index", 0);
                        intent.putExtra("name", "news");
                        intent.putExtra("type", "Image");
                        startActivity(intent);
                    }
                });
            }
            announcementFrom.setText(jsonObject.getString("name"));
            url = jsonObject.getString("url");
            String loadurl = jsonObject.getString("description");
            System.out.println("helloggggg" + loadurl);
            loadurl = loadurl.replace("<\\/p>", "</p>");
            loadurl = loadurl.replaceAll("\n", " ");
            loadurl = loadurl.replaceAll("&nbsp;", " ");
            loadurl = loadurl.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
            loadurl = loadurl.replaceAll("\r\n", " ");

//            announcementDetail.setText(Html.fromHtml(loadurl));
            Linkify.addLinks(announcementDetail, Linkify.ALL);
            URLSpan spans[] = announcementDetail.getUrls();
            for (URLSpan span : spans) {
                link = span.getURL();

            }
            System.out.print(link);

            String webViewURL = jsonObject.getString("description");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setBackgroundColor(444444);
            webView.getSettings().setDefaultFontSize(14);
            webView.loadDataWithBaseURL("about:blank", webViewURL,"text/html", "utf-8", null);
            Log.d(TAG, "Description URL : "+ webViewURL);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        if (!url.equals("")) {
            readMore.setVisibility(View.VISIBLE);
        }
    }

    public void getAnnouncementDetailFromServer(final String announcementId) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(8000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.ANNOUNCEMENT_DETAIL + announcementId + "/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                }

                populateData(jsonObject.toString());

//                if (jsonObject.has("Announcement")) {
//                    try {
//                        JSONArray announcements = jsonObject.getJSONArray("Announcement");
//                        for (int i=0; i<announcements.length(); i++) {
//                            if (announcements.getJSONObject(i).getString("pk").equalsIgnoreCase(announcementId)) {
//                                populateData(announcements.getJSONObject(i).toString());
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                try {
//                    ShortcutBadger.applyCount(AnnouncementDetailActivity.this, Integer.parseInt(jsonObject.getString("badge_counter")));
//                    String pklength1 = jsonObject.getString("badge_counter");
//                    if (!pklength1.equals("0") && !pklength1.equals("null") && !pklength1.equals("")) {
//                        searchcounter.setVisibility(View.VISIBLE);
//                        searchcounter.setText("" + pklength1);
//                        if (getActivity() != null) {
//                            ShortcutBadger.applyCount(AnnouncementDetailActivity.this, Integer.parseInt(pklength1));
//                        }
//                    } else {
//                        searchcounter.setVisibility(View.GONE);
//                    }
//                } catch (JSONException js) {
//                    js.printStackTrace();
//                }

//                try {
//                    Iterator keys = jsonObject.keys();
//                    while (keys.hasNext()) {
//                        String currentDynamicKey = (String) keys.next();
//                        if (!currentDynamicKey.equals("badge_counter")) {
//                            if (currentDynamicKey.equals("greetings")) {
//
//                                JSONObject greetingJsonObjects = jsonObject.getJSONObject("greetings");
//                                JSONArray jsonArray = new JSONArray();
//                                jsonArray.put(greetingJsonObjects);
//                                hashMap.put(currentDynamicKey, jsonArray);
//                                System.out.println(hashMap);
//                            } else {
//
//                                JSONArray currentDynamicValue = jsonObject.getJSONArray(currentDynamicKey);
//                                hashMap.put(currentDynamicKey, currentDynamicValue);
//                                System.out.println("json is" + currentDynamicValue);
//                            }
//                        }
//                    }
//                    System.out.println("Hash Map is" + hashMap);
//                    pointsSummeryTextView.setClickable(true);


//                    if (getActivity() != null) {
//                        whatsOnListAdapter = new WhatsOnListAdapter(activity, R.layout.whats_on_adapter, keysArrayList);
//                        ddListView.setItemsInList(keysArrayList);
//                        ddListView.setAdapter(whatsOnListAdapter);
//                        ddListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                    }
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(AnnouncementDetailActivity.this);
                }

                if (statusCode == 0) {
                    Toast.makeText(AnnouncementDetailActivity.this, "Bad Network Connection!", Toast.LENGTH_LONG).show();
                }

                System.out.print("net connection   " + errorResponse);
                if (statusCode == 500) {
                    Toast.makeText(AnnouncementDetailActivity.this, "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRetry(int retryNo) {

            }
        });
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
            getAnnouncementDetailFromServer(announcementId);
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