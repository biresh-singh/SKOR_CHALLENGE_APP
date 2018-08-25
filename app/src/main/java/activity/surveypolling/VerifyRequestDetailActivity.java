package activity.surveypolling;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import bean.Question;
import bean.QuestionOption;
import bean.QuestionStatistic;
import bean.StatisticAnswer;
import bean.SurveyPolling;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

public class VerifyRequestDetailActivity extends AppCompatActivity {

    //Layout
    private TextView mHeaderTitleLabel;
    private TextView mHeaderRightLabel;
    private LinearLayout mCardLayout;
    private RelativeLayout mSurveyEndedLayout;
    private TextView mRecipientsLabel;
    private Button mSendButton;
    private TextView mRequestedByLabel;
    private TextView mSurveyTitleLabel;
    private TextView mSurveyDescriptionLabel;
    private TextView mDeadlineLabel;
    private TextView mCategoryLabel;
    private TextView mDeadlineHeaderLabel;
    private FlexboxLayout mRestrictionLayout;

    private Dialog mDialog;

    //Variable
    //private ArrayList<SurveyPollingCategory> mCategories;
    //private List<Department> mDepartments = new ArrayList<>();
    private String mHeaderTitle;
    private SurveyPolling mSurveyPollingData;
    private ArrayList<StatisticAnswer> mStatisticAnswers;
    private boolean isFromVerify = false;
    //private boolean isDepartmentDone = false;
    //private boolean isPending = false;

    //Static
    private static final int REJECT_CODE = 0;
    private static final int EDIT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_polling_request_detail);

        initData();
        initLayout();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mStatisticAnswers == null &&
                (mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_PUBLISHED) ||
                 mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_COMPLETED)))
            callStatisticAnswerApi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REJECT_CODE) {
                Resources resources = getResources();
                DialogHelper.createOKDialog(VerifyRequestDetailActivity.this, resources.getString(R.string.reject_message_title),
                        resources.getString(R.string.reject_message_content), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
            }
            else if (requestCode == EDIT_CODE) {
                if (data != null) {
                    SurveyPolling item = data.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);
                    if (item != null) {
                        mSurveyPollingData = item;
                        initializeSurveyPolling();
                    }
                }
            }
        }
    }

    protected void initData() {
        Intent intent = getIntent();
        mSurveyPollingData = intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);
        isFromVerify = intent.getBooleanExtra("isFromVerify",false);
    }

    protected void initLayout() {
        mHeaderTitleLabel = (TextView) findViewById(R.id.header_title_label);
        mHeaderRightLabel = (TextView) findViewById(R.id.header_right_label);
        mCardLayout = (LinearLayout) findViewById(R.id.question_layout);
        mSurveyEndedLayout = (RelativeLayout) findViewById(R.id.survey_ended_layout);
        mRecipientsLabel = (TextView) findViewById(R.id.finish_label);
        mSendButton = (Button) findViewById(R.id.send_button);
        mRequestedByLabel = (TextView) findViewById(R.id.requested_by_label);
        mSurveyTitleLabel = (TextView) findViewById(R.id.preview_title_label);
        mSurveyDescriptionLabel = (TextView) findViewById(R.id.preview_description_label);
        mDeadlineLabel = (TextView) findViewById(R.id.deadline_value_label);
        mCategoryLabel = (TextView) findViewById(R.id.category_value_label);
        mDeadlineHeaderLabel = (TextView) findViewById(R.id.deadline_label);
        mRestrictionLayout = (FlexboxLayout) findViewById(R.id.restriction_flexbox_layout);

        //Set Header label text
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY) {
            mHeaderTitle = getResources().getString(R.string.survey_request_detail_header_title);
            mHeaderTitleLabel.setText(mHeaderTitle);
            mDeadlineHeaderLabel.setText(R.string.survey_deadline_text);
        }
        else {
            mHeaderTitle = getResources().getString(R.string.polling_request_detail_header_title);
            mHeaderTitleLabel.setText(mHeaderTitle);
            mDeadlineHeaderLabel.setText(R.string.polling_deadline_text);
        }

        if(isFromVerify) {
            //Show/Hide Survey Ended Layout
            if (mSurveyPollingData.hasFinished() &&
                    (mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_PUBLISHED) ||
                            mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_COMPLETED))) {

                //Show ended layout
                mSurveyEndedLayout.setVisibility(View.VISIBLE);

                //Hide review button
                mHeaderRightLabel.setVisibility(View.GONE);

                if (mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_PUBLISHED)) {
                    //Set Total recipients
                    String text = null;
                    if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
                        text = getString(R.string.survey_ended_send_email_text, mSurveyPollingData.recipientCount);
                    else
                        text = getString(R.string.polling_ended_send_email_text, mSurveyPollingData.recipientCount);

                    mRecipientsLabel.setText(Html.fromHtml(text));

                    //Show send button
                    mSendButton.setVisibility(View.VISIBLE);
                } else if (mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_COMPLETED)) {
                    //Set Total recipients
                    String text = null;
                    if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
                        text = getString(R.string.survey_ended_text, mSurveyPollingData.recipientCount);
                    else
                        text = getString(R.string.polling_ended_text, mSurveyPollingData.recipientCount);

                    mRecipientsLabel.setText(Html.fromHtml(text));

                    //Show send button
                    mSendButton.setVisibility(View.GONE);
                }
            } else {
                //Hide ended layout
                mSurveyEndedLayout.setVisibility(View.GONE);

                //Show/Hide review button
                if (mSurveyPollingData.state.equalsIgnoreCase(SurveyPolling.STATE_DRAFT))
                    mHeaderRightLabel.setVisibility(View.VISIBLE);
                else
                    mHeaderRightLabel.setVisibility(View.GONE);
            }
        }else{
            mSurveyEndedLayout.setVisibility(View.GONE);
            mHeaderRightLabel.setVisibility(View.GONE);
        }
        initializeSurveyPolling();
    }

    private void initializeSurveyPolling() {
        //Set title label text
        if (mSurveyPollingData.name != null)
            mSurveyTitleLabel.setText(mSurveyPollingData.name);

        //Show/Hide descrption
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY) {
            //Set description label text
            if (mSurveyPollingData.description != null) {
                mSurveyDescriptionLabel.setText(mSurveyPollingData.description);
                mSurveyDescriptionLabel.setVisibility(View.VISIBLE);
            } else
                mSurveyDescriptionLabel.setVisibility(View.GONE);
        } else {
            mSurveyDescriptionLabel.setVisibility(View.GONE);
        }

        //Set requested by label text
        if (mSurveyPollingData.creator != null && mSurveyPollingData.createdDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault());
            mRequestedByLabel.setText(getResources().getString(R.string.requested_by_text, mSurveyPollingData.creator, dateFormat.format(mSurveyPollingData.createdDate)));
        }

        //Set deadline label text
        if (mSurveyPollingData.endDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, K:mm a", Locale.getDefault());
            mDeadlineLabel.setText(dateFormat.format(mSurveyPollingData.endDate).replace("a.m.", "AM").replace("p.m.", "PM"));
        }

        //Set category label text
        mCategoryLabel.setText("");
        if (!mSurveyPollingData.surveyPollingTopics.isEmpty()) {
            //int selectedCategoryId = mSurveyPollingData.topics.get(0);
            mCategoryLabel.setText(mSurveyPollingData.surveyPollingTopics.get(0).name);
        }

        //Generate restriction item
        generateRestrictionItems();

        //Generate question card
        if(isFromVerify) {
            if (mStatisticAnswers == null)
                generateQuestionCard(mSurveyPollingData.questions);
            else
                generateQuestionWithAnswerCard(mStatisticAnswers);
        }else{
            generateQuestionCard(mSurveyPollingData.questions);
        }

    }

    protected void initEvent() {
        //Do nothing
    }

    private void generateRestrictionItems() {
        //Clear all child view
        mRestrictionLayout.removeAllViews();

        // set gender restriction
        switch (mSurveyPollingData.genderRestriction) {
            case SurveyPolling.GENDER_MALE:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.gender_male_text));
                break;
            case SurveyPolling.GENDER_FEMALE:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.gender_female_text));
                break;
            case SurveyPolling.GENDER_ALL:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.gender_all_text));
                break;
            default:
                Log.d("generateRestrictionItem", "gender default case : " + mSurveyPollingData.genderRestriction);
                break;
        }

        // set status restriction
        switch (mSurveyPollingData.statusRestriction) {
            case SurveyPolling.STATUS_SINGLE:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.status_single_text));
                break;
            case SurveyPolling.STATUS_MARRIED:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.status_married_text));
                break;
            case SurveyPolling.STATUS_DIVORCED:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.status_divorced_text));
                break;
            case SurveyPolling.STATUS_ALL:
                mRestrictionLayout.addView(generateRestrictionItemLayout(R.string.status_all_text));
                break;
            default:
                Log.d("generateRestrictionItem", "gender default case : " + mSurveyPollingData.statusRestriction);
                break;
        }

        // set age restriction
        mRestrictionLayout.addView(generateRestrictionItemLayout(mSurveyPollingData.startAgeRestriction + "-" + mSurveyPollingData.endAgeRestriction + " YO"));

        // set department restriction
        for (String departmentName : mSurveyPollingData.departmentName)
            mRestrictionLayout.addView(generateRestrictionItemLayout(departmentName));

        /*if (!mSurveyPollingData.departmentRestriction.isEmpty())
            callDepartmentListApi();
        else
            isDepartmentDone = true;*/
    }

    private View generateRestrictionItemLayout(String restrictionLabelText) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_survey_polling_restriction_item, null);
        TextView restrictionLabel = (TextView) layout.findViewById(R.id.restriction_label);

        //Set item background
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
            restrictionLabel.setBackgroundResource(R.drawable.shape_survey_restriction_item_blue);
        else
            restrictionLabel.setBackgroundResource(R.drawable.shape_polling_restriction_item_green);

        restrictionLabel.setText(restrictionLabelText);
        return layout;
    }

    private View generateRestrictionItemLayout(int restrictionLabelTextResourceId) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_survey_polling_restriction_item, null);
        TextView restrictionLabel = (TextView) layout.findViewById(R.id.restriction_label);

        //Set item background
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
            restrictionLabel.setBackgroundResource(R.drawable.shape_survey_restriction_item_blue);
        else
            restrictionLabel.setBackgroundResource(R.drawable.shape_polling_restriction_item_green);

        restrictionLabel.setText(restrictionLabelTextResourceId);
        return layout;
    }

    /*private void generateDepartmentRestriction() {
        for (Integer departmentId : mSurveyPollingData.departmentRestriction) {
            for (Department department : mDepartments) {
                if (department.id == departmentId) {
                    mRestrictionLayout.addView(generateRestrictionItemLayout(department.name));
                }
            }
        }
        isDepartmentDone = true;
    }*/

    private void generateQuestionCard(List<Question> questions) {
        //Remove all child view
        mCardLayout.removeAllViews();

        int i = 0;
        for (Question question : questions) {
            generateQuestionCard(question, i!=0);
            i++;
        }
    }

    private void generateQuestionCard(final Question question, boolean showLineSeperator) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final VerifyRequestDetailActivity.QuestionCardPlaceHolder placeHolder = new VerifyRequestDetailActivity.QuestionCardPlaceHolder();
        View questionCard = null;
        if (question.type == Question.TYPE_TEXT) {
            questionCard = inflater.inflate(R.layout.layout_preview_edit_text, mCardLayout, false);
            placeHolder.answerEditText = (EditText) questionCard.findViewById(R.id.preview_answer_edit_text);
        } else if (question.type == Question.TYPE_SELECT_ONE_OR_MORE || question.type == Question.TYPE_MULTIPLE_CHOICE ||
                question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {

            questionCard = inflater.inflate(R.layout.layout_preview_option_list, mCardLayout, false);
            placeHolder.multipleOptionHintLabel = (TextView) questionCard.findViewById(R.id.preview_multiple_option_hint);
            placeHolder.optionsRadioGroup = (RadioGroup) questionCard.findViewById(R.id.preview_option_radio_group);
            placeHolder.optionsLayout = (LinearLayout) questionCard.findViewById(R.id.preview_option_check_box_layout);
            placeHolder.othersEditText = (EditText) questionCard.findViewById(R.id.preview_option_others_edit_text);

            //Show/Hide multiple option hint label
            if (question.type == Question.TYPE_SELECT_ONE_OR_MORE)
                placeHolder.multipleOptionHintLabel.setVisibility(View.VISIBLE);
            else
                placeHolder.multipleOptionHintLabel.setVisibility(View.GONE);

            //Show/Hide other layout
            if (question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {
                placeHolder.othersEditText.setVisibility(View.VISIBLE);

                //Set event for other edit text
                placeHolder.othersEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        question.answer = placeHolder.othersEditText.getText().toString();

                        //Set background color
                        if (question.answer.isEmpty()) {
                            placeHolder.othersEditText.setBackgroundResource(R.drawable.shape_survey_polling_item_white);
                            placeHolder.othersEditText.setTextColor(Color.BLACK);
                        } else {
                            placeHolder.optionsRadioGroup.clearCheck();
                            question.selectedRadioIndex = -1;

                            if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
                                placeHolder.othersEditText.setBackgroundResource(R.drawable.layerlist_survey_item_checked);
                            else
                                placeHolder.othersEditText.setBackgroundResource(R.drawable.shape_survey_polling_item_green);

                            placeHolder.othersEditText.setTextColor(Color.WHITE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            } else
                placeHolder.othersEditText.setVisibility(View.GONE);

            //Generate option
            int optionSize = question.options.size();
            if (question.type == Question.TYPE_SELECT_ONE_OR_MORE) {
                for (int i = 0; i < optionSize; i++) {
                    QuestionOption optionValue = question.options.get(i);
                    addCheckOptionToLayout(inflater, placeHolder, question, optionValue);
                }
            } else {
                for (int i = 0; i < optionSize; i++) {
                    QuestionOption optionValue = question.options.get(i);
                    addRadioOptionToLayout(inflater, placeHolder, question, optionValue);
                }
            }
        } else if (question.type == Question.TYPE_YES_NO) {
            questionCard = inflater.inflate(R.layout.layout_preview_yes_no, mCardLayout, false);
            placeHolder.yesNoRadioGroup = (RadioGroup) questionCard.findViewById(R.id.preview_yes_no_radio_group);
            RadioButton yesRadioButton = (RadioButton) questionCard.findViewById(R.id.preview_yes_button);
            RadioButton noRadioButton = (RadioButton) questionCard.findViewById(R.id.preview_no_button);

            //Set radiobutton background
            if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY) {
                yesRadioButton.setBackgroundResource(R.drawable.selector_survey_item_plain_checkbox);
                noRadioButton.setBackgroundResource(R.drawable.selector_survey_item_plain_checkbox);
            }
            else {
                yesRadioButton.setBackgroundResource(R.drawable.selector_polling_item_plain_checkbox);
                noRadioButton.setBackgroundResource(R.drawable.selector_polling_item_plain_checkbox);
            }
        }

        if (questionCard != null) {
            final View questionCardLayout = questionCard;

            //Set placeholder to tag
            questionCardLayout.setTag(placeHolder);

            //Initialize layout + title
            placeHolder.previewItemLayout = (LinearLayout) questionCardLayout.findViewById(R.id.preview_item_layout);
            placeHolder.titleLabel = (TextView) questionCardLayout.findViewById(R.id.preview_title_label);

            //Set layout
            if (!showLineSeperator)
                placeHolder.previewItemLayout.setBackground(null);

            //Set question title value
            placeHolder.titleLabel.setText(question.title);

            //Add question card to layout
            mCardLayout.addView(questionCardLayout);
        }
    }

    private void addCheckOptionToLayout(LayoutInflater inflater, final VerifyRequestDetailActivity.QuestionCardPlaceHolder placeHolder, final Question question, final QuestionOption optionValue) {
        final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.layout_preview_option_checkbox, null);

        //Set checkbox tag
        checkBox.setTag(Integer.valueOf(placeHolder.optionsLayout.getChildCount()));

        //Set checkbox text
        checkBox.setText(optionValue.option);

        //Set checkbox background
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
            checkBox.setBackgroundResource(R.drawable.selector_survey_item_checkbox);
        else
            checkBox.setBackgroundResource(R.drawable.selector_polling_item_checkbox);

        //Create layout param
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Resources resources = getResources();
        layoutParams.setMargins(0, resources.getDimensionPixelSize(R.dimen.question_margin_top), 0, resources.getDimensionPixelSize(R.dimen.question_margin_bottom));

        //Add option to layout
        placeHolder.optionsLayout.addView(checkBox, layoutParams);
    }

    private void addRadioOptionToLayout(LayoutInflater inflater, final VerifyRequestDetailActivity.QuestionCardPlaceHolder placeHolder, final Question question, final QuestionOption optionValue) {
        final RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_preview_option_radiobutton, null);

        //Set radiobutton id
        radioButton.setId(placeHolder.optionsRadioGroup.getChildCount());

        //Set radiobutton text
        radioButton.setText(optionValue.option);

        //Set radiobutton background
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
            radioButton.setBackgroundResource(R.drawable.selector_survey_item_checkbox);
        else
            radioButton.setBackgroundResource(R.drawable.selector_polling_item_checkbox);

        //Create layout param
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Resources resources = getResources();
        layoutParams.setMargins(0, resources.getDimensionPixelSize(R.dimen.question_margin_top), 0, resources.getDimensionPixelSize(R.dimen.question_margin_bottom));

        //Add option to layout
        placeHolder.optionsRadioGroup.addView(radioButton, layoutParams);
    }

    private void generateQuestionWithAnswerCard(ArrayList<StatisticAnswer> statisticAnswers) {
        //Remove all child view
        mCardLayout.removeAllViews();

        int i = 0;
        for (StatisticAnswer statisticAnswer : statisticAnswers) {
            for (Question question : mSurveyPollingData.questions) {
                if (question.id == statisticAnswer.questionId) {
                    generateQuestionWithAnswerCard(question, statisticAnswer, i!=0);
                    i++;
                    break;
                }
            }
        }
    }

    private void generateQuestionWithAnswerCard(Question question, StatisticAnswer answer, boolean showLineSeperator) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        VerifyRequestDetailActivity.QuestionAnswerCardPlaceholder placeholder = new QuestionAnswerCardPlaceholder();
        View questionAnswerCard = inflater.inflate(R.layout.layout_preview_answer, mCardLayout, false);
        placeholder.previewItemLayout = (LinearLayout) questionAnswerCard.findViewById(R.id.preview_item_layout);
        placeholder.titleLabel = ((TextView) questionAnswerCard.findViewById(R.id.preview_title_label));
        placeholder.emailAnswerHint = ((TextView) questionAnswerCard.findViewById(R.id.preview_email_answer_hint));
        placeholder.multipleOptionHintLabel = ((TextView) questionAnswerCard.findViewById(R.id.preview_multiple_option_hint));
        placeholder.optionsLayout = ((LinearLayout) questionAnswerCard.findViewById(R.id.preview_option_result_layout));

        //Set layout
        if (!showLineSeperator)
            placeholder.previewItemLayout.setBackground(null);

        //Set question text
        placeholder.titleLabel.setText(answer.question);

        //Show/Hide multiple option hint label
        if (question.type == Question.TYPE_SELECT_ONE_OR_MORE)
            placeholder.multipleOptionHintLabel.setVisibility(View.VISIBLE);
        else
            placeholder.multipleOptionHintLabel.setVisibility(View.GONE);

        //Show/Hide email answer hint label
        if (question.type == Question.TYPE_TEXT)
            placeholder.emailAnswerHint.setVisibility(View.VISIBLE);
        else {
            placeholder.emailAnswerHint.setVisibility(View.GONE);

            //Generate options
            int optionSize = answer.answers.size();
            for (int i = 0; i < optionSize; i++) {
                addOptionResultToLayout(inflater, placeholder, answer.answers.get(i));
            }
        }

        //Set placeholder to tag
        questionAnswerCard.setTag(placeholder);

        //Add question answer card to layout
        mCardLayout.addView(questionAnswerCard);
    }

    private void addOptionResultToLayout(LayoutInflater inflater, VerifyRequestDetailActivity.QuestionAnswerCardPlaceholder placeHolder, QuestionStatistic answer) {
        View optionResultLayout = inflater.inflate(R.layout.layout_survey_polling_complete_option, null);

        View fillPercentageView = optionResultLayout.findViewById(R.id.percentage_left_layout);
        View unFillPercentageView = optionResultLayout.findViewById(R.id.percentage_right_layout);
        TextView optionLabel = (TextView) optionResultLayout.findViewById(R.id.option_label);
        TextView optionPercentageLabel = (TextView) optionResultLayout.findViewById(R.id.option_percentage_label);

        //Set option label text
        optionLabel.setText(answer.question);

        //Set option percentage label text
        int percentage = answer.percentage;
        if (percentage > 100)
            percentage = 100;
        else if (percentage < 0) {
            percentage = 0;
        }
        optionPercentageLabel.setText(percentage + "%");

        //Calculate percentage
        float percentageWeight = ((float) percentage) / 100;

        //Set fillPercentageView layout weight
        LinearLayout.LayoutParams fillPercentageViewLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, percentageWeight);
        fillPercentageView.setLayoutParams(fillPercentageViewLayoutParams);

        //Set fillPercentageView color
        if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
            fillPercentageView.setBackgroundResource(R.drawable.shape_survey_polling_item_blue);
        else
            fillPercentageView.setBackgroundResource(R.drawable.shape_survey_polling_item_green);

        //Set unFillPercentageView layout weight
        LinearLayout.LayoutParams unfillPercentageViewLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f - percentageWeight);
        unFillPercentageView.setLayoutParams(unfillPercentageViewLayoutParams);

        //Add option result to layout
        placeHolder.optionsLayout.addView(optionResultLayout);
    }

    //Service
    /*private void callDepartmentListApi() {
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
        client.get(constants.Constants.BASEURL + Constants.GET_DEPARTMENT_LIST_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                DepartmentResponse response = new DepartmentResponse(jsonArray);
                mDepartments.clear();
                mDepartments.addAll(response.data);

                generateDepartmentRestriction();

                if (isPending)
                    validateCallApi();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(VerifyRequestDetailActivity.this);
                } else if (statusCode == 400) {

                } else if (statusCode == 500) {

                }
                if (isPending)
                    validateCallApi();
            }
        });
    }*/

    private void callApproveSurveyPollingApi(int rewardPoint, int creatorPoint) {
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
        paramMap.put("points", String.valueOf(rewardPoint));
        paramMap.put("points_creator", String.valueOf(creatorPoint));

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

                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(VerifyRequestDetailActivity.this);
                        } else if (statusCode == 400) {

                        } else if (statusCode == 500) {

                        }
                    }
                });
    }

    private void callStatisticAnswerApi() {
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
        client.get(constants.Constants.BASEURL + Constants.generateStatisticAnswerSurveyPollingApi(mSurveyPollingData.id), params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        if (response.has("result")) {
                            mStatisticAnswers = new ArrayList<>();
                            try {
                                JSONArray jsonArray = response.getJSONArray("result");
                                int size = jsonArray.length();
                                for (int i = 0; i < size; i++) {
                                    mStatisticAnswers.add(new StatisticAnswer(jsonArray.getJSONObject(i)));
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(isFromVerify)
                                generateQuestionWithAnswerCard(mStatisticAnswers);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(VerifyRequestDetailActivity.this);
                        } else if (statusCode == 400) {

                        } else if (statusCode == 500) {

                        }
                    }
                });
    }

    private void callShareResultApi(int surveyPollingId) {
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

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content_Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.post(this, constants.Constants.BASEURL + Constants.generateShareResultApi(surveyPollingId),
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        DialogHelper.createOKDialog(VerifyRequestDetailActivity.this, mHeaderTitle, getResources().getString(R.string.share_result_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                            UserManager.getInstance().logOut(VerifyRequestDetailActivity.this);
                        } else if (statusCode == 400) {

                        } else if (statusCode == 500) {

                        }
                    }
                });
    }

    //Event
    public void onBackButtonClicked(View view) {
        finish();
    }

    public void onReviewButtonClicked(View view) {
        final Resources resources = getResources();
        final String title = resources.getString(R.string.review_text);
        DialogHelper.createApproveEditRejectDialog(this, title, resources.getString(R.string.review_title_question),
                resources.getString(R.string.approve_text), resources.getString(R.string.edit_text), resources.getString(R.string.reject_text), new DialogHelper.ApproveEditRejectClickListener() {
                    @Override
                    public void onApproveClick() {
                        DialogHelper.createApproveDialog(VerifyRequestDetailActivity.this, new DialogHelper.ApproveDialogClickListener() {
                            @Override
                            public void onDoneClick(String creatorRewardValue, String rewardValue) {
                                int creatorReward = 0;
                                int reward = 0;

                                //Validate creator reward
                                if (!creatorRewardValue.isEmpty()) {
                                    try {
                                        creatorReward = Integer.parseInt(creatorRewardValue);
                                    }
                                    catch (Exception e) {
                                        DialogHelper.createOKDialog(VerifyRequestDetailActivity.this, title,
                                                resources.getString(R.string.approve_message_reward_validation));
                                        return;
                                    }
                                }

                                //Validate reward
                                if (!rewardValue.isEmpty()) {
                                    try {
                                        reward = Integer.parseInt(rewardValue);
                                    }
                                    catch (Exception e) {
                                        DialogHelper.createOKDialog(VerifyRequestDetailActivity.this, title,
                                                resources.getString(R.string.approve_message_reward_validation));
                                        return;
                                    }
                                }

                                callApproveSurveyPollingApi(reward, creatorReward);
                            }
                        });
                    }

                    @Override
                    public void onEditClick() {
                        Intent intent = new Intent(VerifyRequestDetailActivity.this, CreateSurveyPollingDetailActivity.class);
                        intent.putExtra(Constants.PARAM_USER_TYPE, Constants.TYPE_ADMIN);
                        intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
                        intent.putExtra(CreateSurveyPollingDetailActivity.PARAM_IS_REVIEW, true);
                        startActivityForResult(intent, EDIT_CODE);
                    }

                    @Override
                    public void onRejectClick() {
                        Intent intent = new Intent(VerifyRequestDetailActivity.this, RejectCommentActivity.class);
                        intent.putExtra(Constants.PARAM_USER_TYPE, Constants.TYPE_ADMIN);
                        intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
                        intent.putExtra(RejectCommentActivity.PARAM_CATEGORY_LABEL, mCategoryLabel.getText().toString());
                        startActivityForResult(intent, REJECT_CODE);
                    }
                });

    }

    public void onSendButtonClicked(View view) {
        callShareResultApi(mSurveyPollingData.id);
    }

    //ViewHolder
    private static class QuestionCardPlaceHolder {
        private LinearLayout previewItemLayout;
        private TextView titleLabel;
        private EditText answerEditText;
        private TextView multipleOptionHintLabel;
        private RadioGroup yesNoRadioGroup;
        private LinearLayout optionsLayout;
        private RadioGroup optionsRadioGroup;
        private EditText othersEditText;
    }

    private static class QuestionAnswerCardPlaceholder {
        private LinearLayout previewItemLayout;
        private TextView titleLabel;
        private TextView emailAnswerHint;
        private TextView multipleOptionHintLabel;
        private LinearLayout optionsLayout;
    }
}
