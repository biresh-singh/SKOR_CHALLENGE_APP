package fragment.NewsFeed;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.HashMap;

import activity.userprofile.MainActivity;
import adaptor.BookmarkAdapter;
import bean.FeedFeatured;
import bean.FeedFeaturedBookmarkResponse;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import io.realm.RealmList;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 9/26/17.
 */

public class BookmarkFragment extends Fragment{

    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    FeedFeaturedBookmarkResponse feedFeaturedBookmarkResponse;
    RealmList<FeedFeatured> feedFeaturedBookmarkRealmList = new RealmList<>();

    LinearLayout panelButton;
    EditText searchField;
    ImageView submitSearch;

    RecyclerView bookmarkRecyclerView;
    BookmarkAdapter bookmarkAdapter;
    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, null);
        bookmarkRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_bookmark_recyclerView);
        panelButton = (LinearLayout) view.findViewById(R.id.fragment_bookmark_panelLinearLayout);
        searchField = (EditText) view.findViewById(R.id.search_query);
        submitSearch = (ImageView) view.findViewById(R.id.go);

        //this is for api call
        deviceNames = new AndroidDeviceNames(getContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        //adapter
        bookmarkAdapter = new BookmarkAdapter(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        bookmarkRecyclerView.setLayoutManager(linearLayoutManager);
        bookmarkRecyclerView.setNestedScrollingEnabled(false);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);

        panelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

//        getBookmarkList();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getBookmarkList() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/favorites/", params,  new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    feedFeaturedBookmarkResponse = new FeedFeaturedBookmarkResponse(jsonObject);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(feedFeaturedBookmarkResponse);
                    realm.commitTransaction();

                    for (int i=0; i<feedFeaturedBookmarkResponse.getFeedFeaturedRealmList().size(); i++) {
                        feedFeaturedBookmarkRealmList.add(feedFeaturedBookmarkResponse.getFeedFeaturedRealmList().get(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                bookmarkAdapter.updateAdapter(feedFeaturedBookmarkRealmList);


                submitSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final RealmList<FeedFeatured> feedFeaturedBookmarkTempRealmList = new RealmList<>();

                        for (int i=0; i<feedFeaturedBookmarkRealmList.size(); i++) {
                            if (feedFeaturedBookmarkRealmList.get(i).getTitle().contains(searchField.getText().toString())) {
                                feedFeaturedBookmarkTempRealmList.add(feedFeaturedBookmarkRealmList.get(i));
                            }
                        }
                        bookmarkAdapter.updateAdapter(feedFeaturedBookmarkTempRealmList);
                    }
                });

                searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        final RealmList<FeedFeatured> feedFeaturedBookmarkTempRealmList = new RealmList<>();

                        for (int i=0; i<feedFeaturedBookmarkRealmList.size(); i++) {
                            if (feedFeaturedBookmarkRealmList.get(i).getTitle().contains(searchField.getText().toString())) {
                                feedFeaturedBookmarkTempRealmList.add(feedFeaturedBookmarkRealmList.get(i));
                            }
                        }
                        bookmarkAdapter.updateAdapter(feedFeaturedBookmarkTempRealmList);
                        return true;
                    }

                });

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                connectivityMessage("Unable to log in with provided credentials.");
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }
            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            versionCode  = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookmarkAdapter.clear();
        getBookmarkList();
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
            getBookmarkList();
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


}
