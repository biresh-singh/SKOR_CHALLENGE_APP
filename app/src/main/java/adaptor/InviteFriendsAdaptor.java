package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.util.List;

import CustomClass.RobotoBoldTextView;
import activity.challenge.FunAwardChallengeActivity;
import constants.Constants;
import model.Participants;
import utils.CircleImageView;

/**
 * Created by biresh.singh on 15-06-2018.
 */


public class InviteFriendsAdaptor extends RecyclerView.Adapter<InviteFriendsAdaptor.MyFunAwardAdaptor> {
    private Context mContext;
    private List<Participants> mParticipantsList;
    public InviteFriendsAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyFunAwardAdaptor onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_invite_friends_item, parent, false);
        return new MyFunAwardAdaptor(view);
    }

    @Override
    public void onBindViewHolder(MyFunAwardAdaptor holder, int position) {
        final MyFunAwardAdaptor itemHolder = (MyFunAwardAdaptor) holder;
        itemHolder.tvName.setText(mParticipantsList.get(position).getFirstName()+" "+mParticipantsList.get(position).getLastName());

        Glide.with(mContext).load(Constants.BASESTAGINFURL+mParticipantsList.get(position).getPartcipantImage())
                .apply(RequestOptions.errorOf(R.mipmap.ic_launcher))
                .into(itemHolder.userimage);

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(mContext, FunAwardChallengeActivity.class);

                mContext.startActivity(intent);*/
            }
        });
    }
    public void setData(final List<Participants> infoList) {
        if(infoList != null)
        {
            this.mParticipantsList = infoList;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {

        return (null != mParticipantsList ? mParticipantsList.size() : 0);
    }

    public static class MyFunAwardAdaptor extends RecyclerView.ViewHolder{

        RobotoBoldTextView tvName;
        CircleImageView userimage;
        public MyFunAwardAdaptor(View itemView) {
            super(itemView);
            tvName = (RobotoBoldTextView) itemView.findViewById(R.id.tvName);
            userimage = (CircleImageView) itemView.findViewById(R.id.userimage);
        }
    }
}