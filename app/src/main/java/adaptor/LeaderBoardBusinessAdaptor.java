package adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.root.skor.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import bean.LeaderBoardItem;
import utils.CircleImageView;


public class LeaderBoardBusinessAdaptor extends BaseAdapter{

    Context mContext;
    TextView department;
    TextView member;
    TextView totalpoints;
    TextView averagepoint;
    CircleImageView userimage;
    ArrayList<LeaderBoardItem> mArrayList;
    LayoutInflater layoutInflater;

    public LeaderBoardBusinessAdaptor(Context context, ArrayList<LeaderBoardItem> arrayListleaderBoardItems) {
        this.mContext=context;
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

            view=layoutInflater.inflate(R.layout.customleaderboard,null);



        }
        else
        {
            view=convertView;
        }
        department=(TextView)view.findViewById(R.id.department);
        member=(TextView)view.findViewById(R.id.member);
        totalpoints=(TextView)view.findViewById(R.id.totalpoints);
        averagepoint=(TextView)view.findViewById(R.id.averagepoint);
        userimage=(CircleImageView)view.findViewById(R.id.userimage);
        LeaderBoardItem leaderBoardItem=mArrayList.get(position);
        department.setText(leaderBoardItem.mName);
        member.setText(leaderBoardItem.mMemberPoint+" Members");
        totalpoints.setText(separatorPoints(leaderBoardItem.mTotalPoints)+" Pts");
        averagepoint.setText(separatorPoints(leaderBoardItem.mAveragePoint)+" Pts");

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
