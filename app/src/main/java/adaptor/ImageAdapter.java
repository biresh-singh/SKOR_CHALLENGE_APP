package adaptor;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.util.ArrayList;

import bean.PhotoItem;
import constants.Constants;
import interfaces.CallbackInterface;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<PhotoItem> mPhotoItemsArrayList;
    Activity mApplicationContext;
    private CallbackInterface mCallback;

    public ImageAdapter(Activity applicationContext, ArrayList<PhotoItem> photoItemsArrayList) {
        this.mApplicationContext = applicationContext;
        this.mPhotoItemsArrayList = photoItemsArrayList;
        try {
            mCallback = (CallbackInterface) applicationContext;
        } catch (ClassCastException ex) {
           ex.printStackTrace();
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_imageview, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String photoItem = mPhotoItemsArrayList.get(position).mPhoto;
        // holder.userimage.setImageResource(R.drawable.no_image);
        String imageUrl = Constants.BASEURL + photoItem;
       /* imageUrl=imageUrl.replace("//","/");*/
        Log.d("imageurl", imageUrl);
        Glide.with(mApplicationContext).load(imageUrl)
                .apply(RequestOptions.errorOf(R.mipmap.ic_launcher))
                .into(holder.userimage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onHandleSelection();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPhotoItemsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userimage;

        public ViewHolder(View itemView) {
            super(itemView);

            userimage = (ImageView) itemView.findViewById(R.id.userimage);
        }
    }
}