package activity.rewardz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.root.skor.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dss-17 on 3/3/17.
 */

public class WebviewBrochureActivity extends AppCompatActivity {

    WebView brochureWebView;
    String brochureURL;
    LinearLayout backButton;
    private static final String TAG = "WebviewBrochureActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewbrochure);
        brochureWebView = (WebView) findViewById(R.id.activity_webviewbrochure_webview);
        backButton = (LinearLayout) findViewById(R.id.activity_webviewbrochure_titlebar);

        brochureWebView.getSettings().setJavaScriptEnabled(true);
        brochureWebView.setBackgroundColor(444444);
        brochureWebView.getSettings().setDefaultFontSize(14);
        brochureWebView.getSettings().setLoadWithOverviewMode(true);
        brochureWebView.getSettings().setBuiltInZoomControls(true);

        if (getIntent().getStringExtra("brochureURL").endsWith(".pdf")) {
            try {
                brochureURL = URLEncoder.encode(getIntent().getStringExtra("brochureURL"), "UTF-8");
                brochureWebView.loadUrl("https://drive.google.com/viewer?url=" + brochureURL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            brochureURL = getIntent().getStringExtra("brochureURL");
            brochureWebView.loadUrl(brochureURL);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
