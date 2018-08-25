package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import java.text.NumberFormat;
import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.challenge.ChallengeDetailActivity;
import activity.challenge.FunAwardActivity;
import activity.challenge.YogaClassDetailsActivity;
import constants.Constants;
import fragment.challenge.MyChallengeFragment;
import model.Challenge;
import model.MyChallenge;
import model.Tags;
import utils.FontManager;

/**
 * Created by biresh.singh on 15-06-2018.
 */


public class IndividualAdaptor extends RecyclerView.Adapter<IndividualAdaptor.MyChallengeHolder> {
    private Context mContext;
    private List<Challenge> mIndividualList;
    public IndividualAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyChallengeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_challenge_raw, parent, false);
        return new MyChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(MyChallengeHolder holder,final int position) {
       /* List<MyChallenge> mychallengeLists=null;
        if(MyChallengeFragment.getInstance().mychallengeLists!=null) {
            mychallengeLists = MyChallengeFragment.getInstance().mychallengeLists;
        }*/
        final IndividualAdaptor.MyChallengeHolder itemHolder = (IndividualAdaptor.MyChallengeHolder) holder;
        itemHolder.tvTitle.setText(mIndividualList.get(position).getChallengetitle());
        itemHolder.tvPoints.setHint("+"+ NumberFormat.getInstance().format(mIndividualList.get(position).getValue())+" "+mIndividualList.get(position).getRewards().getName());
        itemHolder.txtProgress.setHint("0%");
        itemHolder.progressBar.setProgress(0);
        itemHolder.progressBar.setVisibility(View.GONE);
        itemHolder.txtProgress.setVisibility(View.GONE);
        List<Tags> tagList=mIndividualList.get(position).getTags();

        //boolean flag=false;
       /* if(mychallengeLists!=null) {
            for (MyChallenge myChallenge : mychallengeLists) {

                if (mIndividualList.get(position).getid() == myChallenge.getChallenge().getid()) {
                    flag = true;
                }

            }
        }*/


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

        /*if(flag==true)
        {
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();

                    Challenge challenge=mIndividualList.get(position);
                    challenge.setChallengeTypeID(mIndividualList.get(position).getChallengeType().getid());
                    challenge.setChallengeTypeName(mIndividualList.get(position).getChallengeType().getName());

                    challenge.setValidationID(mIndividualList.get(position).getValidation().getid());
                    challenge.setValidationName(mIndividualList.get(position).getValidation().getName());

                    challenge.setRewardsID(mIndividualList.get(position).getRewards().getid());
                    challenge.setRewardName(mIndividualList.get(position).getRewards().getName());

                    if(mIndividualList.get(position).getValidation().getid()==9)
                    {
                        Intent intent = new Intent(mContext, YogaClassDetailsActivity.class);
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                        intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                    else if(mIndividualList.get(position).getValidation().getid()==6)
                    {
                        Intent intent = new Intent(mContext, FunAwardActivity.class);
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                        intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                    else {
                        SnackbarManager.show(Snackbar.with(mContext).text("Challenge has already been accepted successfully").textColor(Color.WHITE)
                                .color(Color.parseColor("#FF9B30")));
                    }


                }
            });
        }
        else
        {*/
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    Intent intent = new Intent(mContext, ChallengeDetailActivity.class);
                    Challenge challenge=mIndividualList.get(position);
                    challenge.setChallengeTypeID(mIndividualList.get(position).getChallengeType().getid());
                    challenge.setChallengeTypeName(mIndividualList.get(position).getChallengeType().getName());

                    challenge.setValidationID(mIndividualList.get(position).getValidation().getid());
                    challenge.setValidationName(mIndividualList.get(position).getValidation().getName());

                    challenge.setRewardsID(mIndividualList.get(position).getRewards().getid());
                    challenge.setRewardName(mIndividualList.get(position).getRewards().getName());

                    intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                    intent.putExtra(Constants.GETCHALLENGEDATA,challenge);
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mIndividualList.get(position).getRewards());

                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
      /*  }*/
        /*if(position==0) {
            itemHolder.tvTitle.setText(mIndividualList.get(position).getChallengetitle());
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
        }*/


    }

    public void setData(final List<Challenge> infoList) {
        if(infoList != null)
        {
            this.mIndividualList = infoList;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {

        return (null != mIndividualList ? mIndividualList.size() : 0);
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
