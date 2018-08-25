package activity.newsfeed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.root.skor.R;

import constants.Constants;

/**
 * Created by dss-17 on 10/3/17.
 */

public class AdsDetailActivity extends AppCompatActivity {
    ImageView adsImageView, backButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);

        adsImageView = (ImageView) findViewById(R.id.activity_ads_detail_adsImageView);
        backButton = (ImageView) findViewById(R.id.activity_news_detail_backImageView);

        String adsImageURL = getIntent().getStringExtra("image");
        Glide.with(this).load(Constants.BASEURL + adsImageURL).into(adsImageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
