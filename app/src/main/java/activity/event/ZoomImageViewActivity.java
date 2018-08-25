package activity.event;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adaptor.ViewPagerAdapter;
import bean.PhotoItem;
import constants.Constants;


public class ZoomImageViewActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView toolbar_title, toolbar_title1;
    LinearLayout back;
    int position1;
    String id;
    int counter = 0;
    File direct;
    RelativeLayout externallayout;
    int shareImagePosition;
    FloatingActionButton floatingActionButton, twitter, whatsapp, instagram, facebook;
    Animation show_fab_1;
    public static int count = 0;
    int packageNameCounter = 0;
    ArrayList<PhotoItem> photoItemsImageThumbnailArrayList = new ArrayList<>();
    static boolean isClick;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title1);
        toolbar_title1 = (TextView) findViewById(R.id.toolbar_title);
        back = (LinearLayout) findViewById(R.id.back);
        id = SeeAllPhotoActivity.id;
        externallayout = (RelativeLayout) findViewById(R.id.externallayout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        twitter = (FloatingActionButton) findViewById(R.id.twitter);
        whatsapp = (FloatingActionButton) findViewById(R.id.whatsapp);
        instagram = (FloatingActionButton) findViewById(R.id.instagram);
        facebook = (FloatingActionButton) findViewById(R.id.facebook);
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.viewanimation);
        photoItemsImageThumbnailArrayList = SeeAllPhotoActivity.photoItemsImageThumbnailArrayList;
        position1 = SeeAllPhotoActivity.position1;
        shareImagePosition = position1;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", id);
                setResult(2, intent);
                finish();
            }
        });
        floatingActionButton.startAnimation(show_fab_1);
        viewPager = (ViewPager) findViewById(R.id.pagers);
        try {
            if (getApplicationContext() != null) {
                viewPagerAdapter = new ViewPagerAdapter(ZoomImageViewActivity.this, photoItemsImageThumbnailArrayList, position1);
                viewPager.setAdapter(viewPagerAdapter);
            }
            viewPager.setCurrentItem(position1);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    toolbar_title.setText("by " + photoItemsImageThumbnailArrayList.get(position).mFullName);
                    toolbar_title1.setText(photoItemsImageThumbnailArrayList.get(position).mTitle);
                    floatingActionButton.startAnimation(show_fab_1);
                    isClick = false;
                    shareImagePosition = position;
                    counter = 0;
                    count = 0;

                }

                @Override
                public void onPageSelected(int position) {
                    toolbar_title.setText(photoItemsImageThumbnailArrayList.get(position).mFullName);
                    toolbar_title1.setText(photoItemsImageThumbnailArrayList.get(position).mTitle);
                    shareImagePosition = position;
                    counter = 0;
                    isClick = false;
                    count = 0;
                    floatingActionButton.startAnimation(show_fab_1);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    counter = 0;
                    count = 0;
                }
            });

            whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whatsapp.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);
                    twitter.setVisibility(View.GONE);
                    instagram.setVisibility(View.GONE);
                    sendValue(direct, "com.whatsapp");
                }
            });
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whatsapp.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);
                    twitter.setVisibility(View.GONE);
                    instagram.setVisibility(View.GONE);
                    sendValue(direct, "com.facebook.katana");
                }
            });
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whatsapp.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);
                    twitter.setVisibility(View.GONE);
                    instagram.setVisibility(View.GONE);
                    sendValue(direct, "com.twitter.android");
                }
            });
            instagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whatsapp.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);
                    twitter.setVisibility(View.GONE);
                    instagram.setVisibility(View.GONE);
                    sendValue(direct, "com.instagram.android");
                }
            });
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isClick == false) {
                        isClick = true;

                        floatingActionButton.startAnimation(show_fab_1);
                        String imagePath = Constants.BASEURL + photoItemsImageThumbnailArrayList.get(shareImagePosition).mImageThumbnail;
                        direct = new File(Environment.getExternalStorageDirectory() + "/Imagedownload1/fileName" + id + shareImagePosition + ".jpg");
                        if (!direct.exists()) {
                            downloadFile(imagePath);
                        }
                        direct = new File(Environment.getExternalStorageDirectory() + "/Imagedownload1/fileName" + id + shareImagePosition + ".jpg");
                        counter++;

                        externallayout.setVisibility(View.VISIBLE);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
                        if (!resInfos.isEmpty()) {
                            System.out.println("Have package");
                            for (ResolveInfo resInfo : resInfos) {

                                String packageName = resInfo.activityInfo.packageName;
                                packageNameCounter++;
                                Log.i("Package Name", packageName);
                                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.instagram.android") || packageName.contains("com.whatsapp")) {
                                    if (packageName.contains("com.twitter.android")) {
                                        twitter.setVisibility(View.VISIBLE);
                                        count++;
                                        twitter.startAnimation(show_fab_1);
                                        floatingActionButton.startAnimation(show_fab_1);
                                    }
                                    if (packageName.contains("com.facebook.katana")) {
                                        facebook.setVisibility(View.VISIBLE);
                                        facebook.startAnimation(show_fab_1);
                                        count++;
                                        floatingActionButton.startAnimation(show_fab_1);
                                    }
                                    if (packageName.contains("com.instagram.android")) {
                                        instagram.setVisibility(View.VISIBLE);
                                        count++;
                                        floatingActionButton.startAnimation(show_fab_1);
                                        instagram.startAnimation(show_fab_1);
                                    }
                                    if (packageName.contains("com.whatsapp")) {
                                        whatsapp.setVisibility(View.VISIBLE);
                                        whatsapp.startAnimation(show_fab_1);
                                        count++;
                                        floatingActionButton.startAnimation(show_fab_1);


                                    }
                                } else {
                                    int resInfoslength = resInfos.size();
                                    if (packageNameCounter == resInfoslength) {
                                        if (count == 0) {
                                            packageNameCounter = 0;
                                            if (getApplicationContext() != null) {
                                                connectivityMessage(
                                                        "Whatsapp,Instagram,Twitter,Facebook have not been installed.");
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            isClick = false;
                            if (getApplicationContext() != null) {
                                connectivityMessage(
                                        "Whatsapp,Instagram,Twitter,Facebook have not been installed.");

                            }
                        }
                    } else {
                        isClick = false;
                        floatingActionButton.startAnimation(show_fab_1);
                        externallayout.setVisibility(View.GONE);
                        whatsapp.setVisibility(View.GONE);
                        facebook.setVisibility(View.GONE);
                        twitter.setVisibility(View.GONE);
                        instagram.setVisibility(View.GONE);
                    }

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void sendValue(File direct, String packagename) {
        isClick = false;
        Intent shareIntent = new Intent();
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(direct));
        shareIntent.setPackage(packagename);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            if (getApplicationContext() != null) {
                connectivityMessage(
                        "Whatsapp,Instagram,Twitter,Facebook have not been installed.");

            }
        }
    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#FF9B30")), this);
            }
        } else if (msg.equals("Network Connected")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#4BCC1F")), this);
            }
        } else {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.RED), this);
            }
        }

    }

    public void downloadFile(String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Imagedownload");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/Imagedownload1", "fileName" + id + shareImagePosition + ".jpg");

        mgr.enqueue(request);

    }
}