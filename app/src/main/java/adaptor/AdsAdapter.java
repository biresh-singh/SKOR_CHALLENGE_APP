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

import activity.newsfeed.AdsDetailActivity;
import bean.FeedFeatured;
import constants.Constants;
import io.realm.RealmList;

/**
 * Created by dss-17 on 10/12/17.
 */

public class AdsAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    RealmList<FeedFeatured> adsNewsRealmList = new RealmList<>();

    public AdsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void updateAdapter(RealmList<FeedFeatured> adsNewsRealmList) {
        this.adsNewsRealmList = adsNewsRealmList;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return adsNewsRealmList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        View view = inflater.inflate(R.layout.item_ads, viewGroup, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_ads_imageView);

        if (adsNewsRealmList.get(position).getImage() != null){
            Glide.with(context).load(Constants.BASEURL + adsNewsRealmList.get(position).getImage().getDisplay())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdsDetailActivity.class);
                intent.putExtra("image", adsNewsRealmList.get(position).getImage().getDisplay());
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
