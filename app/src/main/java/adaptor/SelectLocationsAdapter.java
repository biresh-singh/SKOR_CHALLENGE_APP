package adaptor;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

import bean.RewardLocation;

/**
 * Created by dss-10 on 4/18/17.
 */

public class SelectLocationsAdapter extends BaseAdapter {
    private Context context;
    List<RewardLocation> rewardLocationList = new ArrayList<>();
    public int selectedItem = 0;
    SelectLocationListener listener;

    public SelectLocationsAdapter(Context context, SelectLocationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateAdapter(List<RewardLocation> rewardLocationList, int position){
        selectedItem = 0;
        this.rewardLocationList = rewardLocationList;
        selectedItem = position;
        notifyDataSetChanged();
    }

    public void updateView(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    public interface SelectLocationListener {
        void onSelectLocationListener(int position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = new Holder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.item_select_location, null);
            view.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            view.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));

            holder.locationName = (TextView) view.findViewById(R.id.item_select_location_addressTextView);
            holder.locationRadioButton = (RadioButton) view.findViewById(R.id.item_select_location_radioButton);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.locationName.setText(rewardLocationList.get(position).address);

        holder.locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(position);
                listener.onSelectLocationListener(position);
            }
        });

        holder.locationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(position);
                listener.onSelectLocationListener(position);
            }
        });

        if(position == selectedItem){
            holder.locationRadioButton.setChecked(true);
        }else{
            holder.locationRadioButton.setChecked(false);
        }

        return view;
    }

    @Override
    public Object getItem(int position) {
        return rewardLocationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return rewardLocationList.size();
    }

    public class Holder {
        TextView locationName;
        RadioButton locationRadioButton;
    }

    public int dpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
