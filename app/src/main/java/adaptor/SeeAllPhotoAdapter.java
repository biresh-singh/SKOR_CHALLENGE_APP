package adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import bean.PhotoItem;
import constants.Constants;


public class SeeAllPhotoAdapter extends BaseAdapter {
Context mContext;
    ArrayList<PhotoItem>mPhotoItems;
    LayoutInflater inflater;
    public SeeAllPhotoAdapter(Context applicationContext, ArrayList<PhotoItem> photoItemsArrayList) {
        this.mContext=applicationContext;
        this.mPhotoItems=photoItemsArrayList;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPhotoItems.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;

        if(convertView==null)
        {

            view=inflater.inflate(R.layout.see_all_adapter,null);



        }
        else
        {
            view=convertView;
        }
        ImageView userimage=(ImageView)view.findViewById(R.id.userimage);
        String mPhotoitems=mPhotoItems.get(position).mPhoto;


        Glide.with(mContext).load(Constants.BASEURL+mPhotoitems).into(userimage);

        return view;
    }
}
