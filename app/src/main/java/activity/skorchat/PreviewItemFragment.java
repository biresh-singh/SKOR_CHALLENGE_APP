package activity.skorchat;

import android.annotation.SuppressLint;
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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;
import com.root.skor.R;

import utils.AppController;

/**
 * Created by dss-17 on 7/3/17.
 */

public class PreviewItemFragment extends Fragment {
    ImageView playVideoButton;
    ZoomageView previewImageView;
    VideoView previewVideoView;
    String url, type;

    public PreviewItemFragment() {

    }

    @SuppressLint("ValidFragment")
    public PreviewItemFragment(String url, String type) {
        this.url = url;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewImageView = (ZoomageView) view.findViewById(R.id.fragment_preview_item_previewImageView);
        previewVideoView = (VideoView) view.findViewById(R.id.fragment_preview_item_previewVideoView);
        playVideoButton = (ImageView) view.findViewById(R.id.fragment_preview_item_playVideButton);

        previewVideoView.setVisibility(View.GONE);
        previewImageView.setVisibility(View.GONE);
        playVideoButton.setVisibility(View.GONE);

        if (type.equals("video/mp4")) {
            previewVideoView.setVisibility(View.VISIBLE);
            previewVideoView.setVideoPath(url);
            previewVideoView.start();
        } else{
            previewImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(url).into(previewImageView);
        }

        previewVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideoButton.setVisibility(View.VISIBLE);
            }
        });

        playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewVideoView.start();
                playVideoButton.setVisibility(View.GONE);
            }
        });
    }
}
