package activity.history;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.ActivityHistoryListAdapter;
import bean.HistoryItem;
import constants.Constants;
import database.SharedDatabase;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import event.RefreshTokenEvent;
import fragment.PointsSummeryFragments;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;

public class ActivityHistoryActivity extends Activity {
    ListView historyListview;
    boolean isMonthly = false;
    ArrayList<HistoryItem> getCalenderItemArrayList = new ArrayList<>();
    ArrayList<String> dateArrayForSection = new ArrayList<>();
    public static ArrayList<HistoryItem> historyItemArrayList = new ArrayList<>();
    String currentMonth;
    TextView week, month;
    String activityType;
    TextView activityTypeTextView;
    ImageView activityTypeImageView;
    LinearLayout activityLayout;
    LinearLayout back;
    String slug;
    int totalPoints;
    TextView todaysTotalPoints;
    TextView totalPointsTextView;
    String facility_location_name;
    RelativeLayout defaultLayout;
    Button buttonFirst;
    Button buttonSecond;
    Date previous_date1;
    Date previous_date2;
    TextView defaultTextView;
    LinearLayout previousWeek, nextWeek;
    TextView weekDate;
    String newStartDate;
    boolean isMonthChanged = false;
    Date date = null;
    String endDate;
    CheckInternetConnection checkInternetConnection;
    TextView datetime;
    String firstDateOfweek = null;
    Date lastUpdatedDate = null;
    SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public String token;
    public SharedDatabase sharedDatabase;

    private ArrayList<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        historyListview = (ListView) findViewById(R.id.list);
        historyListview.setDivider(null);
        week = (TextView) findViewById(R.id.week);
        month = (TextView) findViewById(R.id.month);
        activityTypeTextView = (TextView) findViewById(R.id.activity_type);
        activityLayout = (LinearLayout) findViewById(R.id.activity_layout);
        back = (LinearLayout) findViewById(R.id.back);
        defaultLayout = (RelativeLayout) findViewById(R.id.default_layout);
        buttonFirst = (Button) findViewById(R.id.button_first);
        buttonSecond = (Button) findViewById(R.id.button_second);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        defaultTextView = (TextView) findViewById(R.id.default_text);
        totalPointsTextView = (TextView) findViewById(R.id.total_points);
        activityTypeImageView = (ImageView) findViewById(R.id.activity_type_image);
        todaysTotalPoints = (TextView) findViewById(R.id.todays_total_points);
        previousWeek = (LinearLayout) findViewById(R.id.previousweek);
        nextWeek = (LinearLayout) findViewById(R.id.nextweek);
        weekDate = (TextView) findViewById(R.id.weekdate);
        datetime = (TextView) findViewById(R.id.date_time);
        todaysTotalPoints.setText(PointsSummeryFragments.totalPointsEraned1 + " pts");
        activityLayout.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("color")));
        activityTypeTextView.setText(getIntent().getStringExtra("name"));
        activityType = getIntent().getStringExtra("name");
        slug = getIntent().getStringExtra("slug");
        Glide.with(getApplicationContext()).load(Constants.BASEURL + getIntent().getStringExtra("icon")).into(activityTypeImageView);

        Calendar cal = Calendar.getInstance();

        currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
        date = new Date();
        lastUpdatedDate = new Date();


        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, -(today + 1) + Calendar.MONDAY);
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        Date previous_date = calendar.getTime();
        date = previous_date;
        previous_date1 = date;
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        Date date1 = new Date();
        datetime.setText("Total Points as " + dateFormat.format(date));
        calendar.add(Calendar.DATE, 6);
        Date lastdate = new Date();
        lastdate.setTime(calendar.getTime().getTime());
        endDate = String.valueOf(lastdate.getDate());
        lastUpdatedDate = lastdate;
        previous_date2 = lastdate;
        String prevDate = mFormatter.format(date);
        firstDateOfweek = prevDate;
        weekDate.setText(String.valueOf(date.getDate()) + " " + new SimpleDateFormat("MMM").format(date) + "-" + lastdate.getDate() + " " + new SimpleDateFormat("MMM").format(lastdate));
        currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
        dateArrayForSection.add(currentMonth);
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callHistoryApi(date);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }

        /* *********************clciking listener to go weekview*************************/

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {

                    try {

                        isMonthly = false;

                        week.setBackgroundResource(R.drawable.text_bg);
                        week.setTextColor(Color.BLACK);
                        month.setBackgroundColor(Color.TRANSPARENT);
                        month.setTextColor(Color.parseColor("#dfdfdf"));
                        date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setFirstDayOfWeek(Calendar.MONDAY);
                        c.setTime(previous_date1);
                        int today = c.get(Calendar.DAY_OF_WEEK);
                        c.add(Calendar.DAY_OF_WEEK, -(today + 1) + Calendar.MONDAY);
                        System.out.println("Date KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK" + c.getTime().getDate());
                        previous_date1.setTime(c.getTime().getTime());
                        c.add(Calendar.DATE, 6);
                        Date lastdate = new Date();
                        lastdate.setTime(c.getTime().getTime());
                        weekDate.setText(String.valueOf(previous_date1.getDate()) + " " + new SimpleDateFormat("MMM").format(previous_date1) + "-" + lastdate.getDate() + " " + new SimpleDateFormat("MMM").format(lastdate));
                        date = previous_date1;
                        callHistoryApi(date);
                        lastUpdatedDate = new Date();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }


            }
        });

        /* ********************clicking listener goint ot month view**************************/

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {

                        isMonthly = true;
                        month.setBackgroundResource(R.drawable.text_bg);
                        month.setTextColor(Color.BLACK);
                        week.setBackgroundColor(Color.TRANSPARENT);
                        week.setTextColor(Color.parseColor("#dfdfdf"));
                        String dateStart = mFormatter.format(date);
                        String[] splitDate = dateStart.split("-");
                        newStartDate = splitDate[0] + "-" + splitDate[1] + "-" + "01";
                        endDate = mFormatter.format(new Date());
                        String[] splitEnddate = endDate.split("-");
                        Calendar calendar = Calendar.getInstance();

                        calendar.setTime(date);

                        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        String endDate = splitDate[0] + "-" + splitDate[1] + "-" + String.valueOf(max);
                        // Use the date formatter to produce a formatted date string
                        Date previousDate = calendar.getTime();
                        //callHistoryApi(newStartDate,endDate);
                        Calendar cal = Calendar.getInstance();
                        weekDate.setText("01" + " " + new SimpleDateFormat("MMM").format(calendar.getTime()) + "-" + max + " " + new SimpleDateFormat("MMM").format(calendar.getTime()));
                        callHistoryApi(previousDate);
                        lastUpdatedDate = new Date();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }
            }
        });

        /* ********************to clicking going to previous week and month**************************/

        previousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        if (isMonthly) {
                            isMonthChanged = true;
                            callApiForPreviousMonth(false);
                        } else {
                            callWeeklyApi(false);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }
            }
        });



        /* *****************clicking going to next week and  month*****************************/

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {

                    try {
                        Date firstDayOfweek = mFormatter.parse(firstDateOfweek);

                        String date1 = mFormatter.format(date);

                        lastUpdatedDate = mFormatter.parse(date1);
                        //currentDate=firstDayOfweek;
                        int value = lastUpdatedDate.compareTo(firstDayOfweek);

                        if (value == 0 || value == 1) {

                        } else {
                            if (isMonthly) {
                                isMonthChanged = true;
                                callApiForPreviousMonth(true);

                            } else {
                                isMonthChanged = false;
                                callWeeklyApi(true);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_POINT_SUMMERY);
                MainActivity.isInternetConnection = false;
                startActivity(intent);
                finish();

            }
        });

        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                if (slug.equals("healthy-activities")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_CONNECTED_APPS);

                    startActivity(intent);
                    finish();
                } else if (slug.equals("referral")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_REFER);

                    startActivity(intent);
                    finish();
                } else if (slug.equals("check-in-events")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_EVENT_CHECKIN);

                    startActivity(intent);
                    finish();
                } else if (slug.equals("check-in-events-facilities")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_EVENT_CHECKIN);
                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.isInternetConnection = false;
                if (slug.equals("check-in-events-facilities")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_FACILITY_CHECKIN);

                    startActivity(intent);
                    finish();
                }
                if (slug.equals("healthy-activities")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", Constants.ACTION_CONNECTED_APPS);

                    startActivity(intent);
                    finish();
                }
            }
        });


    }
    /* ********************* select prevoius month or next month *************************/

    @Override
    protected void onResume() {
        super.onResume();
        if (activityType.equalsIgnoreCase("Steps")) {
            AppController.getInstance().getMixpanelAPI().track("StepsHistory");
        } else if (activityType.equalsIgnoreCase("Healthy Activities (Kms)")) {
            AppController.getInstance().getMixpanelAPI().track("HealthyActivitiesHistory");
        } else if (activityType.equalsIgnoreCase("Check in Events & Facilities")) {
            AppController.getInstance().getMixpanelAPI().track("CheckInHistory");
        } else if (activityType.equalsIgnoreCase("Program/Activities")) {
            AppController.getInstance().getMixpanelAPI().track("ProgramHistory");
        } else if (activityType.equalsIgnoreCase("Appreciation")) {
            AppController.getInstance().getMixpanelAPI().track("AppreciationHistory");
        } else if (activityType.equalsIgnoreCase("Referral")) {
            AppController.getInstance().getMixpanelAPI().track("ReferralHistory ");
        } else if (activityType.equalsIgnoreCase("Sales")) {
            AppController.getInstance().getMixpanelAPI().track("SalesHistory");
        }
    }

    public void callApiForPreviousMonth(boolean isNext) {


        Calendar calendar = Calendar.getInstance();
        if (isMonthChanged) {
            calendar.setTime(date);
        }
        if (isNext) {
            calendar.add(Calendar.MONTH, 1);
        } else {
            calendar.add(Calendar.MONTH, -1);
        }

        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Use the date formatter to produce a formatted date string
        Date previousDate = calendar.getTime();

        date.setTime(calendar.getTime().getTime());
        lastUpdatedDate = date;

        endDate = String.valueOf(date.getDate());
        String dateInString = mFormatter.format(date);
        String[] splitDate = dateInString.split("-");
        String startDate = splitDate[0] + "-" + splitDate[1] + "-" + "01";
        String endDate = splitDate[0] + "-" + splitDate[1] + "-" + String.valueOf(max);
        System.out.println("Date KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK" + calendar.getTime().getDate());
        weekDate.setText("01 " + new SimpleDateFormat("MMM").format(calendar.getTime()) + "-" + max + " " + new SimpleDateFormat("MMM").format(calendar.getTime()));

        isMonthly = true;
        callHistoryApi(date);
        previous_date1 = lastUpdatedDate;
    }

    public void callWeeklyApi(boolean isNext) {
        if (isNext) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 0);

            // Use the date formatter to produce a formatted date string
            Date previousDate = calendar.getTime();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 7);
            date.setTime(c.getTime().getTime());
            lastUpdatedDate = date;
            endDate = String.valueOf(date.getDate());
            c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 6);
            Date dateEnd = new Date();
            dateEnd.setTime(c.getTime().getTime());
            previous_date2 = dateEnd;
            System.out.println("Date KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK" + c.getTime().getDate());
            weekDate.setText(String.valueOf(date.getDate()) + " " + new SimpleDateFormat("MMM").format(date) + "-" + dateEnd.getDate() + " " + new SimpleDateFormat("MMM").format(dateEnd));

        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            lastUpdatedDate = date;
            Date previousDate = calendar.getTime();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, -7);
            date.setTime(c.getTime().getTime());
      /*      lastUpdatedDate=date;*/
            endDate = String.valueOf(date.getDate());
            System.out.println("Date KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK" + c.getTime().getDate());

            previous_date2 = calendar.getTime();
            weekDate.setText(String.valueOf(date.getDate()) + " " + new SimpleDateFormat("MMM").format(date) + "-" + previousDate.getDate() + " " + new SimpleDateFormat("MMM").format(previousDate));

        }
        callHistoryApi(date);
        // callHistoryApi(mFormatter.format(previousDate), mFormatter.format(lastdate).toString());
    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#FF9B30")), this);
            }
        } else if (msg.equals("Network Connected")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#4BCC1F")), this);
            }
        } else {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.RED), this);
            }
        }

    }

    /* ********************calling History Api**************************/

    public void callHistoryApi(Date date) {

        totalPoints = 0;
        //

        String url;
        if (isMonthly) {

            String startDate = mFormatter.format(date);
            String[] startDate1 = startDate.split("-");
            startDate = startDate1[0] + "-" + startDate1[1] + "-" + "01";
            startDate = "date=" + startDate;
            url = Constants.BASEURL + Constants.HISTORY_API + "/monthly/?category=" + slug + "&" + startDate;
        } else {
            String startDate = mFormatter.format(date);
            startDate = "date=" + startDate;
            url = Constants.BASEURL + Constants.HISTORY_API + "/weekly/?category=" + slug + "&" + startDate;
        }


        String authProvider = SettingsManager.getInstance().getAuthProvider();
        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);

                Loader.dialogDissmiss(ActivityHistoryActivity.this);
                if (jsonArray.length() == 0) {
                    if (slug.equals("healthy-activities")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("No Data in Healthy Activity.");
                        defaultTextView.setTypeface(null, Typeface.BOLD);
                        buttonFirst.setText("You are not connected to any apps");

                        buttonSecond.setText("Connect");

                        buttonFirst.setVisibility(View.GONE);
                        // buttonSecond.setVisibility(View.GONE);


                    } else if (slug.equals("corporate-program")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("No Data in Programs/Activities.");
                        defaultTextView.setTypeface(null, Typeface.BOLD);
                        buttonFirst.setVisibility(View.GONE);
                        buttonFirst.setBackgroundResource(R.drawable.viewall);

                        buttonSecond.setVisibility(View.GONE);
                    } else if (slug.equals("referral")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("You dont have any referral as of now");
                        buttonFirst.setText("Add Referral");
                        buttonFirst.setBackgroundResource(R.drawable.viewall);

                        buttonSecond.setVisibility(View.GONE);
                    } else if (slug.equals("check-in-events-facilities")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("You have not checked in any facility and events yet.");
                        buttonFirst.setText("Event Check In");
                        buttonFirst.setBackgroundResource(R.drawable.viewall);

                        buttonSecond.setText("Facility Check In");
                        buttonSecond.setVisibility(View.GONE);
                    } else if (slug.equals("check-in-events")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("You have not checked in any events yet.");
                        buttonFirst.setText("Event Check In");
                        buttonFirst.setBackgroundResource(R.drawable.viewall);
                        buttonSecond.setText("Facility Check In");
                        buttonSecond.setVisibility(View.GONE);
                    } else if (slug.equals("employee-rewards")) {
                        defaultLayout.setVisibility(View.VISIBLE);
                        historyListview.setVisibility(View.GONE);
                        defaultTextView.setText("No Data to Display.");
                        defaultTextView.setTextSize(15);
                        defaultTextView.setTypeface(null, Typeface.BOLD);
                        buttonFirst.setText("Connect");
                        buttonFirst.setBackgroundResource(R.drawable.viewall);
                        buttonFirst.setVisibility(View.GONE);
                        buttonSecond.setVisibility(View.GONE);

                    }

                }
                try {
                    historyItemArrayList.clear();
                    dateArrayForSection.clear();

                    getCalenderItemArrayList.clear();
                    sections.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        defaultLayout.setVisibility(View.GONE);
                        historyListview.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject reasonJsonObject = jsonObject.getJSONObject("reason");
                        String name = reasonJsonObject.getString("name");
                        String points = jsonObject.getString("points");
                        totalPoints = totalPoints + Integer.parseInt(points);
                        String date = jsonObject.getString("created");
                        String icon = jsonObject.getString("icon");
                        JSONObject contextJsonObject = jsonObject.getJSONObject("context");

                        if (contextJsonObject.has("facility_location_name")) {
                            facility_location_name = contextJsonObject.getString("facility_location_name");
                        } else if (contextJsonObject.has("value")) {
                            facility_location_name = contextJsonObject.getString("value");
                        } else if (contextJsonObject.has("event_type")) {
                            facility_location_name = contextJsonObject.getString("event_type");
                        } else if (contextJsonObject.has("reward_name")) {
                            facility_location_name = contextJsonObject.getString("reward_name");
                        } else {
                            facility_location_name = "";
                        }
                        HistoryItem historyItem = new HistoryItem(name, points, date, icon, facility_location_name, "");
                        historyItemArrayList.add(historyItem);

                    }
                    totalPointsTextView.setText(String.valueOf(totalPoints) + " pts");


                    if (getApplicationContext() != null) {
                        ActivityHistoryListAdapter historyListViewAdapter = new ActivityHistoryListAdapter(getApplicationContext(), historyItemArrayList);
                        historyListview.setAdapter(historyListViewAdapter);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(ActivityHistoryActivity.this);
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(ActivityHistoryActivity.this);
                }

                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("" + errorResponse);
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        MainActivity.isInternetConnection = false;
        intent.putExtra("action", Constants.ACTION_POINT_SUMMERY);
        startActivity(intent);
        finish();
        super.onBackPressed();  // optional depending on your needs
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            callHistoryApi(date);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(ActivityHistoryActivity.this);
                }
            });
        }
    }


}
