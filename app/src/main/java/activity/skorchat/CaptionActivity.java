package activity.skorchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.root.skor.R;

import java.io.File;

/**
 * Created by dss-17 on 7/6/17.
 */

public class CaptionActivity extends AppCompatActivity {
    EditText captionEditText;
    ImageView sendButton, previewImageView, playButton;
    VideoView previewVideoView;
    File file;
    String selectedVideoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        previewImageView = (ImageView) findViewById(R.id.activity_caption_previewImageView);
        previewVideoView = (VideoView) findViewById(R.id.activity_caption_previewVideoView);
        sendButton = (ImageView) findViewById(R.id.activity_caption_sendButton);
        captionEditText = (EditText) findViewById(R.id.activity_caption_captionEditText);
        playButton = (ImageView) findViewById(R.id.activity_caption_playVideButton);

        previewImageView.setVisibility(View.GONE);
        previewVideoView.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);

        file = (File) getIntent().getExtras().get("file");
        selectedVideoPath = (String) getIntent().getExtras().get("selectedVideoPath");

        assert file != null;
        if (selectedVideoPath != null) {
            previewVideoView.setVisibility(View.VISIBLE);
            previewImageView.setVisibility(View.GONE);
            previewVideoView.setVideoPath(selectedVideoPath);
            previewVideoView.start();
        } else {
            previewImageView.setVisibility(View.VISIBLE);
            previewVideoView.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            previewImageView.setImageBitmap(bitmap);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = String.valueOf(captionEditText.getText());
                Intent intent = new Intent();
                intent.putExtra("file", file);
                intent.putExtra("caption", caption);
                setResult(7, intent);
                finish();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewVideoView.start();
                playButton.setVisibility(View.VISIBLE);
            }
        });

        previewVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setVisibility(View.GONE);
            }
        });

    }

}
