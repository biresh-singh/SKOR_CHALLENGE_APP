package activity.surveypolling;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import activity.userprofile.LoginActivity;
import bean.SurveyPolling;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

/**
 * Created by Dihardja Software Solutions on 3/7/18.
 */

public class RejectCommentActivity extends AppCompatActivity {

    //Layout
    private ImageView mSurveyPollingImage;
    private TextView mHeaderStartLabel;
    private TextView mHeaderEndLabel;
    private TextView mSurveyPollingTitleLabel;
    private TextView mSurveyPollingCreatorLabel;
    private EditText mSurveyPollingRejectComment;
    private Dialog mDialog;

    //Variable
    private SurveyPolling mSurveyPollingData;

    private String mCategoryLabel;
    //Intent param
    public static String PARAM_CATEGORY_LABEL = "category_label";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_polling_reject);

        initData();
        initLayout();
        initEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        mSurveyPollingData = ((SurveyPolling) intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA));
        mCategoryLabel = intent.getStringExtra(PARAM_CATEGORY_LABEL);
    }

    private void initLayout() {
        mSurveyPollingImage = ((ImageView) findViewById(R.id.activity_survey_polling_image_view));
        mHeaderStartLabel = ((TextView) findViewById(R.id.activity_survey_polling_header_start_label));
        mHeaderEndLabel = ((TextView) findViewById(R.id.activity_survey_polling_header_end_label));
        mSurveyPollingTitleLabel = ((TextView) findViewById(R.id.survey_polling_title_label));
        mSurveyPollingCreatorLabel = ((TextView) findViewById(R.id.survey_polling_creator_label));
        mSurveyPollingRejectComment = ((EditText) findViewById(R.id.activity_survey_polling_reject_comment));

        //set survey/polling type
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY) {
            mSurveyPollingImage.setImageResource(R.drawable.survey_icon);

            if (mSurveyPollingData.topics.size() > 0) {
                mHeaderStartLabel.setText(R.string.survey_about_text);
                if (mCategoryLabel != null)
                    mHeaderEndLabel.setText(mCategoryLabel);
            } else {
                mHeaderStartLabel.setText(R.string.survey_text);
                mHeaderEndLabel.setText("");
            }
        } else {
            mSurveyPollingImage.setImageResource(R.drawable.polling_icon);

            if (mSurveyPollingData.topics.size() > 0) {
                mHeaderStartLabel.setText(R.string.polling_about_text);
                if (mCategoryLabel != null)
                    mHeaderEndLabel.setText(mCategoryLabel);
            } else {
                mHeaderStartLabel.setText(R.string.polling_text);
                mHeaderEndLabel.setText("");
            }
        }

        //set survey/polling title label text
        if (mSurveyPollingData.name != null) {
            mSurveyPollingTitleLabel.setText(mSurveyPollingData.name);
        }

        //set survey/polling creator label text
        if (mSurveyPollingData.creator != null && mSurveyPollingData.createdDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault());
            mSurveyPollingCreatorLabel.setText(getResources().getString(R.string.requested_by_text, mSurveyPollingData.creator, dateFormat.format(mSurveyPollingData.createdDate)));
        }
    }

    private void initEvent() {

    }

    //Service
    private void callRejectSurveyPollingApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("comment", mSurveyPollingRejectComment.getText().toString());

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content_Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.post(this, constants.Constants.BASEURL + Constants.generatePostRejectSurveyPollingApi(mSurveyPollingData.id),
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(RejectCommentActivity.this);
                        } else if (statusCode == 400) {
                            if (responseString != null) {
                                //Show error dialog (hardcode response text parsing)
                                DialogHelper.createOKDialog(RejectCommentActivity.this,
                                        getResources().getString(R.string.reject_text), responseString.substring(2, responseString.length() - 2));
                            }
                        } else if (statusCode == 500) {

                        }
                    }
                });
    }

    //Events
    public void onBackButtonClicked(View view) {
        finish();
    }

    public void onDoneButtonClicked(View view) {
        //Validate rejection comment
        if (mSurveyPollingRejectComment.getText().toString().trim().isEmpty()) {
            Resources resources = getResources();
            DialogHelper.createOKDialog(this,
                    resources.getString(R.string.reject_text),
                    resources.getString(R.string.reject_comment_validation_empty_text));
        } else {
            callRejectSurveyPollingApi();
        }
    }
}
