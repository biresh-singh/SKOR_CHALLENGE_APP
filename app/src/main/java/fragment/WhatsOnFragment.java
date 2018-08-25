package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import InternetConnection.CheckInternetConnection;
import activity.event.EventDetailActivity;
import activity.rewardz.RewardzDetailActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import adaptor.WhatsOnListAdapter;
import bean.CalenderUtility;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import gcm.MyfireBaseIntentService;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import me.leolin.shortcutbadger.ShortcutBadger;
import utils.AppController;
import utils.CircleImageView;
import utils.DynamicListView;
import utils.GetDeviceInfo;
import utils.Loader;


public class WhatsOnFragment extends Fragment {
    DynamicListView ddListView;
    public static HashMap<String, JSONArray> hashMap = new HashMap<String, JSONArray>();
    ArrayList<String> keysArrayList = new ArrayList<>();
    LinearLayout panel;
    ArrayList<String> pkArrayListUsingKey = new ArrayList<>();
    ImageView gotoCalender;
    AlertDialog alertDialog1;
    String versionName1;

    WhatsOnListAdapter whatsOnListAdapter;
    SimpleDateFormat mFormatter = new SimpleDateFormat("MM/dd/yyyy");
    public static ArrayList<String> calenderEvents = new ArrayList<>();
    public static ArrayList<String> stateArrayList = new ArrayList<>();
    Dialog dialog;
    Activity activity;
    SharedPreferences sharedPreferences, pushNotificationDataSharedPreferences;
    boolean isFirstTime;
    boolean ismatchpk = false;
    CheckInternetConnection checkInternetConnection;
    AndroidDeviceNames deviceNames;
    GetDeviceInfo getDeviceId;
    TextView pointsSummeryTextView;
    String deviceId, gcmRegId;
    ImageView search;
    int count = 0;
    SharedDatabase sharedDatabase;
    String token;

    CoordinatorLayout coordinatorLayout;
    String image, message, pk, object_type, object_id;
    public static String PACKAGE_NAME;
    TextView whatsOnTextView, searchcounter;
    private String pointSummary = "initial";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.whats_on, null);
        getDeviceId = new GetDeviceInfo(getActivity());
        deviceId = getDeviceId.getDeviceId();
        SharedPreferences gcmRegIdpreferences = activity.getSharedPreferences("gcmRegIdpreferences", Context.MODE_PRIVATE);
        gcmRegId = gcmRegIdpreferences.getString("gcmRegId", "");
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        deviceNames = new AndroidDeviceNames(getActivity());
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        ddListView = (DynamicListView) view.findViewById(R.id.listview);
        sharedPreferences = activity.getSharedPreferences("what_on_item_ordering_prefs", 0);
        pushNotificationDataSharedPreferences = activity.getSharedPreferences("pushnotificationkey", Context.MODE_PRIVATE);
        message = pushNotificationDataSharedPreferences.getString("message", "");
        checkInternetConnection = new CheckInternetConnection(activity);
        whatsOnTextView = (TextView) view.findViewById(R.id.whats_on_textview);
        searchcounter = (TextView) view.findViewById(R.id.searchcounter);
        search = (ImageView) view.findViewById(R.id.search);
        PACKAGE_NAME = getActivity().getPackageName();
        if (Constants.BASEURL.equals("")) {
            String baseUrl = getResources().getString(R.string.base_url);

            System.out.println("package name  " + PACKAGE_NAME + " baseurl " + baseUrl);
            Constants.BASEURL = baseUrl;
        }
        image = pushNotificationDataSharedPreferences.getString("image", "");
        pk = pushNotificationDataSharedPreferences.getString("pk", "");
        object_type = pushNotificationDataSharedPreferences.getString("object_type", "");
        object_id = pushNotificationDataSharedPreferences.getString("object_id", "");
        whatsOnTextView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    NavigationDrawerFragment.type = "all";
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                    navigataionCallback.onNavigationDrawerItemSelected(5, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
        searchcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    NavigationDrawerFragment.type = "all";
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                    navigataionCallback.onNavigationDrawerItemSelected(5, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }

                }
            }
        });


        /* ********************checking push notification message**************************/


        if (MyfireBaseIntentService.gcmintent != false) {
            try {
                notificationAlert();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        pointsSummeryTextView = (TextView) view.findViewById(R.id.points_summery);
        pointsSummeryTextView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        whatsOnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        pointsSummeryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetConnection.isConnectingToInternet()) {
                    if (pointSummary.equals("succesfully")) {
                        pointsSummeryTextView.setBackgroundResource(R.drawable.text_bg);
                        whatsOnTextView.setBackgroundColor(Color.TRANSPARENT);
                        NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                        navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                        navigataionCallback.onNavigationDrawerItemSelected(2, "all");
                    }

                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }

                }

            }
        });
        panel = (LinearLayout) view.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        String nextDate = "";
        String currentDate = "";
        try {
            currentDate = mFormatter.format(new Date());
            Date date = new Date();
            Calendar today = Calendar.getInstance();
            date = mFormatter.parse(currentDate);
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = mFormatter.format(today.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CALENDAR);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            calenderEvents = CalenderUtility.readCalendarEvent(activity, currentDate, nextDate);
        }
        JSONArray jsonArray = new JSONArray();
        isFirstTime = sharedPreferences.getBoolean("is_first_time", false);
        if (isFirstTime == false) {
            keysArrayList.add("Event");
            keysArrayList.add("Announcement");
//            keysArrayList.add("2");
            keysArrayList.add("greetings");
            keysArrayList.add("Appointments");
        } else {
            Gson gson = new Gson();


            String json = sharedPreferences.getString("order_of_items", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> arrayList = gson.fromJson(json, type);
            keysArrayList.addAll(arrayList);
        }


        hashMap.put("Announcement", jsonArray);
//        hashMap.put("2", jsonArray);
        hashMap.put("greetings", jsonArray);
        hashMap.put("Event", jsonArray);
        hashMap.put("Appointments", jsonArray);


        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callwhatsOnApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }
        gotoCalender = (ImageView) view.findViewById(R.id.goto_calender);
        gotoCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerFragment.type = "all";
                NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                navigataionCallback.onNavigationDrawerItemSelected(3, "all");
            }
        });

        return view;
    }

    public void notificationAlert() {
        if (!object_id.equals("null") && !object_id.equals("")) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View popupview = layoutInflater.inflate(R.layout.push_notification_popup, null);
            final TextView popupeventmessage = (TextView) popupview.findViewById(R.id.popupeventmessage);
            TextView popupeventmessage1 = (TextView) popupview.findViewById(R.id.popupeventmessage1);
            CircleImageView eventImage = (CircleImageView) popupview.findViewById(R.id.userimage);
            popupeventmessage1.setText(message);
            Button view_morelable_center = (Button) popupview.findViewById(R.id.view_morelable_center);

            Button viewMoreDetail = (Button) popupview.findViewById(R.id.view_morelable);
            if (object_type.equals("None")) {
                popupeventmessage.setText("NONE");
                viewMoreDetail.setText("OK");
                view_morelable_center.setText("OK");
                popupeventmessage.setVisibility(View.GONE);
            } else if (object_type.equals("Announcement") || object_type.equals("2")) {
                popupeventmessage.setText("Announcement");
                viewMoreDetail.setText("OK");
                view_morelable_center.setText("OK");

            } else if (object_type.equals("Point allocation")) {
                popupeventmessage.setText("Point allocation");
                viewMoreDetail.setText("OK");
                view_morelable_center.setText("OK");

            } else if (object_type.equals("Repeated event")) {
                popupeventmessage.setText("");
                viewMoreDetail.setText("OK");
                view_morelable_center.setText("OK");

            } else if (object_type.equals("Event")) {
                popupeventmessage.setText("Event");
                view_morelable_center.setText("View Detail");

            } else if (object_type.equals("Reward")) {
                popupeventmessage.setText("Reward");
                view_morelable_center.setText("View Detail");

            }

            Button submit = (Button) popupview.findViewById(R.id.submit);
            if (image.equals("null") || image.equals("")) {
                if (object_type.equals("None")) {
                    eventImage.setImageResource(R.drawable.announcements);
                } else if (object_type.equals("Announcement") || object_type.equals("2")) {
                    eventImage.setImageResource(R.drawable.announcements);
                } else if (object_type.equals("Repeated event")) {
                    eventImage.setImageResource(R.drawable.greeting1);
                } else if (object_type.equals("Event")) {
                    eventImage.setImageResource(R.drawable.trainingtcon);
                } else if (object_type.equals("Point allocation")) {
                    eventImage.setImageResource(R.drawable.birthday);
                } else if (object_type.equals("Reward")) {
                    eventImage.setImageResource(R.drawable.greeting1);


                }
            } else {
                Glide.with(getActivity()).load(Constants.BASEURL + image).into(eventImage);

            }

            RelativeLayout logo = (RelativeLayout) popupview.findViewById(R.id.logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(popupview);
            alertDialog1 = builder.create();
            alertDialog1.show();
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                    readNotificationServices(pk);
                }
            });


            int messagelength = message.length();
            if (messagelength <= 70) {
                view_morelable_center.setVisibility(View.VISIBLE);
                viewMoreDetail.setVisibility(View.GONE);

            }
            view_morelable_center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (object_type.equals("None")) {
                        popupeventmessage.setText("NONE");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                    } else if (object_type.equals("Announcement") || object_type.equals("2")) {
                        popupeventmessage.setText("Announcement");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Repeated event")) {
                        popupeventmessage.setText("Repeated event");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Point allocation")) {
                        popupeventmessage.setText("Point allocation");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Event")) {
                        if (checkInternetConnection.isConnectingToInternet()) {
                            readNotificationServices(pk);
                            alertDialog1.dismiss();
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }

                    } else if (object_type.equals("Reward")) {
                        alertDialog1.dismiss();
                        SharedPreferences prefs = activity.getSharedPreferences("eligibleRewardPkArrayListKey", 0);
                        Set<String> set = new HashSet<String>();
                        set = prefs.getStringSet("pkArrayListSetKey", null);
                        List<String> strings = new ArrayList<String>(set);
                        int samplelenght = strings.size();
                        for (int i = 0; i < strings.size(); i++) {
                            if (object_id.equals(strings.get(i))) {
                                ismatchpk = true;
                                if (checkInternetConnection.isConnectingToInternet()) {
                                    readNotificationServices(pk);
                                } else {
                                    if (coordinatorLayout != null) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(Color.RED);
                                        snackbar.show();
                                    }

                                }
                                break;
                            } else {
                                if (i == (samplelenght - 1)) {
                                    if (ismatchpk != true) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            readNotificationServices(pk);
                                        } else {
                                            if (coordinatorLayout != null) {
                                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                View snackBarView = snackbar.getView();
                                                snackBarView.setBackgroundColor(Color.RED);
                                                snackbar.show();
                                            }

                                        }
                                    }
                                }
                            }
                        }


                    }
                }
            });
            viewMoreDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (object_type.equals("None")) {
                        popupeventmessage.setText("NONE");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Announcement") || object_type.equals("2")) {
                        popupeventmessage.setText("Announcement");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Repeated event")) {
                        popupeventmessage.setText("Repeated event");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    } else if (object_type.equals("Point allocation")) {
                        popupeventmessage.setText("Point allocation");
                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                    } else if (object_type.equals("Event")) {
                        alertDialog1.dismiss();
                        if (checkInternetConnection.isConnectingToInternet()) {
                            readNotificationServices(pk);
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                    } else if (object_type.equals("Reward")) {
                        alertDialog1.dismiss();
                        SharedPreferences prefs = activity.getSharedPreferences("eligibleRewardPkArrayListKey", 0);
                        Set<String> set = new HashSet<String>();
                        set = prefs.getStringSet("pkArrayListSetKey", null);
                        List<String> strings = new ArrayList<String>(set);
                        int samplelenght = strings.size();
                        for (int i = 0; i < strings.size(); i++) {
                            if (object_id.equals(strings.get(i))) {
                                ismatchpk = true;
                                if (checkInternetConnection.isConnectingToInternet()) {
                                    readNotificationServices(pk);
                                } else {
                                    if (coordinatorLayout != null) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(Color.RED);
                                        snackbar.show();
                                    }
                                }
                                break;
                            } else {
                                if (i == (samplelenght - 1)) {
                                    if (ismatchpk != true) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            readNotificationServices(pk);
                                        } else {
                                            if (coordinatorLayout != null) {
                                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                View snackBarView = snackbar.getView();
                                                snackBarView.setBackgroundColor(Color.RED);
                                                snackbar.show();
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }
                }
            });
            SharedPreferences preferences = activity.getSharedPreferences("pushnotificationkey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = preferences.edit();
            editor1.putString("message", "");
            editor1.putString("object_id", "");
            editor1.putString("object_type", "");
            editor1.putString("from", "");
            editor1.putString("image", "");
            editor1.putString("pk", "");
            editor1.commit();

        }
    }

    public void readNotificationServices(String position) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        SharedPreferences sharedPreferencesToken = activity.getSharedPreferences("token", 1);
        String token = sharedPreferencesToken.getString("token", "");
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.PUSH_NOTIFICATION + position + "/mark_read/";
        client.put(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {

                        if (object_type.equals("None")) {
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    callwhatsOnApi();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }
                        } else if (object_type.equals("Announcement") || object_type.equals("2")) {
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    callwhatsOnApi();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }
                        } else if (object_type.equals("Point allocation")) {
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    callwhatsOnApi();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }
                        } else if (object_type.equals("Repeated event")) {
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    callwhatsOnApi();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }
                        } else if (object_type.equals("Event")) {
                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                            intent.putExtra("id", object_id);
                            getContext().startActivity(intent);
                        } else if (object_type.equals("Reward")) {
                            if (ismatchpk != true) {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Data not Found!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            } else {
                                Intent intent = new Intent(getActivity(), RewardzDetailActivity.class);
                                intent.putExtra("pk", object_id);
                                getContext().startActivity(intent);
                            }

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(getActivity());
                        }

                        if (statusCode == 500) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                        if (checkInternetConnection.isConnectingToInternet()) {
                            try {
                                callwhatsOnApi();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }
                    }

                }
        );


    }


    /* ******************* calling reward detail api to compare pk id***************************/

    public void callRewardzPkApi() {
        if (getActivity() != null) {
            String authProvider = SettingsManager.getInstance().getAuthProvider();

            Loader.showProgressDialog(getActivity());
            HashMap<String, String> paramMap = new HashMap<String, String>();
            RequestParams params = new RequestParams(paramMap);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.setTimeout(80000);
            client.addHeader("USER-AGENT", AppController.useragent);
            client.addHeader("connection", "Keep-Alive");
            client.addHeader("Authorization", authProvider + " " + token);
            client.addHeader("Content-Type", "application/json");
            client.get(Constants.BASEURL + Constants.REWARDZ_DETAIL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    super.onSuccess(statusCode, headers, jsonObject);
                    Loader.dialogDissmiss(getActivity());
                    try {

                        pointSummary = "succesfully";
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject rewardzJson = jsonArray.getJSONObject(i);
                            String pk = rewardzJson.getString("pk");
                            pkArrayListUsingKey.add(pk);

                        }

                        Set<String> set = new HashSet<String>();
                        set.addAll(pkArrayListUsingKey);
                        sharedDatabase.setPkArrayListKey(set);
                        count = 1;

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Loader.dialogDissmiss(getActivity());
                    pointSummary = "succesfully";
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }


    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("WhatsOnToday");
        if (count == 1) {
            if (checkInternetConnection.isConnectingToInternet()) {
                try {
                    callwhatsOnApi();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (coordinatorLayout != null) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();


                }
            }
        }
    }

    /* ********************calling whats on Api **************************/
    public void callwhatsOnApi() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(8000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.WHATS_ON, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);

                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
                }
                try {
                    if (getActivity() != null) {
                        ShortcutBadger.applyCount(getActivity(), Integer.parseInt(jsonObject.getString("badge_counter")));
                    }
                    String pklength1 = jsonObject.getString("badge_counter");
                    if (!pklength1.equals("0") && !pklength1.equals("null") && !pklength1.equals("")) {
                        searchcounter.setVisibility(View.VISIBLE);
                        searchcounter.setText("" + pklength1);
                        if (getActivity() != null) {
                            ShortcutBadger.applyCount(getActivity(), Integer.parseInt(pklength1));
                        }
                    } else {
                        searchcounter.setVisibility(View.GONE);
                    }
                } catch (JSONException js) {
                    js.printStackTrace();
                }

                try {
                    Iterator keys = jsonObject.keys();


                    while (keys.hasNext()) {

                        String currentDynamicKey = (String) keys.next();
                        if (!currentDynamicKey.equals("badge_counter")) {
                            if (currentDynamicKey.equals("greetings")) {

                                JSONObject greetingJsonObjects = jsonObject.getJSONObject("greetings");
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(greetingJsonObjects);
                                hashMap.put(currentDynamicKey, jsonArray);
                                System.out.println(hashMap);
                            } else {

                                JSONArray currentDynamicValue = jsonObject.getJSONArray(currentDynamicKey);
                                hashMap.put(currentDynamicKey, currentDynamicValue);
                                System.out.println("json is" + currentDynamicValue);
                            }
                        }
                    }
                    System.out.println("Hash Map is" + hashMap);
                    pointsSummeryTextView.setClickable(true);


                    if (getActivity() != null) {
                        whatsOnListAdapter = new WhatsOnListAdapter(activity, R.layout.whats_on_adapter, keysArrayList);
                        ddListView.setItemsInList(keysArrayList);
                        ddListView.setAdapter(whatsOnListAdapter);
                        ddListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    }


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if (count == 0) {
                    callRewardzPkApi();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (dialog.isShowing() || dialog != null) {
                    dialog.dismiss();
                    dialog.hide();
                }
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }

                if (statusCode == 0) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Bad Network Connection!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                System.out.print("net connection   " + errorResponse);
                if (statusCode == 500) {
                    if (coordinatorLayout != null) {
                        Toast.makeText(getActivity(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onRetry(int retryNo) {

            }
        });

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
            readNotificationServices(pk);
        } else {
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(getActivity());
                }
            });
        }
    }


}
