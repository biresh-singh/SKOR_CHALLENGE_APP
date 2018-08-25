package activity.rewardz;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import bean.RewardzListItem;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.GPSTracker;

/**
 * Created by dss-17 on 3/6/17.
 */

public class ExpandedMapsNearMeActivity extends AppCompatActivity implements OnMapReadyCallback{
    GoogleMap googleMap;
    GPSTracker gpsTracker;

    HashMap<String, String> markerPKHashMap = new HashMap<>();
    ArrayList<RewardzListItem> rewardzListItems = new ArrayList<>();

    public String token;
    SharedDatabase sharedDatabase;

    String points;
    String url = "", type = "", categorySlug="", position = "", typeDiscountOrPoints = "discount";
    int total_Points;

    String jsonResponse;
    ArrayList<RewardzListItem> filteredRewardzListItems = new ArrayList<>();

    LinearLayout backButton;
    TextView rewardzPointsTopBeearned;
    SupportMapFragment mapFragment;





    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng currentPos = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 10));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandedmapsnearme);

        rewardzPointsTopBeearned = (TextView) findViewById(R.id.activity_expandedmapsnearme_points_to_be_earned);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_expandedmapsnearme_maps);
        mapFragment.getMapAsync(this);
        backButton = (LinearLayout) findViewById(R.id.menupanel);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedDatabase = new SharedDatabase(this);
        token = sharedDatabase.getToken();
        gpsTracker = new GPSTracker(this);

        if (gpsTracker.canGetLocation()) {
            position = "?lat=" + gpsTracker.getLatitude() + "&lng=" + gpsTracker.getLongitude();
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "";
        }

        if (getIntent().hasExtra("category_type")) {
            categorySlug = getIntent().getStringExtra("category_type");
            type = getIntent().getStringExtra("type");
        }

        if (categorySlug.equals("")) {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position;
//            filter.setVisibility(View.GONE);
        } else if (categorySlug.equals("all")) {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position;
//            filter.setVisibility(View.GONE);
        } else {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&category=" + categorySlug;
        }
        if(type.equals("discount")) {
            callNearbyMeAPI();
        }
    }

    public void callNearbyMeAPI() {
        rewardzListItems.clear();
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.loader);
//        dialog.show();
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

//                if (dialog.isShowing() || dialog != null) {
//                    dialog.dismiss();
//                    dialog.hide();
//                }
                try {
//                    pkArrayList.clear();
                    rewardzListItems.clear();
                    filteredRewardzListItems.clear();
                    total_Points = 0;
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject rewardzJson = jsonArray.getJSONObject(i);

                        String pk = rewardzJson.getString("pk");
//                        pkArrayList.add(pk);

                        boolean redeemableFlag = rewardzJson.getBoolean("redeemable");
                        jsonResponse = rewardzJson.toString();
                        String imageUrl = rewardzJson.getString("thumbnail_img_url");
                        String desc = rewardzJson.getString("address");
                        int reward = rewardzJson.getInt("reward");
                        if(redeemableFlag) {
                            total_Points++;
                        }
                        RewardzListItem rewardzListItem = new RewardzListItem(pk, imageUrl, rewardzJson.getString("name"), "", desc, rewardzJson.toString(), points, redeemableFlag, reward);
                        rewardzListItems.add(rewardzListItem);
                        filteredRewardzListItems.add(rewardzListItem);
                    }

                    rewardzPointsTopBeearned.setText("You have earned " + total_Points + " Rewards to redeem");

                    populateNearMap(rewardzListItems);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(ExpandedMapsNearMeActivity.this);
                }
            }
        });
    }



    public void populateNearMap(final ArrayList<RewardzListItem> rewardzListItems){
        markerPKHashMap = new HashMap<>();

        for(final RewardzListItem rewardz : rewardzListItems) {
            try {
                final JSONObject rewardzJSONObject = new JSONObject(rewardz.jsonResponse);
                final JSONArray coordinates = rewardzJSONObject.getJSONArray("coordinates");

                MarkerOptions newMarker = new MarkerOptions()
                        .position(new LatLng(coordinates.getDouble(1), coordinates.getDouble(0)))
                        .title(rewardz.mFacilityItem);
                Marker theMarker = googleMap.addMarker(newMarker);

                markerPKHashMap.put(theMarker.getId(), String.valueOf(rewardz.reward));

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        String pk = markerPKHashMap.get(marker.getId());
                        Intent intent = new Intent(getApplicationContext(), RewardzDetailActivity.class);
                        intent.putExtra("pk", pk);
                        intent.putExtra("typeDiscountOrPoints", typeDiscountOrPoints);
                        intent.putExtra("isFromNearMe", true);
                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
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
            callNearbyMeAPI();
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
