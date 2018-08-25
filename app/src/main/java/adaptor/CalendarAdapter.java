package adaptor;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.root.skor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class CalendarAdapter extends BaseAdapter {
    private Context mContext;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    TextView dayView;
    String itemvalue, curentDateString;
    DateFormat df;
    ImageView iw;
    public static boolean isCalenderDateSelected=false;

    private ArrayList<String> items;
    public static List<String> dayString;
    private View previousView;
    HashMap<String,String> mEventTypes;
    ImageView appointmentsIcon;
    String date;
    HashMap<String,String> greetingEventTYpes=new HashMap<>();
    HashMap<String,String> apppointmentsEventTYpes=new HashMap<>();

    public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
        CalendarAdapter.dayString = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }
    public void setEventType(HashMap<String,String> eventTypes)
    {

        this.mEventTypes=eventTypes;
    }

    public void setGreetingsEventType(HashMap<String,String> eventTypes)
    {

        this.greetingEventTYpes=eventTypes;
    }

    public void setAppointmentsEventType(HashMap<String,String> eventTypes)
    {

        this.apppointmentsEventTYpes=eventTypes;
    }
    public void setItems(ArrayList<String> items) {
        if(items!=null) {
            for (int i = 0; i != items.size(); i++) {
                String item=items.get(i);
                if(item!=null) {
                    if (items.get(i).length() == 1) {
                        items.set(i, "0" + items.get(i));
                    }
                }
            }
        }
        this.items = items;
    }

    public int getCount() {
        return dayString.size();
    }

    public Object getItem(int position) {
        return dayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }
        iw = (ImageView) v.findViewById(R.id.date_icon);
        dayView = (TextView) v.findViewById(R.id.date);
        appointmentsIcon=(ImageView)v.findViewById(R.id.appoint_icon);
        // separates daystring into parts.
        String[] separatedTime = dayString.get(position).split("-");
        // taking last part of date. ie; 2 from 2012-12-02
        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        // checking whether the day is in current month or not.
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            // setting offdays to white color.
            dayView.setTextColor(Color.WHITE);
            dayView.setAlpha(0.4f);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.WHITE);
            dayView.setAlpha(0.4f);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            dayView.setAlpha(1f);
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.WHITE);
        }

        if (dayString.get(position).equals(curentDateString)) {
            v.setBackgroundResource(R.drawable.holo_circle);
            //setSelected(v);
            previousView = v;
        } else {
            v.setBackgroundResource(R.drawable.list_item_background);
            iw.setVisibility(View.VISIBLE);
            //v.setBackgroundColor(Color.BLUE);

        }
        dayView.setText(gridvalue);

        // create date string for comparison

            date = dayString.get(position);

            if (date.length() == 1) {
                date = "0" + date;
            }
            String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
            if (monthStr.length() == 1) {
                monthStr = "0" + monthStr;
            }


        // show icon if date is not empty and it exists in the items array

        ImageView greetIcon=(ImageView)v.findViewById(R.id.greet_icon);
       /* if (date.length() > 0 && items != null && items.contains(date)) {*/
        v.setBackgroundColor(Color.TRANSPARENT);
        iw.setVisibility(View.GONE);
        appointmentsIcon.setVisibility(View.GONE);
        greetIcon.setVisibility(View.GONE);



             /*   for (HashMap.Entry<String, String> entry : mEventTypes.entrySet()) {*/
      if(mEventTypes!=null) {
          if (mEventTypes.containsKey(date)) {
              iw.setVisibility(View.VISIBLE);
              //greetIcon.setVisibility(View.VISIBLE);
              v.setBackgroundResource(R.drawable.blue_circle);



          }
      }
        if(greetingEventTYpes!=null) {
        if (greetingEventTYpes.containsKey(date)) {
            greetIcon.setVisibility(View.VISIBLE);
            v.setBackgroundResource(R.drawable.blue_circle);


        }}
        if(apppointmentsEventTYpes!=null) {
            if (apppointmentsEventTYpes.containsKey(date)) {
                //iw.setVisibility(View.VISIBLE);
                appointmentsIcon.setVisibility(View.VISIBLE);
                v.setBackgroundResource(R.drawable.blue_circle);

            }
        }

        return v;
    }

    public View setSelected(View view) {
        isCalenderDateSelected=true;
        if (previousView != null) {
            previousView.setBackgroundResource(R.drawable.list_item_background);
            //iw.setVisibility(View.VISIBLE);

        }
        previousView = view;
        dayView.setTextColor(Color.BLACK);
        view.setBackgroundResource(R.drawable.blue_circle);

        return view;
    }

    public void refreshDays() {
        // clear items
        if(isCalenderDateSelected==false) {
            items.clear();

            dayString.clear();
            Locale.setDefault(Locale.US);
            pmonth = (GregorianCalendar) month.clone();
            // month start day. ie; sun, mon, etc
            firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
            // finding number of weeks in current month.
            maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
            // allocating maximum row number for the gridview.
            mnthlength = maxWeeknumber * 7;
            maxP = getMaxP(); // previous month maximum day 31,30....
            calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
            /**
             * Calendar instance for getting a complete gridview including the three
             * month's (previous,current,next) dates.
             */
            pmonthmaxset = (GregorianCalendar) pmonth.clone();
            /**
             * setting the start date as previous month's required date.
             */
            pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

            /**
             * filling calendar gridview.
             */
            for (int n = 0; n < mnthlength; n++) {

                itemvalue = df.format(pmonthmaxset.getTime());
                pmonthmaxset.add(GregorianCalendar.DATE, 1);
                dayString.add(itemvalue);

            }
        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

}