package adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.root.skor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bean.HistoryItem;


public class HistoryListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater layoutInflater;
    TextView description;
    TextView historyItemTextView;
    TextView poinTextView;
    TextView dateTextView;
    TextView redemptionCode;
    CardView rootCardView;
    de.hdodenhof.circleimageview.CircleImageView historyIcon;
    ArrayList<HistoryItem> historyItemArrayList;


    public HistoryListViewAdapter(Context context, ArrayList<HistoryItem> historyItemArrayList) {
        this.mContext = context;
        this.historyItemArrayList = historyItemArrayList;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        //    ViewHolder viewHolder;
        if (convertView == null) {
            //viewHolder=new ViewHolder();
            view = layoutInflater.inflate(R.layout.history_layout_custom, null);
            //view.setTag(viewHolder);
        }
        else {
            view = convertView;
        }

        rootCardView = (CardView) view.findViewById(R.id.cardView);
        historyItemTextView = (TextView) view.findViewById(R.id.title);
        poinTextView = (TextView) view.findViewById(R.id.time);
        dateTextView = (TextView) view.findViewById(R.id.date);
        description = (TextView) view.findViewById(R.id.desc);
        redemptionCode = (TextView) view.findViewById(R.id.redemption_code);
        historyIcon = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.userimage);
        dateTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        final HistoryItem historyItem = historyItemArrayList.get(position);

        historyItemTextView.setText(historyItem.mName);

        poinTextView.setText(historyItem.mPoints + " pts");

        if (historyItem.mRedemptionCode.equals("")) {
            redemptionCode.setText("" + historyItem.mRedemptionCode);
        } else {
            redemptionCode.setText("Redemption Code " + historyItem.mRedemptionCode);
        }

        if (historyItem.facilityLocationName.equals("")) {
            description.setText(historyItem.facilityLocationName);
        } else {
            description.setText(historyItem.facilityLocationName);
        }

        rootCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyItem.mName.equals("Reward Redemption")) {
                    if (historyItem.mRedemptionCode.contains("http")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(historyItem.mRedemptionCode));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(browserIntent);
                    }
                }
            }
        });




        Glide.with(mContext)
                .asBitmap()
                .load(historyItem.iconUrl)
                .into(new BitmapImageViewTarget(historyIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        // Do bitmap magic here
                        super.setResource(resource);
                    }
                });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date convertedDate = new Date();
        try {

            convertedDate = format.parse(historyItem.mdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            System.out.println("In exception MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
            e.printStackTrace();
        }
        String currentDay = new SimpleDateFormat("EEE").format(convertedDate.getTime());
        String currentMonth = new SimpleDateFormat("MMM").format(convertedDate.getTime());
        String day = new SimpleDateFormat("dd").format(convertedDate.getTime());
        System.out.println(convertedDate);
        dateTextView.setText(day + " " + currentDay);


        return view;
    }


}
