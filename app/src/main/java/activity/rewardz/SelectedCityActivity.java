package activity.rewardz;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import adaptor.RewardzRecyclerViewAdapter;
import bean.RewardzListItem;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;


/**
 * Created by Dihardja Software Solutions on 12/27/16.
 */

public class SelectedCityActivity extends AppCompatActivity {

    ArrayList<RewardzListItem> rewardzListItems = new ArrayList<>();
    private CheckInternetConnection checkInternetConnection;
    public boolean isSelectedCity = true;
    private RecyclerView recyclerView;
    private boolean isSearched;
    private EditText search;
    private RewardzRecyclerViewAdapter rewardzRecyclerViewAdapter;
    private Dialog dialog;
    private TextView headerTitle;
    private int total_Points;
    private ArrayList<String> filterItemArrayList = new ArrayList<>();
    private ArrayList<String> pkArrayList = new ArrayList<>();
    private String url;
    private String city;
    private String city_id;
    private String name1;
    private RelativeLayout headerLayout;
    private String category;
    static String type;
    private String points;
    private String categorySlug = "";
    private String jsonResponse;
    private TextView rewardzPointsTopBeearned;
    private ImageView panelImageView;
    private String image;
    String jsonResponse1;
    String description;
    String pk;
    public SharedDatabase sharedDatabase;
    public String token;
    private LinearLayout filter;
    private ImageView go;
    FrameLayout noRewards;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city);
        findViewById();
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        if (getIntent().hasExtra("city")) {
//            url = getIntent().getStringExtra("url");
            type = getIntent().getStringExtra("type");
            city_id = getIntent().getStringExtra("city_id");
            city = getIntent().getStringExtra("city");
            categorySlug = getIntent().getStringExtra("category_type");
            category = getIntent().getStringExtra("category");
            type = getIntent().getStringExtra("type");
        }
        url = Constants.BASEURL+ Constants.REWARDZ_BY_CITY+city;
        Log.e("onCreate: ", url);


        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        headerTitle.setText(city.toUpperCase());


        isSelectedCity = getIntent().getBooleanExtra("isselected_city_screen", false);
        if (isSelectedCity) {
            headerLayout.setVisibility(View.GONE);
            headerTitle.setText("Search Results");
        }
//        headerTitle.setText(category.toUpperCase());
        isSearched = getIntent().getBooleanExtra("issearched", false);
        if (isSearched == true) {
            String query = getIntent().getStringExtra("query");
            url = Constants.BASEURL + Constants.REWARDZ_API + "?city_id=" + city_id + "&q=" + query;
//            filter.setVisibility(View.GONE);
        }
        else if (categorySlug.equals("")) {
            url = Constants.BASEURL + Constants.REWARDZ_API+ "?city_id=" + city_id;
//            filter.setVisibility(View.GONE);
        }
        else if (categorySlug.equals("all")) {
            url = Constants.BASEURL + Constants.REWARDZ_API+ "?city_id=" + city_id;
//            filter.setVisibility(View.GONE);
        }
        else {
            url = Constants.BASEURL + Constants.REWARDZ_API + "?city_id="+ city_id +"&category=" + categorySlug;


            if(category.equals("cafe")) {
                url = Constants.BASEURL + Constants.REWARDZ_API + "?category=" + categorySlug +"&sub_category=" +category;
            }
        }

        if (checkInternetConnection.isConnectingToInternet()) {
            if (type.equals("point")) {
                callTotalPointsSummeryApi();
            } else if (type.equals("discount")) {
                callSelectedCityApi();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

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
                for (int i = 0; i < filterItemArrayList.size(); i++) {
                    String name = filterItemArrayList.get(i);
                    if (searchText.length() <= name.length()) {
                        if (searchText.equalsIgnoreCase(name.substring(0, searchText.length()))) {

                            name1 = filterItemArrayList.get(i);
                            image = filterItemArrayList.get(i);
                            description = filterItemArrayList.get(i);
                            pk = pkArrayList.get(i);

                            points = filterItemArrayList.get(i);
                            jsonResponse1 = jsonResponse;
                            RewardzListItem rewardzListItem = new
                                    RewardzListItem(pk, image, name1, "", description, jsonResponse1, points, false, 0);
                            rewardzListItems.add(rewardzListItem);
                        }
                    }
                }
                if (rewardzRecyclerViewAdapter != null) {
                    rewardzRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                search.setCursorVisible(false);

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
                        url = Constants.BASEURL + Constants.REWARDZ_API + "?q=" + query  + "&city_id=" + city;
                        callSelectedCityApi();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

                return false;


            }
        });

        panelImageView.setBackgroundResource(R.drawable.back_button);
        panelImageView.setOnClickListener(panelImageTapped);
        filter.setOnClickListener(filterTapped);
        go.setOnClickListener(goTapped);

    }

    private void findViewById() {
        panelImageView = (ImageView) findViewById(R.id.activity_selected_city_panel);
        recyclerView = (RecyclerView) findViewById(R.id.activity_selected_city_recyclerview);
        rewardzPointsTopBeearned = (TextView) findViewById(R.id.points_to_be_earned);
        headerTitle = (TextView) findViewById(R.id.toolbar_title);
        headerLayout = (RelativeLayout) findViewById(R.id.headerlayout);
        search = (EditText) findViewById(R.id.search_query);
        filter = (LinearLayout) findViewById(R.id.activity_selected_city_filter);
        go = (ImageView) findViewById(R.id.go);
        noRewards = (FrameLayout) findViewById(R.id.activity_selected_city_noRewardsFrameLayout);
    }

    ImageView.OnClickListener panelImageTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    LinearLayout.OnClickListener filterTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RewardzFilterActivity.class);
            intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
            startActivityForResult(intent, 200);
        }
    };

    ImageView.OnClickListener goTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rewardzListItems.clear();

            if (checkInternetConnection.isConnectingToInternet()) {
                if (search.getText().toString().equals(name1)) {
                    String query = search.getText().toString();
                    url = Constants.BASEURL + Constants.REWARDZ_API + "?q=" + query;
                    callSelectedCityApi();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void callTotalPointsSummeryApi() {
        dialog = new Dialog(this);
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
                    callSelectedCityApi();
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

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(SelectedCityActivity.this);
                }
                if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            String subcategory = data.getStringExtra("sub_category");
            if (subcategory.equalsIgnoreCase("All")) {
                url = Constants.BASEURL + Constants.REWARDZ_API + "?city_id="+ city_id + "&category=" + categorySlug;
                callSelectedCityApi();
            } else {
                filterItemArrayList.clear();
                url = Constants.BASEURL + Constants.REWARDZ_API + "?city_id="+ city_id + "&category=" + categorySlug + "&sub_category=" + subcategory;
                callSelectedCityApi();
            }
        }
    }


    public void callSelectedCityApi() {
        filterItemArrayList.clear();
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
//
//                if (dialog.isShowing() || dialog != null) {
//                    dialog.dismiss();
//                    dialog.hide();
//                }
                try {
                        total_Points = 0;
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    Log.e("onSuccess: ", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject rewardzJson = jsonArray.getJSONObject(i);
                        Log.e("onSuccess: ", jsonArray.getJSONObject(i).toString());
                        String pk = rewardzJson.getString("pk");
                        pkArrayList.add(pk);
                        //points = rewardzJson.getString("points");
                        //   total_Points=total_Points+Integer.parseInt(pk);

                        boolean redeemableFlag = rewardzJson.getBoolean("redeemable");
                        if (redeemableFlag) {
                            total_Points++;
                        }
                        filterItemArrayList.add(rewardzJson.getString("name"));
                        jsonResponse = rewardzJson.toString();

                        RewardzListItem rewardzListItem = new RewardzListItem(pk, rewardzJson.getString("thumbnail_img_url"), rewardzJson.getString("name"), "", rewardzJson.getString("description"), rewardzJson.toString(), points, redeemableFlag, 0);
                        rewardzListItems.add(rewardzListItem);
                    }

                    if(jsonArray.length() == 0) {
                        noRewards.setVisibility(View.VISIBLE);
                    }

                    if (type.equals("discount")) {
                        rewardzPointsTopBeearned.setText("You have earned " + total_Points + " Rewards to redeem");
                    }

                    rewardzRecyclerViewAdapter = new RewardzRecyclerViewAdapter(getApplicationContext(), rewardzListItems, isSelectedCity, false, type);
                    recyclerView.setAdapter(rewardzRecyclerViewAdapter);

                    rewardzRecyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(SelectedCityActivity.this);
                }

//                if (dialog.isShowing() || dialog != null) {
//                    dialog.dismiss();
//                    dialog.hide();
//                }
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
            if (type.equals("point")) {
                callTotalPointsSummeryApi();
            } else if (type.equals("discount")) {
                callSelectedCityApi();
            }
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
