package adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bean.Comment;
import constants.Constants;
import io.realm.RealmList;

/**
 * Created by dss-17 on 9/19/17.
 */

public class CommentAdapter extends RecyclerView.Adapter {
    Context context;
    boolean fromDetail;
    RealmList<Comment> commentRealmList = new RealmList<>();


    public CommentAdapter(Context context, boolean fromDetail) {
        this.context = context;
        this.fromDetail = fromDetail;
    }

    public void updateAdapter(RealmList<Comment> commentRealmList) {
        this.commentRealmList = commentRealmList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentHolder commentHolder = (CommentHolder) holder;

        if (fromDetail) {
            commentHolder.comment.setText(commentRealmList.get(position).getText());
            commentHolder.name.setText(commentRealmList.get(position).getCreator().getName());
            commentHolder.timeComment.setText(convertDateFromStringInEditProfileForCalendar(commentRealmList.get(position).getCreated()));

            if (commentRealmList.get(position).getCreator().getImage() != null) {
                Glide.with(context).load(Constants.BASEURL + commentRealmList.get(position).getCreator().getImage()).into(commentHolder.image);
            }

            if (position == commentRealmList.size()-1) {
                commentHolder.lineFrameLayout.setVisibility(View.GONE);
            }



        } else {
            commentHolder.comment.setText(commentRealmList.get(commentRealmList.size() - (position + 1)).getText());
            commentHolder.name.setText(commentRealmList.get(commentRealmList.size() - (position + 1)).getCreator().getName());
            commentHolder.timeComment.setText(convertDateFromStringInEditProfileForCalendar(commentRealmList.get(commentRealmList.size() - (position + 1)).getCreated()));

            if (commentRealmList.get(commentRealmList.size() - (position + 1)).getCreator().getImage() != null) {
                Glide.with(context)
                        .load(Constants.BASEURL + commentRealmList.get(commentRealmList.size() - (position + 1))
                        .getCreator()
                        .getImage())
                        .into(commentHolder.image);
            }

            if (position == commentRealmList.size()-1) {
                commentHolder.lineFrameLayout.setVisibility(View.GONE);
            }
        }

    }

    public String convertDateFromStringInEditProfileForCalendar(String dateInString) {

        String dateSubstring = dateInString.substring(0, dateInString.indexOf("T"));

        DateFormat readFormat = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat writeFormat = new SimpleDateFormat("MMM dd, yyyy");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        String newDateString = "";
        try {
            Date startDate = sdf.parse(dateSubstring);
            sdf = new SimpleDateFormat("yyyy-mm-dd");
            newDateString = sdf.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    @Override
    public int getItemCount() {
        if (fromDetail) {
            return commentRealmList.size();
        } else {
            if(commentRealmList.size() < 3) {
                return commentRealmList.size();
            } else {
                return 3;
            }
        }
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView image;
        TextView name, comment, timeComment;
        FrameLayout lineFrameLayout;

        public CommentHolder(View itemView) {
            super(itemView);
            image = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.item_comment_imageCircleImageView);
            name = (TextView) itemView.findViewById(R.id.item_comment_nameTextView);
            comment = (TextView) itemView.findViewById(R.id.item_comment_commentTextView);
            timeComment = (TextView) itemView.findViewById(R.id.item_comment_timeCommentTextView);
            lineFrameLayout = (FrameLayout) itemView.findViewById(R.id.item_comment_lineFrameLayout);
        }
    }

}
