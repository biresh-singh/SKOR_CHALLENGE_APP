package adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import activity.event.EventDetailActivity;
import activity.history.AnniversaryBirthdayListActivity;
import activity.history.AnnouncementDetailActivity;
import activity.newsfeed.NewsDetailActivity;
import activity.surveypolling.FillSurveyPollingActivity;
import activity.surveypolling.PollingSeeResultActivity;
import bean.Anniversary;
import bean.Birthday;
import bean.Category;
import bean.FeedFeatured;
import bean.ObjectModel;
import bean.Topic;
import constants.Constants;
import database.SharedDatabase;
import io.realm.RealmList;
import listener.LoadMoreListener;

/**
 * Created by dss-17 on 8/23/17.
 */

public class AllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "AllAdapter";

    SharedDatabase sharedDatabase;

    Context context;
    boolean canDoPaging;
    String ts_prev = "";

    RealmList<Birthday> birthdayRealmList = new RealmList<>();
    RealmList<Anniversary> anniversaryRealmList = new RealmList<>();
    RealmList<FeedFeatured> feedFeaturedRealmList = new RealmList<>();

    LoadMoreListener loadMoreListener;

    static int TYPE_WELCOME = 0;
    static int TYPE_BIRTHDAY = 1;
    static int TYPE_ANNIVERSARY = 2;
    static int TYPE_TOPNEWS = 3;
    static int TYPE_SURVEY_POLLING_FILL = 4;
    static int TYPE_LOADMORE = 5;

    //Static
    private static SimpleDateFormat mDateTimeParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat mSurveyPollingDateFormatter = new SimpleDateFormat("dd MMM yyyy, K:mm a", Locale.getDefault());

    public void updateFeed(RealmList<FeedFeatured> feedFeaturedRealmList) {
        this.feedFeaturedRealmList = feedFeaturedRealmList;
        notifyDataSetChanged();
    }

    public void updateBirthday(RealmList<Birthday> birthdayRealmList) {
        this.birthdayRealmList = birthdayRealmList;
        notifyDataSetChanged();
    }

    public void updateAnniversary(RealmList<Anniversary> anniversaryRealmList) {
        this.anniversaryRealmList = anniversaryRealmList;
        notifyDataSetChanged();
    }

    public AllAdapter(Context context, LoadMoreListener loadMoreListener, boolean canDoPaging) {
        this.context = context;
        this.loadMoreListener = loadMoreListener;
        this.canDoPaging = canDoPaging;

        sharedDatabase = new SharedDatabase(context);
    }

    public void updatePaging(String ts_prev) {
        this.ts_prev = ts_prev;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_WELCOME) {
            return new WelcomeHolder(LayoutInflater.from(context).inflate(R.layout.item_welcome, parent, false));
        }
        else if (viewType == TYPE_BIRTHDAY) {
            return new BirthdayAnniversaryHolder(LayoutInflater.from(context).inflate(R.layout.item_birthday_anniversary, parent, false));
        }
        else if (viewType == TYPE_ANNIVERSARY) {
            return new BirthdayAnniversaryHolder(LayoutInflater.from(context).inflate(R.layout.item_birthday_anniversary, parent, false));
        }
        else if (viewType == TYPE_LOADMORE) {
            return new LoadMoreHolder(LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false));
        }
        else if (viewType == TYPE_SURVEY_POLLING_FILL) {
            return new SurveyPollingFillHolder(LayoutInflater.from(context).inflate(R.layout.item_survey_polling_news_feed, parent, false));
        }
        else {
            return new TopNewsFeedHolder(LayoutInflater.from(context).inflate(R.layout.item_top_news, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_WELCOME) {
            WelcomeHolder welcomeHolder = (WelcomeHolder) holder;
            welcomeHolder.userName.setText("Welcome, " + sharedDatabase.getFirstName());
        }
        else if (viewType == TYPE_BIRTHDAY) {
            BirthdayAnniversaryHolder birthdayAnniversaryHolder = (BirthdayAnniversaryHolder) holder;
            if (birthdayRealmList.size() == 0) {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.GONE);
            }
            else if (birthdayRealmList.size() == 1) {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.VISIBLE);
                birthdayAnniversaryHolder.description.setText("TODAY IS " + birthdayRealmList.get(position - 1).getUserFirstName() + "'S BIRTHDAY");
            }
            else {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.VISIBLE);
                birthdayAnniversaryHolder.description.setText("TODAY " + birthdayRealmList.size() + " USERS BIRTHDAY");
            }
            birthdayAnniversaryHolder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AnniversaryBirthdayListActivity.class);
                    intent.putExtra("isAnniv", false);
                    context.startActivity(intent);
                }
            });
        }
        else if (viewType == TYPE_ANNIVERSARY) {
            BirthdayAnniversaryHolder birthdayAnniversaryHolder = (BirthdayAnniversaryHolder) holder;
            if (anniversaryRealmList.size() == 0) {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.GONE);
            } else if (anniversaryRealmList.size() == 1) {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.VISIBLE);
                birthdayAnniversaryHolder.description.setText("TODAY IS " + anniversaryRealmList.get(position - 2).getUserFirstName() + "'S ANNIVERSARY");
            } else {
                birthdayAnniversaryHolder.rootRelativeLayout.setVisibility(View.VISIBLE);
                birthdayAnniversaryHolder.description.setText("TODAY " + anniversaryRealmList.size() + " USERS ANNIVERSARY");
            }
            birthdayAnniversaryHolder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AnniversaryBirthdayListActivity.class);
                    intent.putExtra("isAnniv", true);
                    context.startActivity(intent);
                }
            });
        }
        else if (viewType == TYPE_LOADMORE) {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            if (canDoPaging) {
                if (ts_prev == null) {
                    loadMoreHolder.linearLayout.setVisibility(View.GONE);
                } else {
                    loadMoreHolder.linearLayout.setVisibility(View.VISIBLE);

                    loadMoreHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadMoreListener.onLoadMoreListener();
                        }
                    });
                }
            } else {
                loadMoreHolder.linearLayout.setVisibility(View.GONE);
            }

        }
        else {
            if (viewType == TYPE_TOPNEWS) {
                if (feedFeaturedRealmList.get(position - 3).isNews()) {
                    TopNewsFeedHolder topNewsFeedHolder = (TopNewsFeedHolder) holder;
                    final FeedFeatured currentFeaturedFeed = feedFeaturedRealmList.get(position - 3);
                    topNewsFeedHolder.newsTitle.setText(currentFeaturedFeed.getTitle());
                    topNewsFeedHolder.nowTextView.setVisibility(View.GONE);
                    if (currentFeaturedFeed.getImage() != null) {
                        Glide.with(context)
                                .load(Constants.BASEURL +
                                        currentFeaturedFeed.getImage().getDisplay())
                                .apply(RequestOptions.centerCropTransform())
                                .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                                .into(topNewsFeedHolder.newsImage);
                    }
                    else if (currentFeaturedFeed.getVideoThumbnail() != null) {
                        Glide.with(context)
                                .load(Constants.BASEURL +
                                        currentFeaturedFeed.getVideoThumbnail())
                                .apply(RequestOptions.centerCropTransform())
                                .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                                .into(topNewsFeedHolder.newsImage);
                    }
                    else {
                        Glide.with(context)
                                .load(R.drawable.skor)
                                .apply(RequestOptions.centerCropTransform())
                                .into(topNewsFeedHolder.newsImage);
                    }

                    if (currentFeaturedFeed.getFavorite() != null) {
                        topNewsFeedHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_on);
                    }
                    else {
                        topNewsFeedHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_off);
                    }

                    //like and comment button
                    topNewsFeedHolder.likeButton.setVisibility(View.VISIBLE);
                    topNewsFeedHolder.commentButton.setVisibility(View.VISIBLE);
                    topNewsFeedHolder.viewButton.setVisibility(View.VISIBLE);

                    topNewsFeedHolder.likeButton.setTextColor(Color.parseColor("#800000"));
                    topNewsFeedHolder.commentButton.setTextColor(Color.parseColor("#800000"));
                    topNewsFeedHolder.viewButton.setTextColor(Color.parseColor("#800000"));

                    //setting square, title, type color
                    topNewsFeedHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
                    topNewsFeedHolder.newsTitle.setTextColor(Color.parseColor("#800000"));
                    topNewsFeedHolder.newsType.setText("NEWS");

                    if (currentFeaturedFeed.getLike() != null) {
                        topNewsFeedHolder.likeButton.setText("Liked (" + currentFeaturedFeed.getTotalLike() + ")");
                    }
                    else {
                        topNewsFeedHolder.likeButton.setText("Like (" + currentFeaturedFeed.getTotalLike() + ")");
                    }

                    topNewsFeedHolder.likeButton.setText("Like (" + currentFeaturedFeed.getTotalLike() + ")");
                    topNewsFeedHolder.commentButton.setText("Comment (" + currentFeaturedFeed.getTotalComment() + ")");
                    topNewsFeedHolder.viewButton.setText("View (" + currentFeaturedFeed.getTotalView() + ")");

                    topNewsFeedHolder.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, NewsDetailActivity.class);
                            intent.putExtra("feedId", currentFeaturedFeed.getId());
                            context.startActivity(intent);
                        }
                    });
                }
                else { //if (feedFeaturedRealmList.get(position - 3).getObjectModel().equals("announcement") || feedFeaturedRealmList.get(position - 3).getObjectModel().equals("event") || feedFeaturedRealmList.get(position - 3).getObjectModel().equals("appointment")) {
                    TopNewsFeedHolder topNewsFeedHolder = (TopNewsFeedHolder) holder;
                    final FeedFeatured currentFeaturedFeed = feedFeaturedRealmList.get(position - 3);

                    topNewsFeedHolder.newsTitle.setText(currentFeaturedFeed.getTitle());
                    //like and comment button
                    topNewsFeedHolder.likeButton.setVisibility(View.GONE);
                    topNewsFeedHolder.commentButton.setVisibility(View.GONE);
                    topNewsFeedHolder.viewButton.setVisibility(View.GONE);

                    //setting square, title, type color
                    if (currentFeaturedFeed.getObjectModel() != null) {
                        if (currentFeaturedFeed.getObjectModel().getImage() != null) {
                            Glide.with(context)
                                    .load(Constants.BASEURL + currentFeaturedFeed.getObjectModel().getImage().getDisplay())
                                    .apply(RequestOptions.centerCropTransform())
                                    .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                                    .into(topNewsFeedHolder.newsImage);
                        }

                        if (currentFeaturedFeed.getObjectModel().getType().equals("announcement")) {
                            topNewsFeedHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
                            topNewsFeedHolder.newsTitle.setTextColor(Color.parseColor("#89C049"));
                            topNewsFeedHolder.newsType.setText("ANNOUNCEMENT");
                        } else if (currentFeaturedFeed.getObjectModel().getType().equals("event")) {
                            topNewsFeedHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
                            topNewsFeedHolder.newsTitle.setTextColor(Color.parseColor("#5EADBE"));
                            topNewsFeedHolder.newsType.setText("EVENT & ACTIVITIES");

                            if (currentFeaturedFeed.getStart() != null) {
                                //start
                                String startDate = currentFeaturedFeed.getStart();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date dateStart = null;
                                try {
                                    dateStart = sdf.parse(startDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long millisStart = dateStart.getTime();

                                //end
                                String endDate = currentFeaturedFeed.getEnd();
                                Date dateEnd = null;
                                try {
                                    dateEnd = sdf.parse(endDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long millisEnd = dateEnd.getTime();

                                //current6
                                long currentTimeMillis = System.currentTimeMillis();

                                if (millisStart < currentTimeMillis && millisEnd > currentTimeMillis) {
                                    topNewsFeedHolder.nowTextView.setVisibility(View.VISIBLE);
                                } else {
                                    topNewsFeedHolder.nowTextView.setVisibility(View.GONE);
                                }
                            }
                        } else if (currentFeaturedFeed.getObjectModel().getType().equals("challenge")) {
                            topNewsFeedHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
                            topNewsFeedHolder.newsTitle.setTextColor(Color.parseColor("#a25ea4"));
                            topNewsFeedHolder.newsType.setText("CHALLENGE");
                        } else if (currentFeaturedFeed.getObjectModel().getType().equals("appointment")) {
                            topNewsFeedHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
                            topNewsFeedHolder.newsTitle.setTextColor(Color.parseColor("#E9B737"));
                            topNewsFeedHolder.newsType.setText("APPOINTMENT");
                        }
                    }

                    topNewsFeedHolder.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentFeaturedFeed.getObjectModel().getType().equalsIgnoreCase("announcement")) {
                                Intent intent = new Intent(context, AnnouncementDetailActivity.class);
                                intent.putExtra("announcementId", currentFeaturedFeed.getObjectModel().getId());
                                context.startActivity(intent);
                            } else if (currentFeaturedFeed.getObjectModel().getType().equalsIgnoreCase("event")) {
                                Intent intent = new Intent(context, EventDetailActivity.class);
                                intent.putExtra("id", currentFeaturedFeed.getObjectModel().getId());
                                context.startActivity(intent);
                            }
                        }
                    });
                }
            }
            //Survey Polling
            else {
                SurveyPollingFillHolder viewHolder = (SurveyPollingFillHolder) holder;
                Resources resources = context.getResources();
                final FeedFeatured item = feedFeaturedRealmList.get(position - 3);
                final ObjectModel model = item.getObjectModel();

                //Set title
                viewHolder.titleTextView.setText(item.getTitle());

                //Parse created date
                Date createdDate = null;
                try {
                    createdDate = mDateTimeParser.parse(item.getCreated());
                }
                catch (Exception e) {}

                //Set Posted on
                if (createdDate != null) {
                    viewHolder.timeAndDateTextView.setText(resources.getString(R.string.posted_on_text, mSurveyPollingDateFormatter.format(createdDate).replace("a.m.", "AM").replace("p.m.", "PM")));
                    viewHolder.timeAndDateTextView.setVisibility(View.VISIBLE);
                }
                else {
                    viewHolder.timeAndDateTextView.setVisibility(View.INVISIBLE);
                }

                //Survey
                boolean isSurvey = model.getType().equalsIgnoreCase("survey");
                if (isSurvey) {
                    //Set image view
                    viewHolder.iconImageView.setImageResource(R.drawable.survey_icon);

                    //Set topic about
                    RealmList<Topic> topics = item.getObjectModel().getTopics();
                    if (topics != null && topics.size() > 0) {
                        viewHolder.aboutTextView.setText(R.string.survey_about_text);
                        viewHolder.typeTextView.setText(topics.get(0).getTopic());
                        viewHolder.typeTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.aboutTextView.setText(R.string.survey_text);
                        viewHolder.typeTextView.setVisibility(View.GONE);
                    }

                    //Set description
                    String description = item.getDescription();
                    if (description != null) {
                        viewHolder.descriptionTextView.setText(item.getDescription());
                        viewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                    }
                    else
                        viewHolder.descriptionTextView.setVisibility(View.GONE);

                    //Set points text color
                    viewHolder.pointsLabel.setTextColor(resources.getColor(R.color.survey_polling_blue_color));
                }
                //Polling
                else {
                    //Set description
                    viewHolder.descriptionTextView.setVisibility(View.GONE);

                    //Set topic about
                    RealmList<Topic> topics = item.getObjectModel().getTopics();
                    if (topics != null && topics.size() > 0) {
                        viewHolder.aboutTextView.setText(R.string.polling_about_text);
                        viewHolder.typeTextView.setText(topics.get(0).getTopic());
                        viewHolder.typeTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.aboutTextView.setText(R.string.polling_text);
                        viewHolder.typeTextView.setVisibility(View.GONE);
                    }

                    //Set description
                    viewHolder.descriptionTextView.setVisibility(View.GONE);

                    //Set points text color
                    viewHolder.pointsLabel.setTextColor(resources.getColor(R.color.survey_polling_green_color));
                }

                //Have Voted
                if (item.getObjectModel().isHaveVoted()) {
                    //Show footer
                    viewHolder.bottomRelativeLayout.setVisibility(View.VISIBLE);

                    //Set voted viewed by
                    viewHolder.votedByViewedBy.setText(resources.getString(R.string.voted_viewed_by_text, model.getTotalVoted(), model.getTotalView()));
                    viewHolder.votedByViewedBy.setVisibility(View.VISIBLE);

                    //Set points label
                    viewHolder.pointsLabel.setVisibility(View.GONE);

                    if (isSurvey) {
                        //Show/Hide see result, vote, fill survey button
                        viewHolder.seeResultButton.setVisibility(View.INVISIBLE);
                        viewHolder.voteButton.setVisibility(View.INVISIBLE);
                        viewHolder.fillSurveyButton.setVisibility(View.INVISIBLE);

                        //Set voted filled
                        viewHolder.haveVotedFilledLabel.setText(R.string.you_have_filled_this_survey_text);
                    }
                    else {
                        //Show/Hide see result, vote, fill survey button
                        viewHolder.seeResultButton.setVisibility(View.VISIBLE);
                        viewHolder.voteButton.setVisibility(View.INVISIBLE);
                        viewHolder.fillSurveyButton.setVisibility(View.INVISIBLE);

                        //Set voted filled
                        viewHolder.haveVotedFilledLabel.setText(R.string.you_have_voted_text);
                    }

                    //Set love
                    if(!isSurvey){
                        viewHolder.loveCountTextView.setVisibility(View.VISIBLE);
                        viewHolder.loveImageView.setVisibility(View.VISIBLE);
                        viewHolder.loveCountTextView.setText(model.getTotalLike() + "");

                        //Set Comment
                        viewHolder.commentCountTextView.setVisibility(View.VISIBLE);
                        viewHolder.commentImageView.setVisibility(View.VISIBLE);
                        viewHolder.commentCountTextView.setText(model.getTotalComments() + "");
                    }else{
                        viewHolder.loveCountTextView.setVisibility(View.GONE);
                        viewHolder.loveImageView.setVisibility(View.GONE);

                        //Set Comment
                        viewHolder.commentCountTextView.setVisibility(View.GONE);
                        viewHolder.commentImageView.setVisibility(View.GONE);
                    }
                }
                else {
                    //Hide footer
                    viewHolder.bottomRelativeLayout.setVisibility(View.GONE);

                    //Set voted viewed by
                    viewHolder.votedByViewedBy.setVisibility(View.GONE);

                    //Set points label
                    if (model.getPoints() >= 0) {
                        viewHolder.pointsLabel.setText(resources.getString(R.string.get_x_pts_text, model.getPoints()));
                        viewHolder.pointsLabel.setVisibility(View.VISIBLE);
                    }
                    else
                        viewHolder.pointsLabel.setVisibility(View.INVISIBLE);

                    //Show/Hide see result, vote, fill survey button
                    if (isSurvey) {
                        viewHolder.seeResultButton.setVisibility(View.INVISIBLE);
                        viewHolder.voteButton.setVisibility(View.INVISIBLE);
                        viewHolder.fillSurveyButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.seeResultButton.setVisibility(View.INVISIBLE);
                        viewHolder.voteButton.setVisibility(View.VISIBLE);
                        viewHolder.fillSurveyButton.setVisibility(View.INVISIBLE);
                    }
                }

                //Set see result event
                viewHolder.seeResultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSeeResultPollingActivity(item);
                    }
                });

                //Set voted event
                viewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFillSurveyPollingActivity(item);
                    }
                });

                //Set fill survey event
                viewHolder.fillSurveyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFillSurveyPollingActivity(item);
                    }
                });
            }
        }
    }

    private void startFillSurveyPollingActivity(FeedFeatured item) {
        int mode;
        ObjectModel objectModel = item.getObjectModel();
        if (objectModel.getType().equalsIgnoreCase("survey"))
            mode = activity.surveypolling.Constants.MODE_SURVEY;
        else
            mode = activity.surveypolling.Constants.MODE_POLLING;

        Intent intent = new Intent(context, FillSurveyPollingActivity.class);
        intent.putExtra(activity.surveypolling.Constants.PARAM_MODE, mode);
        intent.putExtra(FillSurveyPollingActivity.PARAM_SURVEY_POLLING_ID, objectModel.getId());

	    RealmList<Topic> topics = item.getObjectModel().getTopics();
	    if (topics != null && topics.size() > 0)
            intent.putExtra(FillSurveyPollingActivity.PARAM_CATEGORY_NAME, topics.get(0).getTopic());

        context.startActivity(intent);
    }

    private void startSeeResultPollingActivity(FeedFeatured item) {
        int mode;
        ObjectModel objectModel = item.getObjectModel();

        Intent intent = new Intent(context, PollingSeeResultActivity.class);
        intent.putExtra(activity.surveypolling.Constants.PARAM_MODE, activity.surveypolling.Constants.MODE_POLLING);
        intent.putExtra(PollingSeeResultActivity.PARAM_SURVEY_POLLING_ID, objectModel.getId());

        Category category = item.getCategory();
        if (category != null)
            intent.putExtra(PollingSeeResultActivity.PARAM_CATEGORY_NAME, category.getCategoryName());

        intent.putExtra(PollingSeeResultActivity.PARAM_LOVE_COUNT, objectModel.getTotalLike());
        intent.putExtra(PollingSeeResultActivity.PARAM_COMMENT_COUNT, objectModel.getTotalComments());
        intent.putExtra(PollingSeeResultActivity.PARAM_VOTED_COUNT, objectModel.getTotalVoted());
        intent.putExtra(PollingSeeResultActivity.PARAM_VIEW_COUNT, objectModel.getTotalView());
        intent.putExtra(PollingSeeResultActivity.PARAM_CATEGORY_NAME, objectModel.getTotalComments());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return feedFeaturedRealmList.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_WELCOME;
        }
        else if (position == 1) {
            return TYPE_BIRTHDAY;
        }
        else if (position == 2) {
            return TYPE_ANNIVERSARY;
        }
        else if (position == getItemCount() - 1) {
            return TYPE_LOADMORE;
        }
        else {
            FeedFeatured feedFeatured = feedFeaturedRealmList.get(position - 3);
            if (feedFeatured.isNews()) {
                return TYPE_TOPNEWS;
            }
            else {
                ObjectModel objectModel = feedFeatured.getObjectModel();
                String objectModelType = objectModel.getType();
                if (objectModelType.equals("announcement") || objectModelType.equals("event") ||
                        objectModelType.equals("appointment")) {
                    return TYPE_TOPNEWS;
                }
                else { //if (objectModelType.equals("survey") || objectModelType.equals("polling")) {
                    return TYPE_SURVEY_POLLING_FILL;
                }
            }
        }
    }

    public static class TopNewsFeedHolder extends RecyclerView.ViewHolder {
        ImageView newsImage, bookmarkButton;
        TextView newsTitle, newsType, likeButton, commentButton, viewButton, nowTextView;
        LinearLayout newsRootLinearLayout, contentLinearLayout;

        public TopNewsFeedHolder(View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.item_top_news_image);
            newsTitle = (TextView) itemView.findViewById(R.id.item_top_news_title);
            newsType = (TextView) itemView.findViewById(R.id.item_top_news_type);
            newsRootLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_top_news_rootLinearLayout);
            contentLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_top_news_contentLinearLayout);
            likeButton = (TextView) itemView.findViewById(R.id.item_top_news_likeTextView);
            commentButton = (TextView) itemView.findViewById(R.id.item_top_news_commentTextView);
            viewButton = (TextView) itemView.findViewById(R.id.item_top_news_viewTextView);
            bookmarkButton = (ImageView) itemView.findViewById(R.id.item_top_news_bookmarkImageView);
            nowTextView = (TextView) itemView.findViewById(R.id.item_top_news_nowTextView);

        }
    }

    public static class BirthdayAnniversaryHolder extends RecyclerView.ViewHolder {
        TextView description;
        LinearLayout rootLinearLayout;
        RelativeLayout rootRelativeLayout;

        public BirthdayAnniversaryHolder(View itemView) {
            super(itemView);

            description = (TextView) itemView.findViewById(R.id.item_birthday_anniversary_descriptionTextView);
            rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_birthday_anniversary_rootLinearLayout);
            rootRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_birthday_anniversary_rootRelativeLayout);
        }
    }

    public static class WelcomeHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public WelcomeHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.item_welcome_userNameTextView);
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        public LoadMoreHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_load_more);
        }
    }

    public static class SurveyPollingFillHolder extends RecyclerView.ViewHolder {
        LinearLayout boxLayout;
        ImageView iconImageView, loveImageView, commentImageView;
        TextView aboutTextView, typeTextView, timeAndDateTextView, titleTextView,
                descriptionTextView, votedByViewedBy, pointsLabel, loveCountTextView,
                commentCountTextView, haveVotedFilledLabel;
        Button seeResultButton, voteButton, fillSurveyButton;
        RelativeLayout bottomRelativeLayout;

        public SurveyPollingFillHolder(View itemView) {
            super(itemView);

            boxLayout = (LinearLayout) itemView.findViewById(R.id.survey_polling_box_layout);
            iconImageView = (ImageView) itemView.findViewById(R.id.survey_polling_image_view);
            loveImageView = (ImageView) itemView.findViewById(R.id.love_image_view);
            commentImageView = (ImageView) itemView.findViewById(R.id.comment_image_view);
            aboutTextView = (TextView) itemView.findViewById(R.id.survey_polling_header_start_label);
            typeTextView = (TextView) itemView.findViewById(R.id.survey_polling_header_title_label);
            timeAndDateTextView = (TextView) itemView.findViewById(R.id.survey_polling_date_posted_label);
            titleTextView = (TextView) itemView.findViewById(R.id.survey_polling_title_label);
            descriptionTextView = (TextView) itemView.findViewById(R.id.survey_polling_description_label);
            seeResultButton = (Button) itemView.findViewById(R.id.survey_polling_see_result_button);
            voteButton = (Button) itemView.findViewById(R.id.survey_polling_vote_button);
            fillSurveyButton = (Button) itemView.findViewById(R.id.survey_polling_fill_survey_button);
            votedByViewedBy = (TextView) itemView.findViewById(R.id.survey_polling_voted_viewed_label);
            pointsLabel = (TextView) itemView.findViewById(R.id.survey_polling_point_label);
            bottomRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.survey_polling_news_feed_bottom_layout);
            loveCountTextView = (TextView) itemView.findViewById(R.id.survey_polling_like_label);
            commentCountTextView = (TextView) itemView.findViewById(R.id.survey_polling_comment_label);
            haveVotedFilledLabel = (TextView) itemView.findViewById(R.id.survey_polling_voted_filled_label);
        }
    }
}
