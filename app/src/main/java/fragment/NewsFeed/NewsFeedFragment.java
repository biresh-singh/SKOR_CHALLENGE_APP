package fragment.NewsFeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import activity.newsfeed.AddArticleActivity;
import activity.userprofile.MainActivity;
import adaptor.AdsAdapter;
import adaptor.SubMenuAdapter;
import bean.AdsResponse;
import bean.FeedFeatured;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import fragment.NavigationDrawerFragment;
import io.realm.RealmList;
import listener.SubMenuListener;
import singleton.SettingsManager;
import singleton.ThreadRunnableTask;
import singleton.UserManager;
import utils.AndroidDeviceNames;

/**
 * Created by dss-17 on 8/3/17.
 */

public class NewsFeedFragment extends Fragment {

    //menubar
    ImageView homeImageView, coinImageView, shopImageView, badgeImageView, chatImageView, bellImageView;

    //ads
    ViewPager adsViewPager;
    AdsResponse adsResponse;
    AdsAdapter adsAdapter;
    RealmList<FeedFeatured> adsNewsRealmList = new RealmList<>();

    //feed top news
    ViewPager topNewsViewPager;
    PagerAdapter pagerAdapter;
    int currentPage = 0;

    //submenu
    RecyclerView subMenuRecyclerView;
    LinearLayoutManager linearLayoutManager;
    SubMenuAdapter subMenuAdapter;

    private LinearLayout mOverlayLayout;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mCreateSurveyPollingActionButton;
    private TextView mCreateSurveyPollingLabel;
    private FloatingActionButton mWriteArticleActionButton;
    private TextView mWriteArticleActionLabel;
    public NavigationDrawerFragment navigationDrawerFragment;

    //Listener
    public FloatingActionButtonListener floatingActionButtonListener;

    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    private ThreadRunnableTask adsRunnableTask;

    SharedDatabase sharedDatabase;
    String token;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, null);

        //ads
        adsViewPager = (ViewPager) view.findViewById(R.id.fragment_news_feed_adsViewPager);
        adsAdapter = new AdsAdapter(getContext());
        adsViewPager.setAdapter(adsAdapter);

        //feed top news
        topNewsViewPager = (ViewPager) view.findViewById(R.id.fragment_news_feed_topNewsViewPager);
        topNewsViewPager.canScrollVertically(0);

        //Overlay
        mOverlayLayout = view.findViewById(R.id.overlay_layout);
        mOverlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSubFloatingButton();

                if (floatingActionButtonListener != null)
                    floatingActionButtonListener.onCloseClick();
            }
        });

        //Floating button
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fragment_news_feed_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingActionButtonClicked(view);
            }
        });

        //Create survey polling
        mCreateSurveyPollingLabel = view.findViewById(R.id.create_survey_polling_label);
        mCreateSurveyPollingActionButton = view.findViewById(R.id.create_survey_polling_fab);
        mCreateSurveyPollingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateSurveyPollingButtonClicked(view);
            }
        });

        //Write article
        mWriteArticleActionLabel = view.findViewById(R.id.write_article_label);
        mWriteArticleActionButton = view.findViewById(R.id.write_article_fab);
        mWriteArticleActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWriteArticleButtonClicked(view);
            }
        });

        navigationDrawerFragment = ((NavigationDrawerFragment) ((MainActivity)getActivity()).mNavigationDrawerFragment);

        //Hide sub floating button
        hideSubFloatingButton();

        //feed top news
        pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        topNewsViewPager.setAdapter(pagerAdapter);
        topNewsViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                topNewsViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        topNewsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    subMenuAdapter.UpdateSubMenuPosition(0);
                    subMenuRecyclerView.scrollToPosition(0);
                } else if (position == 1) {
                    subMenuAdapter.UpdateSubMenuPosition(1);
                    subMenuRecyclerView.scrollToPosition(1);
                } else if (position == 2) {
                    subMenuAdapter.UpdateSubMenuPosition(2);
                    subMenuRecyclerView.scrollToPosition(2);
                } else if (position == 3) {
                    subMenuAdapter.UpdateSubMenuPosition(3);
                    subMenuRecyclerView.scrollToPosition(3);
                } else if (position == 4) {
                    subMenuAdapter.UpdateSubMenuPosition(4);
                    subMenuRecyclerView.scrollToPosition(4);
                } else if (position == 5) {
                    subMenuAdapter.UpdateSubMenuPosition(5);
                    subMenuRecyclerView.scrollToPosition(5);
                } else if (position == 6) {
                    subMenuAdapter.UpdateSubMenuPosition(6);
                    subMenuRecyclerView.scrollToPosition(6);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //submenu
        subMenuRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_news_feed_subMenuRecyclerView);
        ArrayList<String> subMenuArrayList = new ArrayList<>();
        subMenuArrayList.add(0, "ALL");
        subMenuArrayList.add(1, "NEWS");
//        subMenuArrayList.add(2, "CHALLENGES");
//        subMenuArrayList.add(3, "ACTIVITY AND EVENTS");
//        subMenuArrayList.add(4, "GREETINGS");
//        subMenuArrayList.add(5, "ANNOUNCEMENT");
//        subMenuArrayList.add(6, "APPOINTMENTS");
        subMenuArrayList.add(2, "ACTIVITY AND EVENTS");
        subMenuArrayList.add(3, "GREETINGS");
        subMenuArrayList.add(4, "ANNOUNCEMENT");
        subMenuArrayList.add(5, "APPOINTMENTS");
        subMenuAdapter = new SubMenuAdapter(getContext(), subMenuArrayList, subMenuListener);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        subMenuRecyclerView.setLayoutManager(linearLayoutManager);
        subMenuRecyclerView.setAdapter(subMenuAdapter);

        //ads height initialization
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int viewPagerRelativeLayoutWidth = displayMetrics.widthPixels / 8;
        adsViewPager.getLayoutParams().height = viewPagerRelativeLayoutWidth;

        //this is for api call
        deviceNames = new AndroidDeviceNames(getContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();

        sharedDatabase.setPosition(1);
        sharedDatabase.setType("all");

        mFloatingActionButton.setVisibility(SettingsManager.getInstance().IsCanCreateFeed().equals("Unallow") ? View.GONE : View.VISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        permissionCreateFeed();
        getAds();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    SubMenuListener subMenuListener = new SubMenuListener() {
        @Override
        public void onSubMenuListener(int position) {
            if (position == 0) {
                topNewsViewPager.setCurrentItem(0, true);
            } else if (position == 1) {
                topNewsViewPager.setCurrentItem(1, true);
            } else if (position == 2) {
                topNewsViewPager.setCurrentItem(2, true);
            } else if (position == 3) {
                topNewsViewPager.setCurrentItem(3, true);
            } else if (position == 4) {
                topNewsViewPager.setCurrentItem(4, true);
            }
//            else if (position == 5) {
//                topNewsViewPager.setCurrentItem(5, true);
//            } else {
//                topNewsViewPager.setCurrentItem(6, true);
//            }
            else {
                topNewsViewPager.setCurrentItem(5, true);
            }
        }
    };

    public void getAds() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/advertisements/", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    adsResponse = new AdsResponse(jsonObject);

//                    realm.beginTransaction();
//                    realm.copyToRealmOrUpdate(adsResponse);
//                    realm.commitTransaction();

                    for (int i = 0; i < adsResponse.getFeedFeaturedRealmList().size(); i++) {
                        adsNewsRealmList.add(adsResponse.getFeedFeaturedRealmList().get(i));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (adsNewsRealmList.size() == 0) {
                    adsViewPager.setVisibility(View.GONE);
                } else {
                    adsAdapter.updateAdapter(adsNewsRealmList);
                }

                //timer handling for ads
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == adsNewsRealmList.size()) {
                            currentPage = 0;
                        }
                        adsViewPager.setCurrentItem(currentPage++, true);
                    }
                };

//                Timer swipeTimer = new Timer();
//                swipeTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        handler.post(Update);
//                    }
//                }, 10000, 10000);

                adsRunnableTask = new ThreadRunnableTask() {
                    @Override
                    public void executeRunnableTask() {
                        handler.post(Update);
                        adsRunnableTask.executeDelayedThread(30000);
                    }

                    @Override
                    public void onTickRunnable(String sec) {

                    }
                };

                adsRunnableTask.executeRunnableTask();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage("Unable to log in with provided credentials.");

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }

                if (statusCode == 400) {
                    if (getContext() != null) {
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                if (statusCode == 500) {
                    if (getContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AllNewsFragment();
            } else if (position == 1) {
                return new NewsFragment();
            }
//            else if(position == 2) {
//                return new ChallengesFragment();
//            } else if(position == 3) {
//                return new ActivityAndEventsFragment();
//            } else if(position == 4) {
//                return new GreetingsFragment();
//            } else if(position == 5) {
//                return new AnnouncementsFragment();
//            } else if(position == 6) {
//                return new AppointmentsFragment();
//            }
            else if (position == 2) {
                return new ActivityAndEventsFragment();
            } else if (position == 3) {
                return new GreetingsFragment();
            } else if (position == 4) {
                return new AnnouncementsFragment();
            } else if (position == 5) {
                return new AppointmentsFragment();
            }
            return new AllNewsFragment();
        }


        @Override
        public int getCount() {
//            return 7;
            return 6;
        }
    }

    private void permissionCreateFeed() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/permission/", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject feed = jsonObject.getJSONObject("feed");
                    mFloatingActionButton.setVisibility(feed.getString("create").equals("Unallow") ? View.GONE : View.VISIBLE);
                    SettingsManager.getInstance().setIsCanCreateFeed(feed.getString("create"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage("Unable to log in with provided credentials.");

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }

                if (statusCode == 400) {
                    if (getContext() != null) {
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                if (statusCode == 500) {
                    if (getContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
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
            permissionCreateFeed();
        }else{
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

    //Event
    public void onFloatingActionButtonClicked(View view) {
        if (mCreateSurveyPollingActionButton.getVisibility() == View.VISIBLE) {
            hideSubFloatingButton();

            if (floatingActionButtonListener != null)
                floatingActionButtonListener.onCloseClick();
        }
        else {
            showSubFloatingButton();

            if (floatingActionButtonListener != null)
                floatingActionButtonListener.onPlusClick();
        }
    }

    public void onCreateSurveyPollingButtonClicked(View view) {
        hideSubFloatingButton();
        if (floatingActionButtonListener != null)
            floatingActionButtonListener.onCloseClick();
        navigationDrawerFragment.surveyPollLayout.performClick();
    }

    public void onWriteArticleButtonClicked(View view) {
        hideSubFloatingButton();
        if (floatingActionButtonListener != null)
            floatingActionButtonListener.onCloseClick();

        Intent intent = new Intent(getContext(), AddArticleActivity.class);
        startActivity(intent);
    }

    private void showSubFloatingButton() {
        //Show button
        mCreateSurveyPollingActionButton.setVisibility(View.VISIBLE);
        mCreateSurveyPollingLabel.setVisibility(View.VISIBLE);
        mWriteArticleActionButton.setVisibility(View.VISIBLE);
        mWriteArticleActionLabel.setVisibility(View.VISIBLE);

        //Show overlay
        mOverlayLayout.setVisibility(View.VISIBLE);

        //Set floating button icon
        mFloatingActionButton.setImageResource(R.drawable.close_floating_icon);
    }

    public void hideSubFloatingButton() {
        //Show button
        mCreateSurveyPollingActionButton.setVisibility(View.GONE);
        mCreateSurveyPollingLabel.setVisibility(View.GONE);
        mWriteArticleActionButton.setVisibility(View.GONE);
        mWriteArticleActionLabel.setVisibility(View.GONE);

        //Show overlay
        mOverlayLayout.setVisibility(View.GONE);

        //Set floating button icon
        mFloatingActionButton.setImageResource(R.drawable.plus_floating_icon);
    }

    public interface FloatingActionButtonListener {
        void onPlusClick();
        void onCloseClick();
    }
}

