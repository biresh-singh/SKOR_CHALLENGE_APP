package activity.surveypolling;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
import bean.QuestionAnswer;
import bean.QuestionOption;
import bean.SurveyPolling;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

/**
 * Created by Ferry on 05/3/2018.
 */

public class FillSurveyPollingActivity extends AppCompatActivity {

	//Layout
	private TextView mHeaderTitleLabel;
	private ImageView mSurveyPollingImageView;
	private TextView mSurveyPollingHeaderStartLabel;
	private TextView mSurveyPollingHeaderTitleLabel;
	private TextView mSurveyPollingHeaderDateLabel;
	private LinearLayout mHeaderTitleDescriptionLayout;
	private TextView mTitleLabel;
	private TextView mDescriptionLabel;
	private LinearLayout mCardLayout;
	private Dialog mDialog;

	//Variable
	private String mHeaderTitle;
	private int mSurveyPollingMode = Constants.MODE_SURVEY;
	private String mSurveyPollingId;
	private String mCategoryName;
	private SurveyPolling mSurveyPollingData;

	//Static
	private static SimpleDateFormat mDateDisplayFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

	//Intent Param
	public static final String PARAM_SURVEY_POLLING_ID = "survey_polling_id";
	public static final String PARAM_CATEGORY_NAME = "category_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_polling_fill);

		initData();
		initLayout();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mSurveyPollingData == null)
			callGetSurveyPollingDetail(mSurveyPollingId);
	}

	protected void initData() {
		Intent intent = getIntent();
		mSurveyPollingMode = intent.getIntExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
		mSurveyPollingId = intent.getStringExtra(PARAM_SURVEY_POLLING_ID);
		mCategoryName = intent.getStringExtra(PARAM_CATEGORY_NAME);
		mSurveyPollingData = intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);

		//Set mode
		if (mSurveyPollingData != null) {
			//Set mode
			if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
				mSurveyPollingMode = Constants.MODE_SURVEY;
			else if (mSurveyPollingData.type == SurveyPolling.TYPE_POLLING)
				mSurveyPollingMode = Constants.MODE_POLLING;
		}

		//Set header title
		if (mSurveyPollingMode == Constants.MODE_SURVEY)
			mHeaderTitle = getResources().getString(R.string.survey_fill_header_title);
		else //Polling
			mHeaderTitle = getResources().getString(R.string.polling_fill_header_title);
	}

	protected void initLayout() {
		mHeaderTitleLabel = (TextView) findViewById(R.id.header_title_label);
		mSurveyPollingImageView = findViewById(R.id.survey_polling_image_view);
		mSurveyPollingHeaderStartLabel = (TextView) findViewById(R.id.survey_polling_header_start_label);
		mSurveyPollingHeaderTitleLabel = (TextView) findViewById(R.id.survey_polling_header_title_label);
		mSurveyPollingHeaderDateLabel = (TextView) findViewById(R.id.survey_polling_date_posted_label);
		mHeaderTitleDescriptionLayout = (LinearLayout) findViewById(R.id.survey_header_title_description_layout);
		mTitleLabel = (TextView) findViewById(R.id.survey_title_label);
		mDescriptionLabel = (TextView) findViewById(R.id.survey_description_label);
		mCardLayout = (LinearLayout) findViewById(R.id.question_layout);

		//Set header title text
		mHeaderTitleLabel.setText(mHeaderTitle);

		//Set title text default value
		if (mSurveyPollingData == null)
			mTitleLabel.setText("");

		//Initialize
		if (mSurveyPollingMode == SurveyPolling.TYPE_SURVEY) {
			//Set image view
			mSurveyPollingImageView.setImageResource(R.drawable.survey_icon);

			//Set category/topic about
			mSurveyPollingHeaderStartLabel.setText(R.string.survey_text);
			mSurveyPollingHeaderTitleLabel.setText("");

			//Set description label text
			mDescriptionLabel.setText("");
			mDescriptionLabel.setVisibility(View.GONE);
		}
		else {
			//Set image view
			mSurveyPollingImageView.setImageResource(R.drawable.polling_icon);

			//Set category/topic about
			mSurveyPollingHeaderStartLabel.setText(R.string.polling_text);
			mSurveyPollingHeaderTitleLabel.setText("");

			//Set posted by
			mSurveyPollingHeaderDateLabel.setText("");

			//Hide title description layout
			mHeaderTitleDescriptionLayout.setVisibility(View.GONE);
		}

		initializeSurveyPolling();
	}

	private void initializeSurveyPolling() {
		//Initialize
		if (mSurveyPollingData != null) {
			//Set title label text
			if (mSurveyPollingData.name != null)
				mTitleLabel.setText(mSurveyPollingData.name);

			if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY) {
				//Set image view
				mSurveyPollingImageView.setImageResource(R.drawable.survey_icon);

				//Set category/topic about
				if (mCategoryName != null) {
					mSurveyPollingHeaderStartLabel.setText(R.string.survey_about_text);
					mSurveyPollingHeaderTitleLabel.setText(mCategoryName);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.VISIBLE);
				}
				else {
					mSurveyPollingHeaderStartLabel.setText(R.string.survey_text);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.GONE);
				}

				//Set description label text
				if (mSurveyPollingData.description != null) {
					mDescriptionLabel.setText(mSurveyPollingData.description);
					mDescriptionLabel.setVisibility(View.GONE);
				}
				else
					mDescriptionLabel.setVisibility(View.GONE);
			}
			else {
				//Set image view
				mSurveyPollingImageView.setImageResource(R.drawable.polling_icon);

				//Set category/topic about
				if (mCategoryName != null) {
					mSurveyPollingHeaderStartLabel.setText(R.string.polling_about_text);
					mSurveyPollingHeaderTitleLabel.setText(mCategoryName);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.VISIBLE);
				}
				else {
					mSurveyPollingHeaderStartLabel.setText(R.string.polling_text);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.GONE);
				}

				//Hide description
				mDescriptionLabel.setVisibility(View.GONE);
			}

			//Set posted by
			mSurveyPollingHeaderDateLabel.setText(Html.fromHtml(getResources().getString(R.string.posted_by_on_text,
					mSurveyPollingData.creator, mDateDisplayFormatter.format(mSurveyPollingData.createdDate))));

			//Generate question
			generateQuestionCard(mSurveyPollingData.questions);
		}
	}

	protected void initEvent() {
		//Do nothing
	}

	private void generateQuestionCard(List<Question> questions) {
		int i = 0;
		for (Question question : questions) {
			generateQuestionCard(question, i!=0);
			i++;
		}
	}

	private void generateQuestionCard(final Question question, boolean showLineSeperator) {
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final QuestionCardPlaceHolder placeHolder = new QuestionCardPlaceHolder();
		View questionCard = null;
		if (question.type == Question.TYPE_TEXT) {
			questionCard = inflater.inflate(R.layout.layout_preview_edit_text, mCardLayout, false);
			placeHolder.answerEditText = (EditText) questionCard.findViewById(R.id.preview_answer_edit_text);

			//Set event for answer edit text
			placeHolder.answerEditText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					question.answer = placeHolder.answerEditText.getText().toString();
				}

				@Override
				public void afterTextChanged(Editable editable) {

				}
			});
		}
		else if (question.type == Question.TYPE_SELECT_ONE_OR_MORE || question.type == Question.TYPE_MULTIPLE_CHOICE ||
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
						}
						else {
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
			}
			else
				placeHolder.othersEditText.setVisibility(View.GONE);

			//Generate option
			int optionSize = question.options.size();
			if (question.type == Question.TYPE_SELECT_ONE_OR_MORE) {
				for (int i = 0; i < optionSize; i++) {
					QuestionOption optionValue = question.options.get(i);
					addCheckOptionToLayout(inflater, placeHolder, question, optionValue);
				}
			}
			else {
				for (int i = 0; i < optionSize; i++) {
					QuestionOption optionValue = question.options.get(i);
					addRadioOptionToLayout(inflater, placeHolder, question, optionValue);
				}
			}

			//Set radiogroup event
			if (question.type == Question.TYPE_MULTIPLE_CHOICE || question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {
				placeHolder.optionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup radioGroup, int id) {
						question.selectedRadioIndex = id;
					}
				});
			}
		}
		else if (question.type == Question.TYPE_YES_NO) {
			questionCard = inflater.inflate(R.layout.layout_preview_yes_no, mCardLayout, false);
			placeHolder.yesNoRadioGroup = (RadioGroup) questionCard.findViewById(R.id.preview_yes_no_radio_group);
			RadioButton yesRadioButton = (RadioButton) questionCard.findViewById(R.id.preview_yes_button);
			RadioButton noRadioButton = (RadioButton) questionCard.findViewById(R.id.preview_no_button);

			//Set event for yes no radiogroup
			placeHolder.yesNoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup radioGroup, int id) {
					if (id == R.id.preview_yes_button)
						question.boolAnswer = 1;
					else
						question.boolAnswer = 0;
				}
			});

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

	private void addCheckOptionToLayout(LayoutInflater inflater, final QuestionCardPlaceHolder placeHolder, final Question question, final QuestionOption optionValue) {
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

		//Set checkbox event
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				Integer index = (Integer)checkBox.getTag();
				if (isChecked)
					question.selectedCheckboxIndex.add(index);
				else
					question.selectedCheckboxIndex.remove(index);
			}
		});

		//Create layout param
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		Resources resources = getResources();
		layoutParams.setMargins(0, resources.getDimensionPixelSize(R.dimen.question_margin_top), 0, resources.getDimensionPixelSize(R.dimen.question_margin_bottom));

		//Add option to layout
		placeHolder.optionsLayout.addView(checkBox, layoutParams);
	}

	private void addRadioOptionToLayout(LayoutInflater inflater, final QuestionCardPlaceHolder placeHolder, final Question question, final QuestionOption optionValue) {
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

		//Set radiobutton event
		radioButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Reset others edit text
				if (placeHolder.othersEditText.getVisibility() == View.VISIBLE) {
					placeHolder.othersEditText.setText("");
				}
			}
		});

		//Create layout param
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		Resources resources = getResources();
		layoutParams.setMargins(0, resources.getDimensionPixelSize(R.dimen.question_margin_top), 0, resources.getDimensionPixelSize(R.dimen.question_margin_bottom));

		//Add option to layout
		placeHolder.optionsRadioGroup.addView(radioButton, layoutParams);
	}

	//Service
	private void callGetSurveyPollingDetail(String id) {
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
		client.get(constants.Constants.BASEURL + activity.surveypolling.Constants.generateGetSurveyPollingDetailApi(id), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				mSurveyPollingData = new SurveyPolling(jsonObject);
				initializeSurveyPolling();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(FillSurveyPollingActivity.this);
				}
				else if (statusCode == 400) {

				}
				else if (statusCode == 500) {

				}
			}
		});
	}

	private void callFillSurveyPollingAPi(int surveyPollingId, List<QuestionAnswer> answers) {
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
			JSONArray jsonArray = new JSONArray();
			for (QuestionAnswer answer : answers) {
				jsonArray.put(QuestionAnswer.toJSONObject(answer));
			}
			jsonParams.put("answers", jsonArray);
			entity = new StringEntity(jsonParams.toString());
		}
		catch (Exception e) {}

		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
		client.setTimeout(30000);
		client.addHeader("connection", "Keep-Alive");
		client.addHeader("Content-Type", "application/json");
		client.addHeader("Authorization", authProvider + " " + token);
		client.addHeader("USER-AGENT", LoginActivity.useragent);
		client.post(this, constants.Constants.BASEURL + Constants.generateAnswerSurveyPollingDetailApi(surveyPollingId), entity, "application/json", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				finish();

				Intent intent = new Intent(FillSurveyPollingActivity.this, FillSurveyPollingThankYouActivity.class);
				intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
				startActivity(intent);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				finish();

				Intent intent = new Intent(FillSurveyPollingActivity.this, FillSurveyPollingThankYouActivity.class);
				intent.putExtra(Constants.PARAM_SURVEY_POLLING_DATA, mSurveyPollingData);
				startActivity(intent);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(FillSurveyPollingActivity.this);
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

	public void onSubmitButtonClicked(View view) {
		hideKeyboard();

		//Validate all text and option are filled
		List<QuestionAnswer> answers = new ArrayList<>();
		if (validateInput(mSurveyPollingData.questions, answers)) {
			callFillSurveyPollingAPi(mSurveyPollingData.id, answers);
		}
		else {
			Resources resources = getResources();
			DialogHelper.createOKDialog(this, mHeaderTitle,
					resources.getString(R.string.fill_questions_validate_input_text));
		}
	}

	private boolean validateInput(List<Question> questions, List<QuestionAnswer> answers) {
		boolean valid = true;
		for (Question question : questions) {
			//Check type
			if (question.type == Question.TYPE_TEXT) {
				String answer = question.answer.trim();
				valid = valid && !answer.isEmpty();
				if (valid) {
					answers.add(new QuestionAnswer(question.id, answer));
				}
				else
					break;
			}
			else if (question.type == Question.TYPE_SELECT_ONE_OR_MORE) {
				valid = valid && (question.selectedCheckboxIndex.size() > 0);
				if (valid) {
					StringBuilder answer = new StringBuilder();
					int i = 0;
					for (Integer index : question.selectedCheckboxIndex) {
						if (i != 0)
							answer.append("|");

						String option = question.options.get(index).option;
						answer.append(option);
						i++;
					}
					answers.add(new QuestionAnswer(question.id, answer.toString()));
				}
				else
					break;
			}
			else if (question.type == Question.TYPE_MULTIPLE_CHOICE) {
				valid = valid && (question.selectedRadioIndex != -1);
				if (valid) {
					answers.add(new QuestionAnswer(question.id, question.options.get(question.selectedRadioIndex).option));
				}
				else
					break;
			}
			else if (question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {
				String answer = question.answer.trim();
				valid = valid && (question.selectedRadioIndex != -1 || !answer.isEmpty());
				if (valid) {
					if (question.selectedRadioIndex != -1)
						answers.add(new QuestionAnswer(question.id, question.options.get(question.selectedRadioIndex).option));
					else
						answers.add(new QuestionAnswer(question.id, answer));
				}
				else
					break;
			}
			else if (question.type == Question.TYPE_YES_NO) {
				valid = valid && (question.boolAnswer != -1);
				if (valid) {
					boolean result = question.boolAnswer == 1;
					answers.add(new QuestionAnswer(question.id, result ? "True" : "False", result ? 1 : 0));
				}
				else
					break;
			}
		}

		return valid;
	}

	//Helper
	private void hideKeyboard() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static class QuestionCardPlaceHolder {
		private LinearLayout previewItemLayout;
		private TextView titleLabel;
		private EditText answerEditText;
		private TextView multipleOptionHintLabel;
		private RadioGroup yesNoRadioGroup;
		private LinearLayout optionsLayout;
		private RadioGroup optionsRadioGroup;
		private EditText othersEditText;
	}
}
