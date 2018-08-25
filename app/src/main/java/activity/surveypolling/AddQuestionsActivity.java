package activity.surveypolling;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.quickblox.chat.utils.DialogUtils;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

import bean.Question;
import bean.QuestionOption;
import utils.DialogHelper;

/**
 * Created by Ferry on 14/2/2018.
 */

public class AddQuestionsActivity extends AppCompatActivity {

	//Layout
	private ScrollView mRootLayout;
	private DragLinearLayout mQuestionCardLayout;
	private LinearLayout mAddQuestionLayout;
	private LinearLayout mAddQuestionOptionLayout;
	private CheckBox mAddQuestionCheckBox;
	private Button mMultipleChoiceOptionButton;
	private Button mMultipleChoiceWithOtherOptionButton;
	private Button mYesNoOptionButton;
	private Button mSelectOneOrMoreOptionButton;
	private Button mTextOptionButton;
	private TextView mReachMaximumQuestionLabel;

	//Variable
	private int mQuestionMode = Constants.MODE_SURVEY;
	private ArrayList<Question> mQuestions = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_polling_add_questions);

		initData();
		initLayout();
		initEvent();

		generateQuestionCard(mQuestions);
	}

	protected void initData() {
		Intent intent = getIntent();
		mQuestionMode = intent.getIntExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
		mQuestions = intent.getParcelableArrayListExtra(Constants.PARAM_QUESTIONS);
		if (mQuestions == null)
			mQuestions = new ArrayList<Question>();
	}

	protected void initLayout() {
		mRootLayout = (ScrollView) findViewById(R.id.add_question_root_layout);
		mQuestionCardLayout = (DragLinearLayout) findViewById(R.id.question_card_layout);
		mAddQuestionLayout = (LinearLayout) findViewById(R.id.add_question_layout);
		mAddQuestionCheckBox = (CheckBox) findViewById(R.id.add_question_check_box);
		mAddQuestionOptionLayout = (LinearLayout) findViewById(R.id.add_question_option_layout);
		mMultipleChoiceOptionButton = (Button) findViewById(R.id.question_multiple_choice_button);
		mMultipleChoiceWithOtherOptionButton = (Button) findViewById(R.id.question_multiple_choice_with_other_button);
		mYesNoOptionButton = (Button) findViewById(R.id.question_yes_no_button);
		mSelectOneOrMoreOptionButton = (Button) findViewById(R.id.question_select_one_or_more_button);
		mTextOptionButton = (Button) findViewById(R.id.question_text_button);
		mReachMaximumQuestionLabel = (TextView) findViewById(R.id.question_reach_maximum_label);

		//Set container scrollview
		mQuestionCardLayout.setContainerScrollView(mRootLayout);

		//Show/Hide question options depending on the mode
		if (mQuestionMode == Constants.MODE_SURVEY) {
			mMultipleChoiceOptionButton.setVisibility(View.VISIBLE);
			mMultipleChoiceWithOtherOptionButton.setVisibility(View.VISIBLE);
			mYesNoOptionButton.setVisibility(View.VISIBLE);
			mSelectOneOrMoreOptionButton.setVisibility(View.VISIBLE);
			mTextOptionButton.setVisibility(View.VISIBLE);

			//Set label of maximum question reach
			mReachMaximumQuestionLabel.setText(getResources().getString(R.string.question_reach_maximum_survey_question));
		}
		else if (mQuestionMode == Constants.MODE_POLLING) {
			mMultipleChoiceOptionButton.setVisibility(View.VISIBLE);
			mMultipleChoiceWithOtherOptionButton.setVisibility(View.GONE);
			mYesNoOptionButton.setVisibility(View.VISIBLE);
			mSelectOneOrMoreOptionButton.setVisibility(View.GONE);
			mTextOptionButton.setVisibility(View.GONE);

			//Set label of maximum question reach
			mReachMaximumQuestionLabel.setText(getResources().getString(R.string.question_reach_maximum_polling_question));
		}

		//Show/Hide "Add Question"
		int maxQuestion = mQuestionMode == Constants.MODE_SURVEY ? 5 : 1;
		if (mQuestions.size() < maxQuestion) {
			mAddQuestionLayout.setVisibility(View.VISIBLE);
			mReachMaximumQuestionLabel.setVisibility(View.GONE);
		}
		else {
			mAddQuestionLayout.setVisibility(View.GONE);
			mReachMaximumQuestionLabel.setVisibility(View.VISIBLE);
		}
	}

	protected void initEvent() {
		mQuestionCardLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
			@Override
			public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
				Log.d("Add Question", "DEBUG firstPos: " + firstPosition + " secondPos: " + secondPosition);
				Question first = mQuestions.get(firstPosition);
				mQuestions.remove(firstPosition);
				mQuestions.add(secondPosition, first);
			}
		});
	}

	private void generateQuestionCard(List<Question> questions) {
		for (Question question : questions) {
			generateQuestionCard(question);
		}
	}

	private void generateQuestionCard(final Question question) {
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final QuestionCardPlaceHolder placeHolder = new QuestionCardPlaceHolder();
		View questionCard = null;
		if (question.type == Question.TYPE_TEXT) {
			questionCard = inflater.inflate(R.layout.layout_question_card_edit_text, mQuestionCardLayout, false);
		}
		else if (question.type == Question.TYPE_SELECT_ONE_OR_MORE || question.type == Question.TYPE_MULTIPLE_CHOICE ||
				 question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {

			questionCard = inflater.inflate(R.layout.layout_question_card_option_list, mQuestionCardLayout, false);
			placeHolder.multipleOptionHintLabel = (TextView) questionCard.findViewById(R.id.question_multiple_option_hint);
			placeHolder.optionsLayout = (LinearLayout) questionCard.findViewById(R.id.question_option_layout);
			placeHolder.othersLayout = (LinearLayout) questionCard.findViewById(R.id.question_other_layout);
			placeHolder.addMoreOptionButton = (Button) questionCard.findViewById(R.id.add_more_option_button);

			//Show/Hide multiple option hint label
			if (question.type == Question.TYPE_SELECT_ONE_OR_MORE)
				placeHolder.multipleOptionHintLabel.setVisibility(View.VISIBLE);
			else
				placeHolder.multipleOptionHintLabel.setVisibility(View.GONE);

			//Show/Hide other layout
			if (question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER)
				placeHolder.othersLayout.setVisibility(View.VISIBLE);
			else
				placeHolder.othersLayout.setVisibility(View.GONE);

			//Add empty option if less than 2
			while (question.options.size() < 2) {
				question.options.add(new QuestionOption());
			}

			//Generate option - First 2 option are mandatory
			int optionSize = question.options.size();
			for (int i=0; i<optionSize; i++) {
				QuestionOption optionValue = question.options.get(i);
				addOptionToLayout(inflater, placeHolder, question, optionValue, i >= 2);
			}

			//Set add more option event
			placeHolder.addMoreOptionButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					QuestionOption option = new QuestionOption();
					question.options.add(option);
					addOptionToLayout(inflater, placeHolder, question, option, true);

					//Show/Hide "addMoreOptionButton"
					if (question.options.size() < 6)
						placeHolder.addMoreOptionButton.setVisibility(View.VISIBLE);
					else
						placeHolder.addMoreOptionButton.setVisibility(View.GONE);
				}
			});
		}
		else if (question.type == Question.TYPE_YES_NO) {
			questionCard = inflater.inflate(R.layout.layout_question_card_yes_no, mQuestionCardLayout, false);
			placeHolder.yesButton = (Button) questionCard.findViewById(R.id.question_yes_no_button);
			placeHolder.noButton = (Button) questionCard.findViewById(R.id.question_yes_no_button);
		}

		if (questionCard != null) {
			final View questionCardLayout = questionCard;

			//Set placeholder to tag
			questionCardLayout.setTag(placeHolder);

			//Initialize question placeholder
			placeHolder.type = question.type;
			placeHolder.titleEditText = (EditText) questionCardLayout.findViewById(R.id.question_title_edit_text);
			placeHolder.dragImageView = (ImageView) questionCardLayout.findViewById(R.id.question_header_drag_image_view);
			placeHolder.deleteButton = (ImageButton) questionCardLayout.findViewById(R.id.question_header_delete_button);

			//Set event for title edit text
			placeHolder.titleEditText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					question.title = placeHolder.titleEditText.getText().toString();
				}

				@Override
				public void afterTextChanged(Editable editable) {

				}
			});

			//Set event for delete button
			placeHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//Yes no confirmation
					Resources resources = getResources();
					DialogHelper.createYesNoDialog(AddQuestionsActivity.this,
							resources.getString(R.string.delete_text),
							resources.getString(R.string.question_delete_confirmation),
							resources.getString(R.string.delete_text),
							resources.getString(R.string.back_text), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							//Remove question card
							int index = mQuestionCardLayout.indexOfChild(questionCardLayout);
							mQuestionCardLayout.removeViewAt(index);
							mQuestions.remove(index);

							//Show/Hide "Add Question"
							int maxQuestion = mQuestionMode == Constants.MODE_SURVEY ? 5 : 1;
							if (mQuestions.size() < maxQuestion) {
								mAddQuestionLayout.setVisibility(View.VISIBLE);
								mReachMaximumQuestionLabel.setVisibility(View.GONE);

								mAddQuestionCheckBox.requestFocus();
							}
							else {
								mAddQuestionLayout.setVisibility(View.GONE);
								mReachMaximumQuestionLabel.setVisibility(View.VISIBLE);

								mReachMaximumQuestionLabel.requestFocus();
							}
						}
					}, null);
				}
			});

			//Set question title value
			placeHolder.titleEditText.setText(question.title);
			placeHolder.titleEditText.requestFocus();
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

			//Add question card to layout
			mQuestionCardLayout.addView(questionCardLayout);

			//Set drag view
			mQuestionCardLayout.setViewDraggable(questionCardLayout, placeHolder.dragImageView);
		}
	}

	private void addOptionToLayout(LayoutInflater inflater, final QuestionCardPlaceHolder placeHolder, final Question question, final QuestionOption optionValue, boolean showCloseButton) {
		final View optionLayout = inflater.inflate(R.layout.layout_question_card_option, placeHolder.optionsLayout, false);
		final EditText optionEditText = optionLayout.findViewById(R.id.option_edit_text);
		ImageButton optionDeleteButton = optionLayout.findViewById(R.id.close_image_button);

		//Set event for option edit text
		optionEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				optionValue.option = optionEditText.getText().toString();
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		//Set event for delete option
		optionDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Remove edit text from placeholder
				placeHolder.optionsEditText.remove(optionEditText);

				//Remove option from layout
				placeHolder.optionsLayout.removeView(optionLayout);

				//Remove option from question
				question.options.remove(optionValue);

				//Reorder edit text hint
				String optionText = getResources().getString(R.string.question_option);
				int number = 1;
				for (EditText editText : placeHolder.optionsEditText) {
					editText.setHint(optionText + " " + number);
					number++;
				}

				//Show/Hide "addMoreOptionButton"
				if (question.options.size() < 6)
					placeHolder.addMoreOptionButton.setVisibility(View.VISIBLE);
				else
					placeHolder.addMoreOptionButton.setVisibility(View.GONE);
			}
		});

		//Add edit text to placeholder
		placeHolder.optionsEditText.add(optionEditText);

		//Set option hint
		optionEditText.setHint(getResources().getString(R.string.question_option) + " " + (placeHolder.optionsLayout.getChildCount()+1));

 		//Set option value
		optionEditText.setText(optionValue.option);

		//Show/Hide close button
		optionDeleteButton.setVisibility(showCloseButton ? View.VISIBLE : View.GONE);

		//Add option to layout
		placeHolder.optionsLayout.addView(optionLayout);
	}

	//Event
	public void onBackButtonClicked(View view) {
		finish();
	}

	public void onSaveButtonClicked(View view) {
		hideKeyboard();

		//Validate all text and option are filled
		if (validateInput(mQuestions)) {
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra(Constants.PARAM_QUESTIONS, mQuestions);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	private boolean validateInput(List<Question> questions) {
		boolean valid = true;
		for (Question question : questions) {
			//Check title
			valid = valid && !question.title.trim().isEmpty();
			if (!valid) {
				Resources resources = getResources();
				DialogHelper.createOKDialog(this,
						resources.getString(R.string.add_questions_header_title),
						resources.getString(R.string.add_questions_validate_input_text));

				return valid;
			}

			if (question.type == Question.TYPE_SELECT_ONE_OR_MORE || question.type == Question.TYPE_MULTIPLE_CHOICE ||
					question.type == Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER) {
				for (QuestionOption option : question.options) {
					String optionValue = option.option.trim();
					valid = valid && !optionValue.isEmpty();
					if (!valid) {
						Resources resources = getResources();
						DialogHelper.createOKDialog(this,
								resources.getString(R.string.add_questions_header_title),
								resources.getString(R.string.add_questions_validate_input_text));
						return valid;
					}
					else if (optionValue.contains("|")) {
						Resources resources = getResources();
						DialogHelper.createOKDialog(this,
								resources.getString(R.string.add_questions_header_title),
								resources.getString(R.string.add_questions_validate_character_text));

						valid = false;
						return valid;
					}
				}
			}
		}

		return valid;
	}

	public void onAddQuestionButtonClicked(View view) {
		if (mAddQuestionOptionLayout.getVisibility() == View.VISIBLE)
			hideAddQuestionOption();
		else
			showAddQuestionOption();
	}

	private void showAddQuestionOption() {
		mAddQuestionOptionLayout.setVisibility(View.VISIBLE);
		mAddQuestionCheckBox.setChecked(true);
		mAddQuestionOptionLayout.requestFocus();
	}

	private void hideAddQuestionOption() {
		mAddQuestionOptionLayout.setVisibility(View.GONE);
		mAddQuestionCheckBox.setChecked(false);
	}

	public void onMultipleChoiceOptionClicked(View view) {
		Question question = new Question();
		question.type = Question.TYPE_MULTIPLE_CHOICE;
		onAddOptionClickedWithQuestion(question);
	}

	public void onMutlipleChoiceWithOtherOptionClicked(View view) {
		Question question = new Question();
		question.type = Question.TYPE_MULTIPLE_CHOICE_WITH_OTHER;
		onAddOptionClickedWithQuestion(question);
	}

	public void onYesNoOptionClicked(View view) {
		Question question = new Question();
		question.type = Question.TYPE_YES_NO;
		onAddOptionClickedWithQuestion(question);
	}

	public void onSelectOneOrMoreOptionClicked(View view) {
		Question question = new Question();
		question.type = Question.TYPE_SELECT_ONE_OR_MORE;
		onAddOptionClickedWithQuestion(question);
	}

	public void onTextOptionClicked(View view) {
		Question question = new Question();
		question.type = Question.TYPE_TEXT;
		onAddOptionClickedWithQuestion(question);
	}

	public void onAddOptionClickedWithQuestion(Question question) {
		hideAddQuestionOption();

		//Add Question Card
		generateQuestionCard(question);
		mQuestions.add(question);

		//Show/Hide "Add Question"
		int maxQuestion = mQuestionMode == Constants.MODE_SURVEY ? 5 : 1;
		if (mQuestions.size() < maxQuestion) {
			mAddQuestionLayout.setVisibility(View.VISIBLE);
			mReachMaximumQuestionLabel.setVisibility(View.GONE);

//			mAddQuestionCheckBox.requestFocus();
		}
		else {
			mAddQuestionLayout.setVisibility(View.GONE);
			mReachMaximumQuestionLabel.setVisibility(View.VISIBLE);

//			mReachMaximumQuestionLabel.requestFocus();
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

	public static class QuestionCardPlaceHolder {
		public int type;
		public ImageView dragImageView;
		public ImageButton deleteButton;
		public EditText titleEditText;
		public TextView multipleOptionHintLabel;
		public Button yesButton;
		public Button noButton;
		public LinearLayout optionsLayout;
		public List<EditText> optionsEditText = new ArrayList<EditText>();
		public LinearLayout othersLayout;
		public Button addMoreOptionButton;
	}
}
