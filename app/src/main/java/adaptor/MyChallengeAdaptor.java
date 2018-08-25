package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.text.NumberFormat;
import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.challenge.ChallengeDetailActivity;
import activity.challenge.FunAwardActivity;
import activity.challenge.GymCheckinDetailsActivity;
import activity.challenge.HealthyArticleDetailsActivity;
import activity.challenge.PhotoHuntActivity;
import activity.challenge.QRTreasureHuntActivity;
import activity.challenge.WalkingDetailsActivity;
import activity.challenge.WriteArticleDetailsActivity;
import activity.challenge.YogaClassDetailsActivity;
import constants.Constants;
import model.Challenge;
import model.MyChallenge;
import model.Tags;
import utils.ArialRegularTextView;
import utils.FontManager;


/**
 * Created by biresh.singh on 15-06-2018.
 */

public class MyChallengeAdaptor extends RecyclerView.Adapter<MyChallengeAdaptor.MyChallengeHolder> {
    private Context mContext;
    private List<MyChallenge> mMyList;
    public MyChallengeAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyChallengeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_challenge_raw, parent, false);
        return new MyChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(MyChallengeHolder holder, final int position) {

        final MyChallengeHolder itemHolder = (MyChallengeHolder) holder;
        itemHolder.tvTitle.setText(mMyList.get(position).getChallenge().getChallengetitle());
        itemHolder.tvPoints.setHint("+"+ NumberFormat.getInstance().format(mMyList.get(position).getChallenge().getValue())+" "+mMyList.get(position).getChallenge().getRewards().getName());
        itemHolder.txtProgress.setHint("0%");
        itemHolder.progressBar.setProgress(0);
        List<Tags> tagList=mMyList.get(position).getChallenge().getTags();


        Typeface iconFont = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(itemHolder.challengeIcon, iconFont);
        //itemHolder.challengeIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/fontawesome-webfont.ttf"));

        itemHolder.challengeIcon.setText(mContext.getResources().getString(R.string.fa_icon_areachart));
        if(tagList.size()>0)
        {
            itemHolder.tagView1.setVisibility(View.VISIBLE);
            for(int i=0;i<tagList.size();i++)
            {
                if(i==0)
                {
                    itemHolder.tagView1.setText(tagList.get(i).getName());
                }
                else if(i==1)
                {
                    itemHolder.tagView2.setVisibility(View.VISIBLE);
                    itemHolder.tagView2.setText(tagList.get(i).getName());
                }
                else if(i==2)
                {
                    itemHolder.tagView3.setVisibility(View.VISIBLE);
                    itemHolder.tagView3.setText(tagList.get(i).getName());
                }
            }
        }
        else
        {
            itemHolder.tagView1.setVisibility(View.GONE);
            itemHolder.tagView2.setVisibility(View.GONE);
            itemHolder.tagView3.setVisibility(View.GONE);
        }
        itemHolder.progressBar.setProgress(0);

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Intent intent = new Intent(mContext, ChallengeDetailActivity.class);
                final Challenge challenge=mMyList.get(position).getChallenge();
                challenge.setChallengeTypeID(mMyList.get(position).getChallenge().getChallengeType().getid());
                challenge.setChallengeTypeName(mMyList.get(position).getChallenge().getChallengeType().getName());

                challenge.setValidationID(mMyList.get(position).getChallenge().getValidation().getid());
                challenge.setValidationName(mMyList.get(position).getChallenge().getValidation().getName());

                challenge.setRewardsID(mMyList.get(position).getChallenge().getRewards().getid());
                challenge.setRewardName(mMyList.get(position).getChallenge().getRewards().getName());

                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {
                                                               /*Bundle bundle = new Bundle();
                                                               Intent intent = null;
                                                               if (challenge.getValidationID() == 1 && challenge.get) {
                                                                   intent = new Intent(mContext, WalkingDetailsActivity.class);
                                                               }
                                                               intent.putExtra(Constants.GETCHALLENGEDATA, challenge);
                                                               intent.putExtras(bundle);
                                                               mContext.startActivity(intent);*/

                                                               Bundle bundle=new Bundle();

                                                               Challenge challenge=mMyList.get(position).getChallenge();
                                                               challenge.setChallengeTypeID(challenge.getChallengeType().getid());
                                                               challenge.setChallengeTypeName(challenge.getChallengeType().getName());

                                                               challenge.setValidationID(challenge.getValidation().getid());
                                                               challenge.setValidationName(challenge.getValidation().getName());

                                                               challenge.setRewardsID(challenge.getRewards().getid());
                                                               challenge.setRewardName(challenge.getRewards().getName());

                                                               if(challenge.getValidation().getid()==9)
                                                               {
                                                                   Intent intent = new Intent(mContext, YogaClassDetailsActivity.class);
                                                                   intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                                                                   intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                                                                   intent.putExtras(bundle);
                                                                   mContext.startActivity(intent);
                                                               }
                                                               else if(challenge.getValidation().getid()==6)
                                                               {
                                                                   Intent intent = new Intent(mContext, FunAwardActivity.class);
                                                                   intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                                                                   intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                                                                   intent.putExtras(bundle);
                                                                   mContext.startActivity(intent);
                                                               }

                                                           }
                                                       });
                /*if(position==0)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                    intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==1)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.YOGACLASS);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==2)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.FUNNIESTARTICLE);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==3)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.EMPLOYEEFUN);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==4)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.GYMCHECKIN);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==5)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.PHOTOHUNT);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==6)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.HEALTHYLIVINGARTICLES);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                if(position==7)
                {
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.WALKINGACTIVITY);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mMyList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mMyList.get(position).getRewards());
                }
                intent.putExtras(bundle);
                mContext.startActivity(intent);*/
            }
        });

        /*if(position==0) {
            itemHolder.tvTitle.setText("Treasure Hunt | Find QR code");
            itemHolder.progressBar.setProgress(50);
        }
        else if(position==1) {
            itemHolder.tvTitle.setText("Yoga class in office");
            itemHolder.progressBar.setProgress(20);
        }
        else if(position==2) {
            itemHolder.tvTitle.setText("Create funniest article");
            itemHolder.progressBar.setProgress(80);
        }
        else if(position==3) {
            itemHolder.tvTitle.setText("Employee fun award");
            itemHolder.progressBar.setProgress(10);
        }
        else if(position==4) {
            itemHolder.tvTitle.setText("Check in to Gym");
            itemHolder.progressBar.setProgress(40);
        }
        else if(position==5) {
            itemHolder.tvTitle.setText("Photo hunt contest");
            itemHolder.progressBar.setProgress(5);
        }
        else if(position==6) {
            itemHolder.tvTitle.setText("Read healthy living article");
            itemHolder.progressBar.setProgress(80);
        }
        else if(position==7) {
            itemHolder.tvTitle.setText("Walking activity in ofice area");
            itemHolder.progressBar.setProgress(90);
        }
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Intent intent=null;
                if(position==0)
                {
                    intent = new Intent(mContext, QRTreasureHuntActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                }
                if(position==1)
                {
                    intent = new Intent(mContext, YogaClassDetailsActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.YOGACLASS);
                }
                if(position==2)
                {
                    intent = new Intent(mContext, WriteArticleDetailsActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.FUNNIESTARTICLE);
                }
                if(position==3)
                {
                    intent = new Intent(mContext, FunAwardActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.EMPLOYEEFUN);
                }
                if(position==4)
                {
                    intent = new Intent(mContext, GymCheckinDetailsActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.GYMCHECKIN);
                }
                if(position==5)
                {
                    intent = new Intent(mContext, PhotoHuntActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.PHOTOHUNT);
                }
                if(position==6)
                {
                    intent = new Intent(mContext, HealthyArticleDetailsActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.HEALTHYLIVINGARTICLES);
                }
                if(position==7)
                {
                    intent = new Intent(mContext, WalkingDetailsActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.WALKINGACTIVITY);
                }
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });*/

    }

    public void setData(final List<MyChallenge> infoList) {
        if(infoList != null)
        {
            this.mMyList = infoList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mMyList ? mMyList.size() : 0);
    }

    public static class MyChallengeHolder extends RecyclerView.ViewHolder{

        RobotoBoldTextView tvTitle;
        RobotoRegularTextView tvPoints,tagView1,tagView2,tagView3,txtProgress;
        ProgressBar progressBar;
        TextView challengeIcon;
        public MyChallengeHolder(View itemView) {
            super(itemView);

            tvTitle = (RobotoBoldTextView) itemView.findViewById(R.id.tvTitle);
            tvPoints = (RobotoRegularTextView) itemView.findViewById(R.id.tvPoints);
            tagView1 = (RobotoRegularTextView) itemView.findViewById(R.id.tagView1);
            tagView2 = (RobotoRegularTextView) itemView.findViewById(R.id.tagView2);
            tagView3 = (RobotoRegularTextView) itemView.findViewById(R.id.tagView3);
            txtProgress = (RobotoRegularTextView) itemView.findViewById(R.id.txtProgress);
            challengeIcon= (TextView) itemView.findViewById(R.id.challengeIcon);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
