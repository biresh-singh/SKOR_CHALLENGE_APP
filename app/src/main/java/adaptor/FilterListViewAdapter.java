package adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.root.skor.R;

import java.util.ArrayList;


public class FilterListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater layoutInflater;
    ArrayList<String> filterItems;
 /*   ArrayList<Integer> selectedIndexes=new ArrayList<>();*/
    int selectedIndexes=0;
    public FilterListViewAdapter(Context context,ArrayList<String> filterItems) {
        this.mContext=context;
        this.filterItems=filterItems;
        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return filterItems.size();
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
        View view=null;
        if(convertView==null)
        {
           view=layoutInflater.inflate(R.layout.filter_list_item,null);
        }
        else
        {
           view=convertView;
        }
        ImageView selectedIcon=(ImageView)view.findViewById(R.id.selected_icon);
        if(selectedIndexes==position)
        {
            selectedIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            selectedIcon.setVisibility(View.GONE);
        }
        TextView filterItem=(TextView)view.findViewById(R.id.filter_item);
        filterItem.setText(filterItems.get(position));
        filterItem.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        return view;
    }
    public void setSelectedIndex(int index)
    {
       this.selectedIndexes=index;
    }
}
