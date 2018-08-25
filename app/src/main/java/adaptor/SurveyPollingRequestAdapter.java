package adaptor;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.root.skor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bean.SurveyPolling;

/**
 * Created by mac on 2/13/18.
 */

public class SurveyPollingRequestAdapter extends BaseAdapter {
    private Context mContext;
    private List<SurveyPolling> mData = new ArrayList<>();
    private static SimpleDateFormat mDateTimeDisplayFormat = new SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault());

    public SurveyPollingRequestAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<SurveyPolling> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SurveyPolling getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SurveyPolling item = getItem(position);

        final SurveyPollingHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_survey_polling_verify_request, parent, false);

            holder = new SurveyPollingHolder(convertView);
            convertView.setTag(holder);
        }
        else
            holder = (SurveyPollingHolder) convertView.getTag();

        //CHECK Survey/Polling Type
        if (item.type == SurveyPolling.TYPE_SURVEY) {
            if (item.topics.size() > 0)
                holder.headerStartLabel.setText(R.string.survey_about_text);
            else
                holder.headerStartLabel.setText(R.string.survey_text);

            holder.iconImageView.setImageResource(R.drawable.survey_icon);
        }
        else {
            if (item.topics.size() > 0)
                holder.headerStartLabel.setText(R.string.polling_about_text);
            else
                holder.headerStartLabel.setText(R.string.polling_text);

            holder.iconImageView.setImageResource(R.drawable.polling_icon);
        }

        //Show/Hide new label and status
        if (item.state.equalsIgnoreCase(SurveyPolling.STATE_DRAFT)) {
            holder.newLabel.setVisibility(View.VISIBLE);
            holder.statusLabel.setVisibility(View.GONE);
        }
        else {
            holder.newLabel.setVisibility(View.INVISIBLE);
            holder.statusLabel.setVisibility(View.VISIBLE);

            //Set status label
            if (item.hasFinished() && item.state.equalsIgnoreCase(SurveyPolling.STATE_PUBLISHED))
                holder.statusLabel.setText(SurveyPolling.COMPLETED_LABEL);
            else if (item.state.equalsIgnoreCase(SurveyPolling.STATE_COMPLETED))
                holder.statusLabel.setText(SurveyPolling.SHARED_LABEL);
            else if (item.state.equalsIgnoreCase(SurveyPolling.STATE_PUBLISHED))
                holder.statusLabel.setText(SurveyPolling.APPROVED_LABEL);
            else if (item.state.equalsIgnoreCase(SurveyPolling.STATE_REJECTED))
                holder.statusLabel.setText(SurveyPolling.REJECTED_LABEL);
            else if (item.state.equalsIgnoreCase(SurveyPolling.STATE_SAVE_DRAFT))
                holder.statusLabel.setText(SurveyPolling.DRAFT_LABEL);
            else
                holder.statusLabel.setText(item.state);
        }

        //Set text to label holder
        Resources resources = mContext.getResources();
        String dateTimeDisplay = mDateTimeDisplayFormat.format(item.createdDate);
        holder.creatorLabel.setText(resources.getString(R.string.verify_request_by_time_text, item.creator, dateTimeDisplay));
        holder.titleLabel.setText(item.name);

        //Set topic
        if (!item.surveyPollingTopics.isEmpty()) {
            holder.headerTitleLabel.setText(item.surveyPollingTopics.get(0).name);
        }
        else
            holder.headerTitleLabel.setText("");

        //Set total recipient
        if (item.recipientCount > 0) {
            holder.totalRecipientLabel.setVisibility(View.VISIBLE);
            if (item.hasFinished())
                holder.totalRecipientLabel.setText(resources.getString(R.string.verify_request_recipients_text, item.recipientCount));
            else
                holder.totalRecipientLabel.setText(resources.getString(R.string.verify_request_recipients_on_going_text, item.recipientCount));
        }
        else
            holder.totalRecipientLabel.setVisibility(View.GONE);

        return convertView;
    }

    public static class SurveyPollingHolder {
        ImageView iconImageView;
        TextView headerStartLabel;
        TextView headerTitleLabel;
        TextView titleLabel;
        TextView creatorLabel;
        TextView newLabel;
        TextView totalRecipientLabel;
        TextView statusLabel;

        public SurveyPollingHolder(View itemView) {
            iconImageView = (ImageView) itemView.findViewById(R.id.survey_polling_image_view);
            headerStartLabel = (TextView) itemView.findViewById(R.id.survey_polling_header_start_label);
            headerTitleLabel = (TextView) itemView.findViewById(R.id.survey_polling_header_title_label);
            titleLabel = (TextView) itemView.findViewById(R.id.survey_polling_title_label);
            creatorLabel = (TextView) itemView.findViewById(R.id.survey_polling_creator_label);
            newLabel = (TextView) itemView.findViewById(R.id.survey_polling_new_label);
            totalRecipientLabel = (TextView) itemView.findViewById(R.id.survey_polling_total_recipient_label);
            statusLabel = (TextView) itemView.findViewById(R.id.survey_polling_status_label);
        }
    }

}
