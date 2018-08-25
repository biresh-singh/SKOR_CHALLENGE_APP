package adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.QuickbloxChat;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectGroupListener;
import singleton.InterfaceManager;

/**
 * Created by mac on 10/30/17.
 */

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    List<QuickbloxChat> listGroup = new ArrayList<>();
    List<QuickbloxChat> listGroupTemp = new ArrayList<>();
    SelectGroupListener selectGroupListener;
    private static final String TAG = "GroupAdapter";

    public GroupAdapter(Context mContext, SelectGroupListener selectGroupListener) {
        this.mContext = mContext;
        this.selectGroupListener = selectGroupListener;
    }

    public void updateAdapter(List<QuickbloxChat> listGroup) {
        this.listGroup = listGroup;
        listGroupTemp = listGroup;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final GroupAdapter.ItemHolder itemHolder = (GroupAdapter.ItemHolder) holder;
        final QuickbloxChat groupChat = listGroup.get(position);
        itemHolder.name.setText(groupChat.getName());

        int totalMembers = groupChat.getOccupants();
        int onlineUsers = groupChat.getOnlineUsers();
        itemHolder.onlineStatusTextView.setText(totalMembers + " members, " + onlineUsers + " online");

        if (position == listGroup.size() - 1) {
            itemHolder.dividerView.setVisibility(View.GONE);
        }
        itemHolder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupListener.onSelectGroupListener(groupChat.getDialogId());
            }
        });

        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(mContext,R.color.bluecolor));
        String name = InterfaceManager.sharedInstance().getInitial(groupChat.getName());
        itemHolder.photo.setImageDrawable(cd);
        itemHolder.userPhotoTextView.setText(name);
        itemHolder.userPhotoTextView.setAllCaps(true);
        itemHolder.userPhotoTextView.setVisibility(View.VISIBLE);

        if(groupChat.getPhoto() != null && !groupChat.getPhoto().equals("null")){
            Glide.with(mContext).load(groupChat.getPhoto()).into(((ItemHolder) holder).photo);
            itemHolder.userPhotoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupAdapter.ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_group, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void searchContact(CharSequence searchKeyword){
        List<QuickbloxChat> qbGroupList = new ArrayList<>();
        List<QuickbloxChat> searchedGroupList = new ArrayList<>();
        listGroup = listGroupTemp;
        qbGroupList.addAll(listGroup);
        for (QuickbloxChat qbGroupChat : qbGroupList) {
            if (qbGroupChat.getName().toLowerCase().contains(searchKeyword.toString().toLowerCase())
                    || qbGroupChat.getName().toUpperCase().contains(searchKeyword.toString().toUpperCase())) {
                searchedGroupList.add(qbGroupChat);
            }
        }
        listGroup = searchedGroupList;
        notifyDataSetChanged();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLinearLayout;
        View dividerView;
        TextView name,onlineStatusTextView,userPhotoTextView;
        CircleImageView photo;

        public ItemHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.item_contacts_userName);
            photo = (CircleImageView) itemView.findViewById(R.id.item_contacts_userPhoto);
            onlineStatusTextView = (TextView) itemView.findViewById(R.id.item_contacts_status);
            rootLinearLayout = (RelativeLayout) itemView.findViewById(R.id.item_contacts_rootRelativeLayout);
            dividerView = (View) itemView.findViewById(R.id.item_contacts_dividerView);
            userPhotoTextView = (TextView) itemView.findViewById(R.id.item_contacts_profilePictureTextView);
        }
    }

}
