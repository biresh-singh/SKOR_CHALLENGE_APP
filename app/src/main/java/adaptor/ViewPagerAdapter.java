package adaptor;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import bean.PhotoItem;
import constants.Constants;


public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context mContext;

    ArrayList<PhotoItem> mPhotoItems;
    ImageView imgflag;
    int mPosition;

    LayoutInflater inflater;

    public ViewPagerAdapter(Context context, ArrayList<PhotoItem> photoItems, int position1) {
        this.mContext = context;
        this.mPhotoItems = photoItems;
        this.mPosition = position1;
    }

    @Override
    public int getCount() {
        return mPhotoItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables


        PhotoItem photoItemGallery;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpage_layout_event, container,
                false);
        imgflag = (ImageView) itemView.findViewById(R.id.userimage);
        photoItemGallery = mPhotoItems.get(position);
        String imageUrl = Constants.BASEURL + photoItemGallery.mImageThumbnail;
        Glide.with(mContext).load(imageUrl).into(imgflag);
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}