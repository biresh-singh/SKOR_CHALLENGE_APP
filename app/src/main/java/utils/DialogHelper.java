package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ferry on 21/2/2018.
 */

public class DialogHelper {
	public static void createOKDialog(Context context, String title, String message) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_dialog, null);
			TextView titleLabel = (TextView) rootView.findViewById(R.id.dialog_title);
			titleLabel.setText(title);
			TextView contentLabel = (TextView) rootView.findViewById(R.id.dialog_content);
			contentLabel.setText(message);
			Button okButton = (Button) rootView.findViewById(R.id.dialog_ok_button);
			RelativeLayout positiveNegativeLayout = (RelativeLayout) rootView.findViewById(R.id.positive_negative_layout);
			positiveNegativeLayout.setVisibility(View.GONE);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			okButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public static void createOKDialog(Context context, String title, String message, final DialogInterface.OnClickListener clickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_dialog, null);
			TextView titleLabel = (TextView) rootView.findViewById(R.id.dialog_title);
			titleLabel.setText(title);
			TextView contentLabel = (TextView) rootView.findViewById(R.id.dialog_content);
			contentLabel.setText(message);
			Button okButton = (Button) rootView.findViewById(R.id.dialog_ok_button);
			RelativeLayout positiveNegativeLayout = (RelativeLayout) rootView.findViewById(R.id.positive_negative_layout);
			positiveNegativeLayout.setVisibility(View.GONE);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			okButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					if (clickListener != null)
						clickListener.onClick(null, 0);
				}
			});
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public static void createYesNoDialog(Context context, String title, String message, String positiveLabel, String negativeLabel, final DialogInterface.OnClickListener yesClickListener, final DialogInterface.OnClickListener noClickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_dialog, null);
			TextView titleLabel = (TextView) rootView.findViewById(R.id.dialog_title);
			titleLabel.setText(title);
			TextView contentLabel = (TextView) rootView.findViewById(R.id.dialog_content);
			contentLabel.setText(message);
			Button okButton = (Button) rootView.findViewById(R.id.dialog_ok_button);
			okButton.setVisibility(View.GONE);
			RelativeLayout positiveNegativeLayout = (RelativeLayout) rootView.findViewById(R.id.positive_negative_layout);
			positiveNegativeLayout.setVisibility(View.VISIBLE);
			Button positiveButton = (Button) rootView.findViewById(R.id.dialog_positive_button);
			positiveButton.setText(positiveLabel);
			Button negativeButton = (Button) rootView.findViewById(R.id.dialog_negative_button);
			negativeButton.setText(negativeLabel);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					if (yesClickListener != null)
						yesClickListener.onClick(null, 0);
				}
			});
			negativeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					if (noClickListener != null)
						noClickListener.onClick(null, 1);
				}
			});
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public static void createApproveEditRejectDialog(Context context, String title, String message, String approveLabel, String editLabel, String rejectLabel, final DialogHelper.ApproveEditRejectClickListener approveEditRejectClickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_review_dialog, null);
			TextView titleLabel = (TextView) rootView.findViewById(R.id.dialog_title);
			titleLabel.setText(title);
			TextView contentLabel = (TextView) rootView.findViewById(R.id.dialog_content);
			contentLabel.setText(message);
			Button approveButton = (Button) rootView.findViewById(R.id.dialog_approve_button);
            approveButton.setText(approveLabel);
			Button editButton = (Button) rootView.findViewById(R.id.dialog_edit_button);
            editButton.setText(editLabel);
			Button rejectButton = (Button) rootView.findViewById(R.id.dialog_reject_button);
            rejectButton.setText(rejectLabel);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			approveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					approveEditRejectClickListener.onApproveClick();
				}
			});
			editButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					approveEditRejectClickListener.onEditClick();
				}
			});
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)  {
                    alertDialog.dismiss();
                    approveEditRejectClickListener.onRejectClick();
                }
            });
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public static void createApproveDialog(Context context, final ApproveDialogClickListener approveDialogClickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_approve_dialog, null);
			final EditText creatorRewardText = (EditText) rootView.findViewById(R.id.dialog_creator_reward_edit_text);
			final EditText rewardText = (EditText) rootView.findViewById(R.id.dialog_reward_edit_text);
			Button doneButton = (Button) rootView.findViewById(R.id.dialog_done_button);

			// cancelable false to prevent loss of approved state
			alert.setCancelable(false);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    if (approveDialogClickListener != null) {
                        approveDialogClickListener.onDoneClick(creatorRewardText.getText().toString().trim(), rewardText.getText().toString().trim());
                    }
                }
            });
			alertDialog.show();
		} catch (Exception e) {}
	}

	public static void createAddDialog(final Context context, String title, String hint, String message, String value, final AddDialogClickListener addDialogClickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_add_dialog, null);
			TextView titleLabel = (TextView) rootView.findViewById(R.id.dialog_title);
			titleLabel.setText(title);
			final EditText contentLabel = (EditText) rootView.findViewById(R.id.dialog_content);
			contentLabel.setHint(hint);
			contentLabel.setText(message);
			if (contentLabel.requestFocus()) {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(contentLabel, 0);
			}

			Button positiveButton = (Button) rootView.findViewById(R.id.dialog_positive_button);
			Button negativeButton = (Button) rootView.findViewById(R.id.dialog_negative_button);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(contentLabel.getWindowToken(), 0);

					alertDialog.dismiss();
					if (addDialogClickListener != null)
						addDialogClickListener.onDoneClick(contentLabel.getText().toString());
				}
			});
			negativeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(contentLabel.getWindowToken(), 0);

					alertDialog.dismiss();
					if (addDialogClickListener != null)
						addDialogClickListener.onCancelClick();
				}
			});
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public static void createDatePickerDialog(Context context, Date date, final DatePickerClickListener datePickerClickListener) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View rootView = layoutInflater.inflate(R.layout.layout_survey_polling_date_picker_dialog, null);

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			final DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.dialog_date_picker);
			datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			datePicker.setMinDate(new Date().getTime());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				int headerId = context.getResources().getSystem().getIdentifier("day_picker_selector_layout", "id", "android");
				final View header = datePicker.findViewById(headerId);
				if (header != null) {
					header.setBackgroundResource(R.drawable.shape_survey_polling_datepicker_header);
				}
			}

			Button positiveButton = (Button) rootView.findViewById(R.id.dialog_positive_button);
			Button negativeButton = (Button) rootView.findViewById(R.id.dialog_negative_button);

			alert.setCancelable(true);
			alert.setView(rootView);
			final AlertDialog alertDialog = alert.create();
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					if (datePickerClickListener != null) {
						int year =  datePicker.getYear();
						int month = datePicker.getMonth() + 1; //0 based
						int day = datePicker.getDayOfMonth();

						datePickerClickListener.onDoneClick(year, month, day);
					}
				}
			});
			negativeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)  {
					alertDialog.dismiss();
					if (datePickerClickListener != null)
						datePickerClickListener.onCancelClick();
				}
			});
			alertDialog.show();
		}
		catch (Exception e) {}
	}

	public interface AddDialogClickListener {
		void onDoneClick(String value);
		void onCancelClick();
	}

	public interface DatePickerClickListener {
		void onDoneClick(int year, int month, int day);
		void onCancelClick();
	}

	public interface ApproveEditRejectClickListener {
		void onApproveClick();
		void onEditClick();
		void onRejectClick();
	}

	public interface ApproveDialogClickListener {
		void onDoneClick(String creatorRewardValue, String rewardValue);
	}
}
