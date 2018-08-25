package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import activity.event.EventDetailActivity;
import activity.history.AnnouncementDetailActivity;
import activity.newsfeed.NewsDetailActivity;
import bean.FeedFeatured;
import constants.Constants;
import io.realm.RealmList;
import listener.LoadMoreListener;

/**
 * Created by dss-17 on 9/28/17.
 */

public class SearchNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    RealmList<FeedFeatured> feedFeaturedSearchRealmList = new RealmList<>();

    LoadMoreListener loadMoreListener;

    private static int NEWS = 0;
    private static int LOADMORE = 1;

    public SearchNewsAdapter(Context context, LoadMoreListener loadMoreListener) {
        this.context = context;
        this.loadMoreListener = loadMoreListener;
    }


    public void updateAdapter(RealmList<FeedFeatured> feedFeaturedSearchRealmList) {
        this.feedFeaturedSearchRealmList = feedFeaturedSearchRealmList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADMORE) {
            return new LoadMoreHolder(LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false));
        } else {
            return new HolderNews(LayoutInflater.from(context).inflate(R.layout.item_top_news, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return LOADMORE;
        } else {
            return NEWS;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (position == getItemCount()-1) {

            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;

            loadMoreHolder.linearLayout.setVisibility(View.VISIBLE);

            loadMoreHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreListener.onLoadMoreListener();
                }
            });

        } else {
            HolderNews holderNews = (HolderNews) holder;

            holderNews.newsTitle.setText(feedFeaturedSearchRealmList.get(position).getTitle());

            if (feedFeaturedSearchRealmList.get(position).getFavorite() != null) {
                holderNews.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_on);
            } else {
                holderNews.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_off);
            }
            if (feedFeaturedSearchRealmList.get(position).isNews()) {
                if (feedFeaturedSearchRealmList.get(position).getImage() != null) {
                    Glide.with(context)
                            .load(Constants.BASEURL +
                                    feedFeaturedSearchRealmList.get(position)
                                            .getImage()
                                            .getDisplay())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.centerCropTransform())
                            .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                            .into(holderNews.newsImage);
                } else {
                    Glide.with(context)
                            .load(R.drawable.placeholder_attachment)
                            .apply(RequestOptions.centerCropTransform())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(holderNews.newsImage);
                }

                //like and comment button
                holderNews.likeButton.setVisibility(View.VISIBLE);
                holderNews.commentButton.setVisibility(View.VISIBLE);
                holderNews.viewButton.setVisibility(View.VISIBLE);
                holderNews.likeButton.setTextColor(Color.parseColor("#800000"));
                holderNews.commentButton.setTextColor(Color.parseColor("#800000"));
                holderNews.viewButton.setTextColor(Color.parseColor("#800000"));

                //setting square, title, type color
                holderNews.contentLinearLayout.setBackgroundResource(R.drawable.square_red_maroon);
                holderNews.newsTitle.setTextColor(Color.parseColor("#800000"));
                holderNews.newsType.setTextColor(Color.parseColor("#800000"));
                holderNews.newsType.setText("NEWS");
//                if (feedFeaturedRealmList.get(position-3).getCategory().getCategoryName() != null) {
//                    topNewsFeedHolder.newsType.setText("NEWS - " + feedFeaturedRealmList.get(position-3).getCategory().getCategoryName());
//                }

                if (feedFeaturedSearchRealmList.get(position).getLike() != null) {
                    holderNews.likeButton.setText("Liked (" + feedFeaturedSearchRealmList.get(position).getTotalLike() + ")");
                } else {
                    holderNews.likeButton.setText("Like (" + feedFeaturedSearchRealmList.get(position).getTotalLike() + ")");
                }

                holderNews.likeButton.setText("Like (" + feedFeaturedSearchRealmList.get(position).getTotalLike() + ")");
                holderNews.commentButton.setText("Comment (" + feedFeaturedSearchRealmList.get(position).getTotalComment() + ")");
                holderNews.viewButton.setText("View (" + feedFeaturedSearchRealmList.get(position).getTotalView() + ")");

                holderNews.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, NewsDetailActivity.class);
                        intent.putExtra("feedId", feedFeaturedSearchRealmList.get(position).getId());
                        context.startActivity(intent);
                    }
                });

            } else {
                Glide.with(context)
                        .load(R.drawable.placeholder_attachment)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .into(holderNews.newsImage);

                if (feedFeaturedSearchRealmList.get(position).getObjectModel() != null) {
                    if (feedFeaturedSearchRealmList.get(position).getObjectModel().getImage() != null) {
                        if (feedFeaturedSearchRealmList.get(position).getObjectModel().getImage().getDisplay() != null) {
                            Glide.with(context)
                                    .load(Constants.BASEURL +
                                            feedFeaturedSearchRealmList.get(position).getObjectModel()
                                                    .getImage()
                                                    .getDisplay())
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .apply(RequestOptions.skipMemoryCacheOf(true))
                                    .apply(RequestOptions.centerCropTransform())
                                    .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                                    .into(holderNews.newsImage);
                        }
                    }
                }

                //like and comment button
                holderNews.likeButton.setVisibility(View.GONE);
                holderNews.commentButton.setVisibility(View.GONE);
                holderNews.viewButton.setVisibility(View.GONE);

                //setting square, title, type color
                if (feedFeaturedSearchRealmList.get(position).getObjectModel() != null) {
                    if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("announcement")) {
                        holderNews.contentLinearLayout.setBackgroundResource(R.drawable.square_green);
                        holderNews.newsTitle.setTextColor(Color.parseColor("#89C049"));
                        holderNews.newsType.setTextColor(Color.parseColor("#89C049"));
                        holderNews.newsType.setText("ANNOUNCEMENT");
                    } else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("event")) {
                        holderNews.contentLinearLayout.setBackgroundResource(R.drawable.square_blue);
                        holderNews.newsTitle.setTextColor(Color.parseColor("#5EADBE"));
                        holderNews.newsType.setTextColor(Color.parseColor("#5EADBE"));
                        holderNews.newsType.setText("EVENT & ACTIVITIES");
                    } else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("challenge")) {
                        holderNews.contentLinearLayout.setBackgroundResource(R.drawable.square_purple);
                        holderNews.newsTitle.setTextColor(Color.parseColor("#a25ea4"));
                        holderNews.newsType.setTextColor(Color.parseColor("#a25ea4"));
                        holderNews.newsType.setText("CHALLENGE");
                    } else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("appointment")) {
                        holderNews.contentLinearLayout.setBackgroundResource(R.drawable.square_yellow);
                        holderNews.newsTitle.setTextColor(Color.parseColor("#E9B737"));
                        holderNews.newsType.setTextColor(Color.parseColor("#E9B737"));
                        holderNews.newsType.setText("APPOINTMENT");
                    }
                }

                holderNews.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("announcement")) {
                            Intent intent = new Intent(context, AnnouncementDetailActivity.class);
                            intent.putExtra("announcementId", feedFeaturedSearchRealmList.get(position).getObjectModel().getId());
                            context.startActivity(intent);
                        } else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("event")) {
                            Intent intent = new Intent(context, EventDetailActivity.class);
                            intent.putExtra("id", feedFeaturedSearchRealmList.get(position).getObjectModel().getId());
                            context.startActivity(intent);
                        }

//                        else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("challenge")) {
//
//                        } else if (feedFeaturedSearchRealmList.get(position).getObjectModel().getType().equals("appointment")) {
//
//                        }

                    }
                });

            }

        }

    }

    @Override
    public int getItemCount() {
        return feedFeaturedSearchRealmList.size() + 1;
    }

    public static class HolderNews extends RecyclerView.ViewHolder {
        ImageView newsImage, bookmarkButton;
        TextView newsTitle, newsType, likeButton, commentButton, viewButton;
        LinearLayout newsRootLinearLayout, contentLinearLayout;

        public HolderNews(View itemView) {
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

        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        public LoadMoreHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_load_more);
        }
    }
}
