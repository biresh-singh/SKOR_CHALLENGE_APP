package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import activity.history.ActivityHistoryActivity;
import bean.PointsCategory;

/**
 * Created by Dihardja Software Solutions on 11/3/17.
 */

public class PointCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    public List<PointsCategory> pointsCategoryList = new ArrayList<>();

    public PointCategoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateAdapter(List<PointsCategory> pointsCategoryList) {
        if (this.pointsCategoryList.size() == pointsCategoryList.size()) {
            return;
        }

        this.pointsCategoryList = pointsCategoryList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_point_category, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final PointsCategory pointsCategory = pointsCategoryList.get(position);

        itemHolder.titleTextView.setText(pointsCategory.getName());
        try {
            itemHolder.titleTextView.setTextColor(Color.parseColor(pointsCategory.getColor()));
        }
        catch (Exception e) {
            Log.e("PointCategoryAdapter", "Unknown color: " + pointsCategory.getColor());
        }
        itemHolder.valueTextView.setText(pointsCategory.getValue());
        itemHolder.pointsTextView.setText(separatorPoints(pointsCategory.getPoints()) + " PTS");
        try {
            itemHolder.valueTextView.setTextColor(Color.parseColor(pointsCategory.getColor()));
            itemHolder.pointsTextView.setTextColor(Color.parseColor(pointsCategory.getColor()));
            background(itemHolder.stepsLinearlayout, ContextCompat.getColor(mContext, android.R.color.transparent), Color.parseColor(pointsCategory.getColor()));
        } catch (Exception e) {
            itemHolder.valueTextView.setTextColor(Color.BLACK);
            itemHolder.pointsTextView.setTextColor(Color.BLACK);
            background(itemHolder.stepsLinearlayout, ContextCompat.getColor(mContext, android.R.color.transparent), Color.parseColor("#000000"));
        }

        String imageUrl = mContext.getResources().getString(R.string.base_url) + pointsCategory.getIcon();
        Glide.with(mContext).load(imageUrl).apply(new RequestOptions().centerInside()).into(itemHolder.iconIv);

        itemHolder.stepsLinearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, ActivityHistoryActivity.class);
            intent.putExtra("name", pointsCategory.getName());
            intent.putExtra("color", pointsCategory.getColor());
            intent.putExtra("icon", pointsCategory.getIcon());
            intent.putExtra("slug", pointsCategory.getSlug());
            mContext.startActivity(intent);
            }
        });
    }

    public String separatorPoints(Double balance) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);

        return format.format(balance).split("\\,")[0];
    }

    private void background(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackground(shape);
    }

    @Override
    public int getItemCount() {
        return pointsCategoryList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout stepsLinearlayout;
        TextView pointsTextView, titleTextView, valueTextView;
        ImageView iconIv;

        public ItemHolder(View itemView) {
            super(itemView);

            stepsLinearlayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_steps);
            pointsTextView = (TextView) itemView.findViewById(R.id.text_Points);
            valueTextView = (TextView) itemView.findViewById(R.id.text_value);
            titleTextView = (TextView) itemView.findViewById(R.id.text_title);
            iconIv = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
