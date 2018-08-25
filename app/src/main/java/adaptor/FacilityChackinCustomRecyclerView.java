package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import org.json.JSONObject;

import java.util.ArrayList;

import activity.facilitychecking.FacilityCheckinDetailViewActivity;
import bean.FaciltyCheckinItem;
import constants.Constants;
import utils.CircleImageView;


public class FacilityChackinCustomRecyclerView extends RecyclerView.Adapter<FacilityChackinCustomRecyclerView.FaciltyCheckinViewHolder>{
    Context mContext;
    ArrayList<FaciltyCheckinItem> mFaciltyCheckinItems;
    public static JSONObject selectedFaciltyCheckinItem;
    ArrayList<JSONObject> mJsonObjectArrayList;
    public FacilityChackinCustomRecyclerView(Context context, ArrayList<FaciltyCheckinItem> faciltyCheckinItems, ArrayList<JSONObject> jsonObjectArrayList)
    {
      this.mContext=context;
        this.mFaciltyCheckinItems=faciltyCheckinItems;
        mJsonObjectArrayList=jsonObjectArrayList;
    }


    @Override
    public FaciltyCheckinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_checkin_list_item, parent, false);
        FaciltyCheckinViewHolder faciltyCheckinViewHolder = new FaciltyCheckinViewHolder(v);
        return faciltyCheckinViewHolder;
    }

    @Override
    public void onBindViewHolder(FaciltyCheckinViewHolder holder, final int position) {
               final FaciltyCheckinItem faciltyCheckinItem=mFaciltyCheckinItems.get(position);
        holder.facilityNameTextView.setText(faciltyCheckinItem.name);
        holder.facilityLocationNameTextView.setText(faciltyCheckinItem.locationName);
        Glide.with(mContext).load(Constants.BASEURL+faciltyCheckinItem.thumbnailImageURL).into(holder.facilityImageView);

//        holder.facilityImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedFaciltyCheckinItem=mJsonObjectArrayList.get(position);
//                Intent intent=new Intent(mContext,FacilityCheckinDetailView.class);
//
//                mContext.startActivity(intent);
//            }
//        });

        holder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFaciltyCheckinItem=mJsonObjectArrayList.get(position);
                Intent intent=new Intent(mContext,FacilityCheckinDetailViewActivity.class);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFaciltyCheckinItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FaciltyCheckinViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        CircleImageView facilityImageView;
        TextView facilityNameTextView, facilityLocationNameTextView;
        RelativeLayout rootRelativeLayout;

        public FaciltyCheckinViewHolder(View itemView) {
            super(itemView);
            rootRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.root_relative_layout);
            facilityImageView = (CircleImageView) itemView.findViewById(R.id.facility_image_view);
            facilityNameTextView = (TextView)itemView.findViewById(R.id.facility_name_text_view);
            facilityLocationNameTextView = (TextView)itemView.findViewById(R.id.facility_location_name_text_view);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
