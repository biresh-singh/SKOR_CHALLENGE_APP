package adaptor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectContactListener;
import listener.SelectContactListener2;
import singleton.InterfaceManager;

/**
 * Created by dss-17 on 6/5/17.
 */

public class ContactDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<QBUser> qbUsers;
    SelectContactListener selectContactListener;
    List<QBUser> currentOccupantGroupList;
    SelectContactListener2 selectContactListener2;
    String intent;

    public ContactDisplayAdapter(Context context, SelectContactListener selectContactListener, String intent) {
        this.context = context;
        this.selectContactListener = selectContactListener;
        this.intent = intent;
    }

    public ContactDisplayAdapter(Context context, SelectContactListener2 selectContactListener2, String intent) {
        this.context = context;
        this.selectContactListener2 = selectContactListener2;
        this.intent = intent;
    }

    public void occupantList(List<QBUser> currentOccupantGroupList) {
        this.currentOccupantGroupList = currentOccupantGroupList;
        this.qbUsers = currentOccupantGroupList;
        notifyDataSetChanged();
    }

    public void updateAdapter(List<QBUser> qbUsers) {
        this.qbUsers = qbUsers;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactDisplayAdapter.ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_display, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.name.setText(qbUsers.get(position).getFullName());
        String qbPhoto = qbUsers.get(position).getCustomData();
        itemHolder.profilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(qbUsers.get(position).getFullName()));
        itemHolder.profilePictureTextView.setAllCaps(true);
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.loading_yellow));
        itemHolder.profilePictureImageView.setImageDrawable(cd);
        itemHolder.profilePictureTextView.setVisibility(View.VISIBLE);
        String photoUrl = "";
        try {
            if (qbPhoto != null) {
                if (!qbPhoto.equalsIgnoreCase("")) {
                    JSONObject photoJSONObject = new JSONObject(qbPhoto);
                    photoUrl = photoJSONObject.getString("avatar_url");
                    Glide.with(context).load(photoUrl).into(itemHolder.profilePictureImageView);
                    itemHolder.profilePictureTextView.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        itemHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.equals("fromCreateGroup")) {
                    selectContactListener.onSelectContactListener(qbUsers.get(position), true);
                } else {
                    selectContactListener2.onSelectContactListener(qbUsers.get(position), true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (qbUsers != null) {
            return qbUsers.size();
        }
        return 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePictureImageView;
        CircleImageView cancelButton;
        TextView name, profilePictureTextView;

        public ItemHolder(View view) {
            super(view);
            profilePictureTextView = (TextView) view.findViewById(R.id.tem_contact_display_profilePictureTextView);
            profilePictureImageView = (CircleImageView) view.findViewById(R.id.item_contact_display_photo);
            cancelButton = (CircleImageView) view.findViewById(R.id.item_contact_display_cancelButton);
            name = (TextView) view.findViewById(R.id.item_contact_display_name);

        }
    }


}
