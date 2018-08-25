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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import singleton.InterfaceManager;
import utils.ArialRegularTextView;

/**
 * Created by jessica on 7/7/17.
 */

public class CreateGroupParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<QBUser> groupMembersList;

    public CreateGroupParticipantsAdapter(Context context){
        this.context = context;
    }

    public void updateAdapter(List<QBUser> groupMembersList){
        this.groupMembersList = groupMembersList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_create_group_step2_participants,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.participantsUserNameTextView.setText(groupMembersList.get(position).getFullName());
        String qbPhoto = groupMembersList.get(position).getCustomData();
        itemHolder.participantsProfilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(groupMembersList.get(position).getFullName()));
        itemHolder.participantsProfilePictureTextView.setAllCaps(true);
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context,R.color.loading_yellow));
        itemHolder.participantsProfilePictureImageView.setImageDrawable(cd);
        itemHolder.participantsProfilePictureImageView.setVisibility(View.VISIBLE);

        if(groupMembersList.get(position).getLastRequestAt() != null){
            long currentTime = System.currentTimeMillis();
            long userLastRequestAtTime = groupMembersList.get(position).getLastRequestAt().getTime();
            //online
            if((currentTime - userLastRequestAtTime) < 5*60*1000){
                itemHolder.participantsStatusTextView.setText("Online");
            }
            else{
                if((currentTime - userLastRequestAtTime) < 1*60*60*1000 && (currentTime - userLastRequestAtTime)>= 5*60*1000){
                    String time = String.format("%d ",
                            TimeUnit.MILLISECONDS.toMinutes(currentTime - userLastRequestAtTime)
                    );
                    itemHolder.participantsStatusTextView.setText("Last seen "+ time + " minutes ago");
                }else if((currentTime - userLastRequestAtTime) < 1*24*60*60*1000){
                    String time = String.format("%d ",
                            TimeUnit.MILLISECONDS.toHours(currentTime - userLastRequestAtTime)
                    );
                    itemHolder.participantsStatusTextView.setText("Last seen "+ time + " hours ago");
                }else if((currentTime - userLastRequestAtTime) > 1*24*60*60*1000){
                    long days = TimeUnit.MILLISECONDS.toDays(currentTime - userLastRequestAtTime);
                    if(days == 1){
                        itemHolder.participantsStatusTextView.setText("Last seen yesterday");
                    }else{
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(userLastRequestAtTime);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                        String nameOfDay = formatter.format(calendar.getTime());
                        itemHolder.participantsStatusTextView.setText("Last seen " + nameOfDay);
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
                    Glide.with(context).load(photoUrl).into(itemHolder.participantsProfilePictureImageView);
                    itemHolder.participantsProfilePictureTextView.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return groupMembersList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        CircleImageView participantsProfilePictureImageView;
        TextView participantsProfilePictureTextView;
        ArialRegularTextView participantsStatusTextView,participantsUserNameTextView;

        public ItemHolder(View view){
            super(view);
            participantsProfilePictureImageView = (CircleImageView) view.findViewById(R.id.item_create_group_step2_participants_profilePictureImageView);
            participantsProfilePictureTextView = (TextView) view.findViewById(R.id.item_create_group_step2_participants_profilePictureTextView);
            participantsStatusTextView = (ArialRegularTextView) view.findViewById(R.id.item_create_group_step2_participants_statusTextView);
            participantsUserNameTextView = (ArialRegularTextView) view.findViewById(R.id.item_create_group_step2_participants_userNameTextView);
        }
    }
}
