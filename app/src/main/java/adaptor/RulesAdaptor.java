package adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.root.skor.R;

import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.challenge.ChallengeDetailActivity;
import constants.Constants;
import model.Challenge;
import model.Rule;
import model.Tags;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

/**
 * Created by biresh.singh on 15-06-2018.
 */


public class RulesAdaptor extends RecyclerView.Adapter<RulesAdaptor.MyChallengeHolder> {
    private Context mContext;
    private List<Rule> mRuleList;
    public RulesAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyChallengeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_rules_raw, parent, false);
        return new MyChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(MyChallengeHolder holder,final int position) {

        final RulesAdaptor.MyChallengeHolder itemHolder = (RulesAdaptor.MyChallengeHolder) holder;
        itemHolder.tvRulesText.setText(mRuleList.get(position).getName());
        itemHolder.tv_rules_cnt.setText(Integer.toString(position+1));

    }

    public void setData(final List<Rule> infoList) {
        if(infoList != null)
        {
            this.mRuleList = infoList;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {

        return (null != mRuleList ? mRuleList.size() : 0);
    }


    public static class MyChallengeHolder extends RecyclerView.ViewHolder{


        RobotoRegularTextView tvRulesText,tv_rules_cnt;


        public MyChallengeHolder(View itemView) {
            super(itemView);

            tvRulesText = (RobotoRegularTextView) itemView.findViewById(R.id.tvRulesText);
            tv_rules_cnt = (RobotoRegularTextView) itemView.findViewById(R.id.tv_rules_cnt);
        }
    }
}
