package fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.CalendarAdapter;
import adaptor.CustomCalendarRecyclerViewAdapter;
import bean.CalenderItem;
import bean.CalenderUtility;
import bean.Utility;
import constants.Constants;
import database.SharedDatabase;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;
import utils.SimpleGestureFilter;


public class CalendarView extends Fragment implements OnClickListener, CustomCalendarRecyclerViewAdapter.UpdateHeaderdate/*implements SimpleGestureListener*/ {
    private SimpleGestureFilter detector;
    public GregorianCalendar month, itemmonth;
    public CalendarAdapter adapter;
    public Handler handler;
    public ArrayList<String> items = new ArrayList<>();
    public HashMap<String, String> eventTypes = new HashMap<>();
    public HashMap<String, String> greetingEventTypes = new HashMap<>();
    public HashMap<String, String> appointmentsEventTypes = new HashMap<>();
    ArrayList<String> event;
    LinearLayout rLayout;
    boolean isgreeting = false;
    ArrayList<String> date;
    ArrayList<String> desc;
    boolean whatsonFlag = false;
    ArrayList<String> appointsmentsAray = new ArrayList<>();
    Date weekdate = new Date();
    RelativeLayout calenderlayout;
    ArrayList<String> dateArrayForSection = new ArrayList<>();
    String[] days;
    GridView gridview;
    boolean isdaYClicked = false;
    int selectedViewpagerIndex = 50;
    int weekDaysIndexClicked = 0;
    boolean isCalenderVisible = false;
    String preSelectedItem;
    boolean isCalenderDayClicked = false;
    public static ArrayList<CalenderItem> calenderItemArrayList = new ArrayList<>();
    View view;
    String calenderSelectedDate;
    boolean isMonthSelected = true;
    LinearLayout monthLayout, weekLayout;
    TextView monthTextView, weekTextView;
    ListView recyclerView;
    String currentMonth;
    String currentYear;
    boolean isAllSelected = true;
    boolean isEventSelected = false;
    boolean isGreetingsSelected = false;
    boolean isAppointmentSelected = false;
    int selectedIndexForViewpager = 50;
    List<List<String>> weekdates;
    Date date1, date2;
    RelativeLayout weekView;
    private ArrayList<Section> sections = new ArrayList<Section>();
    String[] weekDays = new String[7];
    public static TextView monthForCalander;
    SimpleDateFormat mFormatter = new SimpleDateFormat("yy-MM-dd");
    CheckInternetConnection checkInternetConnection;
    TextView currentMonthTextView;
    ImageView calenderIcon;
    String secondmonth, firstmonth, fourthmonth, fifthmonth, sixthmonth;
    String selectedMonthIndex = "0";
    String selectedMonth = "";
    ViewPager viewPager;
    TextView noDataFoundTextview;
    int gridvalue = 0;
    Animation slideDown, slideUp;
    TextView headerDateTextView;
    boolean isFirstTime = true;
    Button addAppointmentsButton;
    SimpleSectionedListAdapter simpleSectionedGridAdapter;
    ArrayList<CalenderItem> getCalenderItemArrayList = new ArrayList<>();
    Date mDate;
    RelativeLayout relativeLayout;
    int weeks;
    int permissionCheck;
    ArrayList<String> monthsArray = new ArrayList<>();
    LinkedHashMap<String, ArrayList<CalenderItem>> hashMap = new LinkedHashMap<>();
    boolean isFromDashBoard = false;
    CustomCalendarRecyclerViewAdapter customCalendarRecyclerViewAdapter;
    Calendar now = Calendar.getInstance();
    TextView firstMonth, secondMonth, thirdMonth, fourthMonth, fifthMonth, sixthMonth;
    TextView allEvents, eventsTextView, greetingsTextView, appointmentsTextView;
    public static ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    ImageView rightArrow;
    ImageView leftArrow;
    LinearLayout linearlayoutWeek;
    TextView firstday, secondday, thirdday, fourthday, fifthday, sixthday, seventhday;
    public SharedDatabase sharedDatabase;
    public String token;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.calendar, null);
        Locale.setDefault(Locale.US);
        Calendar cal = Calendar.getInstance();
        mDate = new Date();
        currentMonth = new SimpleDateFormat("MMMM").format(cal.getTime());
        selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
        currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
        selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        sharedDatabase.setPosition(3);
        sharedDatabase.setType("all");
        rLayout = (LinearLayout) view.findViewById(R.id.text);
        RelativeLayout titlebar = (RelativeLayout) view.findViewById(R.id.titlebar);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        adapter = new CalendarAdapter(getActivity(), month);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        weekView = (RelativeLayout) view.findViewById(R.id.week_view);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        headerDateTextView = (TextView) view.findViewById(R.id.header_date);
        addAppointmentsButton = (Button) view.findViewById(R.id.add_appointments);
        noDataFoundTextview = (TextView) view.findViewById(R.id.no_text_data);
        noDataFoundTextview.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down_animation);
        slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        itemmonth = (GregorianCalendar) month.clone();
        calenderlayout = (RelativeLayout) view.findViewById(R.id.calenderlayout);
        monthForCalander = (TextView) view.findViewById(R.id.current_month);
        monthForCalander.setText(currentMonth.toUpperCase() + " " + currentYear);
        jsonObjectArrayList.clear();
        items = new ArrayList<String>();
        calenderIcon = (ImageView) view.findViewById(R.id.calendericon);
        calenderIcon.setOnClickListener(this);
        viewPager = (ViewPager) view.findViewById(R.id.pager);

        if (getActivity() != null) {
            MyCalenderAdapter calenderAdapter = new MyCalenderAdapter(getActivity());
            viewPager.setAdapter(calenderAdapter);
            viewPager.setCurrentItem(50);
            viewPager.setVisibility(View.GONE);
        }

        handler = new Handler();
        TextView title = (TextView) view.findViewById(R.id.title);
        monthLayout = (LinearLayout) view.findViewById(R.id.monthlayout);
        weekLayout = (LinearLayout) view.findViewById(R.id.weeklayout);
        monthTextView = (TextView) view.findViewById(R.id.month);
        weekTextView = (TextView) view.findViewById(R.id.week);
        allEvents = (TextView) view.findViewById(R.id.all);
        eventsTextView = (TextView) view.findViewById(R.id.event);
        greetingsTextView = (TextView) view.findViewById(R.id.greetings);
        appointmentsTextView = (TextView) view.findViewById(R.id.appointments);
        currentMonthTextView = (TextView) view.findViewById(R.id.currentmonth);
        currentMonthTextView.setText(currentMonth.toUpperCase() + "  " + currentYear);
        firstMonth = (TextView) view.findViewById(R.id.firstmonth);
        secondMonth = (TextView) view.findViewById(R.id.secondmonth);
        thirdMonth = (TextView) view.findViewById(R.id.thirdmonth);
        fourthMonth = (TextView) view.findViewById(R.id.fourthmonth);
        fifthMonth = (TextView) view.findViewById(R.id.fifthmonth);
        sixthMonth = (TextView) view.findViewById(R.id.sixthmonth);
        firstday = (TextView) view.findViewById(R.id.firstday);
        secondday = (TextView) view.findViewById(R.id.secondday);
        thirdday = (TextView) view.findViewById(R.id.thirdday);
        fourthday = (TextView) view.findViewById(R.id.fourthday);
        fifthday = (TextView) view.findViewById(R.id.fifthday);
        sixthday = (TextView) view.findViewById(R.id.sixthday);
        seventhday = (TextView) view.findViewById(R.id.seventhday);
        linearlayoutWeek = (LinearLayout) view.findViewById(R.id.weeklayout);
        leftArrow = (ImageView) view.findViewById(R.id.left_arrow);
        rightArrow = (ImageView) view.findViewById(R.id.right_arrow);
        recyclerView = (ListView) view.findViewById(R.id.recycleview);
        recyclerView.setDivider(null);
        customCalendarRecyclerViewAdapter = new CustomCalendarRecyclerViewAdapter(getActivity(), calenderItemArrayList);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
        RelativeLayout previous = (RelativeLayout) view.findViewById(R.id.previous);
        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth(1);
                refreshCalendar();
            }
        });
        RelativeLayout next = (RelativeLayout) view.findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth(1);
                refreshCalendar();

            }
        });
        LinearLayout panel = (LinearLayout) view.findViewById(R.id.menupanel);
        panel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedDatabase.setPosition(3);
                sharedDatabase.setType("all");
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        updateMonthText();
        getAllWeeks();
        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
        checkInternetConnection = new CheckInternetConnection(getActivity());

/* ****************Selecting To Appointment event Greeting******************************/

        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                calenderItemArrayList.clear();
                Bundle bundle = getArguments();
                preSelectedItem = bundle.getString("pre_selected_item");
                if (preSelectedItem.equals("all")) {

                    getAllWeeks();
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                    callEventsApi(selectedMonth);

                } else if (preSelectedItem.equals("events")) {
                    isAllSelected = false;
                    isEventSelected = true;
                    isGreetingsSelected = false;
                    isAppointmentSelected = false;

                    eventsTextView.setBackgroundResource(R.drawable.text_bg);
                    eventsTextView.setTextColor(Color.BLACK);
                    allEvents.setTextColor(Color.WHITE);
                    allEvents.setBackgroundColor(Color.TRANSPARENT);
                    greetingsTextView.setTextColor(Color.WHITE);
                    greetingsTextView.setBackgroundColor(Color.TRANSPARENT);
                    appointmentsTextView.setTextColor(Color.WHITE);
                    appointmentsTextView.setBackgroundColor(Color.TRANSPARENT);
                    getAllWeeks();
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    callEventsApi(selectedMonth);
                } else if (preSelectedItem.equals("greetings")) {
                    isFirstTime = false;
                    isFromDashBoard = true;
                    isAllSelected = false;
                    isEventSelected = false;
                    isGreetingsSelected = true;
                    isAppointmentSelected = false;

                    greetingsTextView.setBackgroundResource(R.drawable.text_bg);
                    greetingsTextView.setTextColor(Color.BLACK);
                    eventsTextView.setTextColor(Color.WHITE);
                    eventsTextView.setBackgroundColor(Color.TRANSPARENT);
                    allEvents.setTextColor(Color.WHITE);
                    allEvents.setBackgroundColor(Color.TRANSPARENT);
                    appointmentsTextView.setTextColor(Color.WHITE);
                    appointmentsTextView.setBackgroundColor(Color.TRANSPARENT);
                    getAllWeeks();
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    callGreetApi(selectedMonth);


                } else if (preSelectedItem.equals("appointments")) {
                    new ForDashBoardAddAppointments().execute();
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }
        try {
            monthLayout.setOnClickListener(this);
            monthTextView.setOnClickListener(this);
            weekTextView.setOnClickListener(this);
            allEvents.setOnClickListener(this);
            eventsTextView.setOnClickListener(this);
            greetingsTextView.setOnClickListener(this);
            appointmentsTextView.setOnClickListener(this);
            firstMonth.setOnClickListener(this);
            secondMonth.setOnClickListener(this);
            thirdMonth.setOnClickListener(this);
            fourthMonth.setOnClickListener(this);
            fifthMonth.setOnClickListener(this);
            sixthMonth.setOnClickListener(this);
            final Animation rightSwipe = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right);
            final Animation leftSwipe = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left);
            firstday.setOnClickListener(this);
            secondday.setOnClickListener(this);
            thirdday.setOnClickListener(this);
            fourthday.setOnClickListener(this);
            fifthday.setOnClickListener(this);
            sixthday.setOnClickListener(this);
            seventhday.setOnClickListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

/* ****************Big Calaender View Pager  clicking listener******************************/

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    CalendarAdapter.isCalenderDateSelected = false;
                    if (selectedViewpagerIndex > position) {
                        selectedViewpagerIndex = position;
                        if (selectedIndexForViewpager <= 5) {
                            for (int i = 0; i < selectedIndexForViewpager; i++) {
                                setPreviousMonth(1);
                            }

                        } else {
                            setPreviousMonth(1);
                        }
                        selectedIndexForViewpager = position;
                        refreshCalendar();
                    } else {
                        selectedViewpagerIndex = position;
                        if (selectedIndexForViewpager <= 5) {
                            for (int i = 0; i < selectedIndexForViewpager; i++) {
                                setNextMonth(1);
                            }
                        } else {
                            setNextMonth(1);
                        }
                        selectedIndexForViewpager = position;
                        refreshCalendar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        /* ****************AClicking Listener to right Arrow******************************/

        rightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        resetWeekColor();
                        isMonthSelected = false;
                        calenderItemArrayList.clear();
                        now = Calendar.getInstance();
                        now.setTime(mDate);
                        now.add(Calendar.DATE, 7);
                        currentMonth = new SimpleDateFormat("MMM").format(now.getTime());
                        currentYear = new SimpleDateFormat("yyyy").format(now.getTime());
                        selectedMonthIndex = new SimpleDateFormat("MM").format(now.getTime());
                        mDate.setTime(now.getTime().getTime());
                        int day = mDate.getDate();
                        getCurrentWeekdays();
                        selectedMonth = "date=" + weekDays[1];
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        int position = getSelectedMonthIndex();
                        monthButtonsSelection(position);
                        if (isEventSelected) {
                            callEventsApi(selectedMonth);

                        } else if (isGreetingsSelected) {
                            callGreetApi(selectedMonth);
                        } else if (isAllSelected) {
                            callEventsApi(selectedMonth);

                        } else if (isAppointmentSelected) {
                            addAppointments();

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        /* ****************Clicking Listener to Left Arrow******************************/

        leftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        resetWeekColor();
                        isMonthSelected = false;
                        calenderItemArrayList.clear();
                        getCurrentWeekdays();
                        selectedMonth = "date=" + weekDays[1];
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        now = Calendar.getInstance();
                        now.setTime(mDate);
                        now.add(Calendar.DATE, -7);
                        currentMonth = new SimpleDateFormat("MMM").format(now.getTime());
                        currentYear = new SimpleDateFormat("yyyy").format(now.getTime());
                        selectedMonthIndex = new SimpleDateFormat("MM").format(now.getTime());
                        mDate.setTime(now.getTime().getTime());
                        int day = mDate.getDate();
                        getCurrentWeekdays();
                        selectedMonth = "date=" + weekDays[1];

                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        int position = getSelectedMonthIndex();
                        monthButtonsSelection(position);
                        if (isEventSelected) {
                            callEventsApi(selectedMonth);
                        } else if (isGreetingsSelected) {
                            callGreetApi(selectedMonth);
                        } else if (isAllSelected) {
                            callEventsApi(selectedMonth);
                        } else if (isAppointmentSelected) {

                            addAppointments();


                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }

            }
        });
        addAppointmentsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                startActivity(intent);
            }
        });


        return view;
    }

    class ForDashBoardAddAppointments extends AsyncTask<Void, Void, Void> {


        public ForDashBoardAddAppointments() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (whatsonFlag == false) {
                        appointmentWhatonOn();
                        whatsonFlag = true;
                    }
                    handler.postDelayed(this, 2000);
                }
            }, 2000);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                Loader.showProgressDialog(getActivity());
            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (getActivity() != null) {
                Loader.dialogDissmiss(getActivity());
                customCalendarRecyclerViewAdapter = new CustomCalendarRecyclerViewAdapter(getActivity(), calenderItemArrayList);
                simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), customCalendarRecyclerViewAdapter,
                        R.layout.list_item_header, R.id.header);
                simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                recyclerView.setAdapter(simpleSectionedGridAdapter);
                simpleSectionedGridAdapter.notifyDataSetChanged();
                customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                isFromDashBoard = false;
            }

        }
    }
    /* ****************Selecting Month Indexing******************************/

    public int getSelectedMonthIndex() {
        int position = 50;
        for (int i = 0; i < monthsArray.size(); i++) {
            String month = monthsArray.get(i);
            if (month.equals(currentMonth)) {
                if (i == 0) {
                    position = 50;

                } else if (i == 1) {
                    position = 49;

                } else if (i == 2) {
                    position = 48;

                } else if (i == 3) {
                    position = 51;

                } else if (i == 4) {
                    position = 52;

                } else if (i == 5) {
                    position = 43;

                }
                break;
            }
        }
        return position;
    }

    /* ****************Selecting Month Button to Clicking ******************************/

    public void monthButtonsSelection(int position) {
        if (position == 48) {
            firstMonth.setBackgroundResource(R.drawable.rectangle);
            firstMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            thirdMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            sixthMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 49) {
            secondMonth.setBackgroundResource(R.drawable.rectangle);
            firstMonth.setTextColor(Color.WHITE);
            secondMonth.setTextColor(Color.BLACK);
            thirdMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            sixthMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            sixthMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 50) {
            thirdMonth.setBackgroundResource(R.drawable.rectangle);
            thirdMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            firstMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            sixthMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            sixthMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 51) {
            fourthMonth.setBackgroundResource(R.drawable.rectangle);
            fourthMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            thirdMonth.setTextColor(Color.WHITE);
            firstMonth.setTextColor(Color.WHITE);
            sixthMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            sixthMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 52) {
            fifthMonth.setBackgroundResource(R.drawable.rectangle);
            fifthMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            thirdMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            sixthMonth.setTextColor(Color.WHITE);
            firstMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
            sixthMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 53) {
            sixthMonth.setBackgroundResource(R.drawable.rectangle);
            sixthMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            thirdMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            firstMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
        } else if (position == 43) {
            sixthMonth.setBackgroundResource(R.drawable.rectangle);
            sixthMonth.setTextColor(Color.BLACK);
            secondMonth.setTextColor(Color.WHITE);
            thirdMonth.setTextColor(Color.WHITE);
            fourthMonth.setTextColor(Color.WHITE);
            firstMonth.setTextColor(Color.WHITE);
            fifthMonth.setTextColor(Color.WHITE);
            secondMonth.setBackgroundColor(Color.TRANSPARENT);
            thirdMonth.setBackgroundColor(Color.TRANSPARENT);
            fourthMonth.setBackgroundColor(Color.TRANSPARENT);
            fifthMonth.setBackgroundColor(Color.TRANSPARENT);
            firstMonth.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    /* ****************get Number of Week******************************/

    public void getAllWeeks() {


        Calendar cal = Calendar.getInstance();

        int maxWeeknumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        //Toast.makeText(getActivity(),String.valueOf(maxWeeknumber),Toast.LENGTH_LONG).show();
        int week = cal.get(Calendar.WEEK_OF_MONTH);
        //Toast.makeText(getActivity(),String.valueOf(week),Toast.LENGTH_LONG).show();


    }
    /* ****************count number of Week******************************/

    public synchronized List<List<String>> getNumberOfWeeks(int year, int month) {
        --month;
        getCalenderItemArrayList = new ArrayList<>();
        dateArrayForSection.clear();
        //calenderItemArrayList.clear();
        hashMap.clear();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        weekdates = new ArrayList<List<String>>();
        List<String> dates;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        while (c.get(Calendar.MONTH) == month) {
            dates = new ArrayList<String>();
            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
            dates.add(format.format(c.getTime()));
            c.add(Calendar.DAY_OF_MONTH, 6);
            dates.add(format.format(c.getTime()));
            weekdates.add(dates);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        System.out.println(weekdates);
        weeks = weekdates.size();
        putData();
        return weekdates;
    }
    /* ****************Putting Data in HashMap ******************************/

    public void putData() {
        String startDate = "";
        for (int j = 0; j < weeks; j++) {

            getCalenderItemArrayList = new ArrayList<>();
            List<String> weekDate = weekdates.get(j);
            startDate = weekDate.get(0);
            String lastdate = weekDate.get(1);
            String[] split_StartDate = startDate.split("-");
            String[] splitLastDate = lastdate.split("-");

            String hashMapKey = split_StartDate[0] + dateformat(split_StartDate[1]) + "-" + splitLastDate[0] + " " + dateformat(splitLastDate[1]);
            dateArrayForSection.add(hashMapKey);
            hashMap.put(hashMapKey, getCalenderItemArrayList);


        }

    }

    /* ****************Sorting Data Event AppointMent Greeting and All******************************/

    public synchronized void filterData() {
        String startDate = "";

        for (int i = 0; i < calenderItemArrayList.size(); i++) {
            final CalenderItem calenderItem = calenderItemArrayList.get(i);

            for (int j = 0; j < weeks; j++) {
                List<String> weekDate = weekdates.get(j);
                startDate = weekDate.get(0);
                //weekSectionItemArray=hashMap.get(startDate);
                String lastdate = weekDate.get(1);
                String[] split_StartDate = startDate.split("-");
                String[] splitLastDate = lastdate.split("-");
                /*+dateformat(split_StartDate[1])
                dateformat(splitLastDate[1])
                 */
                String hashMapKey = split_StartDate[0] + dateformat(split_StartDate[1]) + "-" + splitLastDate[0] + " " + dateformat(splitLastDate[1]);
                dateArrayForSection.add(hashMapKey);
                getCalenderItemArrayList = hashMap.get(hashMapKey);
                String itemDate = calenderItem.mEventdate;
                String eventdate;
                if (calenderItem.mIsEvent) {
                    try {
                        String[] split = itemDate.split("T");
                        eventdate = split[0];


                        String[] splitDate = eventdate.split("-");
                        Integer dateAsInt = Integer.parseInt(splitDate[2]);

                        String[] splitStartDate = startDate.split("-");
                        Integer startdateAsInt = Integer.parseInt(splitStartDate[0]);

                        String[] splitEndDate = lastdate.split("-");
                        Integer enddateAsInt = Integer.parseInt(splitEndDate[0]);
                        eventdate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = sdf.parse(eventdate);
                        Date date2 = sdf.parse(startDate);
                        Date date3 = sdf.parse(lastdate);
                        if ((date1.compareTo(date2) >= 0) && date1.compareTo(date3) <= 0) {
                            if (!(getCalenderItemArrayList == null)) {
                                getCalenderItemArrayList.add(calenderItem);
                            }
                        }
                        hashMap.put(hashMapKey, getCalenderItemArrayList);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (calenderItem.mIsAppointsments) {
                    try {
                        String[] split = itemDate.split(" ");
                        eventdate = split[0];
                        String[] splitDate = eventdate.split("/");
                        Integer dateAsInt = Integer.parseInt(splitDate[0]);
                        String[] splitStartDate = startDate.split("-");
                        Integer startdateAsInt = Integer.parseInt(splitStartDate[0]);
                        String[] splitEndDate = lastdate.split("-");
                        Integer enddateAsInt = Integer.parseInt(splitEndDate[0]);
                        eventdate = splitDate[0] + "-" + splitDate[1] + "-" + splitDate[2];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = sdf.parse(eventdate);
                        Date date2 = sdf.parse(startDate);
                        Date date3 = sdf.parse(lastdate);
                        if ((date1.compareTo(date2) >= 0) && (date1.compareTo(date3) <= 0)) {
                            if (!(getCalenderItemArrayList == null)) {
                                getCalenderItemArrayList.add(calenderItem);
                            }
                        }
                        hashMap.put(hashMapKey, getCalenderItemArrayList);

                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else {


                    try {
                        eventdate = calenderItem.mEventdate;
                        String[] splitDate = eventdate.split("-");
                        Integer dateAsInt = Integer.parseInt(splitDate[2]);
                        String[] splitStartDate = startDate.split("-");
                        Integer startdateAsInt = Integer.parseInt(splitStartDate[0]);
                        String[] splitEndDate = lastdate.split("-");
                        Integer enddateAsInt = Integer.parseInt(splitEndDate[0]);
                        eventdate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = sdf.parse(eventdate);
                        Date date2 = sdf.parse(startDate);
                        Date date3 = sdf.parse(lastdate);
                        if ((date1.compareTo(date2) >= 0) && (date1.compareTo(date3) <= 0)) {
                            if (!(getCalenderItemArrayList == null)) {
                                getCalenderItemArrayList.add(calenderItem);
                            }
                        }
                        hashMap.put(hashMapKey, getCalenderItemArrayList);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }


        Object[] st = dateArrayForSection.toArray();
        for (Object s : st) {
            if (dateArrayForSection.indexOf(s) != dateArrayForSection.lastIndexOf(s)) {
                dateArrayForSection.remove(dateArrayForSection.lastIndexOf(s));
            }
        }


        if (dateArrayForSection.size() > 0) {

            checkForhashmapValue();
            removeEmptyDataFromMap();


        }


    }
/* ****************Checking HashMap Values******************************/

    public synchronized void checkForhashmapValue() {
        for (int i = 0; i < hashMap.size(); i++) {
            ArrayList<CalenderItem> calenderItemArrayList = new ArrayList<>();
            calenderItemArrayList = hashMap.get(dateArrayForSection.get(i));

            if (calenderItemArrayList == null) {
                hashMap.remove(dateArrayForSection.get(i));
                dateArrayForSection.remove(i);

                checkForhashmapValue();
            }
        }
    }
    /* ****************Remove Empty Data From HashMap******************************/

    public synchronized void removeEmptyDataFromMap() {

        for (int i = 0; i < hashMap.size(); i++) {
            ArrayList<CalenderItem> calenderItemArrayList = new ArrayList<>();
            calenderItemArrayList = hashMap.get(dateArrayForSection.get(i));
            if (calenderItemArrayList != null) {
                if (calenderItemArrayList.size() == 0) {
                    hashMap.remove(dateArrayForSection.get(i));
                    dateArrayForSection.remove(i);
                    removeEmptyDataFromMap();
                }
            }
        }
    }
    /* ****************Data synchronized Event******************************/

    public synchronized void sortData(ArrayList<CalenderItem> calenderItemArrayList) {
        System.out.println("before sortingDDDDDDDDDDDDDD" + calenderItemArrayList);
        Collections.sort(calenderItemArrayList, new Comparator<CalenderItem>() {
            public int compare(CalenderItem m1, CalenderItem m2) {
                String itemDate1 = m1.mEventdate;
                String itemDate2 = m2.mEventdate;

                String eventdate1;
                String eventdate2 = "";

                if (m1.mIsEvent && m2.mIsEvent) {
                    try {
                        String[] split = itemDate1.split("T");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        String[] split2 = itemDate2.split("T");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);


                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsAppointsments && m2.mIsAppointsments) {
                    try {
                        String[] split = itemDate1.split(" ");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("/");

                        eventdate1 = splitDate[0] + "-" + splitDate[1] + "-" + splitDate[2];
                        String[] split2 = itemDate2.split(" ");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("/");

                        eventdate2 = splitDate2[0] + "-" + splitDate2[1] + "-" + splitDate2[2];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);


                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsEvent && m2.mIsAppointsments) {
                    try {
                        String[] split = itemDate1.split("T");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        String[] split2 = itemDate2.split(" ");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("/");

                        eventdate2 = splitDate2[0] + "-" + splitDate2[1] + "-" + splitDate2[2];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsAppointsments && m2.mIsEvent) {
                    try {
                        String[] split = itemDate1.split(" ");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("/");

                        eventdate1 = splitDate[0] + "-" + splitDate[1] + "-" + splitDate[2];
                        String[] split2 = itemDate2.split("T");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsEvent && m2.mIsGreetings) {
                    try {
                        String[] split = itemDate1.split("T");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        String[] splitDate2 = itemDate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsGreetings && m2.mIsEvent) {
                    try {

                        String[] splitDate = itemDate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        String[] split2 = itemDate2.split("T");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsGreetings && m2.mIsAppointsments) {
                    try {
                        String[] splitDate = itemDate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                        String[] split2 = itemDate2.split(" ");
                        eventdate2 = split2[0];


                        String[] splitDate2 = eventdate2.split("/");

                        eventdate2 = splitDate2[0] + "-" + splitDate2[1] + "-" + splitDate2[2];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                } else if (m1.mIsAppointsments && m2.mIsGreetings) {
                    try {
                        String[] split = itemDate1.split(" ");
                        eventdate1 = split[0];


                        String[] splitDate = eventdate1.split("/");

                        eventdate1 = splitDate[0] + "-" + splitDate[1] + "-" + splitDate[2];
                        String[] splitDate2 = itemDate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else if (m1.mIsGreetings && m2.mIsGreetings) {
                    String[] split = itemDate1.split("T");
                    eventdate1 = split[0];

                    try {
                        String[] splitDate = itemDate1.split("-");

                        eventdate1 = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];


                        String[] splitDate2 = itemDate2.split("-");

                        eventdate2 = splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0];
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        date1 = sdf.parse(eventdate1);
                        date2 = sdf.parse(eventdate2);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                m1.setDate(date1);
                m2.setDate(date2);

                return m1.getDate().compareTo(m2.getDate());


            }
        });
        System.out.println("After Sortingssssssssssssssssssssssssssss" + calenderItemArrayList);
    }

    /* ****************Updateing Month Text******************************/

    public void updateMonthText() {
        Calendar cal = Calendar.getInstance();
        currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(currentMonth);
        thirdMonth.setText(currentMonth.toUpperCase());


        cal.add(Calendar.MONTH, -1);
//format it to MMM-yyyy // January-2012
        secondmonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(secondmonth);
        secondMonth.setText(secondmonth.toUpperCase());
        cal.add(Calendar.MONTH, -1);
        firstmonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(firstmonth);
        firstMonth.setText(firstmonth.toUpperCase());
        cal.add(Calendar.MONTH, 3);
        fourthmonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(fourthmonth);
        fourthMonth.setText(fourthmonth.toUpperCase());
        cal.add(Calendar.MONTH, 1);
        fifthmonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(fifthmonth);
        fifthMonth.setText(fifthmonth.toUpperCase());
        cal.add(Calendar.MONTH, 1);
        sixthmonth = new SimpleDateFormat("MMM").format(cal.getTime());
        monthsArray.add(sixthmonth);
        sixthMonth.setText(sixthmonth.toUpperCase());


    }

/* ****************Api Calling Month and Week******************************/

    public void callEventsApi(final String date) {
        try {
            calenderItemArrayList.clear();
            if (isMonthSelected) {
                Constants.EVENTS_URL = "events/api/events/monthly/?" + date;
                ;
                if (!isAllSelected) {

                    calenderItemArrayList.clear();
                }

            } else {
                Constants.EVENTS_URL = "events/api/events/weekly/?" + date;
                ;
                if (!isAllSelected) {
                    calenderItemArrayList.clear();
                }
            }
            if (getActivity() != null) {
                Loader.dialogDissmiss(getActivity());
                Loader.showProgressDialog(getActivity());
            }
            HashMap<String, String> paramMap = new HashMap<String, String>();
       /* paramMap.put("Token", token);*/

            String authProvider = SettingsManager.getInstance().getAuthProvider();

            RequestParams params = new RequestParams(paramMap);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);

            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.get(Constants.BASEURL + Constants.EVENTS_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                    super.onSuccess(statusCode, headers, jsonArray);
                    if (getActivity() != null) {
                        jsonObjectArrayList.clear();
                        calenderItemArrayList.clear();
                        if (!isAllSelected) {
                            calenderItemArrayList.clear();
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                jsonObjectArrayList.add(jsonObject);
                                String eventName = jsonObject.getString("name");
                                String eventTime = jsonObject.getString("start");
                                String imageUrl = jsonObject.getString("thumbnail");
                                String email="";
                                if (jsonObject.has("user_email")) {
                                    email = jsonObject.getString("user_email");
                                }
                                String eventDate = jsonObject.getString("end");
                                String rsvpState = jsonObject.getString("rsvp_state");
                                String id = jsonObject.getString("id");
                                String address = jsonObject.getString("address");
                                CalenderItem calenderItem = new CalenderItem(imageUrl, eventName, eventDate, eventTime, true, false, rsvpState, id, jsonObject.toString(), "", false, address, email);
                                calenderItemArrayList.add(calenderItem);

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }


                        }
                        if (calenderItemArrayList.size() == 0) {
                            noDataFoundTextview.setVisibility(View.VISIBLE);
                            addAppointmentsButton.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFoundTextview.setVisibility(View.GONE);
                            addAppointmentsButton.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }


                        for (int i = 0; i < calenderItemArrayList.size(); i++) {
                            CalenderItem calenderItem = calenderItemArrayList.get(i);
                            String[] splitDate = calenderItem.mEventdate.split("T");

                            Utility.addEvent(calenderItem.meventName, splitDate[0], "event");


                        }
                        if (calenderItemArrayList.size() > 0) {
                            noDataFoundTextview.setVisibility(View.GONE);
                            addAppointmentsButton.setVisibility(View.GONE);
                        }

                        if (calenderItemArrayList.size() > 0) {
                            sortData(calenderItemArrayList);
                            filterData();
                            refreshCalendar();
                        } else {
                        }
                        sections.clear();
                        int value = 0;
                        if (dateArrayForSection.size() > 0) {
                            sections.add(new Section(0, dateArrayForSection.get(0)));
                        } else {
                            calenderItemArrayList.clear();
                        }
                        if (getActivity() != null) {
                            customCalendarRecyclerViewAdapter = new CustomCalendarRecyclerViewAdapter(getActivity(), calenderItemArrayList);
                            simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), customCalendarRecyclerViewAdapter,
                                    R.layout.list_item_header, R.id.header);

                        }
                        if (calenderItemArrayList.size() > 0) {

                            for (int i = 1; i < hashMap.size(); i++) {

                                ArrayList<CalenderItem> calenderItemArrayList = hashMap.get(dateArrayForSection.get(i - 1));
                                Object[] st = calenderItemArrayList.toArray();
                                for (Object s : st) {
                                    if (calenderItemArrayList.indexOf(s) != calenderItemArrayList.lastIndexOf(s)) {
                                        calenderItemArrayList.remove(calenderItemArrayList.lastIndexOf(s));
                                    }
                                }
                                value = value + calenderItemArrayList.size();
                                sections.add(new Section(value, dateArrayForSection.get(i)));
                            }


                            simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                            recyclerView.setAdapter(simpleSectionedGridAdapter);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        } else {

                            simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));

                            recyclerView.setAdapter(simpleSectionedGridAdapter);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    simpleSectionedGridAdapter.notifyDataSetChanged();
                                    customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                        Loader.dialogDissmiss(getActivity());
                        if (isAllSelected) {
                            callGreetApi(date);
                        }

                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (getActivity() != null) {
                        Loader.dialogDissmiss(getActivity());

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(getActivity());
                        }

                        if (statusCode == 500) {
                            Toast.makeText(getActivity(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /* **************** Greeting Api Calling******************************/

    public void callGreetApi(final String date) {
        if (isMonthSelected) {
            Constants.EVENTS_URL = "events/api/greetings/monthly/?" + date;

            if (!isAllSelected) {

                calenderItemArrayList.clear();
            }
        } else {
            Constants.EVENTS_URL = "events/api/greetings/weekly/?" + date;
            ;
            if (!isAllSelected) {
                calenderItemArrayList.clear();
            }
        }
        if (getActivity() != null) {
            Loader.dialogDissmiss(getActivity());
            Loader.showProgressDialog(getActivity());
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.EVENTS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                if (getActivity() != null) {
                    if (!isAllSelected) {
                        calenderItemArrayList.clear();
                    }


                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.e("event " + i + " : ", jsonObject.toString());
                            String eventName = jsonObject.getString("user_first_name");
                            eventName = eventName + " " + jsonObject.getString("user_last_name");
                            String eventTime = jsonObject.getString("type");
                            String imageUrl = jsonObject.getString("thumbnail");
                            String eventDate = jsonObject.getString("date");
                            String email = jsonObject.getString("user_email");

                            CalenderItem calenderItem = new CalenderItem(imageUrl, eventName, eventDate, "", false, false, "", "", jsonObject.toString(), eventTime, true, "", email);
                            calenderItemArrayList.add(calenderItem);


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                    }
                    for (int i = 0; i < calenderItemArrayList.size(); i++) {
                        CalenderItem calenderItem = calenderItemArrayList.get(i);
                        String[] splitDate = calenderItem.mEventdate.split("T");
                        Utility.addEvent(calenderItem.meventName, splitDate[0], "greeting");


                    }
                    if (calenderItemArrayList.size() == 0) {
                        noDataFoundTextview.setVisibility(View.VISIBLE);
                        addAppointmentsButton.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noDataFoundTextview.setVisibility(View.GONE);
                        addAppointmentsButton.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }


                    if (calenderItemArrayList.size() > 0) {

                        sortData(calenderItemArrayList);
                        filterData();
                        refreshCalendar();

                    } else {
                    }


                    checkForhashmapValue();
                    if (isFromDashBoard) {
                        int value = 0;
                        sections.clear();

                        for (int i = 1; i < hashMap.size(); i++) {

                            ArrayList<CalenderItem> calenderItemArrayList = hashMap.get(dateArrayForSection.get(i - 1));
                            Object[] st = calenderItemArrayList.toArray();
                            for (Object s : st) {
                                if (calenderItemArrayList.indexOf(s) != calenderItemArrayList.lastIndexOf(s)) {
                                    calenderItemArrayList.remove(calenderItemArrayList.lastIndexOf(s));
                                }
                            }
                            value = value + calenderItemArrayList.size();
                            sections.add(new Section(value, dateArrayForSection.get(i)));
                        }
                        //customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                        if (getActivity() != null) {
                            simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), customCalendarRecyclerViewAdapter,
                                    R.layout.list_item_header, R.id.header);
                        }
                        simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                        recyclerView.setAdapter(simpleSectionedGridAdapter);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                                simpleSectionedGridAdapter.notifyDataSetChanged();

                            }
                        });
                        isFromDashBoard = false;
                    } else {
                        if (calenderItemArrayList.size() > 0) {
                            sections.clear();
                            int value = 0;
                            if (dateArrayForSection.size() > 0) {
                                sections.add(new Section(0, dateArrayForSection.get(0)));
                            } else {
                                calenderItemArrayList.clear();
                            }

                            for (int i = 1; i < hashMap.size(); i++) {

                                ArrayList<CalenderItem> calenderItemArrayList = hashMap.get(dateArrayForSection.get(i - 1));
                                value = value + calenderItemArrayList.size();
                                sections.add(new Section(value, dateArrayForSection.get(i)));
                            }
                            if (!isAllSelected) {
                                simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        simpleSectionedGridAdapter.notifyDataSetChanged();
                                        customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }


                    Loader.dialogDissmiss(getActivity());
                    if (isAllSelected) {
                        selectedMonth = date;

                        if (isMonthSelected == true) {
                            getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        }

                        addAppointments();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (getActivity() != null) {
                    Loader.dialogDissmiss(getActivity());
                    if (statusCode == 401) {
                        UserManager.getInstance().logOut(getActivity());
                    }

                    if (statusCode == 500) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });


    }

    /* ****************button clicking to set next month******************************/

    protected void setNextMonth(int value) {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), value);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + value);
        }

        Format formatterMonth = new SimpleDateFormat("MMMM");
        String Month = formatterMonth.format(month.getTime());
        Format formatterYear = new SimpleDateFormat("yyyy");
        String year = formatterYear.format(month.getTime());
        Format formatterSelectedMonth = new SimpleDateFormat("MM");
        String selectedMonth = formatterSelectedMonth.format(month.getTime());
        getNumberOfWeeks(Integer.parseInt(year), Integer.parseInt(selectedMonth));

        monthForCalander.setText(Month.toUpperCase() + " " + year);
        currentMonthTextView.setText(Month.toUpperCase() + " " + year);

    }

/* ****************Button clicking to set previous Month******************************/

    protected void setPreviousMonth(int value) {


        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), value);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - value);
        }
        Format formatterMonth = new SimpleDateFormat("MMMM");
        String monthFullname = formatterMonth.format(month.getTime());
        Format formatterYear = new SimpleDateFormat("yyyy");
        String year = formatterYear.format(month.getTime());
        Format formatterSelectedMonth = new SimpleDateFormat("MM");
        String selectedMonth = formatterSelectedMonth.format(month.getTime());
        getNumberOfWeeks(Integer.parseInt(year), Integer.parseInt(selectedMonth));
        monthForCalander.setText(monthFullname.toUpperCase() + " " + year);


        currentMonthTextView.setText(monthFullname.toUpperCase() + " " + year);

    }

/* ****************Month Or Week Greeting event Appoint Clicking to Referesh Calendar******************************/

    public void refreshCalendar() {
        final TextView title = (TextView) view.findViewById(R.id.title);

        if (adapter != null) {

            adapter.refreshDays();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            handler.post(calendarUpdater);

        }// generate some calendar items
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
            }
        });
      /*  title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));*/
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            try {
                items.clear();

                // Print dates of the current week
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String itemvalue;
                event = Utility.readCalendarEvent(getActivity());
          /*  Log.d("=====Event====", event.toString());
            Log.d("=====Date ARRAY====", Utility.startDates.toString());*/

                for (int i = 0; i < Utility.startDates.size(); i++) {
                    itemvalue = df.format(itemmonth.getTime());
                    itemmonth.add(GregorianCalendar.DATE, 1);
                    items.add(Utility.startDates.get(i).toString());
                    eventTypes.put(Utility.startDates.get(i).toString(), Utility.typeOfEvent.get(i).toString());
                }
                for (int i = 0; i < Utility.grettingstartDates.size(); i++) {
                    itemvalue = df.format(itemmonth.getTime());
                    itemmonth.add(GregorianCalendar.DATE, 1);
                    items.add(Utility.grettingstartDates.get(i).toString());
                    greetingEventTypes.put(Utility.grettingstartDates.get(i).toString(), Utility.grettingTypeEvents.get(i).toString());
                }

                for (int i = 0; i < Utility.appointmentsstartDates.size(); i++) {
                    itemvalue = df.format(itemmonth.getTime());
                    itemmonth.add(GregorianCalendar.DATE, 1);
                    items.add(Utility.appointmentsstartDates.get(i).toString());
                    appointmentsEventTypes.put(Utility.appointmentsstartDates.get(i).toString(), Utility.appointmentsTypesEvent.get(i).toString());
                }

                if (items != null) {
                    adapter.setItems(items);
                    adapter.notifyDataSetChanged();
                }
                if (eventTypes != null) {
                    adapter.setEventType(eventTypes);
                    adapter.notifyDataSetChanged();
                }
                if (greetingEventTypes != null) {
                    adapter.setGreetingsEventType(greetingEventTypes);
                    adapter.notifyDataSetChanged();
                }
                if (appointmentsEventTypes != null) {
                    adapter.setAppointmentsEventType(appointmentsEventTypes);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /* ****************Performing on clicking Listener******************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.calendericon: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    CalendarAdapter.isCalenderDateSelected = false;
                    if (isCalenderVisible == false) {

                        viewPager.setVisibility(View.VISIBLE);
                        calenderlayout.setVisibility(View.VISIBLE);
                        isCalenderVisible = true;
                        //viewPager.startAnimation(slideDown);
                    } else {
                        //viewPager.startAnimation(slideUp);
                        viewPager.setVisibility(View.GONE);
                        calenderlayout.setVisibility(View.GONE);
                        isCalenderVisible = false;

                    }
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.month: {
                if (isMonthSelected == false) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        isFirstTime = false;
                        Utility.typeOfEvent.clear();
                        Utility.nameOfEvent.clear();
                        Utility.startDates.clear();
                        Utility.endDates.clear();
                        Utility.descriptions.clear();
                        eventTypes.clear();
                        greetingEventTypes.clear();
                        appointmentsEventTypes.clear();
                        calenderItemArrayList.clear();

                        dateArrayForSection.clear();
                        hashMap.clear();

                        isMonthSelected = true;
                        weekView.setVisibility(View.INVISIBLE);

                        monthLayout.setVisibility(View.VISIBLE);
                        monthTextView.setTextColor(Color.parseColor("#000000"));
                        monthTextView.setBackgroundResource(R.drawable.text_bg);
                        weekTextView.setTextColor(Color.parseColor("#ffffff"));
                        weekTextView.setBackgroundColor(Color.TRANSPARENT);
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        if (isAllSelected) {

                            callEventsApi(selectedMonth);

                        } else if (isGreetingsSelected) {

                            if (isgreeting == true) {
                                callGreetApi("date=" + Integer.parseInt(currentYear) + "-" + selectedMonthIndex + "-" + "01");
                            } else {
                                isgreeting = false;
                                callGreetApi(selectedMonth);
                            }


                        } else if (isEventSelected) {

                            callEventsApi(selectedMonth);

                        } else if (isAppointmentSelected) {
                            addAppointments();
                        }
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
                break;

            }
            case R.id.week: {
                if (isMonthSelected == true) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        isFirstTime = false;
                        isdaYClicked = false;
                        Utility.typeOfEvent.clear();
                        Utility.nameOfEvent.clear();
                        Utility.startDates.clear();
                        Utility.endDates.clear();
                        Utility.descriptions.clear();
                        eventTypes.clear();
                        greetingEventTypes.clear();
                        appointmentsEventTypes.clear();
                        calenderItemArrayList.clear();
                        isMonthSelected = false;
                        weekView.setVisibility(View.VISIBLE);
                        isgreeting = true;

                        monthLayout.setVisibility(View.GONE);

                        monthTextView.setTextColor(Color.parseColor("#ffffff"));
                        weekTextView.setBackgroundResource(R.drawable.text_bg);
                        weekTextView.setTextColor(Color.parseColor("#000000"));
                        monthTextView.setBackgroundColor(Color.TRANSPARENT);
                        getCurrentWeekdays();
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                        selectedMonth = "date=" + weekDays[1];
                        if (isAllSelected) {

                            //selectedMonth=selectedMonthIndex;
                            callEventsApi(selectedMonth);

                        } else if (isGreetingsSelected) {

                            callGreetApi(selectedMonth);

                        } else if (isEventSelected) {

                            callEventsApi(selectedMonth);

                        } else if (isAppointmentSelected) {
                            addAppointments();

                            //addAppointments();
                        }
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
                break;
            }
            case R.id.all: {
                if (!isAllSelected) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        isFirstTime = false;
                        Utility.typeOfEvent.clear();
                        Utility.nameOfEvent.clear();
                        Utility.startDates.clear();
                        Utility.endDates.clear();
                        Utility.descriptions.clear();

                        eventTypes.clear();
                        greetingEventTypes.clear();
                        appointmentsEventTypes.clear();
                        calenderItemArrayList.clear();


                        dateArrayForSection.clear();
                        hashMap.clear();
                        isAllSelected = true;
                        isEventSelected = false;
                        isGreetingsSelected = false;
                        isAppointmentSelected = false;

                        allEvents.setBackgroundResource(R.drawable.text_bg);
                        allEvents.setTextColor(Color.BLACK);
                        eventsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        eventsTextView.setBackgroundColor(Color.TRANSPARENT);
                        greetingsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        greetingsTextView.setBackgroundColor(Color.TRANSPARENT);
                        appointmentsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        appointmentsTextView.setBackgroundColor(Color.TRANSPARENT);
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                        callEventsApi(selectedMonth);

                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }

                break;
            }
            case R.id.event: {
                if (!isEventSelected) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        isFirstTime = false;
                        Utility.typeOfEvent.clear();
                        Utility.nameOfEvent.clear();
                        Utility.startDates.clear();
                        Utility.endDates.clear();
                        Utility.descriptions.clear();
                        eventTypes.clear();
                        greetingEventTypes.clear();
                        appointmentsEventTypes.clear();
                        calenderItemArrayList.clear();


                        dateArrayForSection.clear();
                        hashMap.clear();
                        isAllSelected = false;
                        isEventSelected = true;
                        isGreetingsSelected = false;
                        isAppointmentSelected = false;

                        eventsTextView.setBackgroundResource(R.drawable.text_bg);
                        eventsTextView.setTextColor(Color.BLACK);
                        allEvents.setTextColor(Color.parseColor("#dfdfdf"));
                        allEvents.setBackgroundColor(Color.TRANSPARENT);
                        greetingsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        greetingsTextView.setBackgroundColor(Color.TRANSPARENT);
                        appointmentsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        appointmentsTextView.setBackgroundColor(Color.TRANSPARENT);
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                        callEventsApi(selectedMonth);


                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
                break;
            }
            case R.id.greetings: {
                if (!isGreetingsSelected) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        isFirstTime = false;
                        Utility.typeOfEvent.clear();
                        Utility.nameOfEvent.clear();
                        Utility.startDates.clear();
                        Utility.endDates.clear();
                        Utility.descriptions.clear();
                        eventTypes.clear();
                        greetingEventTypes.clear();
                        appointmentsEventTypes.clear();
                        calenderItemArrayList.clear();

                        isgreeting = false;
                        dateArrayForSection.clear();
                        hashMap.clear();
                        isAllSelected = false;
                        isEventSelected = false;
                        isGreetingsSelected = true;
                        isAppointmentSelected = false;

                        greetingsTextView.setBackgroundResource(R.drawable.text_bg);
                        greetingsTextView.setTextColor(Color.BLACK);
                        eventsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        eventsTextView.setBackgroundColor(Color.TRANSPARENT);
                        allEvents.setTextColor(Color.parseColor("#dfdfdf"));
                        allEvents.setBackgroundColor(Color.TRANSPARENT);
                        appointmentsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                        appointmentsTextView.setBackgroundColor(Color.TRANSPARENT);
                        getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));


                        callGreetApi(selectedMonth);

                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
                break;
            }
            case R.id.appointments: {
                if (!isAppointmentSelected) {
                    if (checkInternetConnection.isConnectingToInternet())

                        appointmentWhatonOn();

                    appointmentsTextView.setBackgroundResource(R.drawable.text_bg);
                    appointmentsTextView.setTextColor(Color.BLACK);
                    eventsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                    eventsTextView.setBackgroundColor(Color.TRANSPARENT);
                    allEvents.setTextColor(Color.parseColor("#dfdfdf"));
                    allEvents.setBackgroundColor(Color.TRANSPARENT);
                    greetingsTextView.setTextColor(Color.parseColor("#dfdfdf"));
                    greetingsTextView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }

                }
                break;
            }
            case R.id.firstmonth: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    firstMonth.setClickable(false);
                    secondMonth.setClickable(true);
                    thirdMonth.setClickable(true);
                    fourthMonth.setClickable(true);
                    fifthMonth.setClickable(true);
                    sixthMonth.setClickable(true);
                    isFirstTime = false;
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -2);
                    mDate = cal.getTime();
                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);
                    if (48 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 48 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 48;
                    }
                    viewPager.setCurrentItem(48);
                    if (isEventSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {
                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {
                        addAppointments();
                    }
                    firstMonth.setBackgroundResource(R.drawable.rectangle);
                    firstMonth.setTextColor(Color.BLACK);
                    secondMonth.setTextColor(Color.WHITE);
                    thirdMonth.setTextColor(Color.WHITE);
                    fourthMonth.setTextColor(Color.WHITE);
                    sixthMonth.setTextColor(Color.WHITE);
                    fifthMonth.setTextColor(Color.WHITE);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    thirdMonth.setBackgroundColor(Color.TRANSPARENT);
                    fourthMonth.setBackgroundColor(Color.TRANSPARENT);
                    fifthMonth.setBackgroundColor(Color.TRANSPARENT);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    sixthMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;


            }
            case R.id.secondmonth: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    isFirstTime = false;
                    firstMonth.setClickable(true);
                    secondMonth.setClickable(false);
                    thirdMonth.setClickable(true);
                    fourthMonth.setClickable(true);
                    fifthMonth.setClickable(true);
                    sixthMonth.setClickable(true);
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    mDate = cal.getTime();
                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    //callCalenderApis(selectedMonth);
                    if (49 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 49 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 49;
                    }
                    viewPager.setCurrentItem(49);
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);
                    // month = (GregorianCalendar) GregorianCalendar.getInstance();


                    if (isEventSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {
                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {

                        addAppointments();

                    }

                    secondMonth.setBackgroundResource(R.drawable.rectangle);
                    firstMonth.setTextColor(Color.WHITE);
                    secondMonth.setTextColor(Color.BLACK);
                    thirdMonth.setTextColor(Color.WHITE);
                    fourthMonth.setTextColor(Color.WHITE);
                    sixthMonth.setTextColor(Color.WHITE);
                    fifthMonth.setTextColor(Color.WHITE);
                    firstMonth.setBackgroundColor(Color.TRANSPARENT);
                    thirdMonth.setBackgroundColor(Color.TRANSPARENT);
                    fourthMonth.setBackgroundColor(Color.TRANSPARENT);
                    fifthMonth.setBackgroundColor(Color.TRANSPARENT);
                    sixthMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.thirdmonth: {

                if (checkInternetConnection.isConnectingToInternet()) {
                    firstMonth.setClickable(true);
                    secondMonth.setClickable(true);
                    thirdMonth.setClickable(false);
                    fourthMonth.setClickable(true);
                    fifthMonth.setClickable(true);
                    sixthMonth.setClickable(true);
                    isFirstTime = false;
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();

                    Calendar cal = Calendar.getInstance();
                    mDate = cal.getTime();
                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    // callCalenderApis(selectedMonth);

                    if (50 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 50 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 50;
                    }
                    viewPager.setCurrentItem(50);

                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);


                    if (isEventSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {
                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {
                        addAppointments();


                    }

                    thirdMonth.setBackgroundResource(R.drawable.rectangle);
                    thirdMonth.setTextColor(Color.BLACK);
                    secondMonth.setTextColor(Color.WHITE);
                    firstMonth.setTextColor(Color.WHITE);
                    fourthMonth.setTextColor(Color.WHITE);
                    sixthMonth.setTextColor(Color.WHITE);
                    fifthMonth.setTextColor(Color.WHITE);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    firstMonth.setBackgroundColor(Color.TRANSPARENT);
                    fourthMonth.setBackgroundColor(Color.TRANSPARENT);
                    fifthMonth.setBackgroundColor(Color.TRANSPARENT);
                    sixthMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.fourthmonth: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    firstMonth.setClickable(true);
                    secondMonth.setClickable(true);
                    thirdMonth.setClickable(true);
                    fourthMonth.setClickable(false);
                    fifthMonth.setClickable(true);
                    sixthMonth.setClickable(true);
                    isFirstTime = false;
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();


                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, 1);
                    mDate = cal.getTime();

                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    if (51 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 51 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 51;
                    }
                    viewPager.setCurrentItem(51);

                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    //filterData();

                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);


                    if (isEventSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {
                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {
                        addAppointments();


                    }


                    fourthMonth.setBackgroundResource(R.drawable.rectangle);
                    fourthMonth.setTextColor(Color.BLACK);
                    secondMonth.setTextColor(Color.WHITE);
                    thirdMonth.setTextColor(Color.WHITE);
                    firstMonth.setTextColor(Color.WHITE);
                    sixthMonth.setTextColor(Color.WHITE);
                    fifthMonth.setTextColor(Color.WHITE);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    thirdMonth.setBackgroundColor(Color.TRANSPARENT);
                    firstMonth.setBackgroundColor(Color.TRANSPARENT);
                    fifthMonth.setBackgroundColor(Color.TRANSPARENT);
                    sixthMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.fifthmonth: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    firstMonth.setClickable(true);
                    secondMonth.setClickable(true);
                    thirdMonth.setClickable(true);
                    fourthMonth.setClickable(true);
                    fifthMonth.setClickable(false);
                    sixthMonth.setClickable(true);
                    isFirstTime = false;
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();


                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, 2);
                    mDate = cal.getTime();
                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    if (52 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 52 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 52;
                    }
                    viewPager.setCurrentItem(52);
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);
                    // month = (GregorianCalendar) GregorianCalendar.getInstance();
                    //setNextMonth(2);
                    //viewPager.setCurrentItem(50);


                    if (isEventSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {
                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {
                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {
                        addAppointments();


                    }

                    fifthMonth.setBackgroundResource(R.drawable.rectangle);
                    fifthMonth.setTextColor(Color.BLACK);
                    secondMonth.setTextColor(Color.WHITE);
                    thirdMonth.setTextColor(Color.WHITE);
                    fourthMonth.setTextColor(Color.WHITE);
                    sixthMonth.setTextColor(Color.WHITE);
                    firstMonth.setTextColor(Color.WHITE);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    thirdMonth.setBackgroundColor(Color.TRANSPARENT);
                    fourthMonth.setBackgroundColor(Color.TRANSPARENT);
                    firstMonth.setBackgroundColor(Color.TRANSPARENT);
                    sixthMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.sixthmonth: {
                if (checkInternetConnection.isConnectingToInternet()) {
                    firstMonth.setClickable(true);
                    secondMonth.setClickable(true);
                    thirdMonth.setClickable(true);
                    fourthMonth.setClickable(true);
                    fifthMonth.setClickable(true);
                    sixthMonth.setClickable(false);
                    isFirstTime = false;
                    Utility.typeOfEvent.clear();
                    Utility.nameOfEvent.clear();
                    Utility.startDates.clear();
                    Utility.endDates.clear();
                    Utility.descriptions.clear();
                    calenderItemArrayList.clear();
                    dateArrayForSection.clear();
                    hashMap.clear();

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, 3);
                    mDate = cal.getTime();
                    currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());

                    selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
                    currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                    String currentMonthFull = new SimpleDateFormat("MMMM").format(cal.getTime());
                    currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
                    selectedMonth = "date=" + currentYear + "-" + selectedMonthIndex + "-" + "01";
                    //callCalenderApis(selectedMonth);
                    if (53 > selectedIndexForViewpager) {
                        selectedIndexForViewpager = 53 - selectedIndexForViewpager;
                    } else {
                        selectedIndexForViewpager = selectedIndexForViewpager - 53;
                    }

                    viewPager.setCurrentItem(53);
                    getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));

                    monthForCalander.setText(currentMonthFull.toUpperCase() + " " + currentYear);
                    //month = (GregorianCalendar) GregorianCalendar.getInstance();
                    //setNextMonth(3);
                    // viewPager.setCurrentItem(50);

                    if (isEventSelected) {

                        callEventsApi(selectedMonth);

                    } else if (isGreetingsSelected) {

                        callGreetApi(selectedMonth);

                    } else if (isAllSelected) {

                        callEventsApi(selectedMonth);

                    } else if (isAppointmentSelected) {
                        addAppointments();


                    }

                    sixthMonth.setBackgroundResource(R.drawable.rectangle);
                    sixthMonth.setTextColor(Color.BLACK);
                    secondMonth.setTextColor(Color.WHITE);
                    thirdMonth.setTextColor(Color.WHITE);
                    fourthMonth.setTextColor(Color.WHITE);
                    firstMonth.setTextColor(Color.WHITE);
                    fifthMonth.setTextColor(Color.WHITE);
                    secondMonth.setBackgroundColor(Color.TRANSPARENT);
                    thirdMonth.setBackgroundColor(Color.TRANSPARENT);
                    fourthMonth.setBackgroundColor(Color.TRANSPARENT);
                    fifthMonth.setBackgroundColor(Color.TRANSPARENT);
                    firstMonth.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    if (getActivity() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                break;
            }
            case R.id.firstday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 0;

                // Change UI
                resetWeekColor();
                firstday.setBackgroundResource(R.drawable.rectangle);
                firstday.setTextColor(Color.BLACK);


                // Update Adapter
                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(firstday.getText().toString().trim());
                break;

            }
            case R.id.secondday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 1;

                resetWeekColor();
                secondday.setBackgroundResource(R.drawable.rectangle);
                secondday.setTextColor(Color.BLACK);


                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(secondday.getText().toString().trim());

                break;
            }
            case R.id.thirdday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 2;

                resetWeekColor();
                thirdday.setBackgroundResource(R.drawable.rectangle);
                thirdday.setTextColor(Color.BLACK);

                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(thirdday.getText().toString().trim());

                break;
            }
            case R.id.fourthday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 3;

                resetWeekColor();
                fourthday.setBackgroundResource(R.drawable.rectangle);
                fourthday.setTextColor(Color.BLACK);

                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(fourthday.getText().toString().trim());

                break;
            }
            case R.id.fifthday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 4;

                resetWeekColor();
                fifthday.setBackgroundResource(R.drawable.rectangle);
                fifthday.setTextColor(Color.BLACK);

                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(fifthday.getText().toString().trim());

                break;
            }
            case R.id.sixthday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 5;

                resetWeekColor();
                sixthday.setBackgroundResource(R.drawable.rectangle);
                sixthday.setTextColor(Color.BLACK);

                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(sixthday.getText().toString().trim());

                break;
            }
            case R.id.seventhday: {
                isdaYClicked = true;
                weekDaysIndexClicked = 6;

                resetWeekColor();
                seventhday.setBackgroundResource(R.drawable.rectangle);
                seventhday.setTextColor(Color.BLACK);

                customCalendarRecyclerViewAdapter.showCalendarBasedOnSelectedDate(seventhday.getText().toString().trim());
                break;
            }

        }
    }

    private void appointmentWhatonOn() {
        {
            isFirstTime = false;
            Utility.typeOfEvent.clear();
            Utility.nameOfEvent.clear();
            Utility.startDates.clear();
            Utility.endDates.clear();
            Utility.descriptions.clear();
            eventTypes.clear();
            greetingEventTypes.clear();
            appointmentsEventTypes.clear();
            calenderItemArrayList.clear();


            dateArrayForSection.clear();
            hashMap.clear();
            isAllSelected = false;
            isEventSelected = false;
            isGreetingsSelected = false;
            isAppointmentSelected = true;

            appointmentsTextView.setBackgroundResource(R.drawable.text_bg);
            appointmentsTextView.setTextColor(Color.BLACK);
            eventsTextView.setTextColor(Color.WHITE);
            eventsTextView.setBackgroundColor(Color.TRANSPARENT);
            greetingsTextView.setTextColor(Color.WHITE);
            greetingsTextView.setBackgroundColor(Color.TRANSPARENT);
            allEvents.setTextColor(Color.WHITE);
            allEvents.setBackgroundColor(Color.TRANSPARENT);
            getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
            addAppointments();


        }
    }

    private void resetWeekColor() {
        firstday.setTextColor(Color.WHITE);
        secondday.setTextColor(Color.WHITE);
        thirdday.setTextColor(Color.WHITE);
        fourthday.setTextColor(Color.WHITE);
        fifthday.setTextColor(Color.WHITE);
        sixthday.setTextColor(Color.WHITE);
        seventhday.setTextColor(Color.WHITE);
        firstday.setBackgroundColor(Color.TRANSPARENT);
        secondday.setBackgroundColor(Color.TRANSPARENT);
        thirdday.setBackgroundColor(Color.TRANSPARENT);
        fourthday.setBackgroundColor(Color.TRANSPARENT);
        fifthday.setBackgroundColor(Color.TRANSPARENT);
        sixthday.setBackgroundColor(Color.TRANSPARENT);
        seventhday.setBackgroundColor(Color.TRANSPARENT);
    }

    /* ****************Clicing Button To find Current Week Day******************************/

    public void getCurrentWeekdays() {
        ArrayList<String> daysArray = new ArrayList<>();


        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat weekDaysFormat = new SimpleDateFormat("yyyy-MM-dd");
        now.setTime(mDate);
        String currentYear = new SimpleDateFormat("yyyy").format(now.getTime());
        String currentMonthFull = new SimpleDateFormat("MMMM").format(now.getTime());
        currentMonthTextView.setText(currentMonthFull.toUpperCase() + "  " + currentYear);
        mDate.setTime(now.getTime().getTime());
        days = new String[7];
        weekDays = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            weekDays[i] = weekDaysFormat.format(now.getTime());


            String[] splitdates = days[i].split("/");
            String date = splitdates[1];
            daysArray.add(date);
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (isCalenderDayClicked) {
            ++gridvalue;
            for (int i = 0; i < daysArray.size(); i++) {

                int day = Integer.parseInt(daysArray.get(i));
                if (day == gridvalue) {

                    if (i == 0) {
                        resetWeekColor();
                        firstday.setBackgroundResource(R.drawable.text_bg);
                        firstday.setTextColor(Color.BLACK);
                    } else if (i == 1) {
                        resetWeekColor();
                        secondday.setBackgroundResource(R.drawable.text_bg);
                        secondday.setTextColor(Color.BLACK);
                    } else if (i == 2) {
                        resetWeekColor();
                        thirdday.setBackgroundResource(R.drawable.text_bg);
                        thirdday.setTextColor(Color.BLACK);
                    } else if (i == 3) {
                        resetWeekColor();
                        fourthday.setBackgroundResource(R.drawable.text_bg);
                        fourthday.setTextColor(Color.BLACK);
                    } else if (i == 4) {
                        resetWeekColor();
                        fifthday.setBackgroundResource(R.drawable.text_bg);
                        fifthday.setTextColor(Color.BLACK);
                    } else if (i == 5) {
                        resetWeekColor();
                        sixthday.setBackgroundResource(R.drawable.text_bg);
                        sixthday.setTextColor(Color.BLACK);
                    } else if (i == 6) {
                        resetWeekColor();
                        seventhday.setBackgroundResource(R.drawable.text_bg);
                        seventhday.setTextColor(Color.BLACK);
                    }
                    break;
                }

            }
        }

        firstday.setText(daysArray.get(0));
        secondday.setText(daysArray.get(1));
        thirdday.setText(daysArray.get(2));
        fourthday.setText(daysArray.get(3));
        fifthday.setText(daysArray.get(4));
        sixthday.setText(daysArray.get(5));
        seventhday.setText(daysArray.get(6));


        System.out.println(daysArray);
    }

    class BackGroundAsyncTaskForAddAppointments extends AsyncTask<Void, Void, Void> {


        public BackGroundAsyncTaskForAddAppointments() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {
            addcalanderAppointments();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                Loader.showProgressDialog(getActivity());
            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (getActivity() != null) {
                Loader.dialogDissmiss(getActivity());

                if (isFromDashBoard) {
                    customCalendarRecyclerViewAdapter = new CustomCalendarRecyclerViewAdapter(getActivity(), calenderItemArrayList);
                    simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), customCalendarRecyclerViewAdapter,
                            R.layout.list_item_header, R.id.header);
                    simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                    recyclerView.setAdapter(simpleSectionedGridAdapter);
                    simpleSectionedGridAdapter.notifyDataSetChanged();
                    customCalendarRecyclerViewAdapter.notifyDataSetChanged();
                    isFromDashBoard = false;
                } else {
                    sortData(calenderItemArrayList);
                    filterData();
                    if (calenderItemArrayList.size() > 0) {
                        sections.clear();
                        int value = 0;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                        if (dateArrayForSection.size() > 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setVisibility(View.VISIBLE);

                                }
                            });
                            sections.add(new Section(value, dateArrayForSection.get(0)));

                        } else {

                            //recyclerView.setVisibility(View.INVISIBLE);
                        }

                        for (int i = 1; i < hashMap.size(); i++) {

                            ArrayList<CalenderItem> calenderItemArrayList = hashMap.get(dateArrayForSection.get(i - 1));
                            Object[] st = calenderItemArrayList.toArray();
                            for (Object s : st) {
                                if (calenderItemArrayList.indexOf(s) != calenderItemArrayList.lastIndexOf(s)) {
                                    calenderItemArrayList.remove(calenderItemArrayList.lastIndexOf(s));
                                }
                            }
                            value = value + calenderItemArrayList.size();
                            sections.add(new Section(value, dateArrayForSection.get(i)));
                        }
                    }
                    if (simpleSectionedGridAdapter != null && customCalendarRecyclerViewAdapter != null) {
                        simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
                        simpleSectionedGridAdapter.notifyDataSetChanged();
                        customCalendarRecyclerViewAdapter.notifyDataSetChanged();

                    }
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        }
    }
    /* ****************Add Appointment Calling ******************************/

    public synchronized void addcalanderAppointments() {

        Utility.typeOfEvent.clear();
        Utility.nameOfEvent.clear();
        Utility.startDates.clear();
        Utility.endDates.clear();
        Utility.descriptions.clear();
        //calenderItemArrayList.clear();
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        if (isMonthSelected == true) {

            try {
                String[] splitWeekdate = selectedMonth.split("=");
                weekdate = form.parse(splitWeekdate[1]);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            String dateStart = mFormatter.format(weekdate);
            String[] splitDate = dateStart.split("-");
            String month = splitDate[1];
            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.MONTH, 0);

            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            String startDate = selectedMonthIndex + "/" + "01" + "/" + splitDate[0];
            String endDate = selectedMonthIndex + "/" + String.valueOf(max) + "/" + splitDate[0];
            appointsmentsAray = new ArrayList<>();
            raiseRuntimePermisionForcalender();
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                appointsmentsAray = CalenderUtility.readCalendarEvent(getActivity(), startDate, endDate);
            }

            System.out.println(appointsmentsAray);
            ArrayList<String> startDateArray = CalenderUtility.getStartDateArray();
            ArrayList<String> endDateArray = CalenderUtility.getEnddateArray();
            for (int i = 0; i < startDateArray.size(); i++) {
                String apointMentDate = startDateArray.get(i);
                String[] splitAppointmentsDates = apointMentDate.split(" ");
                String datePart = splitAppointmentsDates[0];
                String[] splitDatePart = datePart.split("/");
                String dateForCalender = splitDatePart[2] + "-" + splitDatePart[1] + "-" + splitDatePart[0];
                Utility.addEvent(appointsmentsAray.get(i), dateForCalender, "appointments");

                refreshCalendar();

            }

            if (appointsmentsAray.size() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataFoundTextview.setVisibility(View.VISIBLE);
                        addAppointmentsButton.setVisibility(View.VISIBLE);
                    }
                });

            } else {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataFoundTextview.setVisibility(View.GONE);
                        addAppointmentsButton.setVisibility(View.GONE);
                    }
                });

            }
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String currentDate = new SimpleDateFormat("dd").format(cal.getTime());
            String selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
            String currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
            String todaysdate = "01" + "-" + selectedMonthIndex + "-" + currentYear;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

            for (int i = 0; i < appointsmentsAray.size(); i++) {
                try {
                    String startDateOfEvent = startDateArray.get(i);
                    String[] eventDate = startDateOfEvent.split(" ");
                    String[] eventDateWithoutSlash = eventDate[0].split("/");
                    String event_date = eventDateWithoutSlash[0] + "-" + eventDateWithoutSlash[1] + "-" + eventDateWithoutSlash[2];
                    Date date1 = sdf.parse(todaysdate);
                    Date date2 = sdf.parse(event_date);
                    if (date2.compareTo(date1) >= 0) {
                        CalenderItem calenderItem = new CalenderItem("", appointsmentsAray.get(i), startDateArray.get(i), "", false, true, "", "", "", "", false, "", "");
                        calenderItemArrayList.add(calenderItem);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addAppointmentsButton.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addAppointmentsButton.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }

        } else {
            ArrayList<String> appointsmentsAray = new ArrayList<>();
            raiseRuntimePermisionForcalender();
            permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                String startDate = days[0];
                String endDate = days[6];
                appointsmentsAray = CalenderUtility.readCalendarEvent(getActivity(), startDate, endDate);
            }
            System.out.println(appointsmentsAray);
            ArrayList<String> startDateArray = CalenderUtility.getStartDateArray();
            ArrayList<String> endDateArray = CalenderUtility.getEnddateArray();

            for (int i = 0; i < startDateArray.size(); i++) {
                String apointMentDate = startDateArray.get(i);
                String[] splitAppointmentsDates = apointMentDate.split(" ");
                String datePart = splitAppointmentsDates[0];
                String[] splitDatePart = datePart.split("/");
                String dateForCalender = splitDatePart[2] + "-" + splitDatePart[1] + "-" + splitDatePart[0];
                Utility.addEvent(appointsmentsAray.get(i), dateForCalender, "appointments");

                refreshCalendar();

            }
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            String currentDate = new SimpleDateFormat("dd").format(cal.getTime());
            String selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
            String currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
            String todaysdate = currentDate + "-" + selectedMonthIndex + "-" + currentYear;

            if (appointsmentsAray.size() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataFoundTextview.setVisibility(View.VISIBLE);
                        addAppointmentsButton.setVisibility(View.VISIBLE);
                    }
                });

            } else {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataFoundTextview.setVisibility(View.GONE);
                        addAppointmentsButton.setVisibility(View.GONE);
                    }
                });

            }
            for (int i = 0; i < appointsmentsAray.size(); i++) {

                try {
                    String startDateOfEvent = startDateArray.get(i);
                    String[] eventDate = startDateOfEvent.split(" ");
                    String[] eventDateWithoutSlash = eventDate[0].split("/");
                    String event_date = eventDateWithoutSlash[0] + "-" + eventDateWithoutSlash[1] + "-" + eventDateWithoutSlash[2];
                    Date date1 = sdf.parse(todaysdate);
                    Date date2 = sdf.parse(event_date);
                    if (date2.compareTo(date1) >= 0) {
                        final CalenderItem calenderItem = new CalenderItem("", appointsmentsAray.get(i), startDateArray.get(i), "", false, true, "", "", "", "", false, "", "");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                calenderItemArrayList.add(calenderItem);
                                addAppointmentsButton.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addAppointmentsButton.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }


        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (calenderItemArrayList.size() > 0) {
                    noDataFoundTextview.setVisibility(View.GONE);
                    addAppointmentsButton.setVisibility(View.GONE);
                } else {
                    addAppointmentsButton.setVisibility(View.VISIBLE);
                    noDataFoundTextview.setVisibility(View.GONE);
                }
            }
        });

        if (isFromDashBoard) {
            int value = 0;
            sections.clear();
            sections.add(new Section(0, dateArrayForSection.get(0)));
            isAllSelected = false;

            for (int i = 1; i < hashMap.size(); i++) {

                ArrayList<CalenderItem> calenderItemArrayList = hashMap.get(dateArrayForSection.get(i - 1));

                value = value + calenderItemArrayList.size();
                sections.add(new Section(value, dateArrayForSection.get(i)));


            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appointsmentsAray = CalenderUtility.readCalendarEvent(getActivity(), days[0], days[6]);
                    System.out.println(appointsmentsAray);
                } else {
                }
                return;
            }


        }
    }

    /* ****************Google Calendar  Run time permission******************************/

    public void raiseRuntimePermisionForcalender() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALENDAR)) {
            } else {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CALENDAR},
                        1);
            }
        }
    }

    private String dateformat(String created) {

        if (created.equals("01")) {
            created = " Jan";
        }
        if (created.equals("02")) {
            created = " Feb";
        }
        if (created.equals("03")) {
            created = " Mar";
        }
        if (created.equals("04")) {
            created = " Apr";
        }
        if (created.equals("05")) {
            created = " May";
        }
        if (created.equals("06")) {
            created = " Jun";
        }
        if (created.equals("07")) {
            created = " Jul";
        }
        if (created.equals("08")) {
            created = " Aug";
        }
        if (created.equals("09")) {
            created = " Sep";
        }
        if (created.equals("10")) {
            created = " Oct";
        }
        if (created.equals("11")) {
            created = " Nov";
        }
        if (created.equals("12")) {
            created = " Dec";
        }

        return created;
    }
  /* ****************Appointment Calling BackGround Services ******************************/

    public void addAppointments() {
        new BackGroundAsyncTaskForAddAppointments().execute();


    }


    @Override
    public void updateText(int index) {
        headerDateTextView.setText(dateArrayForSection.get(index));
    }


    //********************************BigCalendar Adaptor *************************************//


    class MyCalenderAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public MyCalenderAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 300;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mLayoutInflater.inflate(R.layout.pager_item, container, false);


            gridview = (GridView) view.findViewById(R.id.gridview);
            gridview.setAdapter(adapter);
            handler = new Handler();
            handler.post(calendarUpdater);
            gridview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Loader.dialogDissmiss(getActivity());
                    calenderItemArrayList.clear();

                    desc = new ArrayList<String>();
                    date = new ArrayList<String>();
                    ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                    String selectedGridDate = CalendarAdapter.dayString
                            .get(position);
                    String[] separatedTime = selectedGridDate.split("-");
                    String gridvalueString = separatedTime[2].replaceFirst("^0*",
                            "");
                    gridvalue = Integer.parseInt(gridvalueString);
                    isCalenderDayClicked = true;
                    month.add(GregorianCalendar.DATE, --gridvalue);
                    mDate = month.getTime();
                    isMonthSelected = false;
                    isdaYClicked = false;
                    weekView.setVisibility(View.VISIBLE);
                    monthLayout.setVisibility(View.INVISIBLE);
                    weekTextView.setTextColor(Color.parseColor("#000000"));
                    weekTextView.setBackgroundResource(R.drawable.text_bg);
                    monthTextView.setTextColor(Color.parseColor("#ffffff"));
                    monthTextView.setBackgroundColor(Color.TRANSPARENT);
                    getCurrentWeekdays();
                    --gridvalue;
                    month.add(GregorianCalendar.DATE, -gridvalue);
                    String weekDay = weekDays[6];

                    String[] splitWeekDay = weekDays[4].split("-");
                    calenderSelectedDate = "date=" + weekDays[1];
                    selectedMonth = calenderSelectedDate;
                    currentYear = splitWeekDay[0];
                    selectedMonthIndex = splitWeekDay[1];
                    currentMonth = new SimpleDateFormat("MMM").format(month.getTime());
                    //getNumberOfWeeks(Integer.parseInt(currentYear), Integer.parseInt(selectedMonthIndex));
                    getNumberOfWeeks(Integer.parseInt(splitWeekDay[0]), Integer.parseInt(splitWeekDay[1]));
                    monthButtonsSelection(selectedIndexForViewpager);
                    if (isEventSelected) {
                        callEventsApi(calenderSelectedDate);
                    } else if (isGreetingsSelected) {
                        callGreetApi(calenderSelectedDate);
                    } else if (isAllSelected) {

                        callEventsApi(calenderSelectedDate);
                    } else if (isAppointmentSelected) {
                        addAppointments();

                    }
                    if ((gridvalue > 10) && (position < 8)) {
                    } else if ((gridvalue < 7) && (position > 28)) {
                    }
                    ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                    for (int i = 0; i < Utility.startDates.size(); i++) {
                        if (Utility.startDates.get(i).equals(selectedGridDate)) {
                            desc.add(Utility.nameOfEvent.get(i));
                        }
                    }

                    if (desc.size() > 0) {
                        for (int i = 0; i < desc.size(); i++) {
                            TextView rowTextView = new TextView(getActivity());
                            rowTextView.setText("Event:" + desc.get(i));
                            rowTextView.setTextColor(Color.BLACK);
                            rLayout.addView(rowTextView);

                        }

                    }

                    desc = null;

                }

            });

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
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
            if (preSelectedItem.equals("all")) {
                callEventsApi(selectedMonth);
            } else if (preSelectedItem.equals("events")) {
                callEventsApi(selectedMonth);
            } else if (preSelectedItem.equals("greetings")) {
                callGreetApi(selectedMonth);
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }
}
