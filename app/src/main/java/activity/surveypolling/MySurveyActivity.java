package activity.surveypolling;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import activity.userprofile.LoginActivity;
import adaptor.MySurveyAdapter;
import adaptor.SurveyPollingRequestAdapter;
import bean.SurveyPolling;
import bean.SurveyPollingCategoryResponse;
import bean.SurveyPollingResponse;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;

public class MySurveyActivity extends AppCompatActivity {

    private MySurveyAdapter mAdapter;
    private Dialog mDialog;

    private static final String TAG = "SurveyPollingRequestAct";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_survey);
        initData();
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Call verify request list api
        callVerifyRequestListApi();
    }

    protected void initData() {
        mAdapter = new MySurveyAdapter(this);
    }

    protected void initLayout() {
        recyclerView = findViewById(R.id.survey_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        recyclerView.setAdapter(mAdapter);
    }

    //Service
    private void callVerifyRequestListApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.get(constants.Constants.BASEURL + Constants.GET_MY_SURVEY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPollingResponse response = new SurveyPollingResponse(jsonObject);
                mAdapter.setData(response.results);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(MySurveyActivity.this);
                }
                else if (statusCode == 400) {

                }
                else if (statusCode == 500) {

                }
            }
        });
    }

    private void callCategoryListApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.get(constants.Constants.BASEURL + activity.surveypolling.Constants.GET_CATEGORY_LIST_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPollingCategoryResponse response = new SurveyPollingCategoryResponse(jsonArray);
                //mAdapter.setCategories(response.data);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(MySurveyActivity.this);
                }
                else if (statusCode == 400) {

                }
                else if (statusCode == 500) {

                }
            }
        });
    }

    //Event
    public void onBackButtonClicked(View view) {
        finish();
    }
}
