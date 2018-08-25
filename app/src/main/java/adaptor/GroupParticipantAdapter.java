package adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dss-17 on 7/17/17.
 */

public class GroupParticipantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    int groupAdminId;
    List<QBUser> qbUserList = new ArrayList<>();

    public GroupParticipantAdapter(Context context) {
        this.context = context;
        notifyDataSetChanged();
    }

    public void updateAdapter(QBUser qbUser) {
        qbUserList.add(qbUser);
        notifyDataSetChanged();
    }


    public void updateAdapter(List<QBUser> qbUserList) {
        this.qbUserList = qbUserList;
        notifyDataSetChanged();
    }

    public void clearAdapter() {
        qbUserList.clear();
    }

    public void setAdminId(int groupAdminId) {
        this.groupAdminId = groupAdminId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupParticipantAdapter.HolderContact(LayoutInflater.from(context).inflate(R.layout.item_contacts, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HolderContact holderContact = (HolderContact) holder;

        holderContact.contactStatus.setVisibility(View.GONE);
        holderContact.phoneImageView.setVisibility(View.GONE);
        holderContact.onlineIndicatorCircleImageView.setVisibility(View.GONE);

        holderContact.userName.setText(qbUserList.get(position).getFullName());

        Log.e("qbuserlist", "onBindViewHolder: " + qbUserList.get(position).getId().intValue());
        Log.e("admin", "onBindViewHolder: " + groupAdminId);
        holderContact.adminTextView.setVisibility(View.GONE);
        if (qbUserList.get(position).getId().intValue() == groupAdminId) {
            holderContact.adminTextView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return qbUserList.size();
    }

    public class HolderContact extends RecyclerView.ViewHolder {
        TextView userName, contactStatus, adminTextView;
        ImageView phoneImageView;
        de.hdodenhof.circleimageview.CircleImageView onlineIndicatorCircleImageView;

        public HolderContact(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.item_contacts_userName);
            contactStatus = (TextView) view.findViewById(R.id.item_contacts_status);
            phoneImageView = (ImageView) view.findViewById(R.id.item_contacts_phoneImageView);
            onlineIndicatorCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.item_contacts_onlineIndicatorCircleImageView);
            adminTextView = (TextView) view.findViewById(R.id.item_contacts_adminTextView);
        }
    }
}
