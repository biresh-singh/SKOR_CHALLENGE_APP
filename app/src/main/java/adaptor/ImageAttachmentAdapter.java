package adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.root.skor.R;

import java.util.List;

/**
 * Created by mac on 10/26/17.
 */

public class ImageAttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    public ImageAttachmentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProgramPreviewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_image_attachment, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProgramPreviewHolder extends RecyclerView.ViewHolder {
        ImageButton mImage;

        public ProgramPreviewHolder(View itemView) {
            super(itemView);
        }
    }
}
