package adaptor;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import bean.PushNotificationItem;
import utils.CircleImageView;


public class PushNotificationAdaptor extends BaseAdapter {
    private ArrayList<PushNotificationItem> mData ;
    private LayoutInflater mInflater;
    Context mContext;
    TextView title;
    TextView subtitle;
    TextView time;
    CardView relativelayout;
    CircleImageView userimage;

    public PushNotificationAdaptor(Context context, ArrayList<PushNotificationItem> arrayList) {
        mData=arrayList;
        this.mContext=context;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return String.valueOf(mData.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup parent) {
        View view=null;
        if(convertView==null)
        {
            view=mInflater.inflate(R.layout.push_notification_item_list_app,null);
        }
        else
        {
            view=convertView;
        }
        title=(TextView)view.findViewById(R.id.title);
        subtitle=(TextView)view.findViewById(R.id.subtitle);
        time=(TextView)view.findViewById(R.id.time);
        relativelayout=(CardView)view.findViewById(R.id.relativelayout);
        final PushNotificationItem pushNotificationItem=mData.get(position);
        userimage=(CircleImageView)view.findViewById(R.id.userimage);
        int messageLength=pushNotificationItem.mMessage.length();
        String message=pushNotificationItem.mMessage;
        if(messageLength>60){
            title.setText(message.substring(0,60)+"...");

        }
        else {
            title.setText(pushNotificationItem.mMessage);
        }

        subtitle.setText(pushNotificationItem.mObject_type);
        time.setText(pushNotificationItem.mCreated);
        if(pushNotificationItem.mImage.equals("null")||pushNotificationItem.mImage.equals("")){
                if (pushNotificationItem.mObject_type.equals("None")) {
                    userimage.setImageResource(R.drawable.announcements);
                } else if (pushNotificationItem.mObject_type.equals("Announcement")) {
                    userimage.setImageResource(R.drawable.announcements);
                } else if (pushNotificationItem.mObject_type.equals("Event")) {
                    userimage.setImageResource(R.drawable.trainingtcon);
                } else if (pushNotificationItem.mObject_type.equals("Reward")) {
                    userimage.setImageResource(R.drawable.greeting1);
                }else if (pushNotificationItem.mObject_type.equals("Point allocation")) {
                    userimage.setImageResource(R.drawable.birthday);
                }else if (pushNotificationItem.mObject_type.equals("Repeated event")) {
                    userimage.setImageResource(R.drawable.greeting1);
                }

        }else{
        Glide.with(mContext).load(pushNotificationItem.mImage).into(userimage);
        }

        return view;
    }



}
