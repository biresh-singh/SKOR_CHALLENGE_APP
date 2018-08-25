package activity.surveypolling;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import activity.userprofile.LoginActivity;
import adaptor.SurveyPollingRequestAdapter;
import bean.SurveyPolling;
import bean.SurveyPollingCategoryResponse;
import bean.SurveyPollingResponse;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;

/**
 * Created by mac on 2/13/18.
 */

public class VerifyRequestActivity extends AppCompatActivity {

    //Layout
    private ListView mRequestListView;
    private SurveyPollingRequestAdapter mAdapter;
    private Dialog mDialog;

    private static final String TAG = "SurveyPollingRequestAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_polling_verify_request);

        initData();
        initLayout();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Call verify request list api
        callVerifyRequestListApi();
    }

    protected void initData() {
        mAdapter = new SurveyPollingRequestAdapter(this);
    }

    protected void initLayout() {
        mRequestListView = findViewById(R.id.request_list_view);
        mRequestListView.setAdapter(mAdapter);
    }

    protected void initEvent() {
        mRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Push Verify Request Detail Activity
                SurveyPolling data = mAdapter.getItem(position);

                Intent intent = new Intent(VerifyRequestActivity.this, VerifyRequestDetailActivity.class);
                intent.putExtra(activity.surveypolling.Constants.PARAM_SURVEY_POLLING_DATA, data);
                intent.putExtra("isFromVerify",true);
                startActivity(intent);
            }
        });
    }

    //Service
    private void callVerifyRequestListApi() {
        Constants.BASEURL = getResources().getString(R.string.base_url);
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
        client.get(Constants.BASEURL + activity.surveypolling.Constants.VERIFY_REQUEST_LIST_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPollingResponse response = new SurveyPollingResponse(jsonObject);
                mAdapter.notifyDataSetInvalidated();
                mAdapter.setData(response.results);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(VerifyRequestActivity.this);
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
                mAdapter.notifyDataSetInvalidated();
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
                    UserManager.getInstance().logOut(VerifyRequestActivity.this);
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
