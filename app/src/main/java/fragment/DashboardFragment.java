package fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import activity.newsfeed.SearchNewsActivity;
import activity.rewardz.SearchRewardsActivity;
import activity.userprofile.MainActivity;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import fragment.NewsFeed.BookmarkFragment;
import fragment.NewsFeed.ChallengesFragment;
import fragment.NewsFeed.NewsFeedFragment;
import fragment.SkorChat.SkorChatFragment;
import fragment.challenge.ChallengeFragment;
import io.realm.Realm;
import me.leolin.shortcutbadger.ShortcutBadger;
import singleton.SettingsManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 8/30/17.
 */

public class DashboardFragment extends Fragment {
    private static Bitmap bitmap;

    //this is for api call
    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    //menubar
    ImageView toolbarIconImageView, homeImageView, coinImageView, shopImageView, badgeImageView, chatImageView, bellImageView,challengeImageView;
    RelativeLayout searchButton, calendarButton, settingButton;
    TextView toolbarIconTextView, unreadNotificationCount;

    FrameLayout container;
    LinearLayout menuPanel;

    String searchTarget;
    CalendarView calendarViewfragment;
    LeaderBoardIndividualFragment leaderBoardIndividualFragment;
    LeaderBoardBusinessUnitFragment leaderBoardBusinessUnitFragment;
    ReferralEmployeeFragment referralEmployeeFragment;
    ReferralClientFragment referralClientFragment;
    BookmarkFragment bookmarkFragment;
    EmergencyServicesFragment emergencyServicesFragment;
    HistoryFragment historyActivityForAll;

    PointsSummeryFragments pointsSummeryFragments;
    NewsFeedFragment newsFeedFragment;
    SkorChatFragment skorChatFragment;
    RewardzDahboardFragment rewardzDahboardFragment;
    PushNotificationListFragment pushNotificationListFragment;
    ChallengeFragment challengeFragment;
    //Overlay
    private LinearLayout mHeaderOverlayLayout;
    private FragmentManager mFragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);

        //this is for api call
        deviceNames = new AndroidDeviceNames(getContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        menuPanel = (LinearLayout) view.findViewById(R.id.fragment_dashboard_toolbarMenuPanelLinearLayout);

        //menubar
        toolbarIconImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_toolbarTitleImageView);
        homeImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_homeImageView);
        coinImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_coinImageView);
        challengeImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_challenge);
        shopImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_shopImageView);
        badgeImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_badgeImageView);
        chatImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_chatImageView);
        bellImageView = (ImageView) view.findViewById(R.id.fragment_dashboard_bellImageView);
        toolbarIconTextView = (TextView) view.findViewById(R.id.fragment_dashboard_toolbarTitleTextView);
        searchButton = (RelativeLayout) view.findViewById(R.id.fragment_dashboard_toolbarMenuSearchRelativeLayout);
        calendarButton = (RelativeLayout) view.findViewById(R.id.fragment_dashboard_calendarRelativeLayout);
        settingButton = (RelativeLayout) view.findViewById(R.id.fragment_dashboard_skorChatSettingRelativeLayout);
        unreadNotificationCount = (TextView) view.findViewById(R.id.fragment_dashboard_unreadNotificationCount);
        mHeaderOverlayLayout = view.findViewById(R.id.header_overlay_layout);

        if (sharedDatabase.getOrganizationChat().equals("True")) {
            chatImageView.setVisibility(View.VISIBLE);
        } else {
            chatImageView.setVisibility(View.GONE);
        }

        shopImageView.setVisibility(sharedDatabase.getHasdiscountcategories() ? View.VISIBLE : View.GONE);

        //Overlay event
        mHeaderOverlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsFeedFragment != null) {
                    newsFeedFragment.hideSubFloatingButton();
                    mHeaderOverlayLayout.setVisibility(View.GONE);
                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String target = bundle.getString("target");
            String preSelectedItem = bundle.getString("pre_selected_item");
            if (target.equals("challengeFragment")) {
               /* searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("CHALLENGE");

                leaderBoardBusinessUnitFragment = new ChallengeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, leaderBoardBusinessUnitFragment);
                fragmentTransaction.commit();
*/
                allOff();
                challengeImageView.setImageResource(R.drawable.challenge_on);
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "discount";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("CHALLENGE");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);
                displayView(Constants.ChallengeFragment, "myChallenge");

            }
            else if (target.equals("calendarFragment")) {
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "calendar";

                calendarButton.setVisibility(View.VISIBLE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("CALENDAR");

                calendarViewfragment = new CalendarView();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundleCalendarFragment = new Bundle();
                bundleCalendarFragment.putString("pre_selected_item", preSelectedItem);
                calendarViewfragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, calendarViewfragment);
                fragmentTransaction.commit();
            } else if (target.equals("leaderboardIndividualFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("LEADERBOARD");

                leaderBoardIndividualFragment = new LeaderBoardIndividualFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, leaderBoardIndividualFragment);
                fragmentTransaction.commit();
            } else if (target.equals("leaderboardBusinessFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("LEADERBOARD");

                leaderBoardBusinessUnitFragment = new LeaderBoardBusinessUnitFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, leaderBoardBusinessUnitFragment);
                fragmentTransaction.commit();
            } else if (target.equals("referralEmployeeFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("REFERRAL");

                referralEmployeeFragment = new ReferralEmployeeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, referralEmployeeFragment);
                fragmentTransaction.commit();
            } else if (target.equals("referralClientFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("REFERRAL");

                referralClientFragment = new ReferralClientFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, referralClientFragment);
                fragmentTransaction.commit();
            } else if (target.equals("bookmarkFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("BOOKMARKS");

                bookmarkFragment = new BookmarkFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, bookmarkFragment);
                fragmentTransaction.commit();
            } else if (target.equals("emergencyServicesFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("Emergency Services");

                emergencyServicesFragment = new EmergencyServicesFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, emergencyServicesFragment);
                fragmentTransaction.commit();
            } else if (target.equals("historyFragment")) {
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("HISTORY");

                historyActivityForAll = new HistoryFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, historyActivityForAll);
                fragmentTransaction.commit();
            } else if (target.equals("pushNotificationListFragment")) {
                bellImageView.setImageResource(R.drawable.ic_bell_on);
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("NOTIFICATIONS");

                pushNotificationListFragment = new PushNotificationListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, pushNotificationListFragment);
                fragmentTransaction.commit();
            } else if (target.equals("skorChatFragment")) {
                chatImageView.setImageResource(R.drawable.ic_chat_on);
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.VISIBLE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("CHAT");

                skorChatFragment = new SkorChatFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, skorChatFragment);
                fragmentTransaction.commit();
            } else if (target.equals("pointsSummaryFragment")) {
                coinImageView.setImageResource(R.drawable.ic_coins_on);
                searchButton.setVisibility(View.GONE);
                searchTarget = "";

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("POINTS SUMMARY");

                pointsSummeryFragments = new PointsSummeryFragments();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, pointsSummeryFragments);
                fragmentTransaction.commit();
            } else if (target.equals("discount") || target.equals("point")) {
                searchButton.setVisibility(View.VISIBLE);
                if (target.equals("discount")) {
                    shopImageView.setImageResource(R.drawable.ic_shop_on);
                    searchTarget = "discount";
                    toolbarIconTextView.setText("DISCOUNTS REWARDS");
                } else {
                    badgeImageView.setImageResource(R.drawable.ic_badge_on);
                    searchTarget = "rewards";
                    toolbarIconTextView.setText("POINT REWARDS");
                }

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
//                toolbarIconTextView.setText("");

                rewardzDahboardFragment = new RewardzDahboardFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, rewardzDahboardFragment);
                fragmentTransaction.commit();
            }
        } else {

            homeImageView.setImageResource(R.drawable.ic_home_on);
            searchButton.setVisibility(View.VISIBLE);
            searchTarget = "home";

            toolbarIconImageView.setVisibility(View.VISIBLE);
            toolbarIconTextView.setVisibility(View.GONE);
            toolbarIconImageView.setImageBitmap(getBitmapFromURL(Constants.BASEURL + sharedDatabase.getOrganizationLogo()));

            calendarButton.setVisibility(View.GONE);
            settingButton.setVisibility(View.GONE);

            newsFeedFragment = new NewsFeedFragment();
            newsFeedFragment.floatingActionButtonListener = new NewsFeedFragment.FloatingActionButtonListener() {
                @Override
                public void onPlusClick() {
                    mHeaderOverlayLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCloseClick() {
                    mHeaderOverlayLayout.setVisibility(View.GONE);
                }
            };
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, newsFeedFragment);
            fragmentTransaction.commit();
        }

        onClickGroup();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getUnreadNotificationCount();
    }

    public void onClickGroup() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTarget.equals("home")) {
                    Intent intent = new Intent(getContext(), SearchNewsActivity.class);
                    startActivity(intent);
                } else if (searchTarget.equals("discount")) {
                    Intent intent = new Intent(getContext(), SearchRewardsActivity.class);
                    startActivity(intent);
                } else if (searchTarget.equals("rewards")) {
                    Intent intent = new Intent(getContext(), SearchRewardsActivity.class);
                    startActivity(intent);
                } else if (searchTarget.equals("calendar")) {
//                    Intent intent = new Intent(getContext(), SearchRewardsActivity.class);
//                    startActivity(intent);
                }
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarViewfragment.calenderIcon.performClick();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skorChatFragment.settingsImageView.performClick();
            }
        });

        menuPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                homeImageView.setImageResource(R.drawable.ic_home_on);
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "home";
                toolbarIconImageView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setVisibility(View.GONE);
                toolbarIconImageView.setImageBitmap(getBitmapFromURL(Constants.BASEURL + sharedDatabase.getOrganizationLogo()));

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                newsFeedFragment = new NewsFeedFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, newsFeedFragment);
                fragmentTransaction.commit();

            }
        });

        coinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                coinImageView.setImageResource(R.drawable.ic_coins_on);
                searchButton.setVisibility(View.GONE);
                searchTarget = "";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("POINTS SUMMARY");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                pointsSummeryFragments = new PointsSummeryFragments();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, pointsSummeryFragments);
                fragmentTransaction.commit();

            }
        });

        shopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                shopImageView.setImageResource(R.drawable.ic_shop_on);
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "discount";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("DISCOUNT REWARDS");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                rewardzDahboardFragment = new RewardzDahboardFragment();
                FragmentManager fragmentManager = getFragmentManager();
                sharedDatabase.setType("discount");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, rewardzDahboardFragment);
                fragmentTransaction.commit();

            }
        });

        challengeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                challengeImageView.setImageResource(R.drawable.challenge_on);
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "discount";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("CHALLENGE");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);
                displayView(Constants.ChallengeFragment, "myChallenge");
               /* challengeFragment = new ChallengeFragment(getContext(),"");



                FragmentManager fragmentManager = getFragmentManager();
                sharedDatabase.setType("discount");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, challengeFragment);
                fragmentTransaction.commit();*/



            }
        });

        badgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                badgeImageView.setImageResource(R.drawable.ic_badge_on);
                searchButton.setVisibility(View.VISIBLE);
                searchTarget = "rewards";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("POINT REWARDS");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                rewardzDahboardFragment = new RewardzDahboardFragment();
                Bundle bundle = new Bundle();
                sharedDatabase.setType("point");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, rewardzDahboardFragment);
                fragmentTransaction.commit();

            }
        });

        chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedDatabase.getAuthorizedToQuickblox()) {
                    allOff();
                    chatImageView.setImageResource(R.drawable.ic_chat_on);
                    searchButton.setVisibility(View.GONE);
                    searchTarget = "";
                    toolbarIconImageView.setVisibility(View.GONE);
                    toolbarIconTextView.setVisibility(View.VISIBLE);
                    toolbarIconTextView.setText("CHAT");

                    calendarButton.setVisibility(View.GONE);
                    settingButton.setVisibility(View.VISIBLE);

                    skorChatFragment = new SkorChatFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, skorChatFragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), "Not authorized", Toast.LENGTH_SHORT).show();
                }


            }
        });

        bellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allOff();
                bellImageView.setImageResource(R.drawable.ic_bell_on);
                searchButton.setVisibility(View.GONE);
                searchTarget = "";
                toolbarIconImageView.setVisibility(View.GONE);
                toolbarIconTextView.setVisibility(View.VISIBLE);
                toolbarIconTextView.setText("NOTIFICATION");

                calendarButton.setVisibility(View.GONE);
                settingButton.setVisibility(View.GONE);

                pushNotificationListFragment = new PushNotificationListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, pushNotificationListFragment);
                fragmentTransaction.commit();

            }
        });
    }

    public void allOff() {
        homeImageView.setImageResource(R.drawable.ic_home_off);
        coinImageView.setImageResource(R.drawable.ic_coins_off);
        challengeImageView.setImageResource(R.drawable.challenge_off);
        shopImageView.setImageResource(R.drawable.ic_shop_off);
        badgeImageView.setImageResource(R.drawable.ic_badge_off);
        chatImageView.setImageResource(R.drawable.ic_chat_off);
        bellImageView.setImageResource(R.drawable.ic_bell_off);
    }

    public void displayView(String fragmentName, Object obj)
    {  mFragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = mFragmentManager.beginTransaction();

        if (fragmentName.equalsIgnoreCase(Constants.ChallengeFragment))
        {
            challengeFragment = new ChallengeFragment(getContext(),obj);
        }

        if (challengeFragment != null) {
            try {
                if (fragmentName.equals(Constants.ChallengeFragment)) {
                    fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, challengeFragment);
                    fragmentTransaction.commit();
                }
                else
                {
                    fragmentTransaction.replace(R.id.fragment_dashboard_containerFrameLayout, challengeFragment).addToBackStack(fragmentName);
                    fragmentTransaction.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        InputStream input = null;
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getUnreadNotificationCount() {
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

                try {
                    int unreadNotifCount = jsonObject.getInt("badge_counter");
                    if (unreadNotifCount != 0) {
                        unreadNotificationCount.setVisibility(View.VISIBLE);
                        unreadNotificationCount.setText("" + unreadNotifCount);
                        if (getActivity() != null) {
                            ShortcutBadger.applyCount(getActivity(), unreadNotifCount);
                        }
                    } else {
                        unreadNotificationCount.setVisibility(View.GONE);
                    }
                } catch (JSONException js) {
                    js.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 0) {

                }

                if (statusCode == 500) {

                }
            }

            @Override
            public void onRetry(int retryNo) {

            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (getContext() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (getContext() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (getContext() != null) {
                SnackbarManager.show(Snackbar.with(getContext()).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
        }
    }

}
