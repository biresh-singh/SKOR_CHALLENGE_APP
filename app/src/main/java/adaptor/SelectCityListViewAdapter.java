package adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.root.skor.R;

import java.util.ArrayList;

import bean.SelectCityListItem;
import listener.SelectCityListener;

/**
 * Created by Dihardja Software Solutions on 12/27/16.
 */

public class SelectCityListViewAdapter extends BaseAdapter {
//    private Context context;
//    ArrayList<SelectCityListItem> mSelectCity;
//
//    public SelectCityListViewAdapter(Context context, ArrayList<SelectCityListItem> selectCity) {
//        this.context = context;
//        this.mSelectCity = selectCity;
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        Holder holder = new Holder();
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (view == null) {
//            view = inflater.inflate (R.layout.selectcity_list_item, null);
//            holder.cityName = (TextView) view.findViewById(R.id.selectcity_list_item_city);
//            view.setTag(holder);
//        }else {
//            holder = (Holder)view.getTag();
//        }
//        holder.cityName.setText(mSelectCity.get(position).getCity());
//
//        return view;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mSelectCity.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getCount() {
//        return mSelectCity.size();
//    }
//
//    public class Holder{
//        TextView cityName;
//    }

    //new city list
    private Context context;
    ArrayList<SelectCityListItem> mSelectCity;
    public int selectedItem = -1;
    SelectCityListener selectCityListener;


    public SelectCityListViewAdapter(Context context, ArrayList<SelectCityListItem> selectCity, SelectCityListener selectCityListener) {
        this.context = context;
        this.mSelectCity = selectCity;
        this.selectCityListener = selectCityListener;
    }

    public void update(ArrayList<SelectCityListItem> selectCities,Integer theSelectedItem){
        mSelectCity = selectCities;
        selectedItem = theSelectedItem;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = new Holder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.item_newcitylist, null);
            holder.cityName = (TextView) view.findViewById(R.id.item_newcitylist_cityTextView);
            holder.cityRadioButton = (RadioButton) view.findViewById(R.id.item_newcitylist_radioButton);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.cityName.setText(mSelectCity.get(position).getCity());

        holder.cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(mSelectCity,position);
                selectCityListener.onSelectCityListener(mSelectCity.get(position));
            }
        });

        holder.cityRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(mSelectCity,position);
                selectCityListener.onSelectCityListener(mSelectCity.get(position));
            }
        });
        if(position == selectedItem){
            holder.cityRadioButton.setChecked(true);
        }else{
            holder.cityRadioButton.setChecked(false);
        }



        return view;
    }

    @Override
    public Object getItem(int position) {
        return mSelectCity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mSelectCity.size();
    }

    public class Holder {
        TextView cityName;
        RadioButton cityRadioButton;
    }
}
