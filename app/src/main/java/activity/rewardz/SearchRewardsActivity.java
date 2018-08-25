package activity.rewardz;

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

import java.util.ArrayList;
import java.util.HashMap;

import activity.userprofile.MainActivity;
import adaptor.SearchRewardsAdapter;
import bean.RewardzListItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.Loader;

/**
 * Created by dss-17 on 9/29/17.
 */

public class SearchRewardsActivity extends AppCompatActivity {

    LinearLayout backButton;
    EditText searchField;
    ImageView submitSearch;

    SearchRewardsAdapter searchRewardsAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView searchRecyclerView;


    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    ArrayList<RewardzListItem> rewardzListItemArrayList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_rewards);

        backButton = (LinearLayout) findViewById(R.id.activity_search_rewards_backLinearLayout);
        searchRecyclerView = (RecyclerView) findViewById(R.id.activity_search_rewards_recyclerView);
        searchField = (EditText) findViewById(R.id.search_query);
        submitSearch = (ImageView) findViewById(R.id.go);

        //this is for api call
        deviceNames = new AndroidDeviceNames(SearchRewardsActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(SearchRewardsActivity.this);
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        //adapter
        searchRewardsAdapter = new SearchRewardsAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setNestedScrollingEnabled(false);
        searchRecyclerView.setAdapter(searchRewardsAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getRewardSearchList(searchField.getText().toString());
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

    public void getRewardSearchList(String searchKey) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(10000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + Constants.REWARDZ_API + "?q=" + searchKey , params,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    rewardzListItemArrayList = new ArrayList<RewardzListItem>();

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject rewardzJson = jsonArray.getJSONObject(i);
                        RewardzListItem rewardzListItem = new RewardzListItem(rewardzJson.getString("pk"), rewardzJson.getString("thumbnail_img_url"), rewardzJson.getString("name"), "", rewardzJson.getString("description"), rewardzJson.toString(), rewardzJson.getString("points"), rewardzJson.getBoolean("redeemable"), 0);
                        rewardzListItemArrayList.add(rewardzListItem);
                    }
                    searchRewardsAdapter.updateAdapter(rewardzListItemArrayList);
                    Loader.dialogDissmiss(getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage( "We've encountered a technical error. our team is working on it. please try again later");
                Loader.dialogDissmiss(getApplicationContext());
            }
        });
    }


    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = SearchRewardsActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(SearchRewardsActivity.this.getPackageName(), 0);
            versionCode  = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (SearchRewardsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchRewardsActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (SearchRewardsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchRewardsActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (SearchRewardsActivity.this != null) {
                SnackbarManager.show(Snackbar.with(SearchRewardsActivity.this).text(msg).textColor(Color.WHITE).color(Color.RED));
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
            getRewardSearchList(searchField.getText().toString());
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
