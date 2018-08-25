package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
 * Created by dss-17 on 8/4/17.
 */

public class FeedFeaturedAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    RealmList<FeedFeatured> feedFeaturedNewsRealmList = new RealmList<>();

    public FeedFeaturedAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void updateAdapter(RealmList<FeedFeatured> feedFeaturedNewsRealmList) {
        this.feedFeaturedNewsRealmList = feedFeaturedNewsRealmList;
        notifyDataSetChanged();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return feedFeaturedNewsRealmList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        View view = inflater.inflate(R.layout.item_feed_featured, viewGroup, false);
        ImageView feedFeaturedImage = (ImageView) view.findViewById(R.id.item_feed_featured_imageView);
        TextView feedFeaturedDescripton = (TextView) view.findViewById(R.id.item_feed_featured_description);
        FrameLayout feedFeaturedrootFrameLayout = (FrameLayout) view.findViewById(R.id.item_feed_featured_rootFrameLayout);

        feedFeaturedImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.skorlogo));

        if (feedFeaturedNewsRealmList.get(position).getImage() != null) {
            if (!feedFeaturedNewsRealmList.get(position).getImage().getDisplay().equalsIgnoreCase("")) {
                Glide.with(context).load(Constants.BASEURL + feedFeaturedNewsRealmList.get(position).getImage().getDisplay())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.placeholderOf(R.drawable.skor_logo))
                        .into(feedFeaturedImage);
            }
        }
        feedFeaturedDescripton.setText(Html.fromHtml(feedFeaturedNewsRealmList.get(position).getTitle()));

        feedFeaturedrootFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("feedId", feedFeaturedNewsRealmList.get(position).getId());
                context.startActivity(intent);
            }
        });

        viewGroup.addView(view, 0);
        return view;
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
