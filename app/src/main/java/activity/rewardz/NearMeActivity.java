package activity.rewardz;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import adaptor.RewardzRecyclerViewAdapter;
import bean.RewardzListItem;
import constants.Constants;
import database.SharedDatabase;
import singleton.SettingsManager;
import utils.AppController;
import utils.GPSTracker;

/**
 * Created by Dihardja Software Solutions on 12/27/16.
 */

public class NearMeActivity extends AppCompatActivity implements OnMapReadyCallback {
    String url = "";
    ArrayList<RewardzListItem> rewardzListItems = new ArrayList<>();
//    ArrayList<String> filterItemArrayList = new ArrayList<>();
    boolean redeemableFlag;

    ArrayList<RewardzListItem> filteredRewardzListItems = new ArrayList<>();

    ArrayList<String> pkArrayList = new ArrayList<>();
    RewardzRecyclerViewAdapter rewardzRecyclerViewAdapter;
    CheckInternetConnection checkInternetConnection;
    TextView todaysTotalPoints;
    Dialog dialog;
    String points;
    int point_reward;
    int total_Points;
    String jsonResponse;
    RecyclerView recyclerView;
    LinearLayout backLayout, filter;
    LinearLayoutManager linearLayoutManager;
    TextView rewardzPointsTopBeearned;
    EditText search;
    ImageView go;
    String position = "", categorySlug = "", type = "";
    String name1, jsonResponse1, image, description, pk;
    int reward = 0;
    boolean isSearched;
    GoogleMap googleMap;
    GPSTracker gpsTracker;
    public String token;
    SharedDatabase sharedDatabase;
    SupportMapFragment mapFragment;
    HashMap<String, String> markerPKHashMap = new HashMap<>();
    public int remainingPoints;

    FrameLayout expandMaps;



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
        setContentView(R.layout.activity_near_me);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        sharedDatabase = new SharedDatabase(this);
        token = sharedDatabase.getToken();
        gpsTracker = new GPSTracker(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        rewardzPointsTopBeearned = (TextView) findViewById(R.id.points_to_be_earned);
        filter = (LinearLayout) findViewById(R.id.filter);
        search = (EditText) findViewById(R.id.search_query);
        backLayout = (LinearLayout) findViewById(R.id.menupanel);
        go = (ImageView) findViewById(R.id.go);
        linearLayoutManager = new LinearLayoutManager(this);
        expandMaps = (FrameLayout) findViewById(R.id.expandButton);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().hasExtra("category_type")) {
            categorySlug = getIntent().getStringExtra("category_type");
            type = getIntent().getStringExtra("type");
        }

        filter.setOnClickListener(filterTapped);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (gpsTracker.canGetLocation()) {
            position = "?lat=" + gpsTracker.getLatitude() + "&lng=" + gpsTracker.getLongitude();
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "";

        }
        isSearched = getIntent().getBooleanExtra("issearched", false);
        if (isSearched) {
            String query = getIntent().getStringExtra("query");
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&q=" + query;
//            filter.setVisibility(View.GONE);
        } else if (categorySlug.equals("")) {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position;
//            filter.setVisibility(View.GONE);
        } else if (categorySlug.equals("all")) {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position;
//            filter.setVisibility(View.GONE);
        } else {
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&category=" + categorySlug;
        }
        if (checkInternetConnection.isConnectingToInternet()) {

            //callTotalPointsSummeryApi();
            if (type.equals("point")) {
                callTotalPointsSummeryApi();
            } else if (type.equals("discount")) {
                callNearbyMeAPI();
            }

        } else {
            connectivityMessage("No Internet Connection");
        }

        hideSoftKeyboard();

        expandMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearMeActivity.this, ExpandedMapsNearMeActivity.class);
                intent.putExtra("category_type", categorySlug);
                intent.putExtra("type", type);
                intent.putExtra("typeDiscountOrPoints", "discount");
                startActivity(intent);
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                search.setCursorVisible(true);

            }
            /* ********************listener to searching on data**************************/

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = search.getText().toString();

                rewardzListItems.clear();
                for (int i = 0; i < filteredRewardzListItems.size(); i++) {
                    String name = filteredRewardzListItems.get(i).mFacilityItem;
                    if (searchText.length() <= name.length()) {
                        if (searchText.equalsIgnoreCase(name.substring(0, searchText.length()))) {

                            name1 = filteredRewardzListItems.get(i).mFacilityItem;
                            image = filteredRewardzListItems.get(i).mImageURL;
                            description = filteredRewardzListItems.get(i).mAddress;
                            reward = filteredRewardzListItems.get(i).reward;
                            redeemableFlag = filteredRewardzListItems.get(i).mredeemableFlag;

                            pk = pkArrayList.get(i);
                            points = filteredRewardzListItems.get(i).mPoint;
                            jsonResponse1 = filteredRewardzListItems.get(i).jsonResponse;
                            RewardzListItem rewardzListItem = new
                                    RewardzListItem(pk, image, name1, "", description, jsonResponse1, points, redeemableFlag, reward);
                            rewardzListItems.add(rewardzListItem);
                        }
                    }
                }

//                for (int i = 0; i < filterItemArrayList.size(); i++) {
//                    String name = filterItemArrayList.get(i);
//                    if (searchText.length() <= name.length()) {
//                        if (searchText.equalsIgnoreCase(name.substring(0, searchText.length()))) {
//
//                            name1 = filterItemArrayList.get(i);
//                            image = filterItemArrayList.get(i);
//
//                            description = filterItemArrayList.get(i);
//                            pk = pkArrayList.get(i);
//
//                            points = filterItemArrayList.get(i);
//                            jsonResponse1 = jsonResponse;
//                            RewardzListItem rewardzListItem = new
//                                    RewardzListItem(pk, image, name1, "", description, jsonResponse1, points, false, reward);
//                            rewardzListItems.add(rewardzListItem);
//                        }
//                    }
//                }
                if (rewardzRecyclerViewAdapter != null) {
                    rewardzRecyclerViewAdapter.notifyDataSetChanged();
                    populateNearMap(rewardzListItems);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                search.setCursorVisible(false);

            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        go.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (checkInternetConnection.isConnectingToInternet()) {


                        String query = search.getText().toString();
                        url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&q=" + query;
                        callNearbyMeAPI();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

                return false;


            }
        });
    }

    LinearLayout.OnClickListener filterTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RewardzFilterActivity.class);
            intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
            startActivityForResult(intent, 200);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            String subcategory = data.getStringExtra("sub_category");
            if (subcategory.equals("All")) {
                url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&category=" + categorySlug;
                callNearbyMeAPI();
            } else {
                url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "&category=" + categorySlug + "&sub_category=" + subcategory;
//                filterItemArrayList.clear();
                filteredRewardzListItems.clear();
                callNearbyMeAPI();
            }
        }
    }

    /* *******************calling point summary Api for get method to checking its points or discount***************************/

    public void callTotalPointsSummeryApi() {

        dialog = new Dialog(NearMeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();
        HashMap<String, String> paramMap = new HashMap<String, String>();
 /*       paramMap.put("username", usernameValue);
        paramMap.put("password", passwordValue);*/
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.TOTAL_POINTS_SUMMERY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
                }
                try {
                    if (type.equals("point")) {
                        rewardzPointsTopBeearned.setText("You have earned " + jsonObject.getString("remaining_points") + " Points to redeem");
                    }
                    callNearbyMeAPI();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();

                }
                if (statusCode == 400) {
                    connectivityMessage("" + errorResponse);
                }
                if (statusCode == 500) {
                    connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");

                }
            }
        });
    }

    /* *********************Calling Reward Listing Api*************************/

    public void callNearbyMeAPI() {
        rewardzListItems.clear();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();
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

                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
                }
                try {
                    pkArrayList.clear();
                    rewardzListItems.clear();
                    filteredRewardzListItems.clear();
//                    filterItemArrayList.clear();
                    total_Points = 0;
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject rewardzJson = jsonArray.getJSONObject(i);

                        String pk = rewardzJson.getString("pk");
                        pkArrayList.add(pk);

//                        points=rewardzJson.getString("points");
                        //   total_Points=total_Points+Integer.parseInt(pk);
                        redeemableFlag = rewardzJson.getBoolean("redeemable");
//                        filterItemArrayList.add(rewardzJson.getString("name"));
                        jsonResponse = rewardzJson.toString();
                        String imageUrl = rewardzJson.getString("thumbnail_img_url");
                        String desc = rewardzJson.getString("address");
                        int reward = rewardzJson.getInt("reward");
//                        String desc = "";
                        if(redeemableFlag) {
                            total_Points++;
                        }
                        RewardzListItem rewardzListItem = new RewardzListItem(pk, imageUrl, rewardzJson.getString("name"), "", desc, rewardzJson.toString(), points, redeemableFlag, reward);
                        rewardzListItems.add(rewardzListItem);
                        filteredRewardzListItems.add(rewardzListItem);
                    }
//
                    if (type.equals("discount")) {
                        rewardzPointsTopBeearned.setText("You have earned " + total_Points + " Rewards to redeem");
                    }

                    rewardzRecyclerViewAdapter = new RewardzRecyclerViewAdapter(getApplicationContext(), rewardzListItems, false, true, type);
                    recyclerView.setAdapter(rewardzRecyclerViewAdapter);

                    rewardzRecyclerViewAdapter.notifyDataSetChanged();
                    populateNearMap(rewardzListItems);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                /*updateUI(jsonObject.toString());*/

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
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

    public void hideSoftKeyboard(){
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void populateNearMap(final ArrayList<RewardzListItem> rewardzListItems){

        googleMap.clear();

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
                        intent.putExtra("typeDiscountOrPoints", type);
                        intent.putExtra("isFromNearMe", true);
                        startActivity(intent);
                    }
                });

//                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        Intent intent = new Intent(getApplicationContext(), RewardzDetailActivity.class);
//                        intent.putExtra("pk", rewardz);
//                        startActivity(intent);
//                        return false;
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("NearMeOpened");
    }
}
