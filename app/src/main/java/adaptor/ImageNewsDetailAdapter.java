package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.util.ArrayList;

import activity.skorchat.ChattingActivity;
import activity.skorchat.PreviewActivity;
import activity.skorchat.PreviewNewActivity;
import constants.Constants;

/**
 * Created by dss-17 on 10/11/17.
 */

public class ImageNewsDetailAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<String> imageString = new ArrayList<>();

    public ImageNewsDetailAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void updateAdapter(ArrayList<String> imageString) {
        this.imageString = imageString;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageString.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        View view = inflater.inflate(R.layout.item_feed_featured_news_detail, viewGroup, false);
        ImageView feedFeaturedImage = (ImageView) view.findViewById(R.id.item_feed_featured_news_detail_imageView);

        Glide.with(context).load(Constants.BASEURL + imageString.get(position))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(feedFeaturedImage);

        feedFeaturedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewNewActivity.class);
//                intent.putExtra("url", "https://staging.skorpoints.com/" + imageString.get(position));
                ArrayList<String> urls = new ArrayList<String>();
                for (String string : imageString) {
                    urls.add(Constants.BASEURL + string);
                }
                intent.putStringArrayListExtra("urls", urls);
                intent.putExtra("index", position);
                intent.putExtra("name", "news");
                intent.putExtra("type", "Image");
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
