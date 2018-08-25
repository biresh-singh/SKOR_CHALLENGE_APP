package activity.rewardz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.RewardzRecyclerViewAdapter;
import adaptor.SelectCityListViewAdapter;
import bean.RewardzListItem;
import bean.SelectCityListItem;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import listener.SelectCityListener;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;

/**
 * Created by jeet on 7/1/16.
 */
public class EligibleRewardzActivity extends Activity {
    ArrayList<RewardzListItem> rewardzListItems = new ArrayList<>();
    ArrayList<RewardzListItem> filteredRewardzListItems = new ArrayList<>();
    ArrayList<SelectCityListItem> selectCityListItems = new ArrayList<>();
    HashMap<String, String> params = new HashMap<>();
    RecyclerView recyclerView;
    TextView yourRewardz, allRewardz;
    CheckInternetConnection checkInternetConnection;
    TextView todaysTotalPoints;
    Dialog dialog;
    String jsonResponse;
    String name1;
    TextView headerTitle;
    int reward;
    boolean redeemableFlag;
    RelativeLayout headerLayout;
    ImageView panelImageView;
    public boolean isElegibleRewardz = true;
    String categorySlug = "";
    LinearLayout filter;
    String url;
    String urlSelectCity;
    static public String type;
    static String city;
    static String cityName;
    ArrayList<String> filterItemArrayList = new ArrayList<>();
    ArrayList<String> pkArrayList = new ArrayList<>();
    ArrayList<String> pkArrayListUsingKey = new ArrayList<>();
    private ListView selectCityListView;
    public SharedDatabase sharedDatabase;
    public String token;
    String urlWithSubCategory;
    String category;
    boolean isSearched;
    TextView rewardzPointsTopBeearned;
    ImageView go;
    RewardzRecyclerViewAdapter rewardzRecyclerViewAdapter;
    SelectCityListViewAdapter selectCityViewAdapter;
    FrameLayout nearMeFrameLayout, selectCityFrameLayout;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    EditText search;
    String description;
    String pk;
    String points;
    int point_reward;
    int total_Points;
    String jsonResponse1;
    String image;
    private ImageView selectedCityImageView;
    TextView nearMeText, selectCityText;

    RelativeLayout rootRelativeLayout;
    FrameLayout cityFrameLayout;
    ListView selectCitynewListView;
    FrameLayout cancelButton, okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewardz);

        findViewById();
        recyclerView = (RecyclerView) findViewById(R.id.rewardz_recyclerview);

        yourRewardz = (TextView) findViewById(R.id.yourRewards);
        allRewardz = (TextView) findViewById(R.id.allRewards);
        headerTitle = (TextView) findViewById(R.id.toolbar_title);
        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.titlebar);
        headerLayout = (RelativeLayout) findViewById(R.id.headerlayout);
        filter = (LinearLayout) findViewById(R.id.filter);
        panelImageView = (ImageView) findViewById(R.id.panel);
        search = (EditText) findViewById(R.id.search_query);

        nearMeText = (TextView) findViewById(R.id.nearMeText);
        selectCityText = (TextView) findViewById(R.id.selectCityText);
        selectCityListView.setVisibility(View.GONE);

        cityFrameLayout.setVisibility(View.GONE);

        urlSelectCity = Constants.BASEURL + Constants.REWARDZ_SELECT_CITY;
        Log.e("onClick: ", urlSelectCity);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();

        nearMeFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EligibleRewardzActivity.this, NearMeActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("category_type",categorySlug);
                intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
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
                            jsonResponse1 = jsonResponse;
                            RewardzListItem rewardzListItem = new
                                    RewardzListItem(pk, image, name1, "", description, jsonResponse1, points, redeemableFlag, reward);
                            rewardzListItems.add(rewardzListItem);
                        }
                    }
                }

//                rewardzListItems.clear();
//                for (int i = 0; i < filterItemArrayList.size(); i++) {
//                    String name = filterItemArrayList.get(i);
//                    if (searchText.length() <= name.length()) {
//                        if (searchText.equalsIgnoreCase(name.substring(0, searchText.length()))) {
//
//                            name1 = filterItemArrayList.get(i);
//                            image = filterItemArrayList.get(i);
//                            description = filterItemArrayList.get(i);
//                            pk = pkArrayList.get(i);
//
//                            points = filterItemArrayList.get(i);
//                            jsonResponse1 = jsonResponse;
//                            RewardzListItem rewardzListItem = new
//                                    RewardzListItem(pk, image, name1, "", description, jsonResponse1, points, false, 0);
//                            rewardzListItems.add(rewardzListItem);
//                        }
//                    }
//                }
                if (rewardzRecyclerViewAdapter != null) {
                    rewardzRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                search.setCursorVisible(false);

            }
        });

/* ****************go button clicking to perform action to search data******************************/

        go = (ImageView) findViewById(R.id.go);
        go.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (checkInternetConnection.isConnectingToInternet()) {


                        String query = search.getText().toString();
                        url = Constants.BASEURL + Constants.REWARDZ_API + "?q=" + query;
                        try {
                            callRewardzApi();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        connectivityMessage("No Internet Connection");
                    }
                    return false;
                }

                return false;


            }
        });


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardzListItems.clear();

                if (checkInternetConnection.isConnectingToInternet()) {
                    if (search.getText().toString().equals(name1)) {

                        String query = search.getText().toString();
                        url = Constants.BASEURL + Constants.REWARDZ_API + "?q=" + query;
                        callRewardzApi();
                    }
                } else {
                    connectivityMessage("No Internet Connection");

                }
            }
        });

        rewardzPointsTopBeearned = (TextView) findViewById(R.id.points_to_be_earned);
        isElegibleRewardz = getIntent().getBooleanExtra("iseligible_rewardz_screen", false);
        if (isElegibleRewardz) {
            headerLayout.setVisibility(View.GONE);
            headerTitle.setText("Search Results");
        }
        categorySlug = getIntent().getStringExtra("category_type");
        category = getIntent().getStringExtra("category");
        nearMeText.setText("See " + category + " stores near you");
        selectCityText.setText("See " + category + " stores in your city");
        type = getIntent().getStringExtra("type");
        if(type.equalsIgnoreCase("discount")) {
            sharedDatabase.setIsDiscount(true);
        }else {
            sharedDatabase.setIsDiscount(false);
        }
        isSearched = getIntent().getBooleanExtra("issearched", false);
        if (isSearched == true) {

            String query = getIntent().getStringExtra("query");
            url = Constants.BASEURL + Constants.REWARDZ_API + "?q=" + query;
            filter.setVisibility(View.GONE);
        } else if (categorySlug.equals("")) {
            url = Constants.BASEURL + Constants.REWARDZ_API;
            filter.setVisibility(View.GONE);
        } else if (categorySlug.equals("all")) {
            url = Constants.BASEURL + Constants.REWARDZ_API;
            filter.setVisibility(View.GONE);
        } else {
            headerTitle.setText(category.toUpperCase());
//            if (category.equalsIgnoreCase("food & beverage")) {
//                selectCityFrameLayout.setVisibility(View.VISIBLE);
//                nearMeFrameLayout.setVisibility(View.VISIBLE);
//            }
            url = Constants.BASEURL + Constants.REWARDZ_API + "?category=" + categorySlug;
        }


        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        yourRewardz.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        allRewardz.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        rewardzPointsTopBeearned.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        if (checkInternetConnection.isConnectingToInternet()) {

            //callTotalPointsSummeryApi();
            if (type.equals("point")) {
                callTotalPointsSummeryApi();
                callselectCityApi();
                selectCityFrameLayout.setVisibility(View.GONE);
                nearMeFrameLayout.setVisibility(View.GONE);
            } else if (type.equals("discount")) {
                callselectCityApi();
                callRewardzApi();
            }

        } else {
            connectivityMessage("No Internet Connection");
        }
        yourRewardz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rewardzListItems.clear();
                yourRewardz.setTextColor(Color.BLACK);
                yourRewardz.setBackgroundResource(R.drawable.text_bg);
                allRewardz.setBackgroundColor(Color.TRANSPARENT);
                allRewardz.setTextColor(Color.WHITE);
                if (checkInternetConnection.isConnectingToInternet()) {
                    callRewardzApi();
                } else {
                    connectivityMessage("No Internet Connection");
                }

            }
        });
        allRewardz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    rewardzListItems.clear();
                    yourRewardz.setTextColor(Color.WHITE);
                    allRewardz.setBackgroundResource(R.drawable.text_bg);
                    yourRewardz.setBackgroundColor(Color.TRANSPARENT);
                    allRewardz.setTextColor(Color.BLACK);
                    try {
                        callRewardzApi();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    connectivityMessage("No Internet Connection");
                }
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RewardzFilterActivity.class);
                intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
                startActivityForResult(intent, 200);
            }
        });
        LinearLayout panel = (LinearLayout) findViewById(R.id.menupanel);
        panelImageView.setBackgroundResource(R.drawable.back_button);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    MainActivity.isInternetConnection = false;
                    sharedDatabase.setPosition(12);
                    sharedDatabase.setType(type);
                    finish();
                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage("No Internet Connection");
                    }
                }

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(EligibleRewardzActivity.this, SelectedCityActivity.class);
                    intent.putExtra("city_id", city);
                    intent.putExtra("category", category);
                    intent.putExtra("city", cityName);
                    intent.putExtra("url", url + "&city_id=" + city);
                    intent.putExtra("category_type", categorySlug);
                    intent.putExtra("issearched", false);
                    intent.putExtra("type", type);
                    intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
                    startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityFrameLayout.setVisibility(View.GONE);
            }
        });

        cityFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

    }

    private void findViewById() {
        selectCityListView = (ListView) findViewById(R.id.selectCityListView);
        selectedCityImageView = (ImageView) findViewById(R.id.selected_city_imageView);
        recyclerView = (RecyclerView) findViewById(R.id.rewardz_recyclerview);
        nearMeFrameLayout = (FrameLayout) findViewById(R.id.nearMeLayout);
        selectCityFrameLayout = (FrameLayout) findViewById(R.id.selectCityLayout);
        yourRewardz = (TextView) findViewById(R.id.yourRewards);
        allRewardz = (TextView) findViewById(R.id.allRewards);
        //  todaysTotalPoints=(TextView)findViewById(R.id.total_points_of_today);
        headerTitle = (TextView) findViewById(R.id.toolbar_title);
        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.titlebar);
      /*  if (Constants.BASEURL.equals("https://cerrapoints.ae/")) {
            titlebar.setBackground(null);
            titlebar.setBackgroundColor(Color.parseColor("#0080C5"));
        }*/
        headerLayout = (RelativeLayout) findViewById(R.id.headerlayout);
        filter = (LinearLayout) findViewById(R.id.filter);
        panelImageView = (ImageView) findViewById(R.id.panel);
        search = (EditText) findViewById(R.id.search_query);
        selectCityListView.setDivider(null);
//        selectCityFrameLayout.setOnClickListener(selectedCityTapped);
//        selectCityListView.setOnItemClickListener(selectedCityListViewTapped);

        rootRelativeLayout = (RelativeLayout) findViewById(R.id.rewardz_rootRelativeLayout);
        cancelButton = (FrameLayout) findViewById(R.id.rewardz_cancelButton);
        okButton = (FrameLayout) findViewById(R.id.rewardz_okButton);
        cityFrameLayout = (FrameLayout) findViewById(R.id.rewardz_cityFrameLayout);
        selectCitynewListView = (ListView) findViewById(R.id.selectCitynewListView);


        selectCityFrameLayout.setOnClickListener(selectedCityTappedNew);

    }

    public String separatorPoints(Double balance) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);

        return format.format(balance).split("\\,")[0];
    }

    FrameLayout.OnClickListener selectedCityTappedNew = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cityFrameLayout.setVisibility(View.VISIBLE);
            rootRelativeLayout.setClickable(false);
            rootRelativeLayout.setEnabled(false);
            rootRelativeLayout.setFocusable(false);
            selectCityViewAdapter = new SelectCityListViewAdapter(getApplicationContext(), selectCityListItems, adapterListener);
            selectCitynewListView.setAdapter(selectCityViewAdapter);
        }
    };

    SelectCityListener adapterListener = new SelectCityListener() {
        @Override
        public void onSelectCityListener(SelectCityListItem selectCityListItem) {
            city = selectCityListItem.getPk();
            cityName = selectCityListItem.getCity();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        cityFrameLayout.setVisibility(View.GONE);

    }

    FrameLayout.OnClickListener selectedCityTapped = new View.OnClickListener() {
        int counter = 0;
        boolean isSelectCityClosed = true;
        @Override
        public void onClick(View v) {
            counter++;
            if (isSelectCityClosed == true) {
                //callselectCityApi();

//                SelectCityListItem selectCityArray = new SelectCityListItem("1", "Jakarta");
//                selectCityListItems.add(selectCityArray);
//                selectCityArray = new SelectCityListItem("2", "Solo");
//                selectCityListItems.add(selectCityArray);
//                selectCityArray = new SelectCityListItem("3", "Semarang");
//                selectCityListItems.add(selectCityArray);
//                selectCityArray = new SelectCityListItem("4", "Bali");
//                selectCityListItems.add(selectCityArray);
//                selectCityArray = new SelectCityListItem("5", "Yogyakarta");
//                selectCityListItems.add(selectCityArray);
//                selectCityArray = new SelectCityListItem("6", "Bandung");
//                selectCityListItems.add(selectCityArray);

                selectCityViewAdapter = new SelectCityListViewAdapter(getApplicationContext(), selectCityListItems, adapterListener);
                selectCityListView.setAdapter(selectCityViewAdapter);

                selectedCityImageView.setImageResource(R.drawable.arrow_down);
                isSelectCityClosed = false;
                selectCityListView.setVisibility(View.VISIBLE);

                //selectCityListView = new
            } else {
                selectedCityImageView.setImageResource(R.drawable.arrow_right1);
                isSelectCityClosed = true;
                selectCityListView.setAdapter(null);
                selectCityListView.setVisibility(View.GONE);
            }

            //call api set ada adapter
            // set to listView
        }
    };


    ListView.OnItemClickListener selectedCityListViewTapped = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            city = selectCityListItems.get(position).getPk();
            Intent intent = new Intent(EligibleRewardzActivity.this, SelectedCityActivity.class);
            intent.putExtra("city_id", city);
            intent.putExtra("category", category);
            intent.putExtra("city", selectCityListItems.get(position).getCity());
            intent.putExtra("url", url + "&city_id=" + city);
            intent.putExtra("category_type",categorySlug);
            intent.putExtra("issearched", false);
            intent.putExtra("type", type);
            intent.putExtra("jsonobject_for_selected_item", getIntent().getStringExtra("jsonobject_for_selected_item"));
            startActivity(intent);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            String subcategory = data.getStringExtra("sub_category");
            if (subcategory.equals("All")) {
                url = Constants.BASEURL + Constants.REWARDZ_API + "?category=" + categorySlug;
                callRewardzApi();
            } else {
                url = Constants.BASEURL + Constants.REWARDZ_API + "?Category=" + categorySlug + "&sub_category=" + subcategory;
                filterItemArrayList.clear();
                callRewardzApi();
            }
        }
    }

    /* *******************calling point summary Api for get method to checking its points or discount***************************/

    public void callTotalPointsSummeryApi() {

        dialog = new Dialog(EligibleRewardzActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String authProvider = SettingsManager.getInstance().getAuthProvider();
 /*       paramMap.put("username", usernameValue);
        paramMap.put("password", passwordValue);*/
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
                        rewardzPointsTopBeearned.setText("You have earned " + separatorPoints(Double.valueOf(jsonObject.getString("remaining_points"))) + " Points to redeem");
                    }
                    callRewardzApi();
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
                    UserManager.getInstance().logOut(EligibleRewardzActivity.this);
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

    public void callRewardzApi() {
        try {
            rewardzListItems.clear();
            dialog = new Dialog(EligibleRewardzActivity.this);
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

                        total_Points = 0;
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject rewardzJson = jsonArray.getJSONObject(i);
                            String pk = rewardzJson.getString("pk");
                            pkArrayList.add(pk);
                            points = rewardzJson.getString("points");
                            //   total_Points=total_Points+Integer.parseInt(pk);
                            boolean redeemableFlag = rewardzJson.getBoolean("redeemable");
                            if(redeemableFlag) {
                                total_Points++;
                            }
                            filterItemArrayList.add(rewardzJson.getString("name"));
                            jsonResponse = rewardzJson.toString();

                            RewardzListItem rewardzListItem = new RewardzListItem(pk, rewardzJson.getString("thumbnail_img_url"), rewardzJson.getString("name"), "", rewardzJson.getString("description"), rewardzJson.toString(), points, redeemableFlag, 0);
                            rewardzListItems.add(rewardzListItem);
                            filteredRewardzListItems.add(rewardzListItem);
                        }

                        if (type.equals("discount")) {
                            rewardzPointsTopBeearned.setText("You have earned " + total_Points + " Rewards to redeem");

                        }


                        rewardzRecyclerViewAdapter = new RewardzRecyclerViewAdapter(getApplicationContext(), rewardzListItems, isElegibleRewardz, false, type);
                        recyclerView.setAdapter(rewardzRecyclerViewAdapter);

                        rewardzRecyclerViewAdapter.notifyDataSetChanged();


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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /* *********************Calling select city Listing Api*************************/

    public void callselectCityApi() {
        try {
            selectCityListItems.clear();
//            dialog = new Dialog(EligibleRewardzActivity.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setCancelable(false);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            dialog.setContentView(R.layout.loader);
//            dialog.show();

            String authProvider = SettingsManager.getInstance().getAuthProvider();
            HashMap<String, String> paramMap = new HashMap<String, String>();
            RequestParams params = new RequestParams(paramMap);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.get(Constants.BASEURL + Constants.REWARDZ_SELECT_CITY, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                            super.onSuccess(statusCode, headers, jsonObject);

                            if (dialog.isShowing() || dialog != null) {
                                dialog.dismiss();
                                dialog.hide();
                            }
                            try {
                                total_Points = 0;
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                Log.e("onSuccess: ", jsonArray.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject selectCityJson = jsonArray.getJSONObject(i);
                                    SelectCityListItem selectCityArray = new SelectCityListItem(selectCityJson.getString("id"), selectCityJson.getString("city"));
                                    selectCityListItems.add(selectCityArray);
                                }
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject rewardzJson = jsonArray.getJSONObject(i);
//                            String pk = rewardzJson.getString("pk");
//                            pkArrayList.add(pk);
//                            points = rewardzJson.getString("points");
//                            //   total_Points=total_Points+Integer.parseInt(pk);
//                            total_Points++;
//                            boolean redeemableFlag = rewardzJson.getBoolean("redeemable");
//                            filterItemArrayList.add(rewardzJson.getString("name"));
//                            jsonResponse = rewardzJson.toString();
//
//                            RewardzListItem rewardzListItem = new RewardzListItem(pk, rewardzJson.getString("thumbnail_img_url"), rewardzJson.getString("name"), "", rewardzJson.getString("description"), rewardzJson.toString(), points, redeemableFlag);
//                            rewardzListItems.add(rewardzListItem);
//                        }

                                if (type.equals("discount")) {
                                    rewardzPointsTopBeearned.setText("You have earned " + total_Points + " Rewards to redeem");

                                }
                                selectCityViewAdapter = new SelectCityListViewAdapter(getApplicationContext(), selectCityListItems, adapterListener);
                                recyclerView.setAdapter(rewardzRecyclerViewAdapter);

                                selectCityViewAdapter.notifyDataSetChanged();


                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                /*updateUI(jsonObject.toString());*/

                        }

                        @Override
                        public void onFailure(int statusCode, cz.
                                msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject
                                                      errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);

                            if (dialog.isShowing() || dialog != null) {
                                dialog.dismiss();
                                dialog.hide();
                            }
                        }
                    }

            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
                callselectCityApi();
                selectCityFrameLayout.setVisibility(View.GONE);
                nearMeFrameLayout.setVisibility(View.GONE);
            } else if (type.equals("discount")) {
                callselectCityApi();
                callRewardzApi();
            }
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(EligibleRewardzActivity.this);
                }
            });
        }
    }

}
