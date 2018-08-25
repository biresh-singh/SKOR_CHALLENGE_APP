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

import activity.newsfeed.NewsDetailActivity;
import bean.FeedFeatured;
import constants.Constants;
import io.realm.RealmList;

/**
 * Created by dss-17 on 9/26/17.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    RealmList<FeedFeatured> feedFeaturedBookmarkRealmList = new RealmList<>();

    public BookmarkAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkHolder(LayoutInflater.from(context).inflate(R.layout.item_top_news, parent, false));
    }

    public void clear(){
        this.feedFeaturedBookmarkRealmList.clear();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BookmarkHolder bookmarkHolder = (BookmarkHolder) holder;

        bookmarkHolder.newsTitle.setText(feedFeaturedBookmarkRealmList.get(position).getTitle());

        if (feedFeaturedBookmarkRealmList.get(position).getImage()!=null) {
            Glide.with(context)
                    .load(Constants.BASEURL  +
                            feedFeaturedBookmarkRealmList.get(position)
                                    .getImage()
                                    .getDisplay())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                    .into(bookmarkHolder.newsImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.placeholder_attachment)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.centerCropTransform())
                    .into(bookmarkHolder.newsImage);
        }

        if (feedFeaturedBookmarkRealmList.get(position).getFavorite() != null) {
            bookmarkHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_on);
        } else {
            bookmarkHolder.bookmarkButton.setBackgroundResource(R.drawable.ic_bookmark_off);
        }

        if (feedFeaturedBookmarkRealmList.get(position).isNews()) {
            //like and comment button
            bookmarkHolder.likeButton.setVisibility(View.VISIBLE);
            bookmarkHolder.commentButton.setVisibility(View.VISIBLE);
            bookmarkHolder.viewButton.setVisibility(View.VISIBLE);
            bookmarkHolder.likeButton.setTextColor(Color.parseColor("#800000"));
            bookmarkHolder.commentButton.setTextColor(Color.parseColor("#800000"));

            //setting square, title, type color
            bookmarkHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_grey);
            bookmarkHolder.newsTitle.setTextColor(Color.parseColor("#800000"));
            bookmarkHolder.newsType.setTextColor(Color.parseColor("#800000"));
            bookmarkHolder.newsType.setText("NEWS");
//                if (feedFeaturedRealmList.get(position).getCategory().getCategoryName() != null) {
//                    topNewsFeedHolder.newsType.setText("NEWS - " + feedFeaturedRealmList.get(position).getCategory().getCategoryName());
//                }

            if (feedFeaturedBookmarkRealmList.get(position).getLike() != null) {
                bookmarkHolder.likeButton.setText("Liked (" + feedFeaturedBookmarkRealmList.get(position).getTotalLike() + ")");
            } else {
                bookmarkHolder.likeButton.setText("Like (" + feedFeaturedBookmarkRealmList.get(position).getTotalLike() + ")");
            }

            bookmarkHolder.likeButton.setText("Like (" + feedFeaturedBookmarkRealmList.get(position).getTotalLike() + ")");
            bookmarkHolder.commentButton.setText("Comment (" + feedFeaturedBookmarkRealmList.get(position).getTotalComment() + ")");
            bookmarkHolder.viewButton.setText("View (" + feedFeaturedBookmarkRealmList.get(position).getTotalView() + ")");

            bookmarkHolder.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsDetailActivity.class);
                    intent.putExtra("feedId", feedFeaturedBookmarkRealmList.get(position).getId());
                    context.startActivity(intent);
                }
            });

        }
//        else {
//            //like and comment button
//            bookmarkHolder.likeButton.setVisibility(View.GONE);
//            bookmarkHolder.commentButton.setVisibility(View.GONE);
//            bookmarkHolder.viewButton.setVisibility(View.GONE);
//
//            //setting square, title, type color
//            if (feedFeaturedBookmarkRealmList.get(position).getObjectModel() != null) {
//                if (feedFeaturedBookmarkRealmList.get(position).getObjectModel().getType().equals("announcement")) {
//                    bookmarkHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_green);
//                    bookmarkHolder.newsTitle.setTextColor(Color.parseColor("#89C049"));
//                    bookmarkHolder.newsType.setTextColor(Color.parseColor("#89C049"));
//                    bookmarkHolder.newsType.setText("ANNOUNCEMENT");
//                } else if (feedFeaturedBookmarkRealmList.get(position).getObjectModel().getType().equals("event")) {
//                    bookmarkHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_blue);
//                    bookmarkHolder.newsTitle.setTextColor(Color.parseColor("#5EADBE"));
//                    bookmarkHolder.newsType.setTextColor(Color.parseColor("#5EADBE"));
//                    bookmarkHolder.newsType.setText("EVENT & ACTIVITIES");
//                } else if (feedFeaturedBookmarkRealmList.get(position).getObjectModel().getType().equals("challenge")) {
//                    bookmarkHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_purple);
//                    bookmarkHolder.newsTitle.setTextColor(Color.parseColor("#a25ea4"));
//                    bookmarkHolder.newsType.setTextColor(Color.parseColor("#a25ea4"));
//                    bookmarkHolder.newsType.setText("CHALLENGE");
//                } else if (feedFeaturedBookmarkRealmList.get(position).getObjectModel().getType().equals("appointment")) {
//                    bookmarkHolder.contentLinearLayout.setBackgroundResource(R.drawable.square_yellow);
//                    bookmarkHolder.newsTitle.setTextColor(Color.parseColor("#E9B737"));
//                    bookmarkHolder.newsType.setTextColor(Color.parseColor("#E9B737"));
//                    bookmarkHolder.newsType.setText("APPOINTMENT");
//                }
//            }
//
//            bookmarkHolder.newsRootLinearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//        }

    }

    @Override
    public int getItemCount() {
        return feedFeaturedBookmarkRealmList.size();
    }

    public void updateAdapter(RealmList<FeedFeatured> feedFeaturedBookmarkRealmList) {
        this.feedFeaturedBookmarkRealmList = feedFeaturedBookmarkRealmList;
        notifyDataSetChanged();
    }


    private class BookmarkHolder extends RecyclerView.ViewHolder {
        ImageView newsImage, bookmarkButton;
        TextView newsTitle, newsType, likeButton, commentButton, viewButton;
        LinearLayout newsRootLinearLayout, contentLinearLayout;

        public BookmarkHolder(View view) {
            super(view);
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
}
