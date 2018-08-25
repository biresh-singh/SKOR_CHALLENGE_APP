package fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import adaptor.HistoryListViewAdapter;
import bean.HistoryItem;
import constants.Constants;
import database.SharedDatabase;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class HistoryFragment extends Fragment {
    ListView historyListview;
    public SharedDatabase sharedDatabase;
    public String token;
    private ArrayList<String> context = new ArrayList<>();
    boolean isMonthly = false;
    ArrayList<HistoryItem> getCalenderItemArrayList = new ArrayList<>();
    HashMap<String, ArrayList<HistoryItem>> hashMap = new HashMap<>();
    ArrayList<String> dateArrayForSection = new ArrayList<>();
    ArrayList<String> redemptionName = new ArrayList<>();
    ArrayList<String> redemptionPk = new ArrayList<>();
    public static ArrayList<HistoryItem> historyItemArrayList = new ArrayList<>();
    String currentMonth;
    TextView week, month;
    String facility_location_name;
    int totalPoints = 0;
    TextView totalPointsOfToday;
    Date previous_date1 = null;
    Date previous_date2 = null;
    TextView weekDate;
    String newStartDate;
    String firstDateOfweek = null;
    Date lastUpdatedDate = null;
    Date date = null;
    String endDate;
    RelativeLayout totalpointsLayout;
    TextView datetime;
    TextView defaulttext;
    boolean isMonthChanged = false;
    RelativeLayout relativeLayout;
    TextView totalPointsTextView;
    CheckInternetConnection checkInternetConnection;
    LinearLayout previousWeek, nextWeek;
    SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private ArrayList<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, null);

        LinearLayout back = (LinearLayout) view.findViewById(R.id.menupanel);
        historyListview = (ListView) view.findViewById(R.id.list);
        historyListview.setDivider(null);
        week = (TextView) view.findViewById(R.id.week);
        month = (TextView) view.findViewById(R.id.month);
        totalPointsOfToday = (TextView) view.findViewById(R.id.total_points_of_today);
        totalPointsTextView = (TextView) view.findViewById(R.id.total_points);
        previousWeek = (LinearLayout) view.findViewById(R.id.previousweek);
        nextWeek = (LinearLayout) view.findViewById(R.id.nextweek);
        weekDate = (TextView) view.findViewById(R.id.weekdate);
        datetime = (TextView) view.findViewById(R.id.date_time);
        defaulttext = (TextView) view.findViewById(R.id.default_text);
        totalpointsLayout = (RelativeLayout) view.findViewById(R.id.total_points_layout);
        checkInternetConnection = new CheckInternetConnection(getContext());
        Calendar cal = Calendar.getInstance();
        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        relativeLayout = (RelativeLayout) view.findViewById(R.id.default_layout);
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
                callTotalPointsSummeryApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* ********************clicking weeking to set weekview**************************/

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        isMonthly = false;

                        week.setBackgroundResource(R.drawable.text_bg);
                        week.setTextColor(Color.BLACK);
                        month.setBackgroundColor(Color.TRANSPARENT);
                        month.setTextColor(Color.parseColor("#8A8A8A"));
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

        /* ********************clciking on month to month view**************************/

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        isMonthly = true;
                        month.setBackgroundResource(R.drawable.text_bg);
                        month.setTextColor(Color.BLACK);
                        week.setBackgroundColor(Color.TRANSPARENT);
                        week.setTextColor(Color.parseColor("#8A8A8A"));
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

        /* *******************clciking to prevois week or month***************************/

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

        /* *******************clciking condition to going future month week or not***************************/

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        Date firstDayOfweek = mFormatter.parse(firstDateOfweek);
                        String date1 = mFormatter.format(date);
                        //String date2=mFormatter.format(firstDateOfweek);
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

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (getActivity() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.parseColor("#FF9B30")));
            }
        } else if (msg.equals("Network Connected")) {
            if (getActivity() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (getActivity() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("AllHistory");

    }
/* ******************checking condition going to future or previous month, week****************************/

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
    }

    /* *********************call point summary Api to set week view Data*************************/

    public void callTotalPointsSummeryApi() {
        if (checkInternetConnection.isConnectingToInternet()) {
            String authProvider = SettingsManager.getInstance().getAuthProvider();

            Loader.showProgressDialog(getContext());
            HashMap<String, String> paramMap = new HashMap<String, String>();
            RequestParams params = new RequestParams(paramMap);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.get(Constants.BASEURL + Constants.TOTAL_POINTS_SUMMERY, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                    super.onSuccess(statusCode, headers, jsonObject);

                    Loader.dialogDissmiss(getContext());
                    try {


                        totalPointsOfToday.setText(jsonObject.getString("remaining_points") + " pts");
                        try {
                            callHistoryApi(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Loader.dialogDissmiss(getContext());
                    if (statusCode == 401) {
                        UserManager.getInstance().logOut(getActivity());
                    }

                    if (statusCode == 400) {
                        if (getActivity() != null) {
                            connectivityMessage( "" + errorResponse);
                        }
                    }
                    if (statusCode == 500) {
                        if (getActivity() != null) {
                            connectivityMessage( "We've encountered a technical error.our team is working on it. please try again later");
                        }
                    }
                }
            });
        } else {
            connectivityMessage("Waiting for Network!");
        }
    }

    /* ******************calling history Api to update Data on weekview and month view****************************/


    public void callHistoryApi(Date date) {
        if (checkInternetConnection.isConnectingToInternet()) {
            String url;
            totalPoints = 0;
            if (isMonthly) {
                String startDate = mFormatter.format(date);
                String[] startDate1 = startDate.split("-");
                startDate = startDate1[0] + "-" + startDate1[1] + "-" + "01";
                startDate = "date=" + startDate;
                url = Constants.BASEURL + Constants.HISTORY_API + "/monthly/?" + startDate;
            } else {

                String startDate = mFormatter.format(date);
                startDate = "date=" + startDate;
                url = Constants.BASEURL + Constants.HISTORY_API + "/weekly/?" + startDate;
            }
            String authProvider = SettingsManager.getInstance().getAuthProvider();

            Loader.showProgressDialog(getContext());
            HashMap<String, String> paramMap = new HashMap<String, String>();
            RequestParams params = new RequestParams(paramMap);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("Content-Type", "application/json");
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    redemptionName = new ArrayList<String>();
                    redemptionPk = new ArrayList<String>();
                    context = new ArrayList<String>();
                    Loader.dialogDissmiss(getContext());
                    if (jsonArray.length() == 0) {
                        relativeLayout.setVisibility(View.VISIBLE);
                        defaulttext.setText("No data to display");
                        totalpointsLayout.setVisibility(View.GONE);
                    } else {
                        totalpointsLayout.setVisibility(View.VISIBLE);
                    }
                    historyItemArrayList.clear();
                    dateArrayForSection.clear();
                    getCalenderItemArrayList.clear();
                    sections.clear();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            relativeLayout.setVisibility(View.GONE);
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JSONObject reasonJsonObject = jsonObject.getJSONObject("reason");
                            String name = reasonJsonObject.getString("name");
                            redemptionName.add(name);
                            String points = jsonObject.getString("points");
                            totalPoints = totalPoints + Integer.parseInt(points);
                            String date = jsonObject.getString("created");
                            String icon = jsonObject.getString("icon");
                            redemptionPk.add(jsonObject.getString("created"));
                            JSONObject contextJsonObject = jsonObject.getJSONObject("context");

                            String redemptioncode = "";
                            if (contextJsonObject.has("redemption_code")) {
                                redemptioncode = contextJsonObject.getString("redemption_code");
                            } else {
                                redemptioncode = "";
                            }
                            context.add(contextJsonObject.toString());
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
                            HistoryItem historyItem = new HistoryItem(name, points, date, icon, facility_location_name, redemptioncode);
                            historyItemArrayList.add(historyItem);

                        }


                        totalPointsTextView.setText(String.valueOf(totalPoints) + " pts");

                        if (getActivity() != null) {
                            HistoryListViewAdapter historyListViewAdapter = new HistoryListViewAdapter(getContext(), historyItemArrayList);
                            historyListview.setAdapter(historyListViewAdapter);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Loader.dialogDissmiss(getContext());
                    if (statusCode == 400) {
                        if (getActivity() != null) {
                            connectivityMessage( "" + errorResponse);
                        }
                    }
                    if (statusCode == 500) {
                        if (getActivity() != null) {
                            connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                        }
                    }
                }
            });
        } else {
            connectivityMessage("Waiting for Network!");
        }
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
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(getActivity());
                }
            });
        }
    }



}
