package activity.newsfeed;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.root.skor.R;

import constants.Constants;

/**
 * Created by dss-17 on 9/29/17.
 */

public class VideoPreviewActivity extends AppCompatActivity {

    VideoView videoView;
    Button playButton, pauseButton;
    FrameLayout frameLayout;

    int stopPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        videoView = (VideoView) findViewById(R.id.activity_video_preview_videoView);
        playButton = (Button) findViewById(R.id.activity_video_preview_playButton);
        pauseButton = (Button) findViewById(R.id.activity_video_preview_pauseButton);
        frameLayout = (FrameLayout) findViewById(R.id.activity_video_preview_frameLayout);

        String videoUrl = getIntent().getStringExtra("videoUrl");

        Uri uri = Uri.parse(Constants.BASEURL + videoUrl);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
                videoView.start();
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPosition = videoView.getCurrentPosition();

                videoView.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.seekTo(stopPosition);

                videoView.start();
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);

                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        pauseButton.setVisibility(View.GONE);
                    }
                }.start();
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPosition = videoView.getCurrentPosition();
                videoView.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                pauseButton.setVisibility(View.GONE);
            }
        }.start();

    }
}
