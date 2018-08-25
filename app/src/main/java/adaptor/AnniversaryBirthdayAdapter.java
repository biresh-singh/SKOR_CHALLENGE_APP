package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import activity.history.BirthdayWishActivity;
import bean.Anniversary;
import bean.AnniversaryResponse;
import bean.Birthday;
import bean.BirthdayResponse;
import constants.Constants;
import io.realm.RealmList;
import utils.CircleImageView;

/**
 * Created by dss-17 on 10/11/17.
 */

public class AnniversaryBirthdayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    RealmList<Anniversary> anniversaryRealmList = new RealmList<>();
    RealmList<Birthday> birthdayRealmList = new RealmList<>();
    boolean isAnniv;

    public AnniversaryBirthdayAdapter(Context context) {
        this.context = context;
    }

    public void updateAnniversary(AnniversaryResponse anniversaryResponse, boolean isAnniv) {
        this.anniversaryRealmList = anniversaryResponse.getAnniversaryRealmList();
        this.isAnniv = isAnniv;
        notifyDataSetChanged();
    }

    public void updateBirthday(BirthdayResponse birthdayResponse, boolean isAnniv) {
        this.birthdayRealmList = birthdayResponse.getBirthdayRealmList();
        this.isAnniv = isAnniv;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_anniversary_birthday, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;

        if (isAnniv) {
            itemHolder.nameTextView.setText(anniversaryRealmList.get(position).getUserFirstName());
            Glide.with(context).load(Constants.BASEURL + anniversaryRealmList.get(position).getThumbnail()).into(itemHolder.circleImageView);
            itemHolder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BirthdayWishActivity.class);
                    intent.putExtra("type", "Anniversary");
                    intent.putExtra("email", anniversaryRealmList.get(position).getUserEmail());
                    intent.putExtra("name", anniversaryRealmList.get(position).getUserFirstName());
                    intent.putExtra("image", anniversaryRealmList.get(position).getThumbnail());
                    context.startActivity(intent);
                }
            });
        } else {
            itemHolder.nameTextView.setText(birthdayRealmList.get(position).getUserFirstName());
            Glide.with(context).load(Constants.BASEURL + birthdayRealmList.get(position).getThumbnail()).into(itemHolder.circleImageView);
            itemHolder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BirthdayWishActivity.class);
                    intent.putExtra("type", "Birthday");
                    intent.putExtra("email", birthdayRealmList.get(position).getUserEmail());
                    intent.putExtra("name", birthdayRealmList.get(position).getUserFirstName());
                    context.startActivity(intent);
                }
            });
        }


     }

    @Override
    public int getItemCount() {
        if (isAnniv) {
            return anniversaryRealmList.size();
        } else {
            return birthdayRealmList.size();
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        CircleImageView circleImageView;
        RelativeLayout rootRelativeLayout;

        public ItemHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.item_anniversary_birthday_userNameTextView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.item_anniversary_birthday_userCircleImageView);
            rootRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_anniversary_birthday_rootRelativeLayout);



        }
    }
}
