package adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import org.json.JSONObject;

import java.util.ArrayList;

import activity.facilitychecking.FacilityCheckinListActivity;
import bean.FacilityCheckinCategoryItem;
import constants.Constants;


public class FacilityCheckinCategoryRecyclerView extends RecyclerView.Adapter<FacilityCheckinCategoryRecyclerView.FaciltyCheckinViewHolder>{
    Context mContext;
    ArrayList<FacilityCheckinCategoryItem> mFacilityCheckinCategoryItems;
    public static JSONObject selectedFacilityCheckinCategoryItem;
    ArrayList<JSONObject> mJsonObjectArrayList;

    public FacilityCheckinCategoryRecyclerView(Context context, ArrayList<FacilityCheckinCategoryItem> facilityCheckinCategoryItems,
                                               ArrayList<JSONObject> jsonObjectArrayList) {
        this.mContext=context;
        this.mFacilityCheckinCategoryItems = facilityCheckinCategoryItems;
        mJsonObjectArrayList=jsonObjectArrayList;
    }

    @Override
    public FaciltyCheckinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_checkin_category_item, parent, false);
        FaciltyCheckinViewHolder faciltyCheckinViewHolder = new FaciltyCheckinViewHolder(v);
        return faciltyCheckinViewHolder;
    }

    @Override
    public void onBindViewHolder(FaciltyCheckinViewHolder holder, final int position) {
        final FacilityCheckinCategoryItem facilityCheckinCategoryItem = mFacilityCheckinCategoryItems.get(position);

        holder.facilityCategoryTextView.setText(facilityCheckinCategoryItem.name);

        Glide.with(mContext).load(Constants.BASEURL+facilityCheckinCategoryItem.thumbnailImageURL).into(holder.backgroundimageView);
        holder.backgroundimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFacilityCheckinCategoryItem = mJsonObjectArrayList.get(position);

                Intent intent=new Intent(mContext,FacilityCheckinListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("pk", facilityCheckinCategoryItem.pk);
                bundle.putString("categoryName", facilityCheckinCategoryItem.name);

                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        Glide.with(mContext).load(Constants.BASEURL + facilityCheckinCategoryItem.icon).into(holder.facilityCategoryIcon);
    }

    @Override
    public int getItemCount() {
        return mFacilityCheckinCategoryItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FaciltyCheckinViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView backgroundimageView, facilityCategoryIcon;
        TextView facilityCategoryTextView;

        public FaciltyCheckinViewHolder(View itemView) {
            super(itemView);
            backgroundimageView = (ImageView) itemView.findViewById(R.id.background);
            facilityCategoryTextView = (TextView) itemView.findViewById(R.id.category);
            facilityCategoryIcon = (ImageView) itemView.findViewById(R.id.category_icon);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
