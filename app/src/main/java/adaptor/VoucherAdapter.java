package adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.util.ArrayList;

import activity.rewardz.NewVoucherDetailActivity;
import bean.NewEVoucher;
import constants.Constants;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import singleton.InterfaceManager;
import utils.AppController;

/**
 * Created by jessica on 12/27/17.
 */

public class VoucherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<NewEVoucher> newEVoucherArrayList = new ArrayList<>();
    private int type;

    public VoucherAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void updateAdapter(ArrayList<NewEVoucher> newEVoucherArrayList){
        this.newEVoucherArrayList = newEVoucherArrayList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.item_voucher,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewEVoucher newEVoucher = newEVoucherArrayList.get(position);
        final Viewholder viewholder = (Viewholder) holder;
        Long dateLong = InterfaceManager.sharedInstance().utcToDateInMillis(newEVoucher.getValidUntil());
        String date = InterfaceManager.sharedInstance().millisToDateInWallet(dateLong);
        String imageUrl = Constants.BASEURL+newEVoucher.getThumbnailImagePath();
        // type == 0 means unused wallet, type == 1 means used wallet
        if(type == 0) {
            Glide.with(context).load(imageUrl).into(viewholder.voucherImageView);
            viewholder.isAvailableTextView.setText("Available");
            viewholder.expiryDateTextView.setText("Expires on " + date);
        }else{
            Glide.with(context).load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                    .into(viewholder.voucherImageView);
            viewholder.isAvailableTextView.setText("Completed");
            viewholder.expiryDateTextView.setText(date);
        }
        viewholder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , NewVoucherDetailActivity.class);
                intent.putExtra("eVoucher",newEVoucher.getId());
                intent.putExtra("type",type);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newEVoucherArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        private LinearLayout rootLinearLayout;
        private ImageView voucherImageView;
        private TextView expiryDateTextView, isAvailableTextView;
        public Viewholder(View itemView) {
            super(itemView);
            rootLinearLayout = itemView.findViewById(R.id.item_voucher_rootLinearLayout);
            voucherImageView = itemView.findViewById(R.id.item_voucher_imageView);
            expiryDateTextView = itemView.findViewById(R.id.item_voucher_expiryDateTextView);
            isAvailableTextView = itemView.findViewById(R.id.item_voucher_isAvailableTextView);
        }
    }
}
