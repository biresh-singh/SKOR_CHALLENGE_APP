package adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bean.QuickbloxUser;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectContactOnContactListListener;
import singleton.InterfaceManager;

/**
 * Created by mac on 10/30/17.
 */

public class Contacts2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    int totalIndex = 0;
    private static int HEADER = 0;
    private static int DETAIL = 1;
    Map<Integer, Object> contentHashMap = new HashMap<>();
    Map<Integer, Object> contentHashMapTemp = new HashMap<>();
    ArrayList<QuickbloxUser> qbUsersList;
    SelectContactOnContactListListener selectContactOnContactListListener;

    public Contacts2Adapter(Context mContext, SelectContactOnContactListListener selectContactOnContactListListener) {
        this.mContext = mContext;
        this.selectContactOnContactListListener = selectContactOnContactListListener;
        this.contentHashMap = new HashMap<>();
    }

    public void updateAdapter(ArrayList<String> departmentList, Map<String, ArrayList<QuickbloxUser>> departmentGroupingMap, ArrayList<QuickbloxUser> qbUsersList) {
//        if (contentHashMap.size() == departmentList.size() + qbUsersList.size()) {
//            return;
//        }

        totalIndex = 0;

        this.qbUsersList = qbUsersList;
        for (String departmentName : departmentList) {
            contentHashMap.put(totalIndex, departmentName);
            totalIndex++;

            if (departmentGroupingMap.get(departmentName) != null) {
                for (QuickbloxUser quickbloxUser : departmentGroupingMap.get(departmentName)) {
                    contentHashMap.put(totalIndex, quickbloxUser);
                    totalIndex++;
                }
            }
        }
        this.contentHashMapTemp = contentHashMap;

        notifyDataSetChanged();
    }

    public void searchContact(CharSequence searchKeyword) {
        Map<Integer, Object> searchContentHashMap = new HashMap<>();
        Map<Integer, Object> resultHashMap = new HashMap<>();

        contentHashMap = contentHashMapTemp;
        searchContentHashMap = contentHashMap;
        int index = 0;
        for (int i = 0; i < searchContentHashMap.size(); i++) {
            if(contentHashMap.get(i) instanceof String){
                if(searchKeyword.toString().equalsIgnoreCase("")){
                    resultHashMap.put(index, contentHashMapTemp.get(i));
                    index++;
                }
            }else{
                QuickbloxUser qbUser = (QuickbloxUser) searchContentHashMap.get(i);
                if (qbUser.getFullName().toLowerCase().contains(searchKeyword.toString().toLowerCase())
                        || qbUser.getFullName().toLowerCase().contains(searchKeyword.toString().toUpperCase())) {
                    resultHashMap.put(index, qbUser);
                    index++;
                }
            }
        }

        contentHashMap = resultHashMap;
        totalIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mContext, R.color.skor_chat_gray_status));
        if (getItemViewType(position) == HEADER) {
            final Contacts2Adapter.HeaderHolder headerHolder = (Contacts2Adapter.HeaderHolder) holder;
            String department = (String) contentHashMap.get(position);
            headerHolder.mDepartmentTextView.setText(department);
        } else {
            final Contacts2Adapter.ItemHolder itemHolder = (Contacts2Adapter.ItemHolder) holder;
            final QuickbloxUser quickbloxUser = (QuickbloxUser) contentHashMap.get(position);
            String qbPhoto = quickbloxUser.getCustomData();
            itemHolder.photoTextView.setText(InterfaceManager.sharedInstance().getInitial(quickbloxUser.getFullName()));
            itemHolder.photoTextView.setAllCaps(true);
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(mContext, R.color.loading_yellow));
            itemHolder.photo.setImageDrawable(cd);
            itemHolder.photoTextView.setVisibility(View.VISIBLE);
            String photoUrl = "";
            try {
                if (qbPhoto != null) {
                    if (!qbPhoto.equalsIgnoreCase("")) {
                        JSONObject photoJSONObject = new JSONObject(qbPhoto);
                        photoUrl = photoJSONObject.getString("avatar_url");
                        Glide.with(mContext).load(photoUrl)
                                .apply(RequestOptions.noAnimation())
                                .into(itemHolder.photo);
                        itemHolder.photoTextView.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String qbUserName = quickbloxUser.getFullName();

            itemHolder.name.setTypeface(null, Typeface.BOLD);
            itemHolder.name.setText(qbUserName);

            if (contentHashMap.get(position + 1) instanceof String) {
                itemHolder.dividerView.setVisibility(View.GONE);
            }


            if (quickbloxUser.getLastRequestAt() != null) {
                long currentTime = System.currentTimeMillis();
                long userLastRequestAtTime = quickbloxUser.getLastRequestAt().getTime();
                //online
                if ((currentTime - userLastRequestAtTime) < 5 * 60 * 1000) {
                    colorDrawable = new ColorDrawable(ContextCompat.getColor(mContext, R.color.skor_chat_online_indicator));
                    itemHolder.onlineImageView.setImageDrawable(colorDrawable);
                    itemHolder.onlineStatusTextView.setText("Online");
                } else {
                    if ((currentTime - userLastRequestAtTime) < 60 * 60 * 1000 && (currentTime - userLastRequestAtTime) >= 5 * 60 * 1000) {
                        String time = String.format("%d ", TimeUnit.MILLISECONDS.toMinutes(currentTime - userLastRequestAtTime));
                        itemHolder.onlineStatusTextView.setText("Last seen " + time + " minutes ago");
                    } else if ((currentTime - userLastRequestAtTime) < 24 * 60 * 60 * 1000) {
                        String time = String.format("%d ", TimeUnit.MILLISECONDS.toHours(currentTime - userLastRequestAtTime));
                        itemHolder.onlineStatusTextView.setText("Last seen " + time + " hours ago");
                    } else if ((currentTime - userLastRequestAtTime) > 24 * 60 * 60 * 1000) {
                        long days = TimeUnit.MILLISECONDS.toDays(currentTime - userLastRequestAtTime);
                        if (days == 1) {
                            itemHolder.onlineStatusTextView.setText("Last seen yesterday");
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(userLastRequestAtTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                            String nameOfDay = formatter.format(calendar.getTime());
                            itemHolder.onlineStatusTextView.setText("Last seen " + nameOfDay);
                        }
                    }
                }
            }

            itemHolder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectContactOnContactListListener.onSelectContactOnContactListListener(quickbloxUser, false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return totalIndex;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new Contacts2Adapter.HeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.item_department, parent, false));
        } else {
            return new Contacts2Adapter.ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contact_2, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contentHashMap.get(position) instanceof String) {
            return HEADER;
        }
        return DETAIL;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mDepartmentTextView;
        LinearLayout rootLinearLayout;

        public HeaderHolder(View itemView) {
            super(itemView);

            mDepartmentTextView = (TextView) itemView.findViewById(R.id.departmentTextView);
            rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.rootLinearLayout);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLinearLayout;
        View dividerView;
        TextView name;
        TextView onlineStatusTextView, photoTextView;
        CircleImageView photo, onlineImageView;

        public ItemHolder(View itemView) {
            super(itemView);

            onlineImageView = (CircleImageView) itemView.findViewById(R.id.item_contacts_onlineIndicatorCircleImageView);
            name = (TextView) itemView.findViewById(R.id.item_contacts_userName);
            photo = (CircleImageView) itemView.findViewById(R.id.item_contacts_userPhoto);
            onlineStatusTextView = (TextView) itemView.findViewById(R.id.item_contacts_status);
            rootLinearLayout = (RelativeLayout) itemView.findViewById(R.id.item_contacts_rootRelativeLayout);
            dividerView = (View) itemView.findViewById(R.id.item_contacts_dividerView);
            photoTextView = (TextView) itemView.findViewById(R.id.item_contacts_profilePictureTextView);
        }
    }


}
