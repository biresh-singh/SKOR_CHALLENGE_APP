package adaptor;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.event.EventDetailActivity;
import activity.history.AnnouncementDetailActivity;
import activity.history.BirthdayWishActivity;
import activity.userprofile.MainActivity;
import bean.CalenderUtility;
import constants.Constants;
import database.SharedDatabase;
import fragment.NavigationDrawerFragment;
import fragment.WhatsOnFragment;
import utils.AppController;

public class WhatsOnListAdapter extends ArrayAdapter<String> {
    Context mContext;
    LayoutInflater layoutInflater;
    TextView headerTitle;
    LinearLayout linearLayout;
    boolean readmorebutton = false;
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    ArrayList<String> mKeysArrayList = new ArrayList<>();
    TextView defaultText;
    TextView allItemsTextView;
    String eventId;
    ArrayList<String> eventsIds = new ArrayList<>();
    String[] colors = {"#42d583", "#ba68c8", "#4db1f7", "#ffbe10"};
    CheckInternetConnection checkInternetConnection;
    SharedDatabase sharedDatabase;
    String[] headerTitles = {"Events & Training", "Announcements", "Greetings", "Appointsments"};
    int screenWidth = 0;

    public WhatsOnListAdapter(Context context, int textViewResourceId, ArrayList<String> keysArrayList) {
        super(context, textViewResourceId, keysArrayList);
        mContext = context;
        sharedDatabase = new SharedDatabase(context);
        mKeysArrayList = keysArrayList;
        for (int i = 0; i < mKeysArrayList.size(); ++i) {
            mIdMap.put(mKeysArrayList.get(i), i);
        }
        checkInternetConnection = new CheckInternetConnection(mContext);
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Display display = ((MainActivity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;


    }

    @Override
    public int getCount() {
        return mKeysArrayList.size();
    }


    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return -1;
        }
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (screenWidth > 600) {
            view = layoutInflater.inflate(R.layout.whats_on_adapter, null);
        } else {
            view = layoutInflater.inflate(R.layout.whats_on_adapter_smaller, null);
        }

        int s = view.getHeight();

        linearLayout = (LinearLayout) view.findViewById(R.id.parentlayout);
        headerTitle = (TextView) view.findViewById(R.id.header_title);
        allItemsTextView = (TextView) view.findViewById(R.id.all_items);
        headerTitle.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));


        defaultText = (TextView) view.findViewById(R.id.defaulttext);
        ImageView headerImage = (ImageView) view.findViewById(R.id.header_image);
        try {
            if (mKeysArrayList.get(position).equals("greetings")) {
                linearLayout.setBackgroundColor(Color.parseColor(colors[1]));
                headerTitle.setText(mKeysArrayList.get(position).toUpperCase());
                headerImage.setBackgroundResource(R.drawable.birthday);
                JSONArray jsonArray = null;

                jsonArray = WhatsOnFragment.hashMap.get(mKeysArrayList.get(position));

                if (jsonArray.length() > 0) {
                    defaultText.setVisibility(View.VISIBLE);
                    defaultText.setText("No Greetings for today");
                } else {
                    defaultText.setVisibility(View.VISIBLE);
                }
                JSONArray birthdayJsonArray;
                JSONObject greetingJsonObject = jsonArray.getJSONObject(0);
                if (greetingJsonObject.has("Birthday")) {
                    birthdayJsonArray = greetingJsonObject.getJSONArray("Birthday");
                    View birthdaydynamicView = createDynamiclayoutforGreetings(birthdayJsonArray, birthdayJsonArray.length(), false);
                    linearLayout.addView(birthdaydynamicView);
                }
                if (greetingJsonObject.has("Anniversary")) {
                    birthdayJsonArray = greetingJsonObject.getJSONArray("Anniversary");
                    View birthdaydynamicView = createDynamiclayoutforGreetings(birthdayJsonArray, birthdayJsonArray.length(), false);
                    linearLayout.addView(birthdaydynamicView);


                }
                allItemsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                        navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) mContext;
                        navigataionCallback.onNavigationDrawerItemSelected(3, "greetings");
                    }
                });


            } else if (mKeysArrayList.get(position).equals("Appointments")) {
               /* defaultText.setVisibility(View.GONE);*/
                headerTitle.setText(mKeysArrayList.get(position).toUpperCase());
                headerImage.setBackgroundResource(R.drawable.appointments);

                if (WhatsOnFragment.calenderEvents.size() > 0) {

                    defaultText.setVisibility(View.GONE);
                    defaultText.setText("No Greetings today");
                } else {
                    defaultText.setVisibility(View.VISIBLE);
                    defaultText.setText("No Appointments today");

                }

                int length = 0;
                if (WhatsOnFragment.calenderEvents.size() < 2) {
                    length = WhatsOnFragment.calenderEvents.size();
                } else {
                    length = 2;
                }
                for (int i = 0; i < length; i++) {
                    if (WhatsOnFragment.calenderEvents.size() > 2) {
                        allItemsTextView.setVisibility(View.VISIBLE);
                    }
                    View greetingsdynamicView = createDynamicLayoutForAppointments(i);
                    linearLayout.addView(greetingsdynamicView);
                    allItemsTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                            navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) mContext;
                            navigataionCallback.onNavigationDrawerItemSelected(3, "appointments");
                        }
                    });


                }
                linearLayout.setBackgroundColor(Color.parseColor(colors[3]));
                if (WhatsOnFragment.calenderEvents.size() == 0) {


                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }
                    });

                }
            } else {
                defaultText.setVisibility(View.GONE);


                // JSONObject jsonObject = mJsonObject.getJSONObject("events");
                JSONArray jsonArray = null;

                jsonArray = WhatsOnFragment.hashMap.get(mKeysArrayList.get(position));

                if (jsonArray.length() > 0) {
                    defaultText.setVisibility(View.GONE);
                } else {
                    defaultText.setVisibility(View.VISIBLE);

                }
                if (mKeysArrayList.get(position).equals("Announcement")) {
                    headerTitle.setText("ANNOUNCEMENTS");
                    linearLayout.setBackgroundColor(Color.parseColor(colors[0]));
                    headerImage.setBackgroundResource(R.drawable.announcements);
                    defaultText.setText("No Announcements for today");
                } else {
                    headerTitle.setText("ACTIVITIES AND EVENTS");
                    linearLayout.setBackgroundColor(Color.parseColor(colors[2]));
                    defaultText.setText("No Events or Training for today");
                }

                View dynamicView = null;
                int length = 0;
                if (jsonArray.length() < 2) {
                    length = jsonArray.length();
                } else {
                    length = 2;
                }
                for (int j = 0; j < length; j++) {
                    final JSONObject mJson = jsonArray.getJSONObject(j);

                    if (mKeysArrayList.get(position).equals("Announcement")) {
                        dynamicView = createDynamiclayoutforAnnouncement(mJson, false);
                        headerImage.setBackgroundResource(R.drawable.announcements);
                        dynamicView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(mContext, AnnouncementDetailActivity.class);
                                intent.putExtra("selected_announcement_json", mJson.toString());
                                mContext.startActivity(intent);

                            }
                        });
                        linearLayout.setBackgroundColor(Color.parseColor(colors[0]));

                    } else if (mKeysArrayList.get(position).equals("Event")) {
                        if (jsonArray.length() > 2) {
                            allItemsTextView.setVisibility(View.VISIBLE);
                        }
                        dynamicView = createDynamiclayoutforEvents(mJson, true, jsonArray, j);
                        headerImage.setBackgroundResource(R.drawable.trainingtcon);
                        linearLayout.setBackgroundColor(Color.parseColor(colors[2]));
                        allItemsTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                                navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) mContext;
                                navigataionCallback.onNavigationDrawerItemSelected(3, "events");
                            }
                        });


                    }
                    linearLayout.addView(dynamicView);
                }

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        // }
        return view;
    }

    public View createDynamicLayoutForAppointments(int position) {
        View view = layoutInflater.inflate(R.layout.event_list_item, null);
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parentlayout_list_item);
        TextView headerSubTitleFirst = (TextView) view.findViewById(R.id.header_subtitle);
        TextView headerTitleSecond = (TextView) view.findViewById(R.id.subtitle);
        de.hdodenhof.circleimageview.CircleImageView headerSubImage = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.header_subimage);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        final String name = WhatsOnFragment.calenderEvents.get(position);
        ArrayList<String> eventsLocation = CalenderUtility.eventLocation;
        ArrayList<String> startdateArray = CalenderUtility.startDates;
        eventsIds = CalenderUtility.eventIds;
        eventId = eventsIds.get(position);
        String eventTime = startdateArray.get(position);

        String[] splitTime = startdateArray.get(position).split(" ");
        if (eventTime.contains("a") || eventTime.contains("A")) {
            headerTitleSecond.setText(splitTime[1] + " AM  ");
        } else {
            headerTitleSecond.setText(splitTime[1] + " PM  ");
        }
        headerSubImage.setVisibility(View.INVISIBLE);

        headerSubTitleFirst.setText(name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
                Uri uri = ContentUris.withAppendedId(CALENDAR_URI, Long.parseLong(eventId));
                AppController.getInstance().getMixpanelAPI().track("AppointmentDetail");
                Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setData(uri)
                        .setType("vnd.android.cursor.item/event")
                        .putExtra(CalendarContract.Events.TITLE, name);
                mContext.startActivity(intent);

            }


        });


        return view;
    }

    public View createDynamiclayoutforEvents(final JSONObject value, boolean isEvent, JSONArray jsonArray, int j) {
        View view = layoutInflater.inflate(R.layout.event_list_item, null);
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parentlayout_list_item);
        TextView headerSubTitleFirst = (TextView) view.findViewById(R.id.header_subtitle);
        final TextView readmore = (TextView) view.findViewById(R.id.readmore);

        final TextView headerTitleSecond = (TextView) view.findViewById(R.id.subtitle);
        de.hdodenhof.circleimageview.CircleImageView headerSubImage = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.header_subimage);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        ImageView downArrow = (ImageView) view.findViewById(R.id.downarrow);
        TextView timeEvent = (TextView) view.findViewById(R.id.time_event);
        int value1 = jsonArray.length();

        try {
            headerSubTitleFirst.setText(value.getString("name").toString());
            String startTime = value.getString("start");
            String endTime = value.getString("end");
            String[] splitStartTime = startTime.split("T");
            String[] splitEndTime = endTime.split("T");
            String[] splitStartTimeForMinuts = splitStartTime[1].split(":");
            String[] splitEntTimeForMinuts = splitEndTime[1].split(":");


            if (isEvent) {
                timeEvent.setVisibility(View.VISIBLE);

                int compareStartDate = Integer.parseInt(splitStartTimeForMinuts[0]);
                int compareEndDate = Integer.parseInt(splitEntTimeForMinuts[0]);
                if (compareStartDate < 12 && compareEndDate < 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);
                } else if (compareStartDate >= 12 && compareEndDate < 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                } else if (compareStartDate >= 12 && compareEndDate >= 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                } else if (compareStartDate < 12 && compareEndDate >= 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                }
                // timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]+"  PM");
                headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(value.getString("address")).toString()));
                downArrow.setVisibility(View.GONE);


                if (j == 0) {
                    if (value.getString("type").equals("General Event")) {


                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.general_event);
                        }
                    } else if (value.getString("type").equals("Training Event")) {
                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.training_event);

                        }
                    }
                }
                if (j == 1) {
                    if (value.getString("type").equals("General Event")) {

                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.training_event);
                        }

                    } else if (value.getString("type").equals("Training Event")) {
                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.training_event);

                        }
                    }
                }
                if (jsonArray.length() == 1) {
                    if (value.getString("type").equals("General Event")) {

                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.general_event);
                        }

                    } else if (value.getString("type").equals("Training Event")) {
                        if (!value.getString("thumbnail").equals("")) {
                            Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                        } else {
                            headerSubImage.setImageResource(R.drawable.training_event);

                        }
                    }
                }
                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(mContext, EventDetailActivity.class);
                            intent.putExtra("id", value.getString("id"));
                            mContext.startActivity(intent);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            } else {
                final String headerAnouncementDescription = value.getString("description");
                final String headerAnouncementDescriptionlength = value.getString("description");

                timeEvent.setVisibility(View.GONE);
                if (headerAnouncementDescription.length() <= 100) {
                    headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(value.getString("description")).toString()));
                } else {
                    headerAnouncementDescription.replace("<\\/p>", "</p>");
                    headerAnouncementDescription.replaceAll("\n", " ");
                    headerAnouncementDescription.replaceAll("&nbsp;", " ");
                    headerAnouncementDescription.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                    headerAnouncementDescription.replaceAll("\r\n", " ");
                    headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescription.substring(0, 90)).toString()).toString() + "...");
                    readmore.setVisibility(View.VISIBLE);
                }
                downArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String readmoretext = "Read More";
                        SpannableString content = new SpannableString(readmoretext);

                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        readmore.setText(content);
                        if (headerAnouncementDescription.length() > 100) {
                            headerAnouncementDescriptionlength.replace("<\\/p>", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\n", " ");
                            headerAnouncementDescriptionlength.replaceAll("&nbsp;", " ");
                            headerAnouncementDescriptionlength.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\r\n", " ");
                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescriptionlength.substring(0, 90)).toString()).toString() + "...");
                            readmore.setVisibility(View.VISIBLE);
                        }

                    }
                });

                String readmoretext = "Read More";
                SpannableString content = new SpannableString(readmoretext);

                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                readmore.setText(content);
                readmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!readmorebutton) {
                            String readmoretext = "Read Less";
                            SpannableString content = new SpannableString(readmoretext);

                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            readmore.setText(content);
                            readmorebutton = true;
                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescription).toString()));
                        } else {
                            String readmoretext = "Read More";
                            SpannableString content = new SpannableString(readmoretext);

                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            readmore.setText(content);
                            readmorebutton = false;
                            headerAnouncementDescriptionlength.replace("<\\/p>", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\n", " ");
                            headerAnouncementDescriptionlength.replaceAll("&nbsp;", " ");
                            headerAnouncementDescriptionlength.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\r\n", " ");

                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescriptionlength.substring(0, 90)).toString()).toString() + "...");


                        }


                    }
                });
                //headerTitleSecond.setText(value.getString("description"));
                relativeLayout.setBackgroundResource(R.drawable.textbox);
                downArrow.setVisibility(View.VISIBLE);

                // Picasso.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);

            }


        } catch (JSONException ex)

        {
            ex.printStackTrace();
        }


        return view;
    }

    public View createDynamiclayoutforAnnouncement(final JSONObject value, boolean isEvent) {
        View view = layoutInflater.inflate(R.layout.event_list_item, null);
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parentlayout_list_item);
        TextView headerSubTitleFirst = (TextView) view.findViewById(R.id.header_subtitle);
        final TextView readmore = (TextView) view.findViewById(R.id.readmore);

        final TextView headerTitleSecond = (TextView) view.findViewById(R.id.subtitle);
        de.hdodenhof.circleimageview.CircleImageView headerSubImage = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.header_subimage);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        ImageView downArrow = (ImageView) view.findViewById(R.id.downarrow);
        TextView timeEvent = (TextView) view.findViewById(R.id.time_event);

        try {
            headerSubTitleFirst.setText(value.getString("name").toString());
            String startTime = value.getString("start");
            String endTime = value.getString("end");
            String[] splitStartTime = startTime.split("T");
            String[] splitEndTime = endTime.split("T");
            String[] splitStartTimeForMinuts = splitStartTime[1].split(":");
            String[] splitEntTimeForMinuts = splitEndTime[1].split(":");


            if (isEvent) {
                timeEvent.setVisibility(View.VISIBLE);

                int compareStartDate = Integer.parseInt(splitStartTimeForMinuts[0]);
                int compareEndDate = Integer.parseInt(splitEntTimeForMinuts[0]);
                if (compareStartDate < 12 && compareEndDate < 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);
                } else if (compareStartDate >= 12 && compareEndDate < 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                } else if (compareStartDate >= 12 && compareEndDate >= 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                } else if (compareStartDate < 12 && compareEndDate >= 12) {
                    timeEvent.setText(splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1] + " - " + splitEntTimeForMinuts[0] + ":" + splitEntTimeForMinuts[1]);

                }
                headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(value.getString("address")).toString()));
                downArrow.setVisibility(View.GONE);
                ArrayList<String> imageViewArrayListFlag = new ArrayList<>();
                imageViewArrayListFlag.add(value.getString("thumbnail"));
                if (value.getString("type").equals("General Event")) {
                    if (!value.getString("thumbnail").equals("")) {
                        Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                    } else {
                        headerSubImage.setImageResource(R.drawable.general_event);
                    }

                    if (!value.getString("thumbnail").equals("")) {
                        Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                    } else {
                        headerSubImage.setImageResource(R.drawable.training_event);
                    }

                } else if (value.getString("type").equals("Training Event")) {
                    if (!value.getString("thumbnail").equals("")) {
                        Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);
                    } else {
                        headerSubImage.setImageResource(R.drawable.training_event);

                    }
                }
                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(mContext, EventDetailActivity.class);
                            intent.putExtra("id", value.getString("id"));
                            mContext.startActivity(intent);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            } else {
                final String headerAnouncementDescription = value.getString("description");
                final String headerAnouncementDescriptionlength = value.getString("description");

                timeEvent.setVisibility(View.GONE);
                if (headerAnouncementDescription.length() <= 100) {
                    headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(value.getString("description")).toString()));
                } else {
                    headerAnouncementDescription.replace("<\\/p>", "</p>");
                    headerAnouncementDescription.replaceAll("\n", " ");
                    headerAnouncementDescription.replaceAll("&nbsp;", " ");
                    headerAnouncementDescription.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                    headerAnouncementDescription.replaceAll("\r\n", " ");
                    headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescription.substring(0, 90)).toString()).toString() + "...");
                    readmore.setVisibility(View.VISIBLE);
                }
                downArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String readmoretext = "Read More";
                        SpannableString content = new SpannableString(readmoretext);

                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        readmore.setText(content);
                        if (headerAnouncementDescription.length() > 100) {
                            headerAnouncementDescriptionlength.replace("<\\/p>", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\n", " ");
                            headerAnouncementDescriptionlength.replaceAll("&nbsp;", " ");
                            headerAnouncementDescriptionlength.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\r\n", " ");
                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescriptionlength.substring(0, 90)).toString()).toString() + "...");
                            readmore.setVisibility(View.VISIBLE);
                        }

                    }
                });

                String readmoretext = "Read More";
                SpannableString content = new SpannableString(readmoretext);

                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                readmore.setText(content);
                readmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!readmorebutton) {
                            String readmoretext = "Read Less";
                            SpannableString content = new SpannableString(readmoretext);

                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            readmore.setText(content);
                            readmorebutton = true;
                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescription).toString()));
                        } else {
                            String readmoretext = "Read More";
                            SpannableString content = new SpannableString(readmoretext);

                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            readmore.setText(content);
                            readmorebutton = false;
                            headerAnouncementDescriptionlength.replace("<\\/p>", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\n", " ");
                            headerAnouncementDescriptionlength.replaceAll("&nbsp;", " ");
                            headerAnouncementDescriptionlength.replaceAll("</p>\r\n<p></p>\r\n", "</p>");
                            headerAnouncementDescriptionlength.replaceAll("\r\n", " ");

                            headerTitleSecond.setText(Html.fromHtml(Html.fromHtml(headerAnouncementDescriptionlength.substring(0, 90)).toString()).toString() + "...");


                        }


                    }
                });
                relativeLayout.setBackgroundResource(R.drawable.textbox);
                downArrow.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(Constants.BASEURL + value.getString("thumbnail")).into(headerSubImage);

            }


        } catch (JSONException ex)

        {
            ex.printStackTrace();
        }


        return view;
    }

    public View createDynamiclayoutforGreetings(JSONArray jsonArray, int length, boolean isBirhtday) {
        View view = layoutInflater.inflate(R.layout.event_list_item, null);
        TextView headerSubTitleFirst = (TextView) view.findViewById(R.id.header_subtitle);
        TextView headerTitleSecond = (TextView) view.findViewById(R.id.subtitle);
        headerTitleSecond.setVisibility(View.GONE);
        ImageView headerSubImage = (ImageView) view.findViewById(R.id.header_subimage);
        headerSubImage.setVisibility(View.INVISIBLE);
        ArrayList<String> emailArrayList = new ArrayList<>();
        ArrayList<String> typeArrayList = new ArrayList<>();
        String type = "";
        String emailloginUser = sharedDatabase.getEmail();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String email = jsonObject.getString("user_email");
                type = jsonObject.getString("type");
                typeArrayList.add(type);
                emailArrayList.add(email);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        if (!emailArrayList.equals("null")) {
            if (emailArrayList.size() < 2) {
                if (emailArrayList.contains(emailloginUser)) {
                    headerSubTitleFirst.setText("It's your " + type + " Today.");

                } else {
                    headerSubTitleFirst.setText(String.valueOf(length) + " Colleague " + type + " Today.");
                }
            }
            if (emailArrayList.size() == 2) {
                if (emailArrayList.contains(emailloginUser)) {
                    headerSubTitleFirst.setText("It's your and 1 colleague " + type + " Today.");

                } else {
                    headerSubTitleFirst.setText(String.valueOf(length) + " Colleague's " + type + " Today.");

                }

            }
            if (emailArrayList.size() > 2) {
                if (emailArrayList.contains(emailloginUser)) {
                    headerSubTitleFirst.setText("It's your and " + String.valueOf(length - 1) + " colleague's " + type + " Today.");

                } else {
                    headerSubTitleFirst.setText(String.valueOf(length) + " Colleague's " + type + " Today.");

                }
            }

        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.user_image_layout);
        linearLayout.setVisibility(View.VISIBLE);

        if (jsonArray.length() > 2) {
            allItemsTextView.setVisibility(View.VISIBLE);

        }
        int sizeOgGreetingArray = 0;

        if (jsonArray.length() > 4) {
            sizeOgGreetingArray = 4;

        } else {
            sizeOgGreetingArray = jsonArray.length();
        }
        for (int i = 0; i < sizeOgGreetingArray; i++) {
            try {

                defaultText.setVisibility(View.GONE);
                LinearLayout linearLayout1 = new LinearLayout(mContext.getApplicationContext());
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                de.hdodenhof.circleimageview.CircleImageView userImage = new de.hdodenhof.circleimageview.CircleImageView(mContext, null);
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                buttonLayoutParams.setMargins(10, 0, 0, 15);
                String thumbnailUrl = jsonObject.getString("thumbnail");

                userImage.setLayoutParams(buttonLayoutParams);
                if (thumbnailUrl.equals("")) {
                    userImage.setBackgroundResource(R.drawable.default_user);
                } else {
                    Glide.with(mContext).load(Constants.BASEURL + jsonObject.getString("thumbnail")).into(userImage);
                }
                linearLayout1.addView(userImage);

                userImage.getLayoutParams().height = 90;
                userImage.getLayoutParams().width = 90;
                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, BirthdayWishActivity.class);
                        intent.putExtra("selected_announcement_json", jsonObject.toString());
                        intent.putExtra("istoday", true);
                        mContext.startActivity(intent);
                    }
                });

                userImage.requestLayout();
                linearLayout.addView(linearLayout1);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        if (jsonArray.length() > 4) {

            TextView joiningCircle = new TextView(getContext());

            joiningCircle.setTextColor(Color.WHITE);
            joiningCircle.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(10, 0, 0, 15);
            joiningCircle.setLayoutParams(buttonLayoutParams);
            joiningCircle.setBackgroundResource(R.drawable.joining_circle);
            int remainingItems = jsonArray.length() - 4;
            joiningCircle.setText("+" + String.valueOf(remainingItems));
            linearLayout.addView(joiningCircle);
            joiningCircle.getLayoutParams().height = 100;
            joiningCircle.getLayoutParams().width = 100;
            joiningCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) mContext;
                    navigataionCallback.onNavigationDrawerItemSelected(3, "greetings");
                }
            });
        }


        return view;
    }
}
