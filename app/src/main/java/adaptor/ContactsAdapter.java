package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import activity.skorchat.CreateGroupActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectContactListener;
import listener.SelectGroupListener;
import singleton.InterfaceManager;

/**
 * Created by dss-17 on 5/17/17.
 */

public class ContactsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerList;
    private HashMap<Integer, List<Object>> listContact = new HashMap<>();
    private List<Object> listChildData;
    private List<Object> listGroup;
    SelectContactListener selectContactListener;
    SelectGroupListener selectGroupListener;

    public ContactsAdapter(Context context, List<String> headerList, List<Object> listChildData, List<Object>  listGroup, SelectContactListener selectContactListener, SelectGroupListener selectGroupListener) {
        this.context = context;
        this.headerList = headerList;
        this.listGroup = listGroup;
        this.listChildData = listChildData;
        this.listContact.put(0,listGroup);
        this.listContact.put(1,listChildData);
        this.selectContactListener = selectContactListener;
        this.selectGroupListener = selectGroupListener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
            return this.listContact.get(groupPosition).get(childPosition);
    }

    public void searchContact(CharSequence searchKeyword){
        List<Object> qbUserList = new ArrayList<>();
        List<Object> qbGroupList = new ArrayList<>();
        List<Object> searchedUserList = new ArrayList<>();
        List<Object> searchedGroupList = new ArrayList<>();

        listContact.put(1,listChildData);
        listContact.put(0,listGroup);

        qbUserList.addAll(listChildData);
        qbGroupList.addAll(listGroup);
        for (Object qbUser : qbUserList) {
            if (((QBUser)qbUser).getFullName().toLowerCase().contains(searchKeyword.toString().toLowerCase())
                    || ((QBUser)qbUser).getFullName().toLowerCase().contains(searchKeyword.toString().toUpperCase())) {
                searchedUserList.add(qbUser);
            }
        }
        for (Object qbGroupChat : qbGroupList) {
            if (((QBChatDialog)qbGroupChat).getName().toLowerCase().contains(searchKeyword.toString().toLowerCase())
                    || ((QBChatDialog)qbGroupChat).getName().toUpperCase().contains(searchKeyword.toString().toUpperCase())) {
                searchedGroupList.add(qbGroupChat);
            }
        }
        listContact.put(1, searchedUserList);
        listContact.put(0, searchedGroupList);
        notifyDataSetChanged();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_contacts, null);
        }

        CircleImageView photo = (CircleImageView) convertView.findViewById(R.id.item_contacts_userPhoto);
        TextView photoTextView = (TextView) convertView.findViewById(R.id.item_contacts_profilePictureTextView);
        CircleImageView onlineImageView = (CircleImageView) convertView.findViewById(R.id.item_contacts_onlineIndicatorCircleImageView);
        TextView  onlineStatusTextView = (TextView) convertView.findViewById(R.id.item_contacts_status);
        ImageView phoneImageView = (ImageView) convertView.findViewById(R.id.item_contacts_phoneImageView);
        RelativeLayout rootLinearLayout = (RelativeLayout) convertView.findViewById(R.id.item_contacts_rootRelativeLayout);
        View dividerView = (View) convertView.findViewById(R.id.item_contacts_dividerView);
        TextView adminTextView = (TextView) convertView.findViewById(R.id.item_contacts_adminTextView);

        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context,R.color.skor_chat_gray_status));
        onlineImageView.setImageDrawable(colorDrawable);

        photo.setImageDrawable(null);
        onlineStatusTextView.setText("Offline");
        onlineImageView.setVisibility(View.VISIBLE);
        phoneImageView.setVisibility(View.VISIBLE);
        dividerView.setVisibility(View.VISIBLE);
        adminTextView.setVisibility(View.GONE);
        if (groupPosition == 0) {
            if (((QBChatDialog) listContact.get(groupPosition).get(childPosition)).getType().name().equals("GROUP")) {
                final QBChatDialog groupChat = (QBChatDialog) listContact.get(groupPosition).get(childPosition);
                String groupName = groupChat.getName();
                TextView name = (TextView) convertView.findViewById(R.id.item_contacts_userName);
                name.setTypeface(null, Typeface.BOLD);
                name.setText(groupName);

                photoTextView.setVisibility(View.VISIBLE);
                photoTextView.setText(InterfaceManager.sharedInstance().getInitial(groupChat.getName()));
                photoTextView.setAllCaps(true);

                onlineImageView.setVisibility(View.GONE);
                phoneImageView.setVisibility(View.GONE);

                ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context,R.color.bluecolor));
                photo.setImageDrawable(cd);

                String groupPhoto = groupChat.getPhoto();

                if(groupPhoto!=null && !groupPhoto.equals("null")){
                    Glide.with(context).load(groupPhoto)
                            .apply(RequestOptions.noAnimation())
                            .into(photo);
                    photoTextView.setVisibility(View.GONE);
                }
                try {
                    int totalMembers = groupChat.getOccupants().size();
                    int onlineUsers = groupChat.getOnlineUsers().size();
                    onlineStatusTextView.setText(totalMembers + " members, " + onlineUsers +" online");
                } catch (XMPPException e) {

                }

                if(childPosition == listGroup.size()-1){
                    dividerView.setVisibility(View.GONE);
                }
                rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectGroupListener.onSelectGroupListener(groupChat.getDialogId());
                    }
                });
            }
        } else {
            final QBUser qbUser = (QBUser) listContact.get(groupPosition).get(childPosition);
            String qbPhoto = qbUser.getCustomData();
            photoTextView.setText(InterfaceManager.sharedInstance().getInitial(qbUser.getFullName()));
            photoTextView.setAllCaps(true);
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.loading_yellow));
            photo.setImageDrawable(cd);
            photoTextView.setVisibility(View.VISIBLE);
            String photoUrl = "";
            try {
                if(qbPhoto!=null) {
                    if(!qbPhoto.equalsIgnoreCase("")) {
                        JSONObject photoJSONObject = new JSONObject(qbPhoto);
                        photoUrl = photoJSONObject.getString("avatar_url");
                        Glide.with(context).load(photoUrl)
                                .apply(RequestOptions.noAnimation())
                                .into(photo);
                        photoTextView.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String qbUserName = qbUser.getFullName();
            TextView name = (TextView) convertView.findViewById(R.id.item_contacts_userName);
            name.setTypeface(null, Typeface.BOLD);
            name.setText(qbUserName);
            if(qbUser.getLastRequestAt() != null){
                long currentTime = System.currentTimeMillis();
                long userLastRequestAtTime = qbUser.getLastRequestAt().getTime();
                //online
                if((currentTime - userLastRequestAtTime) < 5*60*1000){
                    colorDrawable = new ColorDrawable(ContextCompat.getColor(context,R.color.skor_chat_online_indicator));
                    onlineImageView.setImageDrawable(colorDrawable);
                    onlineStatusTextView.setText("Online");
                }
                else{
                    if((currentTime - userLastRequestAtTime) < 60 * 60 * 1000 && (currentTime - userLastRequestAtTime)>= 5*60*1000){
                        String time = String.format("%d ", TimeUnit.MILLISECONDS.toMinutes(currentTime - userLastRequestAtTime));
                        onlineStatusTextView.setText("Last seen "+ time + " minutes ago");
                    }else if((currentTime - userLastRequestAtTime) < 24 * 60 * 60 * 1000){
                        String time = String.format("%d ", TimeUnit.MILLISECONDS.toHours(currentTime - userLastRequestAtTime));
                        onlineStatusTextView.setText("Last seen "+ time + " hours ago");
                    }else if((currentTime - userLastRequestAtTime) > 24 * 60 * 60 * 1000){
                        long days = TimeUnit.MILLISECONDS.toDays(currentTime - userLastRequestAtTime);
                        if(days == 1){
                            onlineStatusTextView.setText("Last seen yesterday");
                        }else{
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(userLastRequestAtTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                            String nameOfDay = formatter.format(calendar.getTime());
                            onlineStatusTextView.setText("Last seen " + nameOfDay);
                        }
                    }
                }
            }

            rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectContactListener.onSelectContactListener(qbUser,false);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listContact.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_contacts_header, null);
        }

        TextView headerTextView = (TextView) convertView.findViewById(R.id.item_contacts_header_headerTextView);
        headerTextView.setTypeface(null, Typeface.BOLD);
            headerTextView.setText(headerTitle + " (" + listContact.get(groupPosition).size() + ")");

        LinearLayout createGroupButton = (LinearLayout) convertView.findViewById(R.id.item_contacts_header_createGroupLinearLayout);
        if(groupPosition == 0) {
            createGroupButton.setVisibility(View.VISIBLE);
            createGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CreateGroupActivity.class);
                    context.startActivity(intent);
                }
            });
        } else {
            createGroupButton.setVisibility(View.GONE);
        }


        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        ImageView dropDown = (ImageView) convertView.findViewById(R.id.item_contacts_header_dropdown);
        if (isExpanded) {
            dropDown.setImageResource(R.drawable.ic_arrow_up);
            mExpandableListView.expandGroup(groupPosition);
        } else {
            dropDown.setImageResource(R.drawable.ic_arrow_down);
            dropDown.setColorFilter(ContextCompat.getColor(context,R.color.chat_text));
            mExpandableListView.collapseGroup(groupPosition);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
