package adaptor;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import InternetConnection.CheckInternetConnection;
import activity.rewardz.EligibleRewardzActivity;
import activity.rewardz.RewardzDetailActivity;
import bean.RewardzListItem;
import constants.Constants;
import utils.CircleImageView;


public class RewardzRecyclerViewAdapter extends RecyclerView.Adapter<RewardzRecyclerViewAdapter.RewardzViewHolder> {
    Context mContext;
    ArrayList<RewardzListItem> mFaciltyCheckinItems;
    CheckInternetConnection checkInternetConnection;
    boolean isEligibleRewardz;
    boolean isFromNearMe = false;
    public String type;

    public RewardzRecyclerViewAdapter(Context context, ArrayList<RewardzListItem> faciltyCheckinItems, boolean isEligibleRewardz, boolean isFromNearMe, String type)
    {
        this.mContext=context;
        this.mFaciltyCheckinItems=faciltyCheckinItems;
        checkInternetConnection=new CheckInternetConnection(mContext);
        this.isEligibleRewardz=isEligibleRewardz;
        this.isFromNearMe = isFromNearMe;
        this.type = type;
    }


    @Override
    public RewardzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewardz_list_item, parent, false);
        RewardzViewHolder faciltyCheckinViewHolder = new RewardzViewHolder(v);
        return faciltyCheckinViewHolder;
    }

    @Override
    public void onBindViewHolder(final RewardzViewHolder holder, final int position) {
        final RewardzListItem faciltyCheckinItem=mFaciltyCheckinItems.get(position);
        holder.eventName.setText(Html.fromHtml(Html.fromHtml(faciltyCheckinItem.mFacilityItem).toString()));
        holder.eventName.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        if(isEligibleRewardz) {
          holder.eventTime.setVisibility(View.VISIBLE);
        } else {
            holder.eventTime.setVisibility(View.VISIBLE);
        }

        if(EligibleRewardzActivity.type.equals("point")) {
            holder.eventTime.setText(Html.fromHtml(Html.fromHtml(separatorPoints(Double.valueOf(faciltyCheckinItem.mPoint)) + " Points Required").toString()));
        } else{
            holder.eventTime.setText(Html.fromHtml(Html.fromHtml(faciltyCheckinItem.mAddress).toString()));
        }

        if(faciltyCheckinItem.mredeemableFlag){
            holder.attendingOrNotButton.setVisibility(View.VISIBLE);
        }else {
            holder.attendingOrNotButton.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(Constants.BASEURL+faciltyCheckinItem.mImageURL).into(holder.userImage);
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RewardzDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtra("pk",faciltyCheckinItem.pk);
                if (isFromNearMe){
                    intent.putExtra("viewType", "nearMe");
                }

                bundle.putSerializable("rewardzobject", mFaciltyCheckinItems.get(position));
                bundle.putString("typeDiscountOrPoints", type);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        holder.attendingOrNotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RewardzDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtra("pk",faciltyCheckinItem.pk);

                if (isFromNearMe){
                    intent.putExtra("viewType", "nearMe");
                }

                bundle.putSerializable("rewardzobject", mFaciltyCheckinItems.get(position));
                bundle.putString("typeDiscountOrPoints", type);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            }
        });
    }

    public String separatorPoints(Double balance) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);

        return format.format(balance).split("\\,")[0];
    }

    @Override
    public int getItemCount() {
        return mFaciltyCheckinItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class RewardzViewHolder extends  RecyclerView.ViewHolder    {
        RelativeLayout relativeLayout;
        CircleImageView userImage;
        TextView eventName;
        TextView eventTime;
        TextView eventDate;
        Button attendingOrNotButton;
        RelativeLayout containerView;

        RewardzViewHolder(View view) {
            super(view);
            relativeLayout=(RelativeLayout)view.findViewById(R.id.userlayout);
            userImage=(CircleImageView)view.findViewById(R.id.userimage);
            eventName=(TextView)view.findViewById(R.id.title);
            eventTime=(TextView)view.findViewById(R.id.time);
            eventDate=(TextView)view.findViewById(R.id.date);
            attendingOrNotButton=(Button)view.findViewById(R.id.attendingbutton);
            containerView=(RelativeLayout)view.findViewById(R.id.relativelayout);
        }
    }
}

