package activity.surveypolling;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import activity.userprofile.LoginActivity;
import bean.Department;
import bean.Question;
import bean.SurveyPolling;
import bean.SurveyPollingCategory;
import bean.SurveyPollingCategoryResponse;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

/**
 * Created by Ferry on 21/2/2018.
 */

public class CreateSurveyPollingDetailActivity extends AppCompatActivity {

    //Layout
    private TextView mHeaderTitleLabel;
    private TextView mHeaderRightLabel;
    private TextView mRestrictionValueLabel;
    private TextView mStartDateValueLabel;
    private TextView mEndDateValueLabel;
    private FlexboxLayout mCategoryFlexboxLayout;
    private Button mCategoryAddButton;
    private Button mSubmitButton;
    private EditText mSurveyTitleEditText;
    private EditText mSurveyDescriptionEditText;
    private RelativeLayout mSurveyRewardsLayout;
    private TextView mSurveyRewardsLabel;
    private TextView mSurveyRewardsValueLabel;
    private TextView mAddQuetionsValueLabel;
    private Dialog mDialog;

    //Variable
    private String mHeaderTitle;
    private Drawable mButtonBackground;
    private int mSurveyPollingMode = Constants.MODE_SURVEY;
    private int mUserType = Constants.TYPE_USER;
    private SurveyPolling mSurveyPollingData = new SurveyPolling();
    private ArrayList<SurveyPollingCategory> mCategories = new ArrayList<>();
    private boolean mIsReview;

    //Static
    private static SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat mDateDisplayFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    //Intent Param
    public static final String PARAM_IS_REVIEW = "is_review";

    //Intent Request Code
    private static final int RESTRICTION = 1;
    private static final int ADD_QUESTIONS = 2;
    private static final int PREVIEW_SURVEY_POLLING = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_polling_create_detail);

        initData();
        initLayout();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCategories.size() == 0)
            callCategoryListApi();
    }

    protected void initData() {
        Intent intent = getIntent();
        mSurveyPollingMode = intent.getIntExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
        mUserType = intent.getIntExtra(Constants.PARAM_USER_TYPE, Constants.TYPE_USER);

        //Override data if in edit mode
        SurveyPolling data = intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);
        if (data != null) {
            mSurveyPollingData = data;

            //Set mode
            if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
                mSurveyPollingMode = Constants.MODE_SURVEY;
            else if (mSurveyPollingData.type == SurveyPolling.TYPE_POLLING)
                mSurveyPollingMode = Constants.MODE_POLLING;
        } else {
            //Set data type
            mSurveyPollingData.type = mSurveyPollingMode;
        }

        //Is Review param
        mIsReview = intent.getBooleanExtra(PARAM_IS_REVIEW, false);

        //Set header title
        if (mSurveyPollingMode == Constants.MODE_SURVEY) {
            mHeaderTitle = getResources().getString(R.string.create_survey_header_title);
            mButtonBackground = ContextCompat.getDrawable(this, R.drawable.shape_survey_polling_fill_survey);
        } else {
            //Polling
            mHeaderTitle = getResources().getString(R.string.create_polling_header_title);
            mButtonBackground = ContextCompat.getDrawable(this, R.drawable.shape_survey_polling_item_green);
        }
    }

    protected void initLayout() {
        mHeaderTitleLabel = findViewById(R.id.header_title_label);
        mHeaderRightLabel = findViewById(R.id.header_right_label);
        mRestrictionValueLabel = findViewById(R.id.restriction_value_label);
        mStartDateValueLabel = findViewById(R.id.start_date_value_label);
        mEndDateValueLabel = findViewById(R.id.end_date_value_label);
        mCategoryFlexboxLayout = findViewById(R.id.category_flexbox_layout);
        mCategoryAddButton = findViewById(R.id.category_add_button);
        mSurveyTitleEditText = findViewById(R.id.survey_title_edit_text);
        mSurveyDescriptionEditText = findViewById(R.id.survey_description_edit_text);
        mSurveyRewardsLayout = findViewById(R.id.survey_rewards_layout);
        mSurveyRewardsLabel = findViewById(R.id.survey_rewards_label);
        mSurveyRewardsValueLabel = findViewById(R.id.survey_rewards_value_label);
        mAddQuetionsValueLabel = findViewById(R.id.add_quetions_value_label);
        mSubmitButton = findViewById(R.id.submit_button);

        //Set header title
        mHeaderTitleLabel.setText(mHeaderTitle);
        mSubmitButton.setBackground(mButtonBackground);

        if (mSurveyPollingMode == Constants.MODE_SURVEY) {
            //Set survey title hint
            mSurveyTitleEditText.setHint(R.string.survey_title_hint);

            //Hide description
            mSurveyDescriptionEditText.setVisibility(View.GONE);

            //Set header button
            mHeaderRightLabel.setText(R.string.save_text);

            //Set survey reward label
            mSurveyRewardsLabel.setText(R.string.survey_rewards_text);
        } else {
            //Set polling title hint
            mSurveyTitleEditText.setHint(R.string.polling_title_hint);

            //Hide description
            mSurveyDescriptionEditText.setVisibility(View.GONE);

            //Set header button
            mHeaderRightLabel.setVisibility(View.GONE);

            //Set survey reward label
            mSurveyRewardsLabel.setText(R.string.polling_rewards_text);
        }

        //Hide admin layout
        if (mUserType != Constants.TYPE_ADMIN) {
            mCategoryAddButton.setVisibility(View.GONE);
        }

        //Hide survey reward button - 21/3/2018 Cannot edit or add survey reward
        mSurveyRewardsLayout.setVisibility(View.GONE);

        //Set initial value
        if (getIntent().hasExtra(Constants.PARAM_SURVEY_POLLING_DATA)) {
            //Set restriction
            mRestrictionValueLabel.setText(generateRestrictionText(this,
                    mSurveyPollingData.genderRestriction, mSurveyPollingData.statusRestriction,
                    mSurveyPollingData.startAgeRestriction, mSurveyPollingData.endAgeRestriction));

            //Set start date
            mStartDateValueLabel.setText(mDateDisplayFormatter.format(mSurveyPollingData.startDate));

            //Set end date
            mEndDateValueLabel.setText(mDateDisplayFormatter.format(mSurveyPollingData.endDate));

            //Set title
            mSurveyTitleEditText.setText(mSurveyPollingData.name);

            //Set description
            if (mSurveyPollingMode == Constants.MODE_SURVEY) {
                mSurveyDescriptionEditText.setText(mSurveyPollingData.description);
            }

            //Set survey rewards
            if (mUserType == Constants.TYPE_ADMIN && mSurveyPollingData.rewards >= 0) {
                mSurveyRewardsValueLabel.setText(mSurveyPollingData.rewards + "");
            }

            //Set question
            int size = mSurveyPollingData.questions.size();
            if (size > 0) {
                if (size == 1)
                    mAddQuetionsValueLabel.setText(getResources().getString(R.string.x_question_text, size));
                else
                    mAddQuetionsValueLabel.setText(getResources().getString(R.string.x_questions_text, size));
            } else
                mAddQuetionsValueLabel.setText("");
        }
    }

    protected void initEvent() {
        //Set title event
        mSurveyTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSurveyPollingData.name = mSurveyTitleEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Set description event
        mSurveyDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSurveyPollingData.description = mSurveyDescriptionEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESTRICTION) {
                if (data != null) {
                    mSurveyPollingData.genderRestriction = data.getIntExtra(RestrictionActivity.PARAM_GENDER_RESTRICTION, 0);
                    mSurveyPollingData.statusRestriction = data.getIntExtra(RestrictionActivity.PARAM_STATUS_RESTRICTION, 0);
                    mSurveyPollingData.startAgeRestriction = data.getIntExtra(RestrictionActivity.PARAM_START_AGE_RESTRICTION, 20);
                    mSurveyPollingData.endAgeRestriction = data.getIntExtra(RestrictionActivity.PARAM_END_AGE_RESTRICTION, 50);
                    ArrayList<Department> selectedDepartment = data.getParcelableArrayListExtra(RestrictionActivity.PARAM_DEPARTMENT_RESTRICTION);
                    ArrayList<Integer> selectedDepartmentId = data.getIntegerArrayListExtra(RestrictionActivity.PARAM_SELECTED_DEPARTMENT_ID);

                    mSurveyPollingData.departmentRestriction.clear();
                    mSurveyPollingData.departmentRestriction.addAll(selectedDepartmentId);

                    mRestrictionValueLabel.setText(generateRestrictionText(CreateSurveyPollingDetailActivity.this,
                            mSurveyPollingData.genderRestriction, mSurveyPollingData.statusRestriction,
                            mSurveyPollingData.startAgeRestriction, mSurveyPollingData.endAgeRestriction));
                }
            } else if (requestCode == ADD_QUESTIONS) {
                if (data != null) {
                    ArrayList<Question> questions = data.getParcelableArrayListExtra(Constants.PARAM_QUESTIONS);

                    mSurveyPollingData.questions.clear();
                    mSurveyPollingData.questions.addAll(questions);
                    int size = mSurveyPollingData.questions.size();
                    if (size > 0) {
                        if (size == 1)
                            mAddQuetionsValueLabel.setText(getResources().getString(R.string.x_question_text, size));
                        else
                            mAddQuetionsValueLabel.setText(getResources().getString(R.string.x_questions_text, size));
                    } else
                        mAddQuetionsValueLabel.setText("");
                }
            } else if (requestCode == PREVIEW_SURVEY_POLLING) {
                finish();

                Intent newIntent = new Intent(this, CreateSurveyPollingDoneActivity.class);
                newIntent.putExtra(Constants.PARAM_MODE, mSurveyPollingData.type);
                startActivity(newIntent);
            }
        }
    }

    private void generateCategory(List<SurveyPollingCategory> categories) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //Clear all category
        mCategoryFlexboxLayout.removeAllViews();

        //Add category list
        for (SurveyPollingCategory category : categories) {
            addCategory(inflater, category);
        }

        //Add category add button
        mCategoryFlexboxLayout.addView(mCategoryAddButton);
    }

    private void addCategory(LayoutInflater inflater, final SurveyPollingCategory category) {
        final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.layout_survey_polling_category_checkbox, null);

        //Set checkbox text
        checkBox.setText(category.name);

        //Set checked
        if (mSurveyPollingData.topics.contains(category.id))
            checkBox.setChecked(true);

        //Set checkbox event
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = checkBox.isChecked();
                if (isChecked) {
                    mSurveyPollingData.topics.add(category.id);
                } else {
                    mSurveyPollingData.topics.remove(Integer.valueOf(category.id));
                }
            }
        });

        //Create layout param
        Resources resources = getResources();
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_height));
        layoutParams.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_margin_right), 0);

        //Add checkbox to layout
        mCategoryFlexboxLayout.addView(checkBox, layoutParams);
    }

    //Service
    private void callCategoryListApi() {
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
        client.get(constants.Constants.BASEURL + Constants.GET_CATEGORY_LIST_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPollingCategoryResponse response = new SurveyPollingCategoryResponse(jsonArray);
                mCategories.clear();
                mCategories.addAll(response.data);

                //Crosscheck if selected departments are available
                List<Integer> selectedCategoryId = new ArrayList<>();
                for (Integer categoryId : mSurveyPollingData.topics) {
                    for (SurveyPollingCategory category : mCategories) {
                        if (category.id == categoryId) {
                            selectedCategoryId.add(categoryId);
                        }
                    }
                }

                mSurveyPollingData.topics.clear();
                mSurveyPollingData.topics.addAll(selectedCategoryId);

                //Generate category
                generateCategory(mCategories);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
            }
        });
    }

    private void callAddCategoryApi(String name) {
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

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("name", name);
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
        }

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.post(this, constants.Constants.BASEURL + Constants.ADD_CATEGORY_API, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPollingCategory category = new SurveyPollingCategory(jsonObject);
                mCategories.add(category);

                mCategoryFlexboxLayout.removeView(mCategoryAddButton);
                addCategory((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE), category);
                mCategoryFlexboxLayout.addView(mCategoryAddButton);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
            }
        });
    }

    private void callEditSurveyPollingApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        mSurveyPollingData.state = "draft";
        JSONObject jsonParams = SurveyPolling.toJSONObject(mSurveyPollingData);
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
        }

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.put(this, constants.Constants.BASEURL + Constants.generateEditSurveyPollingApi(mSurveyPollingData.id), entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPolling surveyPolling = new SurveyPolling(jsonObject);
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, surveyPolling);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });
    }

    private void callEditDraftSurveyPollingApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        mSurveyPollingData.state = "saved";
        JSONObject jsonParams = SurveyPolling.toJSONObject(mSurveyPollingData);
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
        }

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.put(this, constants.Constants.BASEURL + Constants.generateEditSurveyPollingApi(mSurveyPollingData.id), entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                SurveyPolling surveyPolling = new SurveyPolling(jsonObject);
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, surveyPolling);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });
    }


    private void callApproveSurveyPollingApi(String rewardPoint) {
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
        paramMap.put("points", rewardPoint);

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content_Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.post(this, constants.Constants.BASEURL + Constants.generatePostApproveSurveyPollingApi(mSurveyPollingData.id),
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
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
                            UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                        } else if (statusCode == 400) {

                        } else if (statusCode == 500) {

                        }
                    }
                });
    }

    private void callSaveDraftSurveyPollingApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        mSurveyPollingData.state = "saved";
        JSONObject jsonParams = SurveyPolling.toJSONObject(mSurveyPollingData);

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
        }

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.post(this, constants.Constants.BASEURL + Constants.CREATE_SURVEY_POLLING_API, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                mHeaderTitle = getResources().getString(R.string.save_draft_title);
                DialogHelper.createOKDialog(CreateSurveyPollingDetailActivity.this,
                        mHeaderTitle,
                        getResources().getString(R.string.save_draft_description), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CreateSurveyPollingDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });
    }


    //Event
    public void onBackButtonClicked(View view) {
        finish();
    }

    public void onDoneButtonClicked(View view) {
        //Validate restriction
        Resources resources = getResources();
        if (mRestrictionValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.restriction_validation_empty_text));
        }
        //Validate start date
        else if (mStartDateValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.start_date_validation_empty_text));
        }
        //Validate end date
        else if (mEndDateValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.end_date_validation_empty_text));
        }
        //Validate end date > start date
        else if (mSurveyPollingData.startDate.after(mSurveyPollingData.endDate)) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.end_date_validation_after_start_date_text));
        }
        //Validate category
        else if (mSurveyPollingData.topics.size() == 0) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.category_validation_choosed_text));
        }
        //Validate title
        else if (mSurveyTitleEditText.getText().toString().trim().isEmpty()) {
            if (mSurveyPollingMode == Constants.MODE_SURVEY) {
                DialogHelper.createOKDialog(this,
                        mHeaderTitle,
                        resources.getString(R.string.survey_title_validation_empty_text));
            } else {
                DialogHelper.createOKDialog(this,
                        mHeaderTitle,
                        resources.getString(R.string.polling_title_validation_empty_text));
            }
        }
        //Validate descrition
        else if (mSurveyDescriptionEditText.getVisibility() == View.VISIBLE && mSurveyDescriptionEditText.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.survey_description_validation_empty_text));
        }
        //Validate survey reward
        else if (mSurveyRewardsLayout.getVisibility() == View.VISIBLE && mSurveyRewardsValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.survey_rewards_validation_empty_text));
        }
        //Validate question
        else if (mAddQuetionsValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.add_question_validation_empty_text));
        } else {
            hideKeyboard();

            if (mIsReview && !mSurveyPollingData.state.equalsIgnoreCase("saved")) {
                callEditSurveyPollingApi();
            } else {
                Intent intent = new Intent(this, PreviewSurveyPollingActivity.class);
                intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
                startActivityForResult(intent, PREVIEW_SURVEY_POLLING);
            }
        }
    }

    public void onRestrictionButtonClicked(View view) {
        hideKeyboard();

        Intent intent = new Intent(this, RestrictionActivity.class);
        intent.putExtra(Constants.PARAM_USER_TYPE, mUserType);
        intent.putExtra(RestrictionActivity.PARAM_GENDER_RESTRICTION, mSurveyPollingData.genderRestriction);
        intent.putExtra(RestrictionActivity.PARAM_STATUS_RESTRICTION, mSurveyPollingData.statusRestriction);
        intent.putExtra(RestrictionActivity.PARAM_START_AGE_RESTRICTION, mSurveyPollingData.startAgeRestriction);
        intent.putExtra(RestrictionActivity.PARAM_END_AGE_RESTRICTION, mSurveyPollingData.endAgeRestriction);
        intent.putIntegerArrayListExtra(RestrictionActivity.PARAM_SELECTED_DEPARTMENT_ID, mSurveyPollingData.departmentRestriction);

        startActivityForResult(intent, RESTRICTION);
    }

    public void onStartDateButtonClicked(View view) {
        hideKeyboard();

        DialogHelper.createDatePickerDialog(this,
                mSurveyPollingData.startDate, new DialogHelper.DatePickerClickListener() {
                    @Override
                    public void onDoneClick(int year, int month, int day) {
                        //Set start date
                        String date = String.format(Locale.getDefault(), "%d-%02d-%02d 00:00:00", year, month, day);
                        try {
                            mSurveyPollingData.startDate = mDateFormatter.parse(date);
                        } catch (Exception e) {
                        }
                        mStartDateValueLabel.setText(mDateDisplayFormatter.format(mSurveyPollingData.startDate));
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void onEndDateButtonClicked(View view) {
        hideKeyboard();

        DialogHelper.createDatePickerDialog(this,
                mSurveyPollingData.endDate, new DialogHelper.DatePickerClickListener() {
                    @Override
                    public void onDoneClick(int year, int month, int day) {
                        //Set end date
                        String date = String.format(Locale.getDefault(), "%d-%02d-%02d 23:59:59", year, month, day);
                        try {
                            mSurveyPollingData.endDate = mDateFormatter.parse(date);
                        } catch (Exception e) {
                        }
                        mEndDateValueLabel.setText(mDateDisplayFormatter.format(mSurveyPollingData.endDate));
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void onCategoryAddButtonClicked(View view) {
        hideKeyboard();

        Resources resources = getResources();
        DialogHelper.createAddDialog(this,
                resources.getString(R.string.category_add_header_title),
                resources.getString(R.string.category_add_hint), "", "", new DialogHelper.AddDialogClickListener() {

                    @Override
                    public void onDoneClick(String value) {
                        String categoryName = value.trim();
                        if (categoryName.isEmpty()) {
                            DialogHelper.createOKDialog(CreateSurveyPollingDetailActivity.this,
                                    mHeaderTitle,
                                    getResources().getString(R.string.category_add_validation_empty_text));
                        } else
                            callAddCategoryApi(categoryName);
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void onSurveyRewardButtonClicked(View view) {
        hideKeyboard();

        String value = "";
        if (mSurveyPollingData.rewards >= 0)
            value = mSurveyPollingData.rewards + "";

        Resources resources = getResources();
        DialogHelper.createAddDialog(this,
                resources.getString(R.string.survey_rewards_text),
                resources.getString(R.string.survey_rewards_hint), "", value, new DialogHelper.AddDialogClickListener() {

                    @Override
                    public void onDoneClick(String value) {
                        //Survey rewards
                        long reward;
                        try {
                            reward = Long.parseLong(value);
                        } catch (Exception e) {
                            Resources resources = getResources();
                            DialogHelper.createOKDialog(CreateSurveyPollingDetailActivity.this,
                                    mHeaderTitle,
                                    resources.getString(R.string.survey_rewards_validation_number_text));

                            return;
                        }

                        mSurveyPollingData.rewards = reward;
                        mSurveyRewardsValueLabel.setText(value);
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void onAddQuestionsButtonClicked(View view) {
        hideKeyboard();

        Intent intent = new Intent(this, AddQuestionsActivity.class);
        intent.putExtra(Constants.PARAM_MODE, mSurveyPollingMode);
        intent.putExtra(Constants.PARAM_QUESTIONS, mSurveyPollingData.questions);
        startActivityForResult(intent, ADD_QUESTIONS);
    }

    public void onSaveButtonClicked(View view) {
        Resources resources = getResources();
        if (mStartDateValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.start_date_validation_empty_text));
        }
        //Validate end date
        else if (mEndDateValueLabel.getText().toString().trim().isEmpty()) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.end_date_validation_empty_text));
        }
        //Validate end date > start date
        else if (mSurveyPollingData.startDate.after(mSurveyPollingData.endDate)) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.end_date_validation_after_start_date_text));
        }
        //Validate category
        else if (mSurveyPollingData.topics.size() == 0) {
            DialogHelper.createOKDialog(this,
                    mHeaderTitle,
                    resources.getString(R.string.category_validation_choosed_text));
        }
        //Validate title
        else if (mSurveyTitleEditText.getText().toString().trim().isEmpty()) {
            if (mSurveyPollingMode == Constants.MODE_SURVEY) {
                DialogHelper.createOKDialog(this,
                        mHeaderTitle,
                        resources.getString(R.string.survey_title_validation_empty_text));
            } else {
                DialogHelper.createOKDialog(this,
                        mHeaderTitle,
                        resources.getString(R.string.polling_title_validation_empty_text));
            }
        } else {
            if (mSurveyPollingData.departmentRestriction.isEmpty()) {
                mSurveyPollingData.departmentRestriction.add(0);
            }
            if(mIsReview){
                callEditDraftSurveyPollingApi();
            }else{
                callSaveDraftSurveyPollingApi();
            }
        }
    }

    //Helper
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String generateRestrictionText(Context context, int genderIndex, int statusIndex, int startAge, int endAge) {
        return generateRestrictionText(context, genderIndex, statusIndex, startAge, endAge, null);
    }

    public static String generateRestrictionText(Context context, int genderIndex, int statusIndex, int startAge, int endAge, List<Department> departmentList) {
        Resources resources = context.getResources();
        StringBuilder restriction = new StringBuilder();

        //Gender
        if (genderIndex != SurveyPolling.GENDER_ALL) {
            if (genderIndex == SurveyPolling.GENDER_MALE)
                restriction.append(resources.getString(R.string.gender_male_text));
            else if (genderIndex == SurveyPolling.GENDER_FEMALE)
                restriction.append(resources.getString(R.string.gender_female_text));

            restriction.append(", ");
        }

        //Status
        if (statusIndex != SurveyPolling.STATUS_ALL) {
            if (statusIndex == SurveyPolling.STATUS_SINGLE)
                restriction.append(resources.getString(R.string.status_single_text));
            else if (statusIndex == SurveyPolling.STATUS_MARRIED)
                restriction.append(resources.getString(R.string.status_married_text));
            else if (statusIndex == SurveyPolling.STATUS_DIVORCED)
                restriction.append(resources.getString(R.string.status_divorced_text));

            restriction.append(", ");
        }

        //Age
        String age = startAge + "-" + endAge + " yo";
        restriction.append(age);

        //Department
        if (departmentList != null) {
            for (Department department : departmentList) {
                restriction.append(", ");
                restriction.append(department.name);
            }
        }

        return restriction.toString();
    }
}
