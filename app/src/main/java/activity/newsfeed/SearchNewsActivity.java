package activity.newsfeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import activity.userprofile.MainActivity;
import adaptor.SearchNewsAdapter;
import bean.FeedFeatured;
import bean.FeedFeaturedSearchResponse;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import io.realm.RealmList;
import listener.LoadMoreListener;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 9/28/17.
 */

public class SearchNewsActivity extends AppCompatActivity {
    LinearLayout backButton;
    EditText searchField;
    ImageView submitSearch;

    SearchNewsAdapter searchNewsAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView searchRecyclerView;


    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    FeedFeaturedSearchResponse feedFeaturedSearchResponse;
    RealmList<FeedFeatured> feedFeaturedSearchRealmList = new RealmList<>();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_news);
        backButton = (LinearLayout) findViewById(R.id.activity_search_news_backLinearLayout);
        searchRecyclerView = (RecyclerView) findViewById(R.id.activity_search_news_recyclerView);
        searchField = (EditText) findViewById(R.id.search_query);
        submitSearch = (ImageView) findViewById(R.id.go);


        //this is for api call
        deviceNames = new AndroidDeviceNames(SearchNewsActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(SearchNewsActivity.this);
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        //adapter
        searchNewsAdapter = new SearchNewsAdapter(this, loadMoreListener);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setNestedScrollingEnabled(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getFeedSearchList(searchField.getText().toString(), "");
                return true;
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    submitSearch.setBackgroundResource(R.drawable.btn_x);
                } else {
                    submitSearch.setBackgroundResource(R.drawable.search);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setText("");
            }
        });

    }

    public void getFeedSearchList(String searchKey, String prev) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/list/?search=" + searchKey, params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    feedFeaturedSearchResponse = new FeedFeaturedSearchResponse(jsonObject);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(feedFeaturedSearchResponse);
                    realm.commitTransaction();

                    feedFeaturedSearchRealmList = new RealmList<FeedFeatured>();
                    for (int i=0; i<feedFeaturedSearchResponse.getFeedFeaturedRealmList().size(); i++) {
                        feedFeaturedSearchRealmList.add(feedFeaturedSearchResponse.getFeedFeaturedRealmList().get(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                searchRecyclerView.setAdapter(searchNewsAdapter);
                searchNewsAdapter.updateAdapter(feedFeaturedSearchRealmList);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                connectivityMessage("Unable to log in with provided credentials.");
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(SearchNewsActivity.this);
                }

            }
        });
    }

    LoadMoreListener loadMoreListener = new LoadMoreListener() {
        @Override
        public void onLoadMoreListener() {

        }
    };

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = SearchNewsActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(SearchNewsActivity.this.getPackageName(), 0);
            versionCode  = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (SearchNewsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchNewsActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (SearchNewsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchNewsActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (SearchNewsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchNewsActivity.this).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
        }
    }


    @Override
    public void onBackPressed() {
        // code here to show dialog
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        MainActivity.isInternetConnection = false;
        intent.putExtra("action", Constants.ACTION_POINT_SUMMERY);
        startActivity(intent);
        finish();
        super.onBackPressed();  // optional depending on your needs
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
            getFeedSearchList(searchField.getText().toString(), "");
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
