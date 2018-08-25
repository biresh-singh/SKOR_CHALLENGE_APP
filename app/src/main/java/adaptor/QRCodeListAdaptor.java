package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.root.skor.R;

import activity.challenge.ScanQRCodeActivity;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class QRCodeListAdaptor  extends RecyclerView.Adapter<QRCodeListAdaptor.ColleaguesHolder> {
    private Context mContext;

    public QRCodeListAdaptor(Context context) {
        this.mContext = context;
    }

    @Override
    public ColleaguesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_qr_code_item, parent, false);
        return new ColleaguesHolder(view);
    }

    @Override
    public void onBindViewHolder(ColleaguesHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(mContext, ScanQRCodeActivity.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ColleaguesHolder extends RecyclerView.ViewHolder{
        public ColleaguesHolder(View itemView) {
            super(itemView);
        }
    }
}
