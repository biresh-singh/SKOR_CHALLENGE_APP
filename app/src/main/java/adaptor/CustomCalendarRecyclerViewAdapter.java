package adaptor;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import InternetConnection.CheckInternetConnection;
import activity.event.EventDetailActivity;
import activity.history.BirthdayWishActivity;
import activity.userprofile.MainActivity;
import bean.CalenderItem;
import bean.CalenderUtility;
import utils.CircleImageView;
import constants.Constants;


public class CustomCalendarRecyclerViewAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<CalenderItem> mCalenderItemArrayList = new ArrayList<>();
    ArrayList<CalenderItem> mCalenderItemArrayTempList = new ArrayList<>();
    ArrayList<String> eventsIds = new ArrayList<>();
    String eventId = "1";
    boolean isAppintments = false;
    String[] splitJsonEndTime;
    int endTime;
    String endDate;
    Date date1, date2;
    ArrayList<JSONObject> jsonObjectArrayList;
    CheckInternetConnection checkInternetConnection;
    LayoutInflater layoutInflater;
    private static final String TAG = "CustomCalendarRecyclerV";

    public CustomCalendarRecyclerViewAdapter(Context context, ArrayList<CalenderItem> calenderItemArrayList) {
        this.mContext = context;
        this.mCalenderItemArrayList = calenderItemArrayList;
        this.mCalenderItemArrayTempList = calenderItemArrayList;
        eventsIds = CalenderUtility.eventIds;
        checkInternetConnection = new CheckInternetConnection(mContext);
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public void showCalendarBasedOnSelectedDate(String date) {
        ArrayList<CalenderItem> resultCalendarArrList = new ArrayList<>();

        this.mCalenderItemArrayList = this.mCalenderItemArrayTempList;
        for (CalenderItem calenderItem : this.mCalenderItemArrayTempList) {
            if (date.equalsIgnoreCase(
                    calenderItem.mEventdate.substring(calenderItem.mEventdate.length() - 2, calenderItem.mEventdate.length()))) {
                resultCalendarArrList.add(calenderItem);
            }
        }

        this.mCalenderItemArrayList = resultCalendarArrList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mCalenderItemArrayList.size();
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
    public View getView(final int i, View convertView, ViewGroup parent) {
        //System.out.println("value is BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB " + i);
        View view = null;
        final CalenderViewHolderItem calenderViewHolderItem;
        if (convertView == null) {
            calenderViewHolderItem = new CalenderViewHolderItem();
            view = layoutInflater.inflate(R.layout.calender_list_item, null);
            calenderViewHolderItem.relativeLayout = (RelativeLayout) view.findViewById(R.id.userlayout);
            calenderViewHolderItem.userImage = (CircleImageView) view.findViewById(R.id.userimage);
            calenderViewHolderItem.eventName = (TextView) view.findViewById(R.id.title);
            calenderViewHolderItem.eventTime = (TextView) view.findViewById(R.id.time);
            calenderViewHolderItem.eventDate = (TextView) view.findViewById(R.id.date);
            calenderViewHolderItem.address = (TextView) view.findViewById(R.id.address);

            calenderViewHolderItem.attendingOrNotButton = (Button) view.findViewById(R.id.attendingbutton);
            calenderViewHolderItem.containerView = (RelativeLayout) view.findViewById(R.id.relativelayout);
            calenderViewHolderItem.eventName.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
            view.setTag(calenderViewHolderItem);
        } else {
            view = convertView;
            calenderViewHolderItem = (CalenderViewHolderItem) view.getTag();
        }


        if (i < mCalenderItemArrayList.size()) {
            final CalenderItem calenderItem = mCalenderItemArrayList.get(i);

            calenderViewHolderItem.eventName.setText(calenderItem.meventName);

            isAppintments = false;


            String time = calenderItem.mEventTime;
            if (calenderItem.mIsAppointsments) {

                String appointTime = calenderItem.mEventdate;


                String[] timeSplit = appointTime.split(" ");
                String[] timeSplitArray = timeSplit[1].split(":");
                calenderViewHolderItem.address.setVisibility(View.GONE);
                if (appointTime.contains("a") || appointTime.contains("A")) {
                    calenderViewHolderItem.eventTime.setText(timeSplitArray[0] + ":" + timeSplitArray[1] + " AM  ");
                } else {
                    calenderViewHolderItem.eventTime.setText(timeSplitArray[0] + ":" + timeSplitArray[1] + " PM  ");
                }
                // calenderViewHolderItem.eventTime.setText(timeSplitArray[0] + ":" + timeSplitArray[1] + " PM");
                String dateString = "03/26/2012 11:49:00 AM";
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(timeSplit[0]);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String currentDay = new SimpleDateFormat("EEE").format(convertedDate.getTime());
                String currentMonth = new SimpleDateFormat("MMM").format(convertedDate.getTime());
                String day = new SimpleDateFormat("dd").format(convertedDate.getTime());
                System.out.println(convertedDate);

                calenderViewHolderItem.eventDate.setText(day + " " + currentDay);

                isAppintments = true;

            }
            if (!time.equals("")) {
                String[] timeSplit = time.split("T");
                String dateString = "03/26/2012 11:49:00 AM";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date convertedDate = new Date();
                try {

                    convertedDate = format.parse(time);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String currentDay = new SimpleDateFormat("EEE").format(convertedDate.getTime());
                String currentMonth = new SimpleDateFormat("MMM").format(convertedDate.getTime());
                String day = new SimpleDateFormat("dd").format(convertedDate.getTime());
                System.out.println(convertedDate);
                calenderViewHolderItem.eventDate.setText(day + " " + currentDay);
                try {
                    JSONObject eventjsonObject = new JSONObject(calenderItem.jsonObjectResponse);
                    endDate = eventjsonObject.getString("end");
                    String[] endtimeSplit = endDate.split("T");
                    splitJsonEndTime = endtimeSplit[1].split(":");
                    endTime = Integer.parseInt(splitJsonEndTime[0]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (calenderItem.mIsEvent) {
                    String wrap = timeSplit[1];
                    String[] startTime = wrap.split(":");
                    int timeComapare = Integer.parseInt(startTime[0]);
                    if (timeComapare < 12 && endTime < 12) {
                        calenderViewHolderItem.eventTime.setText(startTime[0] + ":" + startTime[1] + " - " + splitJsonEndTime[0] + ":" + splitJsonEndTime[1]);
                    } else if (timeComapare >= 12 && endTime < 12) {
                        calenderViewHolderItem.eventTime.setText(startTime[0] + ":" + startTime[1] + " - " + splitJsonEndTime[0] + ":" + splitJsonEndTime[1]);

                    } else if (timeComapare >= 12 && endTime >= 12) {
                        calenderViewHolderItem.eventTime.setText(startTime[0] + ":" + startTime[1] + " - " + splitJsonEndTime[0] + ":" + splitJsonEndTime[1]);

                    } else if (timeComapare < 12 && endTime >= 12) {
                        calenderViewHolderItem.eventTime.setText(startTime[0] + ":" + startTime[1] + " - " + splitJsonEndTime[0] + ":" + splitJsonEndTime[1]);

                    }

                }
            } else {
                String dateString = "03/26/2012 11:49:00 AM";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date convertedDate = new Date();
                try {

                    convertedDate = format.parse(calenderItem.mEventdate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String currentDay = new SimpleDateFormat("EEE").format(convertedDate.getTime());
                String currentMonth = new SimpleDateFormat("MMM").format(convertedDate.getTime());
                String day = new SimpleDateFormat("dd").format(convertedDate.getTime());
                System.out.println(convertedDate);
                if (isAppintments == false) {
                    calenderViewHolderItem.eventDate.setText(day + " " + currentDay);
                }
                Glide.with(mContext).load(Constants.BASEURL + mCalenderItemArrayList.get(i).mImageUrl).into(calenderViewHolderItem.userImage);
            }

            if (calenderItem.mIsEvent) {
                calenderViewHolderItem.address.setVisibility(View.VISIBLE);
                calenderViewHolderItem.userImage.setBorderColor(Color.parseColor("#027CBA"));

                calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                String addressOfEvent = Html.fromHtml(calenderItem.address).toString().trim();
                addressOfEvent = addressOfEvent.replaceAll("h2", "h7");
                calenderViewHolderItem.address.setText(addressOfEvent);
                String itemDate1 = calenderItem.mEventdate;

                String eventdate;
                String[] split = itemDate1.split("T");
                eventdate = split[0];


                String[] splitDate = eventdate.split("-");

                eventdate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();


                String selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                String currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                String currentDay = new SimpleDateFormat("dd").format(cal.getTime());
                String currentDate = currentDay + "-" + selectedMonthIndex + "-" + currentYear;
                try {
                    date1 = sdf.parse(eventdate);
                    date2 = sdf.parse(currentDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                if (date1.compareTo(date2) > 0) {
                    //Future event

                    calenderViewHolderItem.attendingOrNotButton.setBackgroundResource(R.drawable.attending_background);
                    if (calenderItem.mRsvpState.equals("not_attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attend");
                    } else if (calenderItem.mRsvpState.equals("attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attending");
                    } else if (calenderItem.mRsvpState.equals("attended")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attended");
                    }

                } else if (date1.compareTo(date2) < 0) {
                    //past event
                    calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                    if (calenderItem.mRsvpState.equals("not_attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attend");
                    } else if (calenderItem.mRsvpState.equals("attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attending");
                    } else if (calenderItem.mRsvpState.equals("attended")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Checked In");
                    }

                } else if (date1.compareTo(date2) == 0) {
                    //current daye event
                    calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                    calenderViewHolderItem.attendingOrNotButton.setBackgroundResource(R.drawable.attending_background);
                    if (calenderItem.mRsvpState.equals("not_attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Check IN");

                    } else if (calenderItem.mRsvpState.equals("attending")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Check In");
                    } else if (calenderItem.mRsvpState.equals("attended")) {
                        calenderViewHolderItem.attendingOrNotButton.setVisibility(View.VISIBLE);
                        calenderViewHolderItem.attendingOrNotButton.setText("Attended");
                    }

                }
                calenderViewHolderItem.relativeLayout.setBackgroundColor(Color.parseColor("#4db1f7"));
                calenderViewHolderItem.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CalenderItem calItem = mCalenderItemArrayList.get(i);
                        if (calItem.mIsEvent) {
                            MainActivity.isInternetConnection = false;
                            Intent intent = new Intent(mContext, EventDetailActivity.class);
                            intent.putExtra("id", calenderItem.mEventId);
                            mContext.startActivity(intent);
                        }


                    }
                });

                Glide.with(mContext).load(Constants.BASEURL + mCalenderItemArrayList.get(i).mImageUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.trainingtcon))
                        .apply(RequestOptions.errorOf(R.drawable.trainingtcon))
                        .into(calenderViewHolderItem.userImage);


            } else if (calenderItem.mIsAppointsments) {
                calenderViewHolderItem.address.setVisibility(View.GONE);
                calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                calenderViewHolderItem.userImage.setBackgroundResource(R.drawable.appointments);
                calenderViewHolderItem.userImage.setBorderColor(Color.parseColor("#ffbe10"));
                calenderViewHolderItem.relativeLayout.setBackgroundColor(Color.parseColor("#ffbe10"));
                calenderViewHolderItem.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
                        Uri uri = ContentUris.withAppendedId(CALENDAR_URI, Long.parseLong(eventId));
                        if (calenderItem.mIsAppointsments) {
                            MainActivity.isInternetConnection = false;
                            CalenderItem calenderItem1 = mCalenderItemArrayList.get(i);
                            Intent intent = new Intent(Intent.ACTION_EDIT)
                                    .setType("vnd.android.cursor.item/event")
                                    .putExtra(CalendarContract.Events.TITLE, calenderViewHolderItem.eventName.getText().toString());

                            mContext.startActivity(intent);

                        }
                    }
                });
                Glide.with(mContext).load(Constants.BASEURL + mCalenderItemArrayList.get(i).mImageUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.appointments))
                        .apply(RequestOptions.errorOf(R.drawable.appointments))
                        .into(calenderViewHolderItem.userImage);


            } else {
                calenderViewHolderItem.address.setVisibility(View.GONE);
                calenderViewHolderItem.userImage.setBorderColor(Color.parseColor("#82498C"));
                calenderViewHolderItem.eventTime.setText(calenderItem.type);
                if (mCalenderItemArrayList.get(i).mImageUrl.equals("")) {
                    calenderViewHolderItem.userImage.setBackgroundResource(R.drawable.circle_birthday);
                }
                //Picasso.with(mContext).load(Constants.BASEURL + mCalenderItemArrayList.get(i).mImageUrl).into(calenderViewHolderItem.userImage);
                calenderViewHolderItem.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.isInternetConnection = false;
                        Intent intent = new Intent(mContext, BirthdayWishActivity.class);
                        intent.putExtra("selected_announcement_json", calenderItem.jsonObjectResponse);
                        intent.putExtra("type", calenderItem.type);
                        intent.putExtra("email", calenderItem.email);
                        intent.putExtra("image", calenderItem.mImageUrl);
                        intent.putExtra("istoday", false);
                        mContext.startActivity(intent);
                    }
                });
                calenderViewHolderItem.attendingOrNotButton.setVisibility(View.GONE);
                calenderViewHolderItem.relativeLayout.setBackgroundColor(Color.parseColor("#ba68c8"));
            }

        }
        return view;

    }


    public class CalenderViewHolderItem

    {

        RelativeLayout relativeLayout;
        CircleImageView userImage;
        TextView eventName;
        TextView eventTime;
        TextView eventDate;
        Button attendingOrNotButton;
        RelativeLayout containerView;
        TextView address;


    }

    public interface UpdateHeaderdate {
        public void updateText(int index);
    }

}

