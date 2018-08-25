package activity.surveypolling;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

/**
 * Created by Ferry on 05/3/2018.
 */

public class CreateSurveyPollingDoneActivity extends AppCompatActivity {

	//Layout
	private TextView mTitleLabel;
	private TextView mTopDescriptionLabel;
	private TextView mBottomDescriptionLabel;

	//Variable
	private int mSurveyPollingMode = Constants.MODE_SURVEY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_polling_create_done);

		initData();
		initLayout();
		initEvent();
	}

	protected void initData() {
		Intent intent = getIntent();
		mSurveyPollingMode = intent.getIntExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
	}

	protected void initLayout() {
		mTitleLabel = (TextView) findViewById(R.id.title_label);
		mTopDescriptionLabel = (TextView) findViewById(R.id.top_description_label);
		mBottomDescriptionLabel = (TextView) findViewById(R.id.bottom_description_label);

		//Initialize value
		if (mSurveyPollingMode == Constants.MODE_SURVEY) {
			mTitleLabel.setText(R.string.create_survey_done_title);
			mTopDescriptionLabel.setText(R.string.create_survey_done_top_description);
			mBottomDescriptionLabel.setText(R.string.create_survey_done_bottom_description);
		}
		else {
			mTitleLabel.setText(R.string.create_polling_done_title);
			mTopDescriptionLabel.setText(R.string.create_polling_done_top_description);
			mBottomDescriptionLabel.setText(R.string.create_polling_done_bottom_description);
		}
	}

	protected void initEvent() {
		//Do nothing
	}

	//Event
	public void onDoneButtonClicked(View view) {
		finish();
	}
}