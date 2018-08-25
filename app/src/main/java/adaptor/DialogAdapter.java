package adaptor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import listener.DeleteDialogListener;
import listener.SelectUserForChatListener;
import singleton.InterfaceManager;

/**
 * Created by dss-17 on 5/12/17.
 */

public class DialogAdapter extends BaseAdapter {

    private ArrayList<QBChatDialog> chatlist;
    private Map<Integer,QBUser> userHashmap;
    private Context context;
    SelectUserForChatListener selectUserForChatListener;
    DeleteDialogListener deleteDialogListener;

    public DialogAdapter(Context c, ArrayList<QBChatDialog> chatlist, SelectUserForChatListener selectUserForChatListener, DeleteDialogListener deleteDialogListener, Map<Integer,QBUser> userHashmap){
        this.chatlist = chatlist;
        this.context = c;
        this.selectUserForChatListener = selectUserForChatListener;
        this.deleteDialogListener = deleteDialogListener;
        this.userHashmap = userHashmap;
    }

    @Override
    public int getCount() {
        return chatlist.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = "";
        ColorDrawable cd = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_chat, null);
        }
        RelativeLayout rootLinearLayout = (RelativeLayout) convertView.findViewById(R.id.item_chat_list_rootRelativeLayout);
        FrameLayout singleNotifFrameLayout = (FrameLayout)convertView.findViewById(R.id.item_chat_list_notificationSingleFrameLayout);
        FrameLayout multipleNotifFrameLayout = (FrameLayout)convertView.findViewById(R.id.item_chat_list_notificationMultipleFrameLayout);
        FrameLayout userPhotoFrameLayout = (FrameLayout)convertView.findViewById(R.id.item_chat_list_userPhotoFrameLayout);
        ImageView userPhotoImageView = (ImageView) convertView.findViewById(R.id.item_chat_list_userPhoto);
        CircleImageView singleNotifImageView = (CircleImageView)convertView.findViewById(R.id.item_chat_list_notificationSingleImageView);
        TextView username = (TextView) convertView.findViewById(R.id.item_chat_list_userName);
        TextView userPhotoTextView = (TextView)convertView.findViewById(R.id.item_chat_list_profilePictureTextView);
        TextView lastmessage = (TextView)convertView.findViewById(R.id.item_chat_list_lastMessage);
        TextView timeTextView = (TextView)convertView.findViewById(R.id.item_chat_list_timeTextView);
        TextView singleNotifTextView = (TextView)convertView.findViewById(R.id.item_chat_list_notificationSingleTextView);
        TextView multipleNotifTextView = (TextView)convertView.findViewById(R.id.item_chat_list_notificationMultipleTextView);

        rootLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserForChatListener.onSelectUserForChatListener(chatlist.get(position).getDialogId());
            }
        });

        rootLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                deleteDialogListener.onDeleteDialogListener(chatlist.get(position));
                return true;
            }
        });



        if(chatlist.get(position).getType() == QBDialogType.GROUP){
            cd = new ColorDrawable(ContextCompat.getColor(context,R.color.bluecolor));
            name = InterfaceManager.sharedInstance().getInitial(chatlist.get(position).getName());
        }
        else{
            name = InterfaceManager.sharedInstance().getInitial(chatlist.get(position).getName());
            cd = new ColorDrawable(ContextCompat.getColor(context,R.color.loading_yellow));
        }

        userPhotoImageView.setImageDrawable(cd);
        userPhotoTextView.setText(name);
        userPhotoTextView.setAllCaps(true);
        userPhotoTextView.setVisibility(View.VISIBLE);

        if(chatlist.get(position).getType() == QBDialogType.PRIVATE){
            QBUser recipient = userHashmap.get(chatlist.get(position).getRecipientId());
            if (recipient != null) {
                String qbPhoto = recipient.getCustomData();
                String photoUrl = "";
                try {
                    if(qbPhoto!=null) {
                        if(!qbPhoto.equalsIgnoreCase("")) {
                            JSONObject photoJSONObject = new JSONObject(qbPhoto);
                            photoUrl = photoJSONObject.getString("avatar_url");
                            Glide.with(context).load(photoUrl)
                                    .apply(RequestOptions.noAnimation())
                                    .into(userPhotoImageView);
                            userPhotoTextView.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        userPhotoImageView.setImageDrawable(null);
        if(chatlist.get(position).getPhoto() != null && !chatlist.get(position).getPhoto().equals("null")){
            Glide.with(context).load(chatlist.get(position).getPhoto()).into(userPhotoImageView);
            userPhotoTextView.setVisibility(View.GONE);
        }

        username.setText(chatlist.get(position).getName());

        lastmessage.setText(chatlist.get(position).getLastMessage());

        long milis = chatlist.get(position).getLastMessageDateSent() * 1000;

        if(milis == 0){
            timeTextView.setVisibility(View.GONE);
        }else{
            timeTextView.setVisibility(View.VISIBLE);
            timeTextView.setText(getDate(milis));
        }

        if (chatlist.get(position).getUnreadMessageCount() == 0) {
            multipleNotifTextView.setVisibility(View.GONE);
        } else {
            multipleNotifTextView.setVisibility(View.VISIBLE);
            multipleNotifTextView.setText(chatlist.get(position).getUnreadMessageCount() + "");
        }

        return convertView;
    }


    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
