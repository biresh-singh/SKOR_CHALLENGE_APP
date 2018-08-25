package activity.history;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import adaptor.AnniversaryBirthdayAdapter;
import bean.AnniversaryResponse;
import bean.BirthdayResponse;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 10/11/17.
 */

public class AnniversaryBirthdayListActivity extends AppCompatActivity{
    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    LinearLayout backButton;
    TextView titleTextView, birthdayButton, anniversaryButton, noDataTextView;
    RecyclerView birthdayRecyclerView, anniversaryRecyclerView;

    AnniversaryBirthdayAdapter anniversaryAdapter;
    AnniversaryBirthdayAdapter birthdayAdapter;
    LinearLayoutManager linearLayoutManagerAnniversary;
    LinearLayoutManager linearLayoutManagerBirthday;

    BirthdayResponse birthdayResponse = null;
    AnniversaryResponse anniversaryResponse = null;

    boolean isAnniv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anniversary_birthday_list);

        //this is for api call
        deviceNames = new AndroidDeviceNames(this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(this);
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        backButton = (LinearLayout) findViewById(R.id.activity_anniversary_birthday_list_backLinearLayout);
        titleTextView = (TextView) findViewById(R.id.activity_anniversary_birthday_list_titleTextView);
        birthdayButton = (TextView) findViewById(R.id.activity_anniversary_birthday_list_birthdayTextView);
        anniversaryButton = (TextView) findViewById(R.id.activity_anniversary_birthday_list_anniversaryRelativeLayout);
        birthdayRecyclerView = (RecyclerView) findViewById(R.id.activity_anniversary_birthday_list_birthdayRecyclerView);
        anniversaryRecyclerView = (RecyclerView) findViewById(R.id.activity_anniversary_birthday_list_anniversaryRecyclerView);
        noDataTextView = (TextView) findViewById(R.id.activity_anniversary_birthday_list_noAnniversaryOrBirthdayTextView);

        linearLayoutManagerAnniversary = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        anniversaryAdapter = new AnniversaryBirthdayAdapter(this);
        anniversaryRecyclerView.setAdapter(anniversaryAdapter);
        anniversaryRecyclerView.setLayoutManager(linearLayoutManagerAnniversary);

        linearLayoutManagerBirthday = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        birthdayAdapter = new AnniversaryBirthdayAdapter(this);
        birthdayRecyclerView.setAdapter(birthdayAdapter);
        birthdayRecyclerView.setLayoutManager(linearLayoutManagerBirthday);

        isAnniv = getIntent().getBooleanExtra("isAnniv", true);

        if (isAnniv) {
            anniversaryRecyclerView.setVisibility(View.VISIBLE);
            birthdayRecyclerView.setVisibility(View.GONE);
            getBirthdayList(isAnniv);
            anniversaryButton.setTextColor(Color.parseColor("#000000"));
            birthdayButton.setTextColor(Color.parseColor("#dfdfdf"));
            titleTextView.setText("Anniversary");
        } else {
            anniversaryRecyclerView.setVisibility(View.GONE);
            birthdayRecyclerView.setVisibility(View.VISIBLE);
            getBirthdayList(isAnniv);
            anniversaryButton.setTextColor(Color.parseColor("#dfdfdf"));
            birthdayButton.setTextColor(Color.parseColor("#000000"));
            titleTextView.setText("Birthday");
        }

        initListener();

    }

    private void initListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        anniversaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anniversaryRecyclerView.setVisibility(View.VISIBLE);
                birthdayRecyclerView.setVisibility(View.GONE);
                getBirthdayList(true);
                anniversaryButton.setTextColor(Color.parseColor("#000000"));
                birthdayButton.setTextColor(Color.parseColor("#dfdfdf"));
                titleTextView.setText("Anniversary");
            }
        });

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anniversaryRecyclerView.setVisibility(View.GONE);
                birthdayRecyclerView.setVisibility(View.VISIBLE);
                getBirthdayList(false);
                anniversaryButton.setTextColor(Color.parseColor("#dfdfdf"));
                birthdayButton.setTextColor(Color.parseColor("#000000"));
                titleTextView.setText("Birthday");
            }
        });
    }

    public void getBirthdayList(final boolean isAnniv) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "events/api/greetings/today/", params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject object = new JSONObject(responseString);
                    if (object.has("Birthday")) {
                        birthdayResponse = new BirthdayResponse(object.getJSONArray("Birthday"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(birthdayResponse);
                        realm.commitTransaction();
                    }
                    if (object.has("Anniversary")) {
                        anniversaryResponse = new AnniversaryResponse(object.getJSONArray("Anniversary"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(anniversaryResponse);
                        realm.commitTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isAnniv) {
                    if (anniversaryResponse != null) {
                        anniversaryAdapter.updateAnniversary(anniversaryResponse, true);
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE);
                        noDataTextView.setText("No anniversary today");
                    }
                } else {
                    if (birthdayResponse != null) {
                        birthdayAdapter.updateBirthday(birthdayResponse, false);
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE);
                        noDataTextView.setText("No birthday today");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(AnniversaryBirthdayListActivity.this);
                }
            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            versionCode  = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (this != null) {
                SnackbarManager.show(Snackbar.with(this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (this != null) {
                SnackbarManager.show(Snackbar.with(this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (this != null) {
                SnackbarManager.show(Snackbar.with(this).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
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
            getBirthdayList(isAnniv);
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
