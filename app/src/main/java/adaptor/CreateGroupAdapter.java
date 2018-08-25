package adaptor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectContactListener;
import listener.SelectContactListener2;
import singleton.InterfaceManager;
import utils.ArialBoldTextView;
import utils.ArialRegularTextView;
import utils.DataHolder;

/**
 * Created by dss-17 on 6/2/17.
 */

public class CreateGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    Context context;
    List<QBUser> qbUsersList;
    List<Object> itemList = new ArrayList<>();
    List<Object> searchedItemList = new ArrayList<>();
    SelectContactListener selectContactListener;
    private ArrayList<Integer> mSectionPositions;
    private final int ITEM_HEADER = 0;
    private final int ITEM_USER = 1;
    String intent;

    List<QBUser> currentOccupantGroupList;
    SelectContactListener2 selectContactListener2;

    public CreateGroupAdapter(Context context, SelectContactListener selectContactListener, List<QBUser> theUserList, String intent) {
        this.context = context;
        this.selectContactListener = selectContactListener;
        this.qbUsersList = theUserList;
        this.intent = intent;

        refreshUserList();
    }

    public CreateGroupAdapter(Context context, SelectContactListener2 selectContactListener2, List<QBUser> theUserList, String intent) {
        this.context = context;
        this.selectContactListener2 = selectContactListener2;
        this.qbUsersList = theUserList;
        this.intent = intent;

        refreshUserList();
    }

    public void occupantsList(List<QBUser> currentOccupantGroupList) {
        this.currentOccupantGroupList = currentOccupantGroupList;
        for (QBUser qbUser : currentOccupantGroupList) {
            checkQBUserInGroup(qbUser);
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(QBUser qbUser){
        checkQBUserInGroup(qbUser);
        notifyDataSetChanged();
    }

    private void refreshUserList(){
        itemList = new ArrayList<>();
        QBUser qbUser = QBChatService.getInstance().getUser();
        int listId;
        int loggedInId = qbUser.getId();
        for (int i = 0; i < qbUsersList.size() ; i++) {
            listId = qbUsersList.get(i).getId();
            if(listId != loggedInId){
                if(i==0){
                    if(Character.isDigit(qbUsersList.get(i).getFullName().charAt(0))){
                        itemList.add("#");
                    }else{
                        itemList.add(qbUsersList.get(i).getFullName().toUpperCase().charAt(0));
                    }
                    itemList.add(qbUsersList.get(i));
                }else{
                    if(qbUsersList.get(i).getFullName().toUpperCase().charAt(0) == qbUsersList.get(i-1).getFullName().toUpperCase().charAt(0)){
                        itemList.add(qbUsersList.get(i));
                    }
                    else{
                        itemList.add(qbUsersList.get(i).getFullName().toUpperCase().charAt(0));
                        itemList.add(qbUsersList.get(i));
                    }
                }
            }
        }
    }

    public void searchContact(CharSequence searchKeyword){
        refreshUserList();
        List<Object> searchedUserList = new ArrayList<>();
        for (int i = 0; i < qbUsersList.size() ; i++) {
            if(qbUsersList.get(i).getFullName().toLowerCase().contains(searchKeyword.toString().toLowerCase())
                    || qbUsersList.get(i).getFullName().toUpperCase().contains(searchKeyword.toString().toUpperCase())){
                if(searchedUserList.size() == 0){
                    if(Character.isDigit(qbUsersList.get(i).getFullName().charAt(0))){
                        searchedUserList.add("#");
                    }else{
                        searchedUserList.add(qbUsersList.get(i).getFullName().toUpperCase().charAt(0));
                    }
                    searchedUserList.add(qbUsersList.get(i));
                }
                else{
                    if(qbUsersList.get(i).getFullName().toUpperCase().charAt(0) == qbUsersList.get(i-1).getFullName().toUpperCase().charAt(0)){
                        searchedUserList.add(qbUsersList.get(i));
                    }
                    else{
                        searchedUserList.add(qbUsersList.get(i).getFullName().toUpperCase().charAt(0));
                        searchedUserList.add(qbUsersList.get(i));
                    }
                }

            }
        }
        itemList = searchedUserList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_HEADER){
            return new HeaderHolder(LayoutInflater.from(context).inflate(R.layout.item_create_group_alphabet_sort_header,parent,false));
        }
        else{
            return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_create_group_contacts_list,parent,false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(itemList.get(position) instanceof QBUser){
            return ITEM_USER;
        }
        else{
            return ITEM_HEADER;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            final HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.headerAlphabetTextView.setText(itemList.get(position)+"");
        }else{
            final ItemHolder itemHolder = (ItemHolder) holder;
            final QBUser qbUser = ((QBUser)itemList.get(position));
            itemHolder.userNameTextView.setText(((QBUser)itemList.get(position)).getFullName());
            String qbPhoto = qbUser.getCustomData();
            itemHolder.profilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(((QBUser)itemList.get(position)).getFullName()));
            itemHolder.profilePictureTextView.setAllCaps(true);
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context,R.color.loading_yellow));
            itemHolder.profilePictureImageView.setImageDrawable(cd);
            itemHolder.profilePictureTextView.setVisibility(View.VISIBLE);
            itemHolder.dividerView.setVisibility(View.VISIBLE);
            if(DataHolder.getInstance().getSelectedUsersForGroupList().contains(qbUser)){
                itemHolder.checkedIndicatorImageView.setImageResource(R.drawable.check_on);
            }else{
                itemHolder.checkedIndicatorImageView.setImageResource(R.drawable.check_off);
            }
            if(position != itemList.size()-1){
                if(!(itemList.get(position+1) instanceof QBUser)){
                    itemHolder.dividerView.setVisibility(View.GONE);
                }
            }
            else{
                itemHolder.dividerView.setVisibility(View.GONE);
            }

            if(((QBUser) itemList.get(position)).getLastRequestAt() != null){
                long currentTime = System.currentTimeMillis();
                long userLastRequestAtTime = ((QBUser) itemList.get(position)).getLastRequestAt().getTime();
                //online
                if((currentTime - userLastRequestAtTime) < 5*60*1000){
                    itemHolder.userStatusTextView.setText("Online");
                }
                else{
                    if((currentTime - userLastRequestAtTime) < 1*60*60*1000 && (currentTime - userLastRequestAtTime)>= 5*60*1000){
                        String time = String.format("%d ",
                                TimeUnit.MILLISECONDS.toMinutes(currentTime - userLastRequestAtTime)
                        );
                        itemHolder.userStatusTextView.setText("Last seen "+ time + " minutes ago");
                    }else if((currentTime - userLastRequestAtTime) < 1*24*60*60*1000){
                        String time = String.format("%d ",
                                TimeUnit.MILLISECONDS.toHours(currentTime - userLastRequestAtTime)
                        );
                        itemHolder.userStatusTextView.setText("Last seen "+ time + " hours ago");
                    }else if((currentTime - userLastRequestAtTime) > 1*24*60*60*1000){
                        long days = TimeUnit.MILLISECONDS.toDays(currentTime - userLastRequestAtTime);
                        if(days == 1){
                            itemHolder.userStatusTextView.setText("Last seen yesterday");
                        }else{
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(userLastRequestAtTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                            String nameOfDay = formatter.format(calendar.getTime());
                            itemHolder.userStatusTextView.setText("Last seen " + nameOfDay);
                        }
                    }
                }
            }

            String photoUrl = "";
            try {
                if(qbPhoto!=null) {
                    if(!qbPhoto.equalsIgnoreCase("")) {
                        JSONObject photoJSONObject = new JSONObject(qbPhoto);
                        photoUrl = photoJSONObject.getString("avatar_url");
                        Glide.with(context).load(photoUrl).into(itemHolder.profilePictureImageView);
                        itemHolder.profilePictureTextView.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemHolder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkQBUserInGroup(qbUser);
                    if (intent.equals("fromGroupInfo")) {
                        selectContactListener2.onSelectContactListener(qbUser,false);
                    } else {
                        selectContactListener.onSelectContactListener(qbUser,false);
                    }
                    notifyDataSetChanged();
                }
            });

        }
    }

    private void checkQBUserInGroup(QBUser qbUser){
        if(DataHolder.getInstance().getSelectedUsersForGroupList().contains(qbUser)){
            DataHolder.getInstance().removeQbUserFromGroup(qbUser);
        }else{
            DataHolder.getInstance().addQbUserToGroup(qbUser);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for(int i = 0; i<itemList.size(); i++){
            if(!(itemList.get(i) instanceof QBUser)){
                sections.add(itemList.get(i)+"");
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePictureImageView,checkedIndicatorImageView;
        ArialRegularTextView userNameTextView, userStatusTextView;
        TextView profilePictureTextView;
        RelativeLayout rootRelativeLayout;
        View dividerView;

        public ItemHolder(View view) {
            super(view);

            profilePictureImageView = (CircleImageView) view.findViewById(R.id.item_create_group_contacts_list_userPhoto);
            checkedIndicatorImageView = (CircleImageView) view.findViewById(R.id.item_create_group_contacts_list_checkedIndicatorCircleImageView);
            profilePictureTextView = (TextView) view.findViewById(R.id.item_create_group_contacts_list_profilePictureTextView);
            userNameTextView = (ArialRegularTextView) view.findViewById(R.id.item_create_group_contacts_list_userName);
            userStatusTextView = (ArialRegularTextView) view.findViewById(R.id.item_create_group_contacts_list_status);
            rootRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_create_group_contacts_list_rootRelativeLayout);
            dividerView = (View) view.findViewById(R.id.item_create_group_contacts_list_dividerView);

        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder{
        ArialBoldTextView headerAlphabetTextView;

        public HeaderHolder(View view){
            super(view);
            headerAlphabetTextView = (ArialBoldTextView) view.findViewById(R.id.item_create_group_alphabet_sort_header_alphabetTextView);
        }
    }


}
