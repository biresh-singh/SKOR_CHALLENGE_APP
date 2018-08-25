package activity.facilitychecking;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.FacilityChackinCustomRecyclerView;
import bean.FaciltyCheckinItem;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;

public class FacilityCheckinListActivity extends AppCompatActivity {
    RecyclerView facilityRecyclerView;
    TextView toolbarTitleTextView;
    ArrayList<FaciltyCheckinItem> faciltyCheckinItems = new ArrayList<>();
    ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    LinearLayout backButtonLinearLayout;
    FrameLayout noDataFrameLayout;
    CheckInternetConnection checkInternetConnection;
    public SharedDatabase sharedDatabase;
    public String token;
    private int currentCategoryPk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_checkin_list);

        facilityRecyclerView = (RecyclerView) findViewById(R.id.facility_recycleview);
        checkInternetConnection = new CheckInternetConnection(FacilityCheckinListActivity.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.myCoordinatorLayout);
        backButtonLinearLayout = (LinearLayout) findViewById(R.id.back_button_linear_layout);
        noDataFrameLayout = (FrameLayout) findViewById(R.id.activity_facility_checkin_list_no_data_frame_layout);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbar_title);

        sharedDatabase = new SharedDatabase(FacilityCheckinListActivity.this);
        token = sharedDatabase.getToken();
        sharedDatabase.setPosition(6);
        sharedDatabase.setType("all");
        Loader.dialogDissmiss(FacilityCheckinListActivity.this);
        LinearLayoutManager llm = new LinearLayoutManager(FacilityCheckinListActivity.this);
        facilityRecyclerView.setLayoutManager(llm);
        facilityRecyclerView.setHasFixedSize(true);

        backButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                sharedDatabase.setPosition(1);
                sharedDatabase.setType("all");
                finish();
            }
        });

        toolbarTitleTextView.setText(getIntent().getExtras().getString("categoryName"));
        currentCategoryPk = Integer.parseInt(getIntent().getExtras().getString("pk"));

        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callFaciltyCheckinListApi();
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

    /* *******************calling facilities checking Api ***************************/

    public void callFaciltyCheckinListApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.FACILTY_CHECKIN + currentCategoryPk, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                Loader.dialogDissmiss(getApplicationContext());
                try {

                    JSONArray jsonArray = jsonObject.getJSONArray("facilities");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject facilityjsonObject = jsonArray.getJSONObject(i);
                            jsonObjectArrayList.add(facilityjsonObject);

                            String pk = facilityjsonObject.getString("pk");
                            String facilityName = facilityjsonObject.getString("name");
                            String facilityThumbnailImageUrl = facilityjsonObject.getString("thumbnail_img_url");
                            String facilityDisplayImageUrl = facilityjsonObject.getString("display_img_url");
                            String facilityLocationName = facilityjsonObject.getJSONArray("locations").getJSONObject(0).getString("name");
                            String facilityLocationAddress = facilityjsonObject.getJSONArray("locations").getJSONObject(0).getString("address");

//                            String imageUrl = facilityjsonObject.getString("display_img_url");
//                            JSONArray locationArray = facilityjsonObject.getJSONArray("locations");
//                            JSONObject locationJsonObject = locationArray.getJSONObject(0);
//                            String address = locationJsonObject.getString("address");
                            FaciltyCheckinItem faciltyCheckinItem = new FaciltyCheckinItem(pk, facilityName, "", facilityThumbnailImageUrl, facilityDisplayImageUrl,
                                    facilityLocationAddress, facilityLocationName);
                            faciltyCheckinItems.add(faciltyCheckinItem);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if(jsonArray.length() == 0) {
                        noDataFrameLayout.setVisibility(View.VISIBLE);
                    } else {
                        noDataFrameLayout.setVisibility(View.GONE);
                    }

                    if (FacilityCheckinListActivity.this != null) {
                        FacilityChackinCustomRecyclerView facilityChackinCustomRecyclerView = new FacilityChackinCustomRecyclerView(FacilityCheckinListActivity.this,
                                faciltyCheckinItems, jsonObjectArrayList);
                        facilityRecyclerView.setAdapter(facilityChackinCustomRecyclerView);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(FacilityCheckinListActivity.this);
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(FacilityCheckinListActivity.this);
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
            callFaciltyCheckinListApi();
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
