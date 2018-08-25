package activity.surveypolling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.root.skor.R;

import bean.SurveyPolling;

/**
 * Created by Ferry on 05/3/2018.
 */

public class FillSurveyPollingThankYouActivity extends AppCompatActivity {

	//Layout
	private TextView mHeaderTitleLabel;
	private TextView mTopDescriptionLabel;
	private TextView mPointLabel;

	//Variable
	private SurveyPolling mSurveyPollingData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_polling_thank_you);

		initData();
		initLayout();
		initEvent();
	}

	protected void initData() {
		Intent intent = getIntent();
		mSurveyPollingData = intent.getParcelableExtra(Constants.PARAM_SURVEY_POLLING_DATA);
	}

	protected void initLayout() {
		mHeaderTitleLabel = (TextView) findViewById(R.id.header_title_label);
		mTopDescriptionLabel = (TextView) findViewById(R.id.top_description_label);
		mPointLabel = (TextView) findViewById(R.id.point_label);

		//Initialize value
		if (mSurveyPollingData.type == Constants.MODE_SURVEY) {
			mHeaderTitleLabel.setText(R.string.survey_thank_you_header_title);
			mTopDescriptionLabel.setText(R.string.thank_you_survey_text);
		}
		else {
			mHeaderTitleLabel.setText(R.string.polling_thank_you_header_title);
			mTopDescriptionLabel.setText(R.string.thank_you_polling_text);
		}

		//Set Point
		if (mSurveyPollingData.rewards >= 0) {
			mPointLabel.setText(getResources().getString(R.string.plus_x_pts_text, mSurveyPollingData.rewards));
			mPointLabel.setVisibility(View.VISIBLE);
		}
		else {
			mPointLabel.setVisibility(View.GONE);
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