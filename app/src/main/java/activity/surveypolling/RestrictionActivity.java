package activity.surveypolling;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.flexbox.FlexboxLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import activity.userprofile.LoginActivity;
import bean.Department;
import bean.DepartmentResponse;
import bean.SurveyPolling;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.DialogHelper;

/**
 * Created by Ferry on 21/2/2018.
 */

public class RestrictionActivity extends AppCompatActivity {

	//Layout
	private RadioGroup mGenderRadioGroup;
	private RadioGroup mStatusRadioGroup;
	private CrystalRangeSeekbar mAgeSeekBar;
	private TextView mAgeMinimumValueLabel;
	private TextView mAgeMaximumValueLabel;
	private RadioGroup mDepartmentRadioGroup;
	private FlexboxLayout mDepartmentFlexboxLayout;
	private CheckBox mAllDepartmentCheckBox;
	private Button mDepartmentAddButton;
	private Dialog mDialog;

	//Variable
	private int mUserType = Constants.TYPE_USER;
	private int mSelectedGenderIndex = SurveyPolling.GENDER_ALL;
	private int mSelectedStatusIndex = SurveyPolling.STATUS_ALL;
	private int mStartAgeRestriction = 20;
	private int mEndAgeRestriction = 50;
	private List<Department> mDepartments = new ArrayList<>();
	private ArrayList<Department> mSelectedDepartment = new ArrayList<>();
	private ArrayList<Integer> mSelectedDepartmentId = new ArrayList<>();

	//Intent Param
	public static final String PARAM_GENDER_RESTRICTION = "gender_restriction";
	public static final String PARAM_STATUS_RESTRICTION = "status_restriction";
	public static final String PARAM_START_AGE_RESTRICTION = "start_age_restriction";
	public static final String PARAM_END_AGE_RESTRICTION = "end_age_restriction";
	public static final String PARAM_DEPARTMENT_RESTRICTION = "department_restriction";
	public static final String PARAM_SELECTED_DEPARTMENT_ID = "selected_department_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_polling_restriction);

		initData();
		initLayout();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mDepartments.size() == 0)
			callDepartmentListApi();
	}

	protected void initData() {
		Intent intent = getIntent();
		mUserType = intent.getIntExtra(Constants.PARAM_USER_TYPE, Constants.TYPE_USER);
		mSelectedGenderIndex = intent.getIntExtra(RestrictionActivity.PARAM_GENDER_RESTRICTION, SurveyPolling.GENDER_ALL);
		mSelectedStatusIndex = intent.getIntExtra(RestrictionActivity.PARAM_STATUS_RESTRICTION, SurveyPolling.STATUS_ALL);
		mStartAgeRestriction = intent.getIntExtra(RestrictionActivity.PARAM_START_AGE_RESTRICTION, 20);
		mEndAgeRestriction = intent.getIntExtra(RestrictionActivity.PARAM_END_AGE_RESTRICTION, 50);
		ArrayList<Integer> selectedDepartmentId = intent.getIntegerArrayListExtra(RestrictionActivity.PARAM_SELECTED_DEPARTMENT_ID);
		if (selectedDepartmentId != null) {
			mSelectedDepartmentId.clear();
			mSelectedDepartmentId.addAll(selectedDepartmentId);
		}
	}

	protected void initLayout() {
		mGenderRadioGroup = findViewById(R.id.gender_radio_group);
		mStatusRadioGroup = findViewById(R.id.status_radio_group);
		mAgeSeekBar = findViewById(R.id.age_range_seekbar);
		mAgeMinimumValueLabel = findViewById(R.id.age_minimum_value_label);
		mAgeMaximumValueLabel = findViewById(R.id.age_maximum_value_label);
		mDepartmentRadioGroup = findViewById(R.id.department_radio_group);
		mDepartmentFlexboxLayout = findViewById(R.id.department_flexbox_layout);
		mAllDepartmentCheckBox = findViewById(R.id.department_all_check_box);
		mDepartmentAddButton = findViewById(R.id.department_add_button);

		//Set selected gender
		if (mSelectedGenderIndex == SurveyPolling.GENDER_MALE)
			mGenderRadioGroup.check(R.id.gender_male_radio_button);
		else if (mSelectedGenderIndex == SurveyPolling.GENDER_FEMALE)
			mGenderRadioGroup.check(R.id.gender_female_radio_button);
		else //gender == SurveyPolling.GENDER_ALL
			mGenderRadioGroup.check(R.id.gender_all_radio_button);

		//Set selected status
		if (mSelectedStatusIndex == SurveyPolling.STATUS_SINGLE)
			mStatusRadioGroup.check(R.id.status_single_radio_button);
		else if (mSelectedStatusIndex == SurveyPolling.STATUS_MARRIED)
			mStatusRadioGroup.check(R.id.status_married_radio_button);
		else if (mSelectedStatusIndex == SurveyPolling.STATUS_DIVORCED)
			mStatusRadioGroup.check(R.id.status_divorced_radio_button);
		else //status == SurveyPolling.STATUS_ALL
			mStatusRadioGroup.check(R.id.status_all_radio_button);

		//Set minimum and maximum start value
		mAgeSeekBar.setMinStartValue(mStartAgeRestriction);
		mAgeSeekBar.setMaxStartValue(mEndAgeRestriction);
		mAgeSeekBar.apply();
		mAgeMinimumValueLabel.setText(mStartAgeRestriction + "");
		mAgeMaximumValueLabel.setText(mEndAgeRestriction + "");

		//Set all department check
		if (mSelectedDepartment.size() == 0)
			mAllDepartmentCheckBox.setChecked(true);
		else
			mAllDepartmentCheckBox.setChecked(false);

		//Hide admin layout -> Not Used
		//if (mUserType != Constants.TYPE_ADMIN)
		mDepartmentAddButton.setVisibility(View.GONE);
	}

	protected void initEvent() {
		//Set gender radiogroup listener
		mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				if (id == R.id.gender_all_radio_button)
					mSelectedGenderIndex = SurveyPolling.GENDER_ALL;
				else if (id == R.id.gender_male_radio_button)
					mSelectedGenderIndex = SurveyPolling.GENDER_MALE;
				else if (id == R.id.gender_female_radio_button)
					mSelectedGenderIndex = SurveyPolling.GENDER_FEMALE;
			}
		});

		//Set status radiogroup listener
		mStatusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				if (id == R.id.status_all_radio_button)
					mSelectedStatusIndex = SurveyPolling.STATUS_ALL;
				else if (id == R.id.status_single_radio_button)
					mSelectedStatusIndex = SurveyPolling.STATUS_SINGLE;
				else if (id == R.id.status_married_radio_button)
					mSelectedStatusIndex = SurveyPolling.STATUS_MARRIED;
				else if (id == R.id.status_divorced_radio_button)
					mSelectedStatusIndex = SurveyPolling.STATUS_DIVORCED;
			}
		});

		//Set age seekbar listener
		mAgeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
			@Override
			public void valueChanged(Number minValue, Number maxValue) {
				mStartAgeRestriction = minValue.intValue();
				mAgeMinimumValueLabel.setText(String.valueOf(minValue));
				mEndAgeRestriction = maxValue.intValue();
				mAgeMaximumValueLabel.setText(String.valueOf(maxValue));
			}
		});

		//Set all department checkbox listener
		mAllDepartmentCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mAllDepartmentCheckBox.setChecked(true);

				//Clear all selected department
				int size = mDepartmentFlexboxLayout.getChildCount();
				for (int i=0; i<size; i++) {
					View child = mDepartmentFlexboxLayout.getChildAt(i);
					if (child instanceof CheckBox && child != mAllDepartmentCheckBox) {
						((CheckBox) child).setChecked(false);
					}
				}
				mSelectedDepartment.clear();

				mSelectedDepartmentId.clear();
				mSelectedDepartmentId.add(SurveyPolling.DEPARTMENT_ALL);
			}
		});
	}

	private void generateDepartment(List<Department> departments) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		//Clear all department
		mDepartmentFlexboxLayout.removeAllViews();

		//Add all department
		Resources resources = getResources();
		FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_height));
		layoutParams.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_margin_right), 0);
		mDepartmentFlexboxLayout.addView(mAllDepartmentCheckBox, layoutParams);

		//Add department list
		for (Department department : departments) {
			addDepartment(inflater, department);
		}

		//Add department add button
		mDepartmentFlexboxLayout.addView(mDepartmentAddButton);
	}

	private void addDepartment(LayoutInflater inflater, final Department department) {
		final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.layout_survey_polling_department_checkbox, null);

		//Set checkbox id
		checkBox.setId(department.id);

		//Set checkbox text
		checkBox.setText(department.name);

		//Set checked
		if (mSelectedDepartment.contains(department))
			checkBox.setChecked(true);

		//Set checkbox event
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean isChecked = checkBox.isChecked();
				if (isChecked) {
					//Remove all department
					mAllDepartmentCheckBox.setChecked(false);
					mSelectedDepartmentId.remove(Integer.valueOf(SurveyPolling.DEPARTMENT_ALL));

					//Add selected department
					mSelectedDepartment.add(department);
					mSelectedDepartmentId.add(department.id);
				}
				else {
					//Remove selected department
					mSelectedDepartment.remove(department);
					mSelectedDepartmentId.remove(Integer.valueOf(department.id));

					//Add all department if non selected
					if (mSelectedDepartment.size() == 0) {
						mAllDepartmentCheckBox.setChecked(true);
						mSelectedDepartmentId.add(SurveyPolling.DEPARTMENT_ALL);
					}
				}
			}
		});

		//Create layout param
		Resources resources = getResources();
		FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_height));
		layoutParams.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.survey_polling_flexbox_margin_right), 0);

		//Add checkbox to layout
		mDepartmentFlexboxLayout.addView(checkBox, layoutParams);
	}

	//Service
	private void callDepartmentListApi() {
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

				//Crosscheck if selected departments are available
				List<Department> selectedDepartment = new ArrayList<>();
				List<Integer> selectedDepartmentId = new ArrayList<>();
				for (Integer departmentId : mSelectedDepartmentId) {
					for (Department department : mDepartments) {
						if (department.id == departmentId) {
							selectedDepartment.add(department);
							selectedDepartmentId.add(department.id);
						}
					}
				}

				mSelectedDepartment.clear();
				mSelectedDepartment.addAll(selectedDepartment);
				mSelectedDepartmentId.clear();
				mSelectedDepartmentId.addAll(selectedDepartmentId);

				//If selected department is empty, check all department
				if (mSelectedDepartment.size() == 0)
					mAllDepartmentCheckBox.setChecked(true);
				else
					mAllDepartmentCheckBox.setChecked(false);

				//Generate department
				generateDepartment(mDepartments);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}

				if (statusCode == 401) {
					UserManager.getInstance().logOut(RestrictionActivity.this);
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

	public void onSaveButtonClicked(View view) {
		Intent intent = new Intent();
		intent.putExtra(PARAM_GENDER_RESTRICTION, mSelectedGenderIndex);
		intent.putExtra(PARAM_STATUS_RESTRICTION, mSelectedStatusIndex);
		intent.putExtra(PARAM_START_AGE_RESTRICTION, mStartAgeRestriction);
		intent.putExtra(PARAM_END_AGE_RESTRICTION, mEndAgeRestriction);
		intent.putParcelableArrayListExtra(PARAM_DEPARTMENT_RESTRICTION, mSelectedDepartment);
		intent.putIntegerArrayListExtra(PARAM_SELECTED_DEPARTMENT_ID, mSelectedDepartmentId);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void onDepartmentAddButtonClicked(View view) {
		Resources resources = getResources();
		DialogHelper.createAddDialog(this,
				resources.getString(R.string.department_add_header_title),
				resources.getString(R.string.department_add_hint), "", "", new DialogHelper.AddDialogClickListener() {

					@Override
					public void onDoneClick(String value) {
						//Add new department VIA API -> Not Used
						Department department = new Department(value);
						mDepartments.add(department);

						mDepartmentFlexboxLayout.removeView(mDepartmentAddButton);
						addDepartment((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE), department);
						mDepartmentFlexboxLayout.addView(mDepartmentAddButton);
					}

					@Override
					public void onCancelClick() {

					}
				});
	}
}
