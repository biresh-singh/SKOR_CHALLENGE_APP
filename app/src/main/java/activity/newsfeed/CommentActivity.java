package activity.newsfeed;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.util.Collections;
import java.util.HashMap;

import adaptor.CommentAdapter;
import bean.CommentResponse;
import bean.FeedFeatured;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;

/**
 * Created by dss-17 on 9/20/17.
 */

public class CommentActivity extends AppCompatActivity {

    public static final String FEED_FEATURED_OBJECT = "feedFeaturedObject";

    private FeedFeatured feedFeatured;

    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    RecyclerView commentRecyclerView;
    EditText commentEditText;
    TextView postButton;
    LinearLayout backButton, commentLinearLayout;
    View mline;

    CommentAdapter commentAdapter;
    LinearLayoutManager linearLayoutManager;

    public static Intent newIntent(Context context, FeedFeatured feedFeatured) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(FEED_FEATURED_OBJECT, feedFeatured);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentRecyclerView = (RecyclerView) findViewById(R.id.activity_comment_commentRecyclerView);
        postButton = (TextView) findViewById(R.id.activity_comment_postTextView);
        commentEditText = (EditText) findViewById(R.id.activity_comment_commentEditText);
        backButton = (LinearLayout) findViewById(R.id.activity_comment_backLinearLayout);
        commentLinearLayout = (LinearLayout) findViewById(R.id.activity_comment_commentButtonLinearLayout);
        mline = (View) findViewById(R.id.view_line);

        // Get data from intent
        if (getIntent().getParcelableExtra(FEED_FEATURED_OBJECT) != null) {
            feedFeatured = getIntent().getParcelableExtra(FEED_FEATURED_OBJECT);
        }

        //this is for api call
        deviceNames = new AndroidDeviceNames(CommentActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(CommentActivity.this);
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

        commentAdapter = new CommentAdapter(this, true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        getComments(feedFeatured.getId());

        //comment section
        if (feedFeatured.isAllowComment()) {
            commentLinearLayout.setVisibility(View.GONE);
        } else {
            commentLinearLayout.setVisibility(View.VISIBLE);
        }

        commentEditText.addTextChangedListener(textWatcher);
        commentEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                postButton.setTextColor(Color.parseColor("#dfdfdf"));
                postButton.setEnabled(false);
            } else {
                postButton.setTextColor(Color.parseColor("#eb411f"));
                postButton.setEnabled(true);

                postButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = commentEditText.getText().toString();
                        postComments(feedFeatured.getId(), comment);
                    }
                });
            }
        }
    };


    public void getComments(String feedId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/comments/?feed=" + feedId, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    CommentResponse commentResponse = new CommentResponse(jsonObject);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(commentResponse);
                    realm.commitTransaction();

                    commentRecyclerView.setAdapter(commentAdapter);
                    commentRecyclerView.setLayoutManager(linearLayoutManager);

                    Collections.reverse(commentResponse.getCommentRealmList());

                    commentAdapter.updateAdapter(commentResponse.getCommentRealmList());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage("Error");
            }
        });
    }

    public void postComments(final String feedId, String comment) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("text", comment);

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.post(Constants.BASEURL + "feeds/api/comments/?feed=" + feedId, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                getComments(feedId);
                commentEditText.getText().clear();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage("Error");
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(CommentActivity.this);
                }

            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = CommentActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(CommentActivity.this.getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (CommentActivity.this != null) {
                SnackbarManager.show(Snackbar.with(CommentActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (CommentActivity.this != null) {
                SnackbarManager.show(Snackbar.with(CommentActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (CommentActivity.this != null) {
                SnackbarManager.show(Snackbar.with(CommentActivity.this).text(msg).textColor(Color.WHITE).color(Color.RED));
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
        if (event.message != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(CommentActivity.this);
                }
            });
        }
    }

}