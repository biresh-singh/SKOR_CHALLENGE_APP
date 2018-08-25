package fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.FacilityCheckinCategoryRecyclerView;
import bean.FacilityCheckinCategoryItem;
import constants.Constants;
import database.SharedDatabase;
import singleton.SettingsManager;
import utils.AppController;
import utils.Loader;


public class FacilityCheckinFragment extends Fragment {
    RecyclerView facilityRecyclerView;
    ArrayList<FacilityCheckinCategoryItem> faciltyCheckinCategoryItems = new ArrayList<>();
    ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
    public SharedDatabase sharedDatabase;
    public String token;
    CoordinatorLayout coordinatorLayout;
    CheckInternetConnection checkInternetConnection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_checkin, null);
        facilityRecyclerView = (RecyclerView) view.findViewById(R.id.facility_recycleview);
        checkInternetConnection = new CheckInternetConnection(getActivity());
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        sharedDatabase.setPosition(6);
        sharedDatabase.setType("all");
        Loader.dialogDissmiss(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        facilityRecyclerView.setLayoutManager(llm);
        facilityRecyclerView.setHasFixedSize(true);
        LinearLayout panel = (LinearLayout) view.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callFaciltyCheckinCategoriesApi();
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
        return view;
    }

    /* *******************calling facilities checking Api ***************************/

    public void callFaciltyCheckinCategoriesApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.FACILTY_CHECKIN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                Loader.dialogDissmiss(getContext());
                try {

                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject facilityjsonObject = jsonArray.getJSONObject(i);
                            jsonObjectArrayList.add(facilityjsonObject);

                            String facilityCategory = facilityjsonObject.getString("name");
                            String icon = facilityjsonObject.getString("icon");
                            String pk = facilityjsonObject.getString("pk");
                            String thumbnailImageUrl = facilityjsonObject.getString("thumbnail_img_url");

//                            JSONArray locationArray = facilityjsonObject.getJSONArray("locations");
//                            JSONObject locationJsonObject = locationArray.getJSONObject(0);
//                            String address = locationJsonObject.getString("address");

                            FacilityCheckinCategoryItem facilityCheckinCategoryItem =
                                    new FacilityCheckinCategoryItem(pk, facilityCategory, thumbnailImageUrl, icon);
                            faciltyCheckinCategoryItems.add(facilityCheckinCategoryItem);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (getActivity() != null) {
                        FacilityCheckinCategoryRecyclerView facilityCheckinCategoryRecyclerView =
                                new FacilityCheckinCategoryRecyclerView(getActivity(), faciltyCheckinCategoryItems, jsonObjectArrayList);
                        facilityRecyclerView.setAdapter(facilityCheckinCategoryRecyclerView);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getActivity());
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

}
