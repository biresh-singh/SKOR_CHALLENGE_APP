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


public class InvitedFriendsAdaptor extends RecyclerView.Adapter<InvitedFriendsAdaptor.MyFunAwardAdaptor> {
    private Context mContext;

    public InvitedFriendsAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public MyFunAwardAdaptor onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_invited_friends_item, parent, false);
        return new MyFunAwardAdaptor(view);
    }

    @Override
    public void onBindViewHolder(MyFunAwardAdaptor holder, int position) {
        final MyFunAwardAdaptor itemHolder = (MyFunAwardAdaptor) holder;

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

    public static class MyFunAwardAdaptor extends RecyclerView.ViewHolder{
        public MyFunAwardAdaptor(View itemView) {
            super(itemView);
        }
    }
}