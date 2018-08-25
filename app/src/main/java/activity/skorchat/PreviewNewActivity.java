package activity.skorchat;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import java.util.ArrayList;

import utils.AppController;

/**
 * Created by dss16 on 11/14/17.
 */

public class PreviewNewActivity extends AppCompatActivity {
    ImageView backButton;
    ViewPager itemsViewPager;
    LinearLayout downloadButton;
    PreviewItemAdapter previewItemAdapter;
    ArrayList<String> urls;
    String selectedUrl = "";
    String name, type;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_new);
        backButton = (ImageView) findViewById(R.id.activity_preview_new_chating_backButton);
        itemsViewPager = (ViewPager) findViewById(R.id.activity_preview_new_itemsViewPager);
        downloadButton = (LinearLayout) findViewById(R.id.activity_preview_new_downloadButton);

        urls = getIntent().getStringArrayListExtra("urls");
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        position = getIntent().getIntExtra("index", 0);
        selectedUrl = urls.get(position);

        previewItemAdapter = new PreviewItemAdapter(this, getSupportFragmentManager(), type);
        itemsViewPager.setAdapter(previewItemAdapter);

        if (urls != null) {
            previewItemAdapter.updateAdapter(urls, urls.size());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) AppController.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
                AppController.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                Uri uri = Uri.parse(urls.get(position));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                request.setVisibleInDownloadsUi(false);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                downloadManager.enqueue(request);
            }
        });

        itemsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectedUrl = urls.get(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        itemsViewPager.setCurrentItem(position, false);
    }

    public class PreviewItemAdapter extends FragmentPagerAdapter {
        Context context;
        ArrayList<String> imageUrl;
        int albumLength;
        String type;

        public PreviewItemAdapter(FragmentManager fm) {
            super(fm);
        }

        public PreviewItemAdapter(Context context, FragmentManager fm, String type) {
            super(fm);
            this.context = context;
            this.type = type;
        }

        public void updateAdapter(ArrayList<String> imageUrl, int albumLength) {
            this.imageUrl = imageUrl;
            this.albumLength = albumLength;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return new PreviewItemFragment(imageUrl.get(position), type);
        }

        @Override
        public int getCount() {
            return albumLength;
        }
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
}
