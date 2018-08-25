package adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import bean.RewardzDashBoardItem;
import constants.Constants;


public class RewardzdashBoardListAdapter extends BaseAdapter{
    Context mContext;
    LayoutInflater layoutInflater;
    ArrayList<RewardzDashBoardItem> rewardzDashBoardItemArrayList;
    public RewardzdashBoardListAdapter(Context context,ArrayList<RewardzDashBoardItem> rewardzDashBoardItemArrayList)
    {
        this.mContext=context;
        this.rewardzDashBoardItemArrayList=rewardzDashBoardItemArrayList;
        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);




    }

    @Override
    public int getCount() {
        return rewardzDashBoardItemArrayList.size();
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
         view=layoutInflater.inflate(R.layout.rewardz_dashboard_list_item,null);
        }
        else
        {
           view=convertView;
        }
        ImageView background=(ImageView)view.findViewById(R.id.background);
        final ImageView icon=(ImageView)view.findViewById(R.id.rewardz_icon);
        RewardzDashBoardItem rewardzDashBoardItem=rewardzDashBoardItemArrayList.get(position);


        TextView category=(TextView)view.findViewById(R.id.category);

        category.setText(rewardzDashBoardItem.mFacilityItem);
        //Picasso.with(mContext).load(rewardzDashBoardItem.mIcon).resize(500,500).into(icon);

        Glide.with(mContext).load(rewardzDashBoardItem.mIcon).into(icon);
        Glide.with(mContext).load(Constants.BASEURL+rewardzDashBoardItem.mImageURL).into(background);

        return view;

    }
}
