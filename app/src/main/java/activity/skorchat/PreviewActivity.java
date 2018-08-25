package activity.skorchat;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jsibbold.zoomage.ZoomageView;
import com.root.skor.BuildConfig;
import com.root.skor.R;

import utils.AppController;
import utils.BaseMediaPlayerActivity;
import utils.VideoCache;

/**
 * Created by dss-17 on 7/3/17.
 */

public class PreviewActivity extends BaseMediaPlayerActivity {
    ImageView previewImageView, playVideoButton, backButton;
    VideoView previewVideoView;
    SimpleExoPlayerView simpleExoPlayerView;
    LinearLayout downloadButton;
    String url, urlWithoutToken, name, type;

    SimpleExoPlayer simpleExoPlayer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        previewImageView = (ZoomageView) findViewById(R.id.activity_preview_previewImageView);
        previewVideoView = (VideoView) findViewById(R.id.activity_preview_previewVideoView);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.activity_preview_simpleExoPlayerView);
        downloadButton = (LinearLayout) findViewById(R.id.activity_preview_downloadButton);
        playVideoButton = (ImageView) findViewById(R.id.activity_preview_playVideButton);
        backButton = (ImageView) findViewById(R.id.activity_preview_backButton);

        url = getIntent().getStringExtra("url");
        if (url.contains("?")) {
            String[] strings = url.split("\\?");
            urlWithoutToken = strings[0];
        }
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");

        initiateMediaPlayer();

//        previewVideoView.setVisibility(View.GONE);
        simpleExoPlayerView.setVisibility(View.GONE);
        previewImageView.setVisibility(View.GONE);
        playVideoButton.setVisibility(View.GONE);
        if (type.equals("video/mp4")) {
//            previewVideoView.setVisibility(View.VISIBLE);
//            previewVideoView.setVideoPath(urlWithoutToken);
//            previewVideoView.start();

            simpleExoPlayerView.setVisibility(View.VISIBLE);
            HttpProxyCacheServer proxy = VideoCache.getInstance();
            prepareMediaPlayer(PreviewActivity.this, urlWithoutToken);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download(urlWithoutToken);
                }
            });
        } else{
            previewImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(url).into(previewImageView);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download(url);
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        previewVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                playVideoButton.setVisibility(View.VISIBLE);
//            }
//        });
//
//        playVideoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                previewVideoView.start();
//                playVideoButton.setVisibility(View.GONE);
//            }
//        });
    }

    public void download(String url) {
        DownloadManager downloadManager = (DownloadManager) AppController.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        AppController.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        request.setVisibleInDownloadsUi(false);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        downloadManager.enqueue(request);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            String action = intent.getAction();
            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
            }
        }
    };

    private void initiateMediaPlayer() {
        simpleExoPlayer = createMediaPlayer();
        simpleExoPlayerView.setPlayer(simpleExoPlayer);

        // Hide ExoPlayer UI controls
        simpleExoPlayerView.setUseController(false);

//        simpleExoPlayerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (mIsStopped) {
//                    simpleExoPlayer.setPlayWhenReady(true);
//                    playPauseFrameLayout.setVisibility(View.GONE);
//                } else {
//                    simpleExoPlayer.setPlayWhenReady(false);
//                    playPauseFrameLayout.setVisibility(View.VISIBLE);
//                }
//
//                mIsStopped = !mIsStopped;
//                return false;
//            }
//        });
    }

    private SimpleExoPlayer createMediaPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // Start playing when the video is ready
        player.setPlayWhenReady(true);
        player.addListener(this);

        return player;
    }

    private void prepareMediaPlayer(PreviewActivity activity, String playUrl) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        DataSource.Factory mDataSourceFactory = new DefaultDataSourceFactory(activity,
                Util.getUserAgent(this, BuildConfig.APPLICATION_ID), bandwidthMeter);

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(playUrl),
                mDataSourceFactory, extractorsFactory, null, null);
        simpleExoPlayer.prepare(videoSource);

        // We're not using LoopingMediaSource because STATE_ENDED in onPlayerStateChanged will never
        // get triggered and I need STATE_ENDED to be triggered to do some UI logic
//        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
//        simpleExoPlayer.prepare(loopingSource);
    }

}
