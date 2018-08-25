package adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import activity.rewardz.RewardzDetailActivity;
import bean.RewardzListItem;
import constants.Constants;
import utils.CircleImageView;

/**
 * Created by dss-17 on 10/2/17.
 */

public class SearchRewardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<RewardzListItem> rewardzListItemArrayList = new ArrayList<>();

    public SearchRewardsAdapter(Context context) {
        this.context = context;
    }

    public void updateAdapter(ArrayList<RewardzListItem> rewardzListItemArrayList) {
        this.rewardzListItemArrayList = rewardzListItemArrayList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewardz_list_item, parent, false);
        RewardzViewHolder rewardzViewHolder = new RewardzViewHolder(v);
        return rewardzViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RewardzViewHolder rewardzViewHolder = (RewardzViewHolder) holder;

        rewardzViewHolder.eventName.setText(rewardzListItemArrayList.get(position).mFacilityItem);
        rewardzViewHolder.eventTime.setText("");
        Glide.with(context).load(Constants.BASEURL+rewardzListItemArrayList.get(position).mImageURL).into(rewardzViewHolder.userImage);

        if(rewardzListItemArrayList.get(position).mredeemableFlag) {
            rewardzViewHolder.eventTime.setVisibility(View.VISIBLE);
            rewardzViewHolder.attendingOrNotButton.setVisibility(View.VISIBLE);
        } else {
            rewardzViewHolder.eventTime.setVisibility(View.VISIBLE);
            rewardzViewHolder.attendingOrNotButton.setVisibility(View.GONE);
        }

//        if(EligibleRewardzActivity.type.equals("point")) {
//            rewardzViewHolder.eventTime.setText(Html.fromHtml(Html.fromHtml(rewardzListItemArrayList.get(position).mPoint + " Points Required").toString()));
//        } else{
//            rewardzViewHolder.eventTime.setText(Html.fromHtml(Html.fromHtml(rewardzListItemArrayList.get(position).mAddress).toString()));
//        }

        rewardzViewHolder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RewardzDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtra("pk", rewardzListItemArrayList.get(position).pk);

                bundle.putSerializable("rewardzobject", rewardzListItemArrayList.get(position));
                bundle.putString("typeDiscountOrPoints", "");
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardzListItemArrayList.size();
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
