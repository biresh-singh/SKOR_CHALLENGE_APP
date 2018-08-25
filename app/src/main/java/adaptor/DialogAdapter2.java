package adaptor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;
import java.util.Map;

import bean.QuickbloxChat;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.DeleteDialogListener;
import listener.SelectUserForChatListener;
import singleton.InterfaceManager;

/**
 * Created by jessica on 1/25/18.
 */

public class DialogAdapter2 extends RecyclerView.Adapter<DialogAdapter2.Itemholder> {
    private ArrayList<QuickbloxChat> chatlist = new ArrayList<>();
    private Map<Integer,QBUser> userHashmap;
    private Context context;
    private DialogAdapterInterface dialogAdapterInterface;
    SelectUserForChatListener selectUserForChatListener;
    DeleteDialogListener deleteDialogListener;

    public DialogAdapter2(Context c , SelectUserForChatListener selectUserForChatListener, DeleteDialogListener deleteDialogListener, DialogAdapterInterface dialogAdapterInterface,Map<Integer,QBUser> userHashmap){
        this.context = c;
        this.selectUserForChatListener = selectUserForChatListener;
        this.deleteDialogListener = deleteDialogListener;
        this.userHashmap = userHashmap;
        this.dialogAdapterInterface = dialogAdapterInterface;
    }

    public void updateAdapter(ArrayList<QuickbloxChat> chatlist){
        this.chatlist = chatlist;
        notifyDataSetChanged();
    }

    public void itemChanged(QuickbloxChat qbChatDialog){
        for (int i = 0 ; i < chatlist.size() ; i++){
            QuickbloxChat chatDialog = chatlist.get(i);
            if(chatDialog.getDialogId().equalsIgnoreCase(qbChatDialog.getDialogId())){
                chatlist.remove(i);
                chatlist.add(0,qbChatDialog);
                notifyItemRangeChanged(0,i+1);
                dialogAdapterInterface.onItemChanged(chatlist);
                break;
            }
        }
    }

    public interface DialogAdapterInterface{
        void onItemChanged(ArrayList<QuickbloxChat> chatList);
    }

    @Override
    public Itemholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Itemholder(LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(Itemholder holder, int position) {
        String name = "";
        ColorDrawable cd = null;
        final QuickbloxChat chatDialog = chatlist.get(position);

        holder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserForChatListener.onSelectUserForChatListener(chatDialog.getDialogId());
            }
        });

        holder.rootLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteDialogListener.onDeleteDialogListener(chatDialog);
                return true;
            }
        });



        if(chatDialog.getType() == QBDialogType.GROUP.getCode()){
            cd = new ColorDrawable(ContextCompat.getColor(context,R.color.bluecolor));
            name = InterfaceManager.sharedInstance().getInitial(chatDialog.getName());
        }
        else{
            name = InterfaceManager.sharedInstance().getInitial(chatDialog.getName());
            cd = new ColorDrawable(ContextCompat.getColor(context,R.color.loading_yellow));
        }

        holder.userPhotoImageView.setImageDrawable(cd);
        holder.userPhotoTextView.setText(name);
        holder.userPhotoTextView.setAllCaps(true);
        holder.userPhotoTextView.setVisibility(View.VISIBLE);

        if(chatDialog.getType() == QBDialogType.PRIVATE.getCode()){
            QBUser recipient = userHashmap.get(chatDialog.getRecipientId());
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
                                    .into(holder.userPhotoImageView);
                            holder.userPhotoTextView.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(chatDialog.getPhoto() != null && !chatDialog.getPhoto().equals("null")){
            Glide.with(context).load(chatDialog.getPhoto()).into(holder.userPhotoImageView);
            holder.userPhotoTextView.setVisibility(View.GONE);
        }

        holder.username.setText(chatDialog.getName());

        holder.lastmessage.setText(chatDialog.getLastMessage());

        long milis = chatDialog.getLastMessageDateSent() * 1000;

        if(milis == 0){
            holder.timeTextView.setVisibility(View.GONE);
        }else{
            holder.timeTextView.setVisibility(View.VISIBLE);
            holder.timeTextView.setText(getDate(milis));
        }

        if (chatDialog.getUnreadMessageCount() == 0) {
            holder.multipleNotifTextView.setVisibility(View.GONE);
        } else {
            holder.multipleNotifTextView.setVisibility(View.VISIBLE);
            holder.multipleNotifTextView.setText(chatDialog.getUnreadMessageCount() + "");
        }

        holder.dividerView.setVisibility(View.VISIBLE);
        if(position == chatlist.size()-1){
            holder.dividerView.setVisibility(View.GONE);
        }
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    public class Itemholder extends RecyclerView.ViewHolder{
        RelativeLayout rootLinearLayout;
        FrameLayout singleNotifFrameLayout,multipleNotifFrameLayout,userPhotoFrameLayout;
        ImageView userPhotoImageView;
        CircleImageView singleNotifImageView;
        TextView username,userPhotoTextView,lastmessage,timeTextView,singleNotifTextView,multipleNotifTextView;
        View dividerView;
        public Itemholder(View itemView) {
            super(itemView);
            rootLinearLayout = (RelativeLayout) itemView.findViewById(R.id.item_chat_list_rootRelativeLayout);
            singleNotifFrameLayout = (FrameLayout)itemView.findViewById(R.id.item_chat_list_notificationSingleFrameLayout);
            multipleNotifFrameLayout = (FrameLayout)itemView.findViewById(R.id.item_chat_list_notificationMultipleFrameLayout);
            userPhotoFrameLayout = (FrameLayout)itemView.findViewById(R.id.item_chat_list_userPhotoFrameLayout);
            userPhotoImageView = (ImageView) itemView.findViewById(R.id.item_chat_list_userPhoto);
            singleNotifImageView = (CircleImageView)itemView.findViewById(R.id.item_chat_list_notificationSingleImageView);
            username = (TextView) itemView.findViewById(R.id.item_chat_list_userName);
            userPhotoTextView = (TextView)itemView.findViewById(R.id.item_chat_list_profilePictureTextView);
            lastmessage = (TextView)itemView.findViewById(R.id.item_chat_list_lastMessage);
            timeTextView = (TextView)itemView.findViewById(R.id.item_chat_list_timeTextView);
            singleNotifTextView = (TextView)itemView.findViewById(R.id.item_chat_list_notificationSingleTextView);
            multipleNotifTextView = (TextView)itemView.findViewById(R.id.item_chat_list_notificationMultipleTextView);
            dividerView = (View) itemView.findViewById(R.id.item_chat_list_dividerView);
        }
    }
}
