package fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import activity.rewardz.EligibleRewardzActivity;
import activity.userprofile.MainActivity;
import adaptor.RewardzdashBoardListAdapter;
import bean.RewardzDashBoardItem;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class RewardzDahboardFragment extends Fragment implements View.OnKeyListener {
    RelativeLayout titleBarRelativeLayout, searchRelativeLayout;
    ListView listview;
    ArrayList<RewardzDashBoardItem> faciltyCheckinItems = new ArrayList<>();
    CheckInternetConnection checkInternetConnection;
    EditText search;
    ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
    String type = "";
    TextView toolBarTitle;
    ImageView go;
    Activity activity;
    public SharedDatabase sharedDatabase;
    public String token;
    CoordinatorLayout coordinatorLayout;
    ArrayList<String> itemsArraylist = new ArrayList<>();
    ArrayList<String> urlArray = new ArrayList<>();
    ArrayList<String> filterItemArrayList = new ArrayList<>();
    RewardzdashBoardListAdapter rewardzdashBoardListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rewardz_dashboard, null);
        listview = (ListView) view.findViewById(R.id.rewardz_listview);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        type = sharedDatabase.getType();
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        titleBarRelativeLayout = (RelativeLayout) view.findViewById(R.id.titlebar);
        searchRelativeLayout = (RelativeLayout) view.findViewById(R.id.header);
        toolBarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        search = (EditText) view.findViewById(R.id.search_query);
        search.setOnKeyListener(this);
        go = (ImageView) view.findViewById(R.id.go);
        checkInternetConnection = new CheckInternetConnection(getActivity());

        if (type.equals("point")) {
            toolBarTitle.setText("POINT REWARDS");
        } else if (type.equals("discount")) {
            toolBarTitle.setText("DISCOUNT");
        } else if (type.equals("")) {
            toolBarTitle.setText("POINT REWARDS");
            type = "point";
        }

        sharedDatabase.setPosition(12);
        sharedDatabase.setType(type);
        LinearLayout panel = (LinearLayout) view.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callFaciltyCheckinApi(type);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.RED);
            snackbar.show();
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        JSONObject jsonObject = jsonObjectArrayList.get(position);
                        String categorySlug = jsonObject.getString("slug");
                        sharedDatabase.setSubCategory(categorySlug);
                        String category = jsonObject.getString("name");
                        Intent intent = new Intent(getActivity(), EligibleRewardzActivity.class);
                        intent.putExtra("iseligible_rewardz_screen", true);
                        intent.putExtra("category_type", categorySlug);
                        intent.putExtra("category", category);
                        intent.putExtra("type", type);
                        intent.putExtra("jsonobject_for_selected_item", jsonObject.toString());
                        MainActivity.isInternetConnection = false;
                        startActivity(intent);
                    } else {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().toString().isEmpty() || search.getText().toString().equals("")) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please input some text", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                } else {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        Intent intent = new Intent(getActivity(), EligibleRewardzActivity.class);
                        intent.putExtra("iseligible_rewardz_screen", true);
                        intent.putExtra("category_type", "");
                        intent.putExtra("category", "");
                        intent.putExtra("issearched", true);
                        intent.putExtra("type", type);
                        MainActivity.isInternetConnection = false;
                        intent.putExtra("query", search.getText().toString().trim());

                        startActivity(intent);
                    } else {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
            }
        });
       /* search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }*/

            /*@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText=search.getText().toString();
            *//*    itemsArraylist.clear();
                urlArray.clear();*//*
                faciltyCheckinItems.clear();
                for(int i=0;i<filterItemArrayList.size();i++)
                {
                    String activityType=filterItemArrayList.get(i);
                    if(searchText.length()<=activityType.length())
                    {
                        if(searchText.equalsIgnoreCase(activityType.substring(0,searchText.length())))
                        {

                            String imageUrl=urlArray.get(i);
                            String icon=urlArray.get(i);
                            String item=itemsArraylist.get(i);
                            RewardzDashBoardItem faciltyCheckinItem = new RewardzDashBoardItem(imageUrl, item,icon);
                            faciltyCheckinItems.add(faciltyCheckinItem);



                        }
                    }
                }

       rewardzdashBoardListAdapter.notifyDataSetChanged();
                // System.out.println("FilteredArray is "+sportsArray);

            }
*/
           /* @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }

    /* ********************calling facilities checkin Api**************************/

    public void callFaciltyCheckinApi(String type) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String url = Constants.BASEURL + Constants.REWARDS_ITEMS + "?type=" + type;
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);

                Loader.dialogDissmiss(getActivity());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject facilityjsonObject = jsonArray.getJSONObject(i);
                        jsonObjectArrayList.add(facilityjsonObject);
                        String facilityItem = facilityjsonObject.getString("name");
                        String imageUrl = facilityjsonObject.getString("display_img_url");
                        String icon = facilityjsonObject.getString("icon");
                        itemsArraylist.add(facilityItem);
                        urlArray.add(imageUrl);
                        urlArray.add(icon);
                        filterItemArrayList.add(facilityItem);
                        RewardzDashBoardItem faciltyCheckinItem = new RewardzDashBoardItem(imageUrl, facilityItem, icon);
                        faciltyCheckinItems.add(faciltyCheckinItem);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }


                }
                if (getActivity() != null) {
                    rewardzdashBoardListAdapter = new RewardzdashBoardListAdapter(activity, faciltyCheckinItems);
                    listview.setAdapter(rewardzdashBoardListAdapter);
                }


            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getActivity());

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }


                if (statusCode == 400) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (checkInternetConnection.isConnectingToInternet()) {
                Intent intent = new Intent(getActivity(), EligibleRewardzActivity.class);
                intent.putExtra("iseligible_rewardz_screen", true);
                intent.putExtra("category_type", "");
                intent.putExtra("category", "");
                intent.putExtra("issearched", true);
                intent.putExtra("type", type);
                intent.putExtra("query", search.getText().toString().trim());
                startActivity(intent);
                //callSearchApi();
            } else {
                if (coordinatorLayout != null) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }
            return false;
        }
        return false;

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
            callFaciltyCheckinApi(type);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(getActivity());
                }
            });
        }
    }

}
