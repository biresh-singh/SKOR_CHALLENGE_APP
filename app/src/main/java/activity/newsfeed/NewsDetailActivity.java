package activity.newsfeed;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import adaptor.CommentAdapter;
import adaptor.ImageNewsDetailAdapter;
import bean.CommentResponse;
import bean.FeedFeatured;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshFeedEvent;
import event.RefreshTokenEvent;
import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.Loader;

/**
 * Created by dss-17 on 9/19/17.
 */

public class NewsDetailActivity extends AppCompatActivity {
    private static final String TAG = "NewsDetailActivity";

    ImageView backButton, imageNews, likeButton, bookmarkButton, documentIcon;
    Button playButton;
    TextView likeCount, viewCount, title, seeRewardButton, noCommentYet, viewAllCommentButton, documentName;
    WebView description;
    RecyclerView commentRecyclerView;
    LinearLayout commentButtonLinearLayout, documentLinearLayout;
    RelativeLayout viewPagerRelativeLayout;
    ViewPager imageViewPager;

    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;

    Realm realm;
    SharedDatabase sharedDatabase;
    String token;

    FeedFeatured feedFeatured;
    String feedId;
    boolean isLiked, isBookmarked;

    CommentAdapter commentAdapter;
    LinearLayoutManager linearLayoutManager;

    ImageNewsDetailAdapter imageNewsDetailAdapter;
    CircleIndicator imageCircleIndicator;

    Bitmap bitmap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        backButton = (ImageView) findViewById(R.id.activity_news_detail_backImageView);
        imageNews = (ImageView) findViewById(R.id.activity_news_detail_imageNewsImageView);
        bookmarkButton = (ImageView) findViewById(R.id.activity_news_detail_bookmarkImageView);
        likeButton = (ImageView) findViewById(R.id.activity_news_detail_likeImageView);
        likeCount = (TextView) findViewById(R.id.activity_news_detail_likeTextView);
        viewCount = (TextView) findViewById(R.id.activity_news_detail_viewsTextView);
        title = (TextView) findViewById(R.id.activity_news_detail_titleTextView);
        description = (WebView) findViewById(R.id.activity_news_detail_descriptionWebView);
        seeRewardButton = (TextView) findViewById(R.id.activity_news_detail_seeRewardTextView);
        documentIcon = (ImageView) findViewById(R.id.activity_news_detail_documentImageView);
        commentRecyclerView = (RecyclerView) findViewById(R.id.activity_news_detail_commentRecyclerView);
        commentButtonLinearLayout = (LinearLayout) findViewById(R.id.activity_news_detail_commentButtonLinearLayout);
        noCommentYet = (TextView) findViewById(R.id.activity_news_detail_noCommentYetTextView);
        viewAllCommentButton = (TextView) findViewById(R.id.activity_news_detail_viewAllCommentTextView);
        documentLinearLayout = (LinearLayout) findViewById(R.id.activity_news_detail_documentLinearLayout);
        documentName = (TextView) findViewById(R.id.activity_news_detail_documentName);
        playButton = (Button) findViewById(R.id.activity_news_detail_playButton);
        viewPagerRelativeLayout = (RelativeLayout) findViewById(R.id.activity_news_detail_viewPagerRelativeLayout);
        imageViewPager = (ViewPager) findViewById(R.id.activity_news_detail_imageViewPager);
        imageCircleIndicator = (CircleIndicator) findViewById(R.id.activity_news_detail_imageViewPagerCircleIndicator);

        //this is for api call
        deviceNames = new AndroidDeviceNames(NewsDetailActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(NewsDetailActivity.this);
        token = sharedDatabase.getToken();
        realm = AppController.getInstance().getRealm();

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int viewPagerRelativeLayoutWidth = displayMetrics.widthPixels/2;
//        viewPagerRelativeLayout.getLayoutParams().height = viewPagerRelativeLayoutWidth;

        commentAdapter = new CommentAdapter(this, false);
        commentRecyclerView.setAdapter(commentAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        imageNewsDetailAdapter = new ImageNewsDetailAdapter(this);
        imageViewPager.setAdapter(imageNewsDetailAdapter);
        imageCircleIndicator.setViewPager(imageViewPager);
        imageNewsDetailAdapter.registerDataSetObserver(imageCircleIndicator.getDataSetObserver());

        feedId = getIntent().getStringExtra("feedId");

        getNewsDetail(feedId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RefreshFeedEvent());
                finish();
            }
        });

        description.setBackgroundResource(android.R.color.transparent);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        reloadLocalData();
//        permissionCreateComment();

    }

    public void getNewsDetail(String feedId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/list/" + feedId + "/", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    feedFeatured = new FeedFeatured(jsonObject);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(feedFeatured);
                    realm.commitTransaction();

                    populateData(feedFeatured);
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

    public void getComments(String commentId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "feeds/api/comments/?feed=" + commentId, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    CommentResponse commentResponse = new CommentResponse(jsonObject);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(commentResponse);
                    realm.commitTransaction();

                    if (commentResponse.getCommentRealmList().size() == 0) {
                        noCommentYet.setVisibility(View.VISIBLE);
                        commentRecyclerView.setVisibility(View.GONE);
                    } else {
                        noCommentYet.setVisibility(View.GONE);
                        commentRecyclerView.setVisibility(View.VISIBLE);
                    }

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

    public void bookmarkFeed(final String feedId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        RequestParams requestParams = new RequestParams();
        requestParams.put("feed", feedId);

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.post(Constants.BASEURL + "feeds/api/favorites/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    feedFeatured.setFavorite(response.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bookmarkButton.setImageResource(R.drawable.bookmarkon);
                isBookmarked = true;

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(NewsDetailActivity.this);
                try {
                    String errorMessage = errorResponse.getString("detail");
                    connectivityMessage(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void unbookmarkFeed(String favoriteId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        RequestParams requestParams = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.delete(Constants.BASEURL + "feeds/api/favorites/" + favoriteId + "/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    feedFeatured.setFavorite(response.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bookmarkButton.setImageResource(R.drawable.bookmark);
                isBookmarked = false;

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(NewsDetailActivity.this);
                try {
                    String errorMessage = errorResponse.getString("detail");
                    connectivityMessage(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void likeFeed(final String feedId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.post(Constants.BASEURL + "feeds/api/" + feedId + "/likes/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    feedFeatured.setLike(response.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                likeButton.setImageResource(R.drawable.likeon);
                isLiked = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(NewsDetailActivity.this);
                try {
                    String errorMessage = errorResponse.getString("detail");
                    connectivityMessage(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void unlikeFeed(String likeId) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        RequestParams requestParams = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.delete(Constants.BASEURL + "feeds/api/like/" + likeId + "/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    feedFeatured.setLike(response.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                likeButton.setImageResource(R.drawable.like);
                isLiked = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(NewsDetailActivity.this);
                try {
                    String errorMessage = errorResponse.getString("detail");
                    connectivityMessage(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void reloadLocalData() {
        CommentResponse commentResponse = realm.where(CommentResponse.class).equalTo("id", "0").findFirst();
        if (commentResponse != null) {
            commentAdapter.updateAdapter(commentResponse.getCommentRealmList());
        }

        FeedFeatured feedFeatured = realm.where(FeedFeatured.class).equalTo("id", feedId).findFirst();
        populateData(feedFeatured);

        getNewsDetail(feedId);
        getComments(feedFeatured.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void populateData(final FeedFeatured feedFeatured) {

        getComments(feedFeatured.getId());

        ArrayList<String> imageString = new ArrayList<>();

        if (feedFeatured.getImages().size() != 0) {
            if (feedFeatured.getImages().size() == 1) {
                imageString.add(feedFeatured.getImages().get(0).getDisplay());
            } else if (feedFeatured.getImages().size() == 2) {
                imageString.add(feedFeatured.getImages().get(0).getDisplay());
                imageString.add(feedFeatured.getImages().get(1).getDisplay());
            } else {
                imageString.add(feedFeatured.getImages().get(0).getDisplay());
                imageString.add(feedFeatured.getImages().get(1).getDisplay());
                imageString.add(feedFeatured.getImages().get(2).getDisplay());
            }

            imageNewsDetailAdapter.updateAdapter(imageString);

            imageNews.setVisibility(View.GONE);
            imageViewPager.setVisibility(View.VISIBLE);
        }

        //comment section
        if (feedFeatured.isAllowComment()) {
            commentButtonLinearLayout.setVisibility(View.GONE);
        } else {
            commentButtonLinearLayout.setVisibility(View.VISIBLE);
        }

        //video
        if (feedFeatured.getVideo() != null) {
            imageNews.setVisibility(View.VISIBLE);
            imageViewPager.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);

            Glide.with(this).load(Constants.BASEURL + feedFeatured.getVideoThumbnail()).into(imageNews);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewsDetailActivity.this, VideoPreviewActivity.class);
                    intent.putExtra("videoUrl", feedFeatured.getVideo());
                    startActivity(intent);
                }
            });
        } else {
            playButton.setVisibility(View.GONE);
        }

        viewAllCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(NewsDetailActivity.this, CommentActivity.class);
//                    intent.putExtra("feedId", feedId);
//                    startActivity(intent);
                Intent intent = CommentActivity.newIntent(NewsDetailActivity.this, feedFeatured);
                startActivity(intent);
            }
        });

        commentButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(NewsDetailActivity.this, CommentActivity.class);
//                    intent.putExtra("feedId", feedId);
//                    startActivity(intent);
                Intent intent = CommentActivity.newIntent(NewsDetailActivity.this, feedFeatured);
                startActivity(intent);
            }
        });

        //document
        if (feedFeatured.getFile() != null) {
            documentLinearLayout.setVisibility(View.VISIBLE);
            imageNews.setImageDrawable(getDrawable(R.drawable.skor));
//            String [] fileNameTemp = feedFeatured.getFile().split("/");
//            documentName.setText(fileNameTemp[fileNameTemp.length-1]);
            documentName.setText(feedFeatured.getFile());

            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 3).equals("pdf")) {
                documentIcon.setBackground(getDrawable(R.drawable.pdf));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 4).equals("docx")) {
                documentIcon.setBackground(getDrawable(R.drawable.docs));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 3).equals("doc")) {
                documentIcon.setBackground(getDrawable(R.drawable.docs));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 4).equals("pptx")) {
                documentIcon.setBackground(getDrawable(R.drawable.slide));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 3).equals("ppt")) {
                documentIcon.setBackground(getDrawable(R.drawable.slide));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 4).equals("xlsx")) {
                documentIcon.setBackground(getDrawable(R.drawable.sheets));
            }
            if (feedFeatured.getFile().substring(feedFeatured.getFile().length() - 3).equals("xls")) {
                documentIcon.setBackground(getDrawable(R.drawable.sheets));
            }

            documentLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> requestPermissionArrayList = new ArrayList<>();
                    String[] requestPermissionsString;
                    if (ActivityCompat.checkSelfPermission(NewsDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    if (ActivityCompat.checkSelfPermission(NewsDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                    if (requestPermissionArrayList.size() > 0) {
                        requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                        ActivityCompat.requestPermissions(NewsDetailActivity.this, requestPermissionsString, 100);
                    } else {
                        DownloadManager downloadManager = (DownloadManager) AppController.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
                        AppController.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                        Uri uri = Uri.parse(Constants.BASEURL + feedFeatured.getFile());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, feedFeatured.getFile());
                        request.setVisibleInDownloadsUi(false);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        downloadManager.enqueue(request);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String documentName = feedFeatured.getFile();
                        if (documentName.substring(documentName.length() - 3).equals("pdf")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/pdf");
                        }
                        if (documentName.substring(documentName.length() - 4).equals("docx")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/msword");
                        }
                        if (documentName.substring(documentName.length() - 3).equals("doc")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/msword");
                        }
                        if (documentName.substring(documentName.length() - 4).equals("pptx")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/vnd.ms-powerpoint");
                        }
                        if (documentName.substring(documentName.length() - 3).equals("ppt")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/vnd.ms-powerpoint");
                        }
                        if (documentName.substring(documentName.length() - 4).equals("xlsx")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/vnd.ms-excel");
                        }
                        if (documentName.substring(documentName.length() - 3).equals("xls")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + feedFeatured.getFile())), "application/vnd.ms-excel");
                        }
                        startActivity(intent);
                    }

                }
            });

        } else {
            documentLinearLayout.setVisibility(View.GONE);
        }

        title.setText(Html.fromHtml(feedFeatured.getTitle()));
        String descriptionHtmlFormatted = feedFeatured.getDescription();
        Log.d(TAG,"HTML Description :" + feedFeatured.getDescription());
        description.loadDataWithBaseURL("about:blank", descriptionHtmlFormatted,"text/html", "utf-8", null);

        likeCount.setText(feedFeatured.getTotalLike());
        viewCount.setText(feedFeatured.getTotalView() + "");

        if (feedFeatured.getObjectModel() == null) {
            seeRewardButton.setVisibility(View.GONE);
        } else {
            seeRewardButton.setVisibility(View.VISIBLE);
        }

        if (feedFeatured.getLike() != null) {
            likeButton.setImageResource(R.drawable.likeon);
        } else {
            likeButton.setImageResource(R.drawable.like);
        }

        if (feedFeatured.getFavorite() != null) {
            bookmarkButton.setImageResource(R.drawable.bookmarkon);
        } else {
            bookmarkButton.setImageResource(R.drawable.bookmark);
        }

        if (feedFeatured.getLike() == null) {
            isLiked = false;
        } else {
            isLiked = true;
        }

        if (feedFeatured.getFavorite() == null) {
            isBookmarked = false;
        } else {
            isBookmarked = true;
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked) {
                    unlikeFeed(feedFeatured.getLike());
                } else {
                    likeFeed(feedId);
                }
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBookmarked) {
                    unbookmarkFeed(feedFeatured.getFavorite());
                } else {
                    bookmarkFeed(feedId);
                }
            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = NewsDetailActivity.this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(NewsDetailActivity.this.getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (NewsDetailActivity.this != null) {
                SnackbarManager.show(Snackbar.with(NewsDetailActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (NewsDetailActivity.this != null) {
                SnackbarManager.show(Snackbar.with(NewsDetailActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (NewsDetailActivity.this != null) {
                SnackbarManager.show(Snackbar.with(NewsDetailActivity.this).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new RefreshFeedEvent());
    }

    public Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
            }
        }
    };

    private void permissionCreateComment() {
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
                getNewsDetail(feedId);
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject feedComment = jsonObject.getJSONObject("feed_comment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                connectivityMessage("Unable to log in with provided credentials.");
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(NewsDetailActivity.this);
                }


                if (statusCode == 400) {
                    if (this != null) {
                        connectivityMessage("Unable to log in with provided credentials.");
                    }
                }

                if (statusCode == 500) {
                    if (this != null) {
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
        if (event.message != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(NewsDetailActivity.this);
                }
            });
        }
    }
}
