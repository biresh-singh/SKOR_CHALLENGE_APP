package fragment.NewsFeed;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.HashMap;

import adaptor.AllAdapter;
import adaptor.FeedFeaturedAdapter;
import bean.AnniversaryResponse;
import bean.BirthdayResponse;
import bean.FeedFeatured;
import bean.FeedFeaturedNewsResponse;
import bean.FeedFeaturedResponse;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import listener.LoadMoreListener;
import io.realm.Realm;
import io.realm.RealmList;
import me.relex.circleindicator.CircleIndicator;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 9/25/17.
 */

public class GreetingsFragment extends Fragment {
    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    //feedfeatured
    ViewPager feedFeaturedViewPager;
    CircleIndicator feedFeaturedViewPagerCircleIndicator;
    FeedFeaturedAdapter feedFeaturedAdapter;

    //feed featured
    FeedFeaturedResponse feedFeaturedResponse;
    RealmList<FeedFeatured> feedFeaturedNewsRealmList = new RealmList<>();

    //news
    RecyclerView allNewsRecyclerView;
    AllAdapter allAdapter;
    LinearLayoutManager linearLayoutManager;

    //news
    FeedFeaturedNewsResponse feedFeaturedNewsResponse;
    RealmList<FeedFeatured> feedFeaturedRealmList = new RealmList<>();

    BirthdayResponse birthdayResponse = null;
    AnniversaryResponse anniversaryResponse = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, null);

        //this is for api call
        deviceNames = new AndroidDeviceNames(getContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        //feedfeatured
        feedFeaturedViewPager = (ViewPager) view.findViewById(R.id.fragment_feed_top_news_feedFeaturedViewPager);
        feedFeaturedViewPagerCircleIndicator = (CircleIndicator) view.findViewById(R.id.fragment_feed_top_news_feedFeaturedViewPagerCircleIndicator);

        //feedfeatured
        feedFeaturedAdapter = new FeedFeaturedAdapter(getContext());
        feedFeaturedViewPagerCircleIndicator.setViewPager(feedFeaturedViewPager);
        feedFeaturedViewPager.setAdapter(feedFeaturedAdapter);
//        feedFeaturedAdapter.registerDataSetObserver(feedFeaturedViewPagerCircleIndicator.getDataSetObserver());

//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//                if (currentPage == XMEN.length) {
//                    currentPage = 0;
//                }
//                mPager.setCurrentItem(currentPage++, true);
//            }
//        };

        //news
        allNewsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_feed_top_news_feedTopNewsRecyclerView);
        allAdapter = new AllAdapter(getContext(), loadMoreListener, false);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allNewsRecyclerView.setLayoutManager(linearLayoutManager);
        allNewsRecyclerView.setNestedScrollingEnabled(false);
        allNewsRecyclerView.setAdapter(allAdapter);

        reloadLocalData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void getFeedFeatured() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/list/feautured/", params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    feedFeaturedResponse = new FeedFeaturedResponse(jsonObject);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(feedFeaturedResponse);
                        }
                    });

                    feedFeaturedRealmList = new RealmList<>();
                    for (int i=0; i<feedFeaturedResponse.getFeedFeaturedRealmList().size(); i++) {
                        feedFeaturedRealmList.add(feedFeaturedResponse.getFeedFeaturedRealmList().get(i));
                    }

                    feedFeaturedAdapter.updateAdapter(feedFeaturedRealmList);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    public void getFeedList(final String prev) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "events/api/greetings/today/" + prev, params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    feedFeaturedNewsResponse = new FeedFeaturedNewsResponse(jsonObject);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(feedFeaturedNewsResponse);
                    realm.commitTransaction();

                    if (prev.equals("")) {
                        feedFeaturedNewsRealmList = new RealmList<>();
                    }

                    for (int i=0; i<feedFeaturedNewsResponse.getFeedFeaturedRealmList().size(); i++) {
                        feedFeaturedNewsRealmList.add(feedFeaturedNewsResponse.getFeedFeaturedRealmList().get(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (birthdayResponse != null) {
                    allAdapter.updateBirthday(birthdayResponse.getBirthdayRealmList());
                }
                if (anniversaryResponse != null) {
                    allAdapter.updateAnniversary(anniversaryResponse.getAnniversaryRealmList());
                }
                allAdapter.updateFeed(feedFeaturedNewsRealmList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    public void getBirthdayList() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "events/api/greetings/today/", params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject object = new JSONObject(responseString);
                    if (object.has("Birthday")) {
                        birthdayResponse = new BirthdayResponse(object.getJSONArray("Birthday"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(birthdayResponse);
                        realm.commitTransaction();
                    }
                    if (object.has("Anniversary")) {
                        anniversaryResponse = new AnniversaryResponse(object.getJSONArray("Anniversary"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(anniversaryResponse);
                        realm.commitTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getFeedList("");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }
            }
        });
    }

    public void reloadLocalData() {
//        FeedFeaturedResponse feedFeaturedResponse = realm.where(FeedFeaturedResponse.class)
//                .equalTo("id", "0")
//                .findFirst();
//        if(feedFeaturedResponse != null) {
//            feedFeaturedRealmList = new RealmList<>();
//            feedFeaturedRealmList.addAll(feedFeaturedResponse.getFeedFeaturedRealmList());
//            feedFeaturedAdapter.updateAdapter(feedFeaturedRealmList);
//        }

        FeedFeaturedNewsResponse feedFeaturedNewsResponse = realm.where(FeedFeaturedNewsResponse.class)
                .equalTo("id", "0")
                .findFirst();
        if(feedFeaturedNewsResponse != null) {
            feedFeaturedNewsRealmList = new RealmList<>();
            feedFeaturedNewsRealmList.addAll(feedFeaturedNewsResponse.getFeedFeaturedRealmList());
            allAdapter.updateFeed(feedFeaturedNewsRealmList);
        }

        getBirthdayList();
//        getFeedFeatured();

    }

    //paging
    LoadMoreListener loadMoreListener = new LoadMoreListener() {
        @Override
        public void onLoadMoreListener() {
            if (feedFeaturedNewsResponse != null) {
                getFeedList(feedFeaturedNewsResponse.getTs_prev());
            }
        }
    };

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
            getBirthdayList();
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
