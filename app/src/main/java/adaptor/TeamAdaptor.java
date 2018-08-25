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
import activity.challenge.TeamChallengeActivity;
import constants.Constants;
import fragment.challenge.MyChallengeFragment;
import model.Challenge;
import model.MyChallenge;
import model.Tags;
import utils.FontManager;

/**
 * Created by biresh.singh on 15-06-2018.
 */


public class TeamAdaptor extends RecyclerView.Adapter<TeamAdaptor.MyChallengeHolder> {
    private Context mContext;
    private List<Challenge> mTeamList;
    public TeamAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyChallengeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_challenge_raw, parent, false);
        return new MyChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(MyChallengeHolder holder,final int position) {
      /*  List<MyChallenge> mychallengeLists=null;
        if(MyChallengeFragment.getInstance().mychallengeLists!=null) {
            mychallengeLists = MyChallengeFragment.getInstance().mychallengeLists;
        }*/
        final MyChallengeHolder itemHolder = (MyChallengeHolder) holder;
        /*if(position==0) {
            itemHolder.tvTitle.setText("Run along together");
            itemHolder.progressBar.setProgress(50);
        }*/

        itemHolder.tvTitle.setText(mTeamList.get(position).getChallengetitle());
        itemHolder.tvPoints.setHint("+"+ NumberFormat.getInstance().format(mTeamList.get(position).getValue())+" "+mTeamList.get(position).getRewards().getName());
        itemHolder.txtProgress.setHint("0%");
        itemHolder.progressBar.setProgress(0);
        itemHolder.progressBar.setVisibility(View.GONE);
        itemHolder.txtProgress.setVisibility(View.GONE);
        List<Tags> tagList=mTeamList.get(position).getTags();
        Typeface iconFont = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(itemHolder.challengeIcon, iconFont);
        //itemHolder.challengeIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/fontawesome-webfont.ttf"));
       /* boolean flag=false;
        for(MyChallenge myChallenge:mychallengeLists)
        {

            if(mTeamList.get(position).getid()==myChallenge.getChallenge().getid())
            {
                flag=true;
            }

        }*/
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
                    SnackbarManager.show(Snackbar.with(mContext).text("Challenge has already been accepted successfully").textColor(Color.WHITE)
                            .color(Color.parseColor("#FF9B30")));
                }
            });
        }
        else
        {*/
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    Intent intent = new Intent(mContext, TeamChallengeActivity.class);
                    intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                    intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                    intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    /*if(position==0)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==1)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.YOGACLASS);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==2)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.FUNNIESTARTICLE);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==3)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.EMPLOYEEFUN);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==4)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.GYMCHECKIN);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==5)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.PHOTOHUNT);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==6)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.HEALTHYLIVINGARTICLES);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }
                    if(position==7)
                    {
                        intent.putExtra(Constants.CHALLENGETYPE,Constants.WALKINGACTIVITY);
                        intent.putExtra(Constants.GETCHALLENGEDATA,mTeamList.get(position));
                        intent.putExtra(Constants.GETCHALLENGEREWARDSDATA,mTeamList.get(position).getRewards());
                    }*/

                }
            });
       /* }*/

    }

    public void setData(final List<Challenge> infoList) {
        if(infoList != null)
        {
            this.mTeamList = infoList;
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        return (null != mTeamList ? mTeamList.size() : 0);
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
            progressBar= (ProgressBar) itemView.findViewById(R.id.progressBar);
            challengeIcon= (TextView) itemView.findViewById(R.id.challengeIcon);
        }
    }
}