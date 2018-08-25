package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.root.skor.R;

import activity.challenge.FunAwardChallengeActivity;

/**
 * Created by biresh.singh on 15-06-2018.
 */


public class InvitedFriendsTeamCheckAdaptor extends RecyclerView.Adapter<InvitedFriendsTeamCheckAdaptor.MyTeamAdaptor> {
    private Context mContext;

    public InvitedFriendsTeamCheckAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyTeamAdaptor onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_invited_friends_check_item, parent, false);
        return new MyTeamAdaptor(view);
    }

    @Override
    public void onBindViewHolder(MyTeamAdaptor holder, int position) {
        final MyTeamAdaptor itemHolder = (MyTeamAdaptor) holder;

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FunAwardChallengeActivity.class);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class MyTeamAdaptor extends RecyclerView.ViewHolder{
        public MyTeamAdaptor(View itemView) {
            super(itemView);
        }
    }
}