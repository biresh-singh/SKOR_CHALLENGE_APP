package adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import bean.SurveyPollingComment;

/**
 * Created by mac on 2/13/18.
 */

public class SurveyPollingCommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<SurveyPollingComment> mData = new ArrayList<>();
    private HashMap<Integer, Bitmap> mImage = new HashMap<Integer, Bitmap>();
    private static SimpleDateFormat mDateDisplayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private String baseUrl;

    public SurveyPollingCommentAdapter(Context context) {
        mContext = context;
        baseUrl = mContext.getResources().getString(R.string.base_url);
        if (baseUrl.charAt(baseUrl.length() - 1) == '/')
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }

    public void setData(List<SurveyPollingComment> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SurveyPollingComment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SurveyPollingComment comment = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_survey_polling_comment, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        //Comment creator image
        Glide.with(mContext).load(baseUrl + comment.creator.image).into(holder.iconImageView);

        //Comment name
        holder.nameLabel.setText(comment.creator.name);

        //Comment date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.dateLabel.setText(dateFormat.format(comment.createdDate));

        //Comment label
        holder.commentLabel.setText(comment.commentText);

        //Comment timeDesc
        if (comment.createdDate != null) {
            long currentTime = System.currentTimeMillis();
            long userLastRequestAtTime = comment.createdDate.getTime();
            if ((currentTime - userLastRequestAtTime) < 60 * 60 * 1000 && (currentTime - userLastRequestAtTime) >= 5 * 60 * 1000) {
                String time = String.format("%d ", TimeUnit.MILLISECONDS.toMinutes(currentTime - userLastRequestAtTime));
                holder.timeDescLabel.setText(time + " minutes ago");
            } else if ((currentTime - userLastRequestAtTime) < 24 * 60 * 60 * 1000) {
                String time = String.format("%d ", TimeUnit.MILLISECONDS.toHours(currentTime - userLastRequestAtTime));
                holder.timeDescLabel.setText(time + " hours ago");
            } else if ((currentTime - userLastRequestAtTime) > 24 * 60 * 60 * 1000) {
                long days = TimeUnit.MILLISECONDS.toDays(currentTime - userLastRequestAtTime);
                if (days == 1) {
                    holder.timeDescLabel.setText("yesterday");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(userLastRequestAtTime);
                    SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                    String nameOfDay = formatter.format(calendar.getTime());
                    holder.timeDescLabel.setText(nameOfDay);
                }
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView dateLabel;
        TextView commentLabel;
        TextView timeDescLabel;

        public ViewHolder(View itemView) {
            iconImageView = (ImageView) itemView.findViewById(R.id.profile_image_view);
            nameLabel = (TextView) itemView.findViewById(R.id.name_label);
            dateLabel = (TextView) itemView.findViewById(R.id.date_label);
            commentLabel = (TextView) itemView.findViewById(R.id.comment_label);
            timeDescLabel = (TextView) itemView.findViewById(R.id.timeDesc_label);
        }
    }

}
