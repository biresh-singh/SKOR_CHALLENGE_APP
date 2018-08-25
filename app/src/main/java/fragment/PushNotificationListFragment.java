package fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import InternetConnection.CheckInternetConnection;
import activity.event.EventDetailActivity;
import activity.newsfeed.NewsDetailActivity;
import activity.rewardz.RewardzDetailActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import adaptor.PushNotificationAdaptor;
import bean.PushNotificationItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import me.leolin.shortcutbadger.ShortcutBadger;
import utils.AppController;
import utils.CircleImageView;
import utils.Loader;

public class PushNotificationListFragment extends Fragment {
    SwipeMenuListView swipeMenuListView;
    ArrayList<PushNotificationItem> notificationListItems = new ArrayList<>();
    ArrayList<String> pkarraylist = new ArrayList<>();
    public static boolean ismatchpk = false;
    LinearLayout menupanel;
    RelativeLayout titleBar;
    int count = 0;
    AlertDialog alertDialog1;
    private List<String> sample = null;
    LinearLayout swipetodeletelayout;
    int appVersion = 0;
    String objectType = "";
    CheckInternetConnection checkInternetConnection;
    public PushNotificationItem notificationListItem;
    PushNotificationAdaptor pushNotificationAdaptor;
    AndroidDeviceNames deviceNames;
    CoordinatorLayout coordinatorLayout;
    public SharedDatabase sharedDatabase;
    public String token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.push_notification_list, null);
        titleBar = (RelativeLayout) view.findViewById(R.id.titlebar);
        swipetodeletelayout = (LinearLayout) view.findViewById(R.id.swipe_todelete_layout);
        checkInternetConnection = new CheckInternetConnection(getActivity());
        deviceNames = new AndroidDeviceNames(getActivity());
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        Set<String> set = sharedDatabase.getPkArrayListKey();

        if (set != null) {
            sample = new ArrayList<String>(set);
        }

        sharedDatabase.setPosition(5);
        sharedDatabase.setType("all");

        swipeMenuListView = (SwipeMenuListView) view.findViewById(R.id.listView);
        swipeMenuListView.setDivider(null);

        /* ********************to given functionality to swipe and delete**************************/

        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                getNotificationServices();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }

            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem swipeMenuPushNotificationItem = new SwipeMenuItem(getActivity());
                    swipeMenuPushNotificationItem.setBackground(new ColorDrawable(Color.parseColor("#ED5351")));
                    swipeMenuPushNotificationItem.setWidth(dp2px(90));
                    swipeMenuPushNotificationItem.setTitle("Delete");
                    swipeMenuPushNotificationItem.setTitleSize(18);
                    swipeMenuPushNotificationItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(swipeMenuPushNotificationItem);
                }
            };

            swipeMenuListView.setMenuCreator(creator);
            swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            if (checkInternetConnection.isConnectingToInternet()) {
                                notificationListItems.remove(position);
                                String pkposition = pkarraylist.get(position);
                                pushNotificationAdaptor.notifyDataSetChanged();
                                deleteNotificationServices(pkposition);
                                break;
                            } else {
                                if (coordinatorLayout != null) {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(Color.RED);
                                    snackbar.show();
                                }
                            }

                    }
                    return false;
                }
            });


            swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
            swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            swipeMenuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                @Override
                public void onSwipeStart(int position) {
                    // swipe start
                    System.out.println("setOnSwipeListener onSwipeStart");

                }

                @Override
                public void onSwipeEnd(int position) {

                    System.out.println("setOnSwipeListener onSwipeEnd");

                }
            });
            menupanel = (LinearLayout) view.findViewById(R.id.menupanel);
            menupanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedDatabase.setPosition(5);
                    sharedDatabase.setType("all");
                    MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });


        } else {
            if (getResources() != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        }
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        {
                            if (notificationListItems.get(position).mObject_type.equals("Event")) {
                                if (checkInternetConnection.isConnectingToInternet()) {
                                    objectType = "Event";
                                    readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                } else {
                                    if (getResources() != null) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(Color.RED);
                                        snackbar.show();
                                    }

                                }
                            } else if (notificationListItems.get(position).mObject_type.equals("Feed")) {
                                objectType = "Feed";
                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                            } else {
                                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                View popupview = layoutInflater.inflate(R.layout.push_notification_popup, null);
                                final TextView popupeventmessage = (TextView) popupview.findViewById(R.id.popupeventmessage);
                                TextView popupeventmessage1 = (TextView) popupview.findViewById(R.id.popupeventmessage1);
                                CircleImageView eventImage = (CircleImageView) popupview.findViewById(R.id.userimage);
                                String message = notificationListItems.get(position).mMessage;
                                int messagelength = message.length();
                                Button view_morelable_center = (Button) popupview.findViewById(R.id.view_morelable_center);

                                popupeventmessage1.setText(notificationListItems.get(position).mMessage);

                                Button view_morelable = (Button) popupview.findViewById(R.id.view_morelable);
                                if (notificationListItems.get(position).mObject_type.equals("None")) {
                                    popupeventmessage.setText("NONE");
                                    view_morelable.setText("OK");
                                    view_morelable_center.setText("OK");
                                    popupeventmessage.setVisibility(View.GONE);

                                } else if (notificationListItems.get(position).mObject_type.equals("Announcement")) {
                                    popupeventmessage.setText("Announcement");
                                    view_morelable_center.setText("OK");
                                    view_morelable.setText("OK");

                                } else if (notificationListItems.get(position).mObject_type.equals("Repeated event")) {
                                    popupeventmessage.setText("");
                                    view_morelable_center.setText("OK");
                                    view_morelable.setText("OK");
                                }
//                            else if (notificationListItems.get(position).mObject_type.equals("Event")) {
//                                popupeventmessage.setText("Event");
//                                view_morelable_center.setText("View Detail");
//
//                            }
                                else if (notificationListItems.get(position).mObject_type.equals("Point allocation")) {
                                    popupeventmessage.setText("Point allocation");
                                    view_morelable_center.setText("OK");

                                } else if (notificationListItems.get(position).mObject_type.equals("Reward")) {
                                    popupeventmessage.setText("Reward");
                                    view_morelable_center.setText("View Detail");

                                }
                                if (notificationListItems.get(position).mImage.equals("null") || notificationListItems.get(position).mImage.equals("")) {

                                    if (notificationListItems.get(position).mObject_type.equals("None")) {
                                        eventImage.setImageResource(R.drawable.announcements);

                                    } else if (notificationListItems.get(position).mObject_type.equals("Announcement")) {
                                        eventImage.setImageResource(R.drawable.announcements);

                                    } else if (notificationListItems.get(position).mObject_type.equals("Repeated event")) {
                                        eventImage.setImageResource(R.drawable.greeting1);

                                    } else if (notificationListItems.get(position).mObject_type.equals("Point allocation")) {
                                        eventImage.setImageResource(R.drawable.birthday);

                                    } else if (notificationListItems.get(position).mObject_type.equals("Event")) {
                                        eventImage.setImageResource(R.drawable.trainingtcon);

                                    } else if (notificationListItems.get(position).mObject_type.equals("Reward")) {
                                        eventImage.setImageResource(R.drawable.greeting1);

                                    }

                                } else {
                                    Glide.with(getActivity()).load(notificationListItems.get(position).mImage).into(eventImage);

                                }
                                RelativeLayout logo = (RelativeLayout) popupview.findViewById(R.id.logo);
                                logo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        alertDialog1.dismiss();


                                    }
                                });
                                if (messagelength <= 70) {
                                    view_morelable_center.setVisibility(View.VISIBLE);
                                    view_morelable.setVisibility(View.GONE);

                                }
                                builder.setView(popupview);
                                alertDialog1 = builder.create();
                                alertDialog1.show();
                                view_morelable_center.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog1.dismiss();

                                        if (notificationListItems.get(position).mObject_type.equals("None")) {
                                            popupeventmessage.setText("NONE");
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Announcement")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Repeated event")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Repeated event";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Point allocation")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Point allocation";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        }
//                                    else if (notificationListItems.get(position).mObject_type.equals("Event")) {
//                                        if (checkInternetConnection.isConnectingToInternet()) {
//                                            objectType = "Event";
//                                            readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
//                                        } else {
//                                            if (getResources() != null) {
//                                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
//                                                View snackBarView = snackbar.getView();
//                                                snackBarView.setBackgroundColor(Color.RED);
//                                                snackbar.show();
//                                            }
//
//                                        }
//                                    }
                                        else if (notificationListItems.get(position).mObject_type.equals("Reward")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                if (!sample.equals("null") && !sample.equals("")) {
                                                    int samplelenght = sample.size();
                                                    for (int i = 0; i < sample.size(); i++) {
                                                        if (notificationListItems.get(position).mObject_id.equals(sample.get(i))) {
                                                            objectType = "Reward";
                                                            readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                                            ismatchpk = true;
                                                        } else {
                                                            if (i == (samplelenght - 1)) {
                                                                if (ismatchpk != true) {
                                                                    if (getActivity() != null) {
                                                                        readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                }
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        }
                                    }
                                });


                                view_morelable.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog1.dismiss();

                                        if (notificationListItems.get(position).mObject_type.equals("None")) {
                                            popupeventmessage.setText("NONE");
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "None";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Repeated event")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Repeated event";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Announcement")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Announcement";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Point allocation")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Point allocation";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Event")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Event";
                                                readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        } else if (notificationListItems.get(position).mObject_type.equals("Reward")) {
                                            if (checkInternetConnection.isConnectingToInternet()) {
                                                objectType = "Reward";
                                                if (!sample.equals("null") && !sample.equals("")) {
                                                    int samplelenght = sample.size();

                                                    for (int i = 0; i < sample.size(); i++) {
                                                        if (notificationListItems.get(position).mObject_id.equals(sample.get(i))) {
                                                            readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                                            ismatchpk = true;

                                                        } else {
                                                            if (i == (samplelenght - 1)) {
                                                                if (ismatchpk != true) {
                                                                    if (coordinatorLayout != null) {
                                                                        readNotificationServices(notificationListItems.get(position).mpk, notificationListItems.get(position).mObject_id);
                                                                        if (coordinatorLayout != null) {
                                                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Data not found!", Snackbar.LENGTH_LONG);
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
                                            } else {
                                                if (getResources() != null) {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                                                    View snackBarView = snackbar.getView();
                                                    snackBarView.setBackgroundColor(Color.RED);
                                                    snackbar.show();
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (getResources() != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
        swipeMenuListView.setCloseInterpolator(new BounceInterpolator());
        return view;
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }

    /*************************
     * is read api
     **********************************************************/


    public void readNotificationServices(String position, final String objectId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

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
                        Loader.dialogDissmiss(getActivity());

                        if (objectType.equals("Event")) {
                            count = 1;
                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                            intent.putExtra("id", objectId);
                            getContext().startActivity(intent);
                        } else if (objectType.equals("Feed")) {
                            Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                            intent.putExtra("feedId", objectId);
                            getContext().startActivity(intent);
                        } else if (objectType.equals("Point allocation")) {
                            count = 0;
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    getNotificationServices();
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
                        } else if (objectType.equals("Repeated event")) {
                            count = 0;
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    getNotificationServices();
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
                        } else if (objectType.equals("None")) {

                            count = 0;
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    getNotificationServices();
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
                        } else if (objectType.equals("Announcement")) {
                            count = 0;
                            if (checkInternetConnection.isConnectingToInternet()) {
                                try {
                                    getNotificationServices();
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
                        } else if (objectType.equals("Reward")) {
                            if (ismatchpk != true) {
                                if (coordinatorLayout != null) {
                                    count = 0;
                                    if (checkInternetConnection.isConnectingToInternet()) {
                                        try {
                                            getNotificationServices();
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
                                    if (coordinatorLayout != null) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Data not found!", Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(Color.RED);
                                        snackbar.show();
                                    }
                                }
                            } else {
                                count = 1;
                                Intent intent = new Intent(getActivity(), RewardzDetailActivity.class);
                                intent.putExtra("pk", objectId);
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
                        if (statusCode == 400) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + res, Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }

                        }

                        if (checkInternetConnection.isConnectingToInternet()) {
                            try {
                                getNotificationServices();
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


    /* *******************deleting notification api for post method***************************/

    public void deleteNotificationServices(String position) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        Loader.showProgressDialog(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.PUSH_NOTIFICATION + position + "/";
        client.delete(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getActivity());
                        try {

                            getNotificationServices();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        if (statusCode == 500) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                        if (statusCode == 400) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + res, Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                        Loader.dialogDissmiss(getActivity());
                        try {

                            getNotificationServices();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
        );


    }

    /*
    * */




    /* *******************calling get notification api for listing page get method***************************/

    public void getNotificationServices() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);

        String url = Constants.BASEURL + Constants.PUSH_NOTIFICATION;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                Loader.dialogDissmiss(getActivity());
                notificationListItems.clear();
                pkarraylist.clear();
                updateUI(jsonArray);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getActivity());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }

                if (statusCode == 400) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                if (statusCode == 500) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
    }

    /* ********************parse and set data from layout**************************/

    @Override
    public void onResume() {
        super.onStart();
        if (count == 1) {
            try {
                getNotificationServices();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AppController.getInstance().getMixpanelAPI().track("NotificationList");

    }

    public void updateUI(JSONObject jsonresponse) {
        try {
            JSONArray jsonObject = jsonresponse.getJSONArray("notifications");
            String pklength1 = jsonresponse.getString("badge_counter");

            if (jsonObject.length() != 0) {
                swipetodeletelayout.setVisibility(View.VISIBLE);
            } else {
                swipetodeletelayout.setVisibility(View.GONE);
            }

            ShortcutBadger.applyCount(getActivity(), Integer.parseInt(pklength1));
            for (int i = 0; i < jsonObject.length(); i++) {
                String state = "";
                JSONObject notificationJson = jsonObject.getJSONObject(i);
                String pk = notificationJson.getString("pk");
                pkarraylist.add(pk);

                String sender = notificationJson.getString("sender");
                String message = notificationJson.getString("message");
                String image = notificationJson.getString("image");
                String object_id = notificationJson.getString("object_id");
                String object_type = notificationJson.getString("object_type");
                if (notificationJson.has("state")) {

                    state = notificationJson.getString("state");
                } else {
                    state = "";
                }
                String created = notificationJson.getString("created");
                created = dateformat(created);
                notificationListItem = new PushNotificationItem(pk, sender, message, image, object_id, object_type, state, created);
                notificationListItems.add(notificationListItem);
            }
            if (getActivity() != null) {
                pushNotificationAdaptor = new PushNotificationAdaptor(getActivity(), notificationListItems);
                swipeMenuListView.setAdapter(pushNotificationAdaptor);
            }

            Set<String> set = new HashSet<String>();
            set.addAll(pkarraylist);
            sharedDatabase.setPkArrayListKey(set);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String dateformat(String created) {
        String validDate;
        String s[] = created.split("-");
        String month = s[1];
        String year = s[0];
        String day = s[2];
        String day1[] = day.split("T");
        String day2 = day1[0];
        String timecreated = day1[1];
        String[] timesplit = timecreated.split(":");
        int apihour = Integer.parseInt(timesplit[0]);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH");
        Calendar cal = Calendar.getInstance();
        String currentdateandTime = dateFormat.format(cal.getTime());
        String[] currentTime = currentdateandTime.split(" ");
        String date = currentTime[0];
        String[] dateSplit = date.split("/");
        int currenthour = Integer.parseInt(currentTime[1]);
        String currentDate = currentTime[0];
        String[] monthcurrent = currentDate.split("/");
        if (day2.equals(dateSplit[2]) && month.equals(monthcurrent[1])) {
            int time = currenthour - apihour;
            if (time <= 0) {
                validDate = "just now";
                return validDate;
            } else if (time >= 1 && time <= 24) {
                validDate = time + " hrs ago";
                return validDate;
            }

        } else {
            if (month.equals("01")) {
                month = "January";
            }
            if (month.equals("02")) {
                month = "February";
            }
            if (month.equals("03")) {
                month = " March";
            }
            if (month.equals("04")) {
                month = " April";
            }
            if (month.equals("05")) {
                month = " May";
            }
            if (month.equals("06")) {
                month = " June";
            }
            if (month.equals("07")) {
                month = " July";
            }
            if (month.equals("08")) {
                month = " August";
            }
            if (month.equals("09")) {
                month = " September";
            }
            if (month.equals("10")) {
                month = " October";
            }
            if (month.equals("11")) {
                month = " November";
            }
            if (month.equals("12")) {
                month = " December";
            }
        }
        validDate = day2 + "th " + month;
        return validDate;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
            getNotificationServices();
        }else{
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