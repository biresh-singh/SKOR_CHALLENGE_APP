package adaptor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.root.skor.R;

import java.util.ArrayList;

import listener.SubMenuListener;

/**
 * Created by dss-17 on 8/22/17.
 */

public class SubMenuAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<String> subMenuArrayList = new ArrayList<>();
    int positionSubMenu;
    SubMenuListener subMenuListener;

    public SubMenuAdapter(Context context, ArrayList<String> subMenuArrayList, SubMenuListener subMenuListener) {
        this.context = context;
        this.subMenuArrayList = subMenuArrayList;
        this.subMenuListener = subMenuListener;
    }

    public void UpdateSubMenuPosition(int positionSubMenu) {
        this.positionSubMenu = positionSubMenu;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sub_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        String title = subMenuArrayList.get(position);

        viewHolder.title.setText(title);

        if (positionSubMenu == 0) {
            if (subMenuArrayList.get(position).equals("ALL")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        } else if (positionSubMenu == 1) {
            if (subMenuArrayList.get(position).equals("NEWS")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        }
//        else if (positionSubMenu == 2) {
//            if (subMenuArrayList.get(position).equals("CHALLENGES")) {
//                viewHolder.title.setTextColor(Color.parseColor("#000000"));
//            } else {
//                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
//            }
//        } else if (positionSubMenu == 3) {
//            if (subMenuArrayList.get(position).equals("ACTIVITY AND EVENTS")) {
//                viewHolder.title.setTextColor(Color.parseColor("#000000"));
//            } else {
//                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
//            }
//        } else if (positionSubMenu == 4) {
//            if (subMenuArrayList.get(position).equals("GREETINGS")) {
//                viewHolder.title.setTextColor(Color.parseColor("#000000"));
//            } else {
//                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
//            }
//        } else if (positionSubMenu == 5) {
//            if (subMenuArrayList.get(position).equals("ANNOUNCEMENT")) {
//                viewHolder.title.setTextColor(Color.parseColor("#000000"));
//            } else {
//                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
//            }
//        } else if (positionSubMenu == 6) {
//            if (subMenuArrayList.get(position).equals("APPOINTMENTS")) {
//                viewHolder.title.setTextColor(Color.parseColor("#000000"));
//            } else {
//                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
//            }
//        }
        else if (positionSubMenu == 2) {
            if (subMenuArrayList.get(position).equals("ACTIVITY AND EVENTS")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        } else if (positionSubMenu == 3) {
            if (subMenuArrayList.get(position).equals("GREETINGS")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        } else if (positionSubMenu == 4) {
            if (subMenuArrayList.get(position).equals("ANNOUNCEMENT")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        } else if (positionSubMenu == 5) {
            if (subMenuArrayList.get(position).equals("APPOINTMENTS")) {
                viewHolder.title.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.title.setTextColor(Color.parseColor("#dfdfdf"));
            }
        }

        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subMenuListener.onSubMenuListener(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return subMenuArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.item_sub_menu_title);
        }
    }
}
