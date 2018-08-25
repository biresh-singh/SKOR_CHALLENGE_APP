package fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import adaptor.LeaderBoardBusinessAdaptor;
import bean.LeaderBoardItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;

public class LeaderBoardBusinessUnitFragment extends Fragment {

    ListView listviewList;
    TextView individualText;
    TextView businessunitText;
    TextView headerBussinessText;
    LinearLayout panel;
    LinearLayout linearLayout;
    Double totalPoints;
    String pk;
    Double averagePoint;
    String memberPoint;
    String name;
    CoordinatorLayout coordinatorLayout;
    ArrayList<LeaderBoardItem> arrayListleaderBoardItems = new ArrayList<>();
    LeaderBoardItem leaderBoardItem;
    LeaderBoardBusinessAdaptor leaderBoardBusinessAdaptor;
    CheckInternetConnection checkInternetConnection;
    View rootView;
    public SharedDatabase sharedDatabase;
    public String token;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.leaderboard_business_activity, null);
        checkInternetConnection = new CheckInternetConnection(getActivity());
        linearLayout = (LinearLayout) rootView.findViewById(R.id.lin);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        headerBussinessText = (TextView) rootView.findViewById(R.id.points_to_be_earned);
        individualText = (TextView) rootView.findViewById(R.id.individual_text);
        businessunitText = (TextView) rootView.findViewById(R.id.businessunit_text);
        listviewList = (ListView) rootView.findViewById(R.id.leaderboard_listview);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.myCoordinatorLayout);
        listviewList.setDivider(null);
        individualText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {

                    businessunitText.setBackgroundColor(Color.TRANSPARENT);
                    individualText.setBackgroundResource(R.drawable.text_bg);

                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
                    navigataionCallback.onNavigationDrawerItemSelected(16, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        businessunitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    individualText.setBackgroundColor(Color.TRANSPARENT);
                    businessunitText.setBackgroundResource(R.drawable.text_bg);
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
                    navigataionCallback.onNavigationDrawerItemSelected(17, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
        panel = (LinearLayout) rootView.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callLeaderBoardBusinessUnitApi();
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

        return rootView;
    }



    /* *******************calling Bussiness Leader board Api to get method***************************/

    public void callLeaderBoardBusinessUnitApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.LEADERBOARD_BUSINESS;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Loader.dialogDissmiss(getActivity());
                updateUI(jsonObject.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getActivity());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }


                if (statusCode == 500) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                if (statusCode == 400) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
    }
    /* **************************parsing data and set to layout********************/

    public void updateUI(String jsonresponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresponse);
            headerBussinessText.setText("Your Team Rank is " + jsonObject.getString("department_rank"));
            JSONArray departmentJsonArray = jsonObject.getJSONArray("ranked_departments");
            for (int i = 0; i < departmentJsonArray.length(); i++) {
                JSONObject departmentJsonObject = departmentJsonArray.getJSONObject(i);
                totalPoints = departmentJsonObject.getDouble("total_points");
                pk = departmentJsonObject.getString("pk");
                averagePoint = departmentJsonObject.getDouble("average_points");
                memberPoint = departmentJsonObject.getString("members_count");
                name = departmentJsonObject.getString("name");
                leaderBoardItem = new LeaderBoardItem(totalPoints, pk, averagePoint, memberPoint, name);
                arrayListleaderBoardItems.add(leaderBoardItem);
            }
            if (getActivity() != null) {
                leaderBoardBusinessAdaptor = new LeaderBoardBusinessAdaptor(getActivity(), arrayListleaderBoardItems);
                listviewList.setAdapter(leaderBoardBusinessAdaptor);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("BusinessUnitLeaderboardViewed");
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
            callLeaderBoardBusinessUnitApi();
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
