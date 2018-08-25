package activity.surveypolling;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import activity.userprofile.LoginActivity;
import adaptor.SurveyPollingCommentAdapter;
import bean.Question;
import bean.QuestionStatistic;
import bean.StatisticAnswer;
import bean.SurveyPolling;
import bean.SurveyPollingComment;
import bean.SurveyPollingLike;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

/**
 * Created by Ferry on 05/3/2018.
 */

public class PollingSeeResultActivity extends AppCompatActivity {

	//Layout
	private TextView mHeaderTitleLabel;
	private TextView mPollingFinishLabel;
	private ImageView mSurveyPollingImageView;
	private TextView mSurveyPollingHeaderStartLabel;
	private TextView mSurveyPollingHeaderTitleLabel;
	private TextView mSurveyPollingHeaderDateLabel;
	private TextView mTitleLabel;
	//private TextView mDescriptionLabel;
	private LinearLayout mOptionLayout;
	private TextView mVoteViewLabel;
	private CheckBox mLoveCheckBox;
	private Button mCommentButton;
	private ListView mCommentList;
	//private RelativeLayout mCommentItemLayout;
	//private CircleImageView mCommentImageView;
	//private TextView mCommentNameLabel;
	//private TextView mCommentDateLabel;
	//private TextView mCommentLabel;
	private EditText mPostCommentEditText;
	private Dialog mDialog;

	//Variable
	private String mHeaderTitle;
	private int mSurveyPollingMode = Constants.MODE_SURVEY;
	private String mSurveyPollingId;
	private String mCategoryName;
	private int mLoveCount;
	private int mCommentCount;
	private int mVotedCount;
	private int mViewCount;
	private SurveyPolling mSurveyPollingData;
	private SurveyPollingCommentAdapter mCommentAdapter;

	//Static
	private static SimpleDateFormat mDateDisplayFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

	//Intent Param
	public static final String PARAM_SURVEY_POLLING_ID = "survey_polling_id";
	public static final String PARAM_CATEGORY_NAME = "category_name";
	public static final String PARAM_LOVE_COUNT = "love_count";
	public static final String PARAM_COMMENT_COUNT = "comment_count";
	public static final String PARAM_VOTED_COUNT = "voted_count";
	public static final String PARAM_VIEW_COUNT = "view_count";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_polling_see_result);

		initData();
		initLayout();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mSurveyPollingData == null)
			callStatisticAnswerApi();
		//response api surveyPollingDetail digabung pada statisticAnswerApi
			//callGetSurveyPollingDetail(mSurveyPollingId);
	}

	protected void initData() {
		Intent intent = getIntent();
		mSurveyPollingMode = intent.getIntExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
		mSurveyPollingId = intent.getStringExtra(PARAM_SURVEY_POLLING_ID);
		mCategoryName = intent.getStringExtra(PARAM_CATEGORY_NAME);
		mLoveCount = intent.getIntExtra(PARAM_LOVE_COUNT, 0);
		mCommentCount = intent.getIntExtra(PARAM_COMMENT_COUNT, 0);
		mVotedCount = intent.getIntExtra(PARAM_VOTED_COUNT, 0);
		mViewCount = intent.getIntExtra(PARAM_VIEW_COUNT, 0);
		mSurveyPollingData = intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);

		//Comment adapter
		mCommentAdapter = new SurveyPollingCommentAdapter(this);

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
			mHeaderTitle = getResources().getString(R.string.survey_see_result_header_title);
		else //Polling
			mHeaderTitle = getResources().getString(R.string.polling_see_result_header_title);
	}

	protected void initLayout() {
		mHeaderTitleLabel = (TextView) findViewById(R.id.header_title_label);
		mPollingFinishLabel = (TextView) findViewById(R.id.polling_finish_label);
		mSurveyPollingImageView = findViewById(R.id.survey_polling_image_view);
		mSurveyPollingHeaderStartLabel = (TextView) findViewById(R.id.survey_polling_header_start_label);
		mSurveyPollingHeaderTitleLabel = (TextView) findViewById(R.id.survey_polling_header_title_label);
		mSurveyPollingHeaderDateLabel = (TextView) findViewById(R.id.survey_polling_date_posted_label);
		mTitleLabel = (TextView) findViewById(R.id.polling_title_label);
		//mDescriptionLabel = (TextView) findViewById(R.id.survey_description_label);
		mOptionLayout = (LinearLayout) findViewById(R.id.polling_option_layout);
		mVoteViewLabel = (TextView) findViewById(R.id.vote_view_label);
		mLoveCheckBox = findViewById(R.id.love_check_box);
		mCommentButton = findViewById(R.id.view_all_comment_button);
		mCommentList = findViewById(R.id.comment_list);
		//mCommentItemLayout = findViewById(R.id.comment_item_layout);
		//mCommentImageView = findViewById(R.id.comment_image_view);
		//mCommentNameLabel = findViewById(R.id.comment_name_label);
		//mCommentDateLabel = findViewById(R.id. comment_date_label);
		//mCommentLabel = findViewById(R.id.comment_label);
		mPostCommentEditText = findViewById(R.id.post_comment_edit_text);

		//Set header title text
		mHeaderTitleLabel.setText(mHeaderTitle);

		//Set title text default value
		if (mSurveyPollingData == null) {
			Resources resources = getResources();

			//Set title label
			mTitleLabel.setText("");

			//Hide finish label
			mPollingFinishLabel.setVisibility(View.GONE);

			//Set vote view label
			mVoteViewLabel.setText(resources.getString(R.string.voted_view_text, mVotedCount, mViewCount));

			//Set love label
			mLoveCheckBox.setText(String.valueOf(mLoveCount));

			//Set view comment button
			mCommentButton.setText(resources.getString(R.string.view_all_comment_text, mCommentCount));

			//Hide comment view
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCommentList.getLayoutParams();
			layoutParams.height = resources.getDimensionPixelSize(R.dimen.survey_polling_comment_list_collapse_height);
			mCommentList.setLayoutParams(layoutParams);
			//mCommentItemLayout.setVisibility(View.INVISIBLE);
		}

		//Initialize
		if (mSurveyPollingMode == SurveyPolling.TYPE_SURVEY) {
			//Set image view
			mSurveyPollingImageView.setImageResource(R.drawable.survey_icon);

			//Set category/topic about
			mSurveyPollingHeaderStartLabel.setText(R.string.survey_text);
			mSurveyPollingHeaderStartLabel.setText("");

			//Set description label text
			//mDescriptionLabel.setText("");
			//mDescriptionLabel.setVisibility(View.VISIBLE);
		}
		else {
			//Set image view
			mSurveyPollingImageView.setImageResource(R.drawable.polling_icon);

			//Set category/topic about
			mSurveyPollingHeaderStartLabel.setText(R.string.polling_text);
			mSurveyPollingHeaderTitleLabel.setText("");

			//Set posted by
			mSurveyPollingHeaderDateLabel.setText("");

			//Hide description
			//mDescriptionLabel.setVisibility(View.GONE);
		}

		//Comment list
		mCommentList.setAdapter(mCommentAdapter);

		initializeSurveyPolling();
	}

	private void initializeSurveyPolling() {
		//Initialize
		if (mSurveyPollingData != null) {
			//Resources
			Resources resources = getResources();

			//Set finish label
			if (Calendar.getInstance().getTime().after(mSurveyPollingData.endDate)) {
				if (mSurveyPollingData.type == SurveyPolling.TYPE_SURVEY)
					mPollingFinishLabel.setText(Html.fromHtml(resources.getString(R.string.survey_has_ended_text, mSurveyPollingData.answerCount)));
				else
					mPollingFinishLabel.setText(Html.fromHtml(resources.getString(R.string.polling_has_ended_text, mSurveyPollingData.answerCount)));

				//Show finish label
				mPollingFinishLabel.setVisibility(View.VISIBLE);
			}
			else
				mPollingFinishLabel.setVisibility(View.GONE);

			//Set posted by
			mSurveyPollingHeaderDateLabel.setText(Html.fromHtml(resources.getString(R.string.posted_by_on_text,
					mSurveyPollingData.creator, mDateDisplayFormatter.format(mSurveyPollingData.createdDate))));

			//Polling
			if (mSurveyPollingData.type == SurveyPolling.TYPE_POLLING) {
				//Set image view
				mSurveyPollingImageView.setImageResource(R.drawable.polling_icon);

				//Set category/topic about
				if (!mSurveyPollingData.surveyPollingTopics.isEmpty()) {
					mSurveyPollingHeaderStartLabel.setText(R.string.polling_about_text);
					mSurveyPollingHeaderTitleLabel.setText(mSurveyPollingData.surveyPollingTopics.get(0).name);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.VISIBLE);
				}
				else {
					mSurveyPollingHeaderStartLabel.setText(R.string.polling_text);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.GONE);
				}

				//Hide description
				//mDescriptionLabel.setVisibility(View.GONE);

				//Set option
				if (mSurveyPollingData.questions.size() > 0) {
					Question question = mSurveyPollingData.questions.get(0);

					//Set polling title label text
					if (question.title != null)
						mTitleLabel.setText(question.title);

					//Set polling option
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					if (mSurveyPollingData.statisticAnswers.size() > 0) {

						//Clear all options
						mOptionLayout.removeAllViews();

						StatisticAnswer statisticAnswerPolling = mSurveyPollingData.statisticAnswers.get(0);
						for (QuestionStatistic answer : statisticAnswerPolling.answers)
							addAnsweredOptionToLayout(inflater, answer, question.yourAnswer, question.type);
					}
				}
			}
			//Survey
			else {
				//Set image view
				mSurveyPollingImageView.setImageResource(R.drawable.survey_icon);

				//Set category/topic about
				if (!mSurveyPollingData.surveyPollingTopics.isEmpty()) {
					mSurveyPollingHeaderStartLabel.setText(R.string.survey_about_text);
					mSurveyPollingHeaderTitleLabel.setText(mSurveyPollingData.surveyPollingTopics.get(0).name);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.VISIBLE);
				}
				else {
					mSurveyPollingHeaderStartLabel.setText(R.string.survey_text);
					mSurveyPollingHeaderTitleLabel.setVisibility(View.GONE);
				}

				//Show description
				//mDescriptionLabel.setVisibility(View.VISIBLE);

				//Set option
				if (mSurveyPollingData.questions.size() > 0) {
					Question question = mSurveyPollingData.questions.get(0);

					//Set polling title label text
					if (question.title != null)
						mTitleLabel.setText(question.title);

					//Set polling option
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					if (mSurveyPollingData.statisticAnswers.size() > 0) {
						StatisticAnswer statisticAnswerPolling = mSurveyPollingData.statisticAnswers.get(0);
						for (QuestionStatistic answer : statisticAnswerPolling.answers)
							addAnsweredOptionToLayout(inflater, answer, question.yourAnswer, question.type);
					}
				}
			}

			//Set vote view label (View count)
			mVoteViewLabel.setText(resources.getString(R.string.voted_view_text, mSurveyPollingData.answerCount, mSurveyPollingData.viewCount));

			//Like
			mLoveCheckBox.setText(String.valueOf(mSurveyPollingData.likes.size()));

			//Check Is Like
			SharedDatabase sharedDatabase = new SharedDatabase(this);
			int userId = sharedDatabase.getUserPk();
			for (SurveyPollingLike like : mSurveyPollingData.likes) {
				if (like.creator.id == userId) {
					mLoveCheckBox.setChecked(true);
					break;
				}
			}

			//Set view comment button
			int commentSize = mSurveyPollingData.comments.size();
			mCommentButton.setText(resources.getString(R.string.view_all_comment_text, commentSize));

			if (commentSize > 0) {
				SurveyPollingComment comment = mSurveyPollingData.comments.get(0);

				//Add data to list
				mCommentAdapter.notifyDataSetInvalidated();
				ArrayList<SurveyPollingComment> list = new ArrayList();
				list.add(comment);
				mCommentAdapter.setData(list);
				mCommentAdapter.notifyDataSetChanged();

				//Collapse List
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCommentList.getLayoutParams();
				layoutParams.height = resources.getDimensionPixelSize(R.dimen.survey_polling_comment_list_collapse_height);
				mCommentList.setLayoutParams(layoutParams);
			}
		}
	}

	protected void initEvent() {
		//Love checkbox
		mLoveCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mSurveyPollingData != null) {
					if (mLoveCheckBox.isChecked()) {
						mLoveCheckBox.setChecked(true);
						callLikeSurveyPolling(mSurveyPollingData.id);
					}
					else
						mLoveCheckBox.setChecked(true);
				}
			}
		});
	}

	private void addAnsweredOptionToLayout(LayoutInflater inflater, final QuestionStatistic answer, String yourAnswer, int type) {
		final View pollingResultLayout = inflater.inflate(R.layout.layout_see_polling_result_option, null);

		View fillPercentageView = pollingResultLayout.findViewById(R.id.polling_percentage_left);
		View unFillPercentageView = pollingResultLayout.findViewById(R.id.polling_percentage_right);
		TextView yourChoiceLabel = (TextView) pollingResultLayout.findViewById(R.id.your_choice_label);
		TextView optionLabel = (TextView) pollingResultLayout.findViewById(R.id.option_label);
		TextView optionPercentageLabel = (TextView) pollingResultLayout.findViewById(R.id.option_percentage_label);

		//Set option label text
		optionLabel.setText(answer.question);

		//Validate percentage
		int percentage = answer.percentage;
		if (percentage > 100)
			percentage = 100;

		//Set option percentage label text
		optionPercentageLabel.setText(percentage + "%");

		//Calculate percentage
		float percentageWeight = (float)percentage / 100;

		//Set fillPercentageView layout weight
		LinearLayout.LayoutParams fillPercentageViewLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, percentageWeight);
		fillPercentageView.setLayoutParams(fillPercentageViewLayoutParams);

		//Set unFillPercentageView layout weight
		LinearLayout.LayoutParams unfillPercentageViewLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f - percentageWeight);
		unFillPercentageView.setLayoutParams(unfillPercentageViewLayoutParams);

		//Set your choice
		yourChoiceLabel.setVisibility(View.GONE);
		if (yourAnswer != null) {
			if (type == Question.TYPE_YES_NO) {
				if (yourAnswer.equalsIgnoreCase(answer.question) ||
						(yourAnswer.equalsIgnoreCase("False") && answer.question.equalsIgnoreCase("No")) ||
						(yourAnswer.equalsIgnoreCase("No") && answer.question.equalsIgnoreCase("False")) ||
						(yourAnswer.equalsIgnoreCase("True") && answer.question.equalsIgnoreCase("Yes")) ||
						(yourAnswer.equalsIgnoreCase("Yes") && answer.question.equalsIgnoreCase("True"))) {
					yourChoiceLabel.setVisibility(View.VISIBLE);
				}
			}
			else if (yourAnswer.equalsIgnoreCase(answer.question)) {
				yourChoiceLabel.setVisibility(View.VISIBLE);
			}
		}

		//Add option result to layout
		mOptionLayout.addView(pollingResultLayout);
	}

	//Service
	private void callGetSurveyPollingDetail(String id) {
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

		HashMap<String, String> paramMap = new HashMap<String, String>();

		RequestParams params = new RequestParams(paramMap);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
		client.setTimeout(30000);
		client.addHeader("connection", "Keep-Alive");
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
		client.addHeader("Authorization", authProvider + " " + token);
		client.addHeader("USER-AGENT", LoginActivity.useragent);
		client.get(constants.Constants.BASEURL + Constants.generateGetSurveyPollingDetailApi(id), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				mSurveyPollingData = new SurveyPolling(jsonObject);
				callStatisticAnswerApi();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(PollingSeeResultActivity.this);
				}
				else if (statusCode == 400) {

				}
				else if (statusCode == 500) {

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
		client.get(constants.Constants.BASEURL + Constants.generateStatisticAnswerSurveyPollingApi(mSurveyPollingId), params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						if (mDialog != null) {
							mDialog.dismiss();
							mDialog = null;
						}

						if (response.has("survey_object")) {
							try {
								JSONObject jsonObject = response.getJSONObject("survey_object");
								mSurveyPollingData = new SurveyPolling(jsonObject);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (response.has("result")) {
							mSurveyPollingData.statisticAnswers.clear();
							try {
								JSONArray jsonArray = response.getJSONArray("result");
								int size = jsonArray.length();
								for (int i = 0; i < size; i++) {
									mSurveyPollingData.statisticAnswers.add(new StatisticAnswer(jsonArray.getJSONObject(i)));
								}
								Collections.reverse(mSurveyPollingData.statisticAnswers);
								initializeSurveyPolling();
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						if (mDialog != null) {
							mDialog.dismiss();
							mDialog = null;
						}

						if (statusCode == 401) {
							UserManager.getInstance().logOut(PollingSeeResultActivity.this);
						} else if (statusCode == 400) {

						} else if (statusCode == 500) {

						}
					}
				});
	}

	private void callPostSurveyPollingComment(int surveyPollingId, String comment) {
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
		paramMap.put("survey", String.valueOf(surveyPollingId));
		paramMap.put("comment", comment);

		RequestParams params = new RequestParams(paramMap);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
		client.setTimeout(30000);
		client.addHeader("connection", "Keep-Alive");
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
		client.addHeader("Authorization", authProvider + " " + token);
		client.addHeader("USER-AGENT", LoginActivity.useragent);
		client.post(constants.Constants.BASEURL + Constants.POST_SURVEY_POLLING_COMMENT_API, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				mPostCommentEditText.setText("");
				callStatisticAnswerApi();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(PollingSeeResultActivity.this);
				} else if (statusCode == 400) {

				} else if (statusCode == 500) {

				}
			}
		});
	}

	private void callLikeSurveyPolling(int surveyPollingId) {
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
		paramMap.put("survey", String.valueOf(surveyPollingId));

		RequestParams params = new RequestParams(paramMap);
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
		client.setTimeout(30000);
		client.addHeader("connection", "Keep-Alive");
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
		client.addHeader("Authorization", authProvider + " " + token);
		client.addHeader("USER-AGENT", LoginActivity.useragent);
		client.post(constants.Constants.BASEURL + Constants.POST_SURVEY_POLLING_LIKE_API, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				mLoveCheckBox.setChecked(true);
				mLoveCount++;

				mLoveCheckBox.setText(String.valueOf(mLoveCount));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(PollingSeeResultActivity.this);
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

	public void onViewAllCommentButtonClicked(View view) {
		if (mSurveyPollingData.comments.size() > 1) {
			//Add data to list
			mCommentAdapter.notifyDataSetInvalidated();
			mCommentAdapter.setData(mSurveyPollingData.comments);
			mCommentAdapter.notifyDataSetChanged();
			Collections.reverse(mSurveyPollingData.comments);
			//Expand List
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCommentList.getLayoutParams();
			layoutParams.height = getResources().getDimensionPixelSize(R.dimen.survey_polling_comment_list_expand_height);
			mCommentList.setLayoutParams(layoutParams);
		}
	}

	public void onPostCommentButtonClicked(View view) {
		String comment = mPostCommentEditText.getText().toString().trim();
		if (comment.isEmpty()) {
			DialogHelper.createOKDialog(this, getString(R.string.comment), getString(R.string.comment_validate_text));
		}
		else {
			if (mSurveyPollingData != null)
				callPostSurveyPollingComment(mSurveyPollingData.id, comment);
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
}
