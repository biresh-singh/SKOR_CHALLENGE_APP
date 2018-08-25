package adaptor;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import bean.LeaderBoardItemIndividual;
import constants.Constants;
import utils.CircleImageView;


public class LeaderBoardIndividualFragmentAdaptor extends BaseAdapter {

    Context mContext;
    TextView firstname;
    TextView averagepoint;
    CircleImageView userimage;
    ArrayList<LeaderBoardItemIndividual> mArrayList;
    LayoutInflater layoutInflater;

    public LeaderBoardIndividualFragmentAdaptor(FragmentActivity activity, ArrayList<LeaderBoardItemIndividual> arrayListleaderBoardItems) {
        this.mContext=activity;
        this.mArrayList=arrayListleaderBoardItems;
        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return mArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;

        if(convertView==null)
        {

            view=layoutInflater.inflate(R.layout.customleaderboard_individual,null);



        }
        else
        {
            view=convertView;
        }
        firstname=(TextView)view.findViewById(R.id.firstname);

        averagepoint=(TextView)view.findViewById(R.id.averagepoint);
        userimage=(CircleImageView)view.findViewById(R.id.userimage);
        LeaderBoardItemIndividual leaderBoardItem=mArrayList.get(position);
        firstname.setText(leaderBoardItem.mFirstName);
        averagepoint.setText(separatorPoints(leaderBoardItem.mTotalPoints)+" Pts");
        Glide.with(mContext).load(Constants.BASEURL+leaderBoardItem.mImageUrl).into(userimage);

        return view;
    }

    public String separatorPoints(Double balance) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);

        return format.format(balance).split("\\,")[0];
    }

}
