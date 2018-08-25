package activity.skorchat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import activity.skorchat.attachment.DocAttachmentFragment;
import activity.skorchat.attachment.ImageAttachmentFragment;
import activity.skorchat.attachment.VideoAttachmentFragment;

/**
 * Created by mac on 10/26/17.
 */

public class AttachmentActivity extends AppCompatActivity {
    private RelativeLayout mImageButton, mVideoButton, mDocButton, mMapButton;
    private View mImageView, mVideoView, mDocView, mMapView;
    private TextView mImageText, mVideoText, mDocText, mMapText;
    private LinearLayout mBackButton;
    private FragmentManager fragmentManager;

    private ImageAttachmentFragment imageAttachmentFragment;
    private VideoAttachmentFragment videoAttachmentFragment;
    private DocAttachmentFragment docAttachmentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        mBackButton = (LinearLayout) findViewById(R.id.activity_attachment_backButton);

        mImageButton = (RelativeLayout) findViewById(R.id.activity_attachment_imageButton);
        mVideoButton = (RelativeLayout) findViewById(R.id.activity_attachment_videoButton);
        mDocButton = (RelativeLayout) findViewById(R.id.activity_attachment_docButton);
        mMapButton = (RelativeLayout) findViewById(R.id.activity_attachment_mapsButton);

        mImageView = (View) findViewById(R.id.activity_attachment_imageView);
        mVideoView = (View) findViewById(R.id.activity_attachment_videoView);
        mDocView = (View) findViewById(R.id.activity_attachment_docView);
        mMapView = (View) findViewById(R.id.activity_attachment_mapView);

        mImageText = (TextView) findViewById(R.id.activity_attachment_imageText);
        mVideoText = (TextView) findViewById(R.id.activity_attachment_videoText);
        mDocText = (TextView) findViewById(R.id.activity_attachment_docText);
        mMapText = (TextView) findViewById(R.id.activity_attachment_mapText);


        mBackButton.setOnClickListener(backTapped);
        mImageButton.setOnClickListener(imageTapped);
        mVideoButton.setOnClickListener(videoTapped);
        mDocButton.setOnClickListener(docTapped);
        mMapButton.setOnClickListener(mapTapped);

        fragmentManager = getSupportFragmentManager();

        imageAttachmentFragment = new ImageAttachmentFragment();
        videoAttachmentFragment = new VideoAttachmentFragment();
        docAttachmentFragment = new DocAttachmentFragment();

        // Always start image fragment first
        fragmentManager.beginTransaction()
                .replace(R.id.container, imageAttachmentFragment)
                .commit();
    }

    View.OnClickListener backTapped =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    View.OnClickListener imageTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetView();
            mImageText.setTextColor(Color.parseColor("#50c2ff"));
            mImageView.setVisibility(View.VISIBLE);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, imageAttachmentFragment)
                    .commit();
        }
    };

    View.OnClickListener videoTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetView();
            mVideoText.setTextColor(Color.parseColor("#50c2ff"));
            mVideoView.setVisibility(View.VISIBLE);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, videoAttachmentFragment)
                    .commit();
        }
    };

    View.OnClickListener docTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetView();
            mDocText.setTextColor(Color.parseColor("#50c2ff"));
            mDocView.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener mapTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetView();
            mMapText.setTextColor(Color.parseColor("#50c2ff"));
            mMapView.setVisibility(View.VISIBLE);

        }
    };

    private void resetView() {
        mImageView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.GONE);
        mDocView.setVisibility(View.GONE);
        mMapView.setVisibility(View.GONE);

        mImageText.setTextColor(Color.parseColor("#000000"));
        mVideoText.setTextColor(Color.parseColor("#000000"));
        mDocText.setTextColor(Color.parseColor("#000000"));
        mMapText.setTextColor(Color.parseColor("#000000"));
    }
}
