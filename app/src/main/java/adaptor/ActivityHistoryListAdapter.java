package adaptor;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bean.HistoryItem;

public class ActivityHistoryListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater layoutInflater;
  TextView historyItemTextView;
    TextView description;
    TextView poinTextView;
    TextView dateTextView;
    de.hdodenhof.circleimageview.CircleImageView historyIcon;
    ArrayList<HistoryItem> historyItemArrayList;


    public ActivityHistoryListAdapter(Context context, ArrayList<HistoryItem> historyItemArrayList) {
        this.mContext=context;
        this.historyItemArrayList=historyItemArrayList;

        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyItemArrayList.size();
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
        //ViewHolder viewHolder;
        if(convertView==null)
        {
            //viewHolder=new ViewHolder();
            view=layoutInflater.inflate(R.layout.history_listview_item,null);


            //view.setTag(viewHolder);

        }
        else
        {
            view=convertView;
        }

        historyItemTextView=(TextView)view.findViewById(R.id.title);
        poinTextView=(TextView)view.findViewById(R.id.time);
        dateTextView=(TextView)view.findViewById(R.id.date);
        description=(TextView)view.findViewById(R.id.desc);
        historyIcon=(de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.userimage);
        dateTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
            HistoryItem historyItem = historyItemArrayList.get(position);
            historyItemTextView.setText(historyItem.mName);
            poinTextView.setText(historyItem.mPoints + " pts");
            if(historyItem.facilityLocationName.equals(""))
            {

                description.setText(historyItem.facilityLocationName);
            }
            else {

                description.setText(historyItem.facilityLocationName);
            }
        Glide.with(mContext).load(historyItem.iconUrl).into(historyIcon);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date convertedDate = new Date();
            try {

                convertedDate = format.parse(historyItem.mdate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String currentDay = new SimpleDateFormat("EEE").format(convertedDate.getTime());
            String currentMonth = new SimpleDateFormat("MMM").format(convertedDate.getTime());
            String day = new SimpleDateFormat("dd").format(convertedDate.getTime());
            System.out.println(convertedDate);
            dateTextView.setText(day + " " + currentDay);

        return view;
    }

    static class ViewHolder
    {
        TextView historyItem;
        TextView description;
    }

}

