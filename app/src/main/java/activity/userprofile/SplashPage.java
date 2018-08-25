package activity.userprofile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.root.skor.R;

import activity.walkthrough.FragmentReplaceAnimationActivity;
import constants.Constants;
import database.SharedDatabase;
import utils.VMRuntime;


public class SplashPage extends Activity {
    public static String PACKAGE_NAME;
    RelativeLayout splase_screen;
    public SharedDatabase sharedDatabase;
    public static boolean pushnotificationisfalse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        splase_screen = (RelativeLayout) findViewById(R.id.splase_screen);
        VMRuntime.getRuntime().setMinimumHeapSize(4096 * 1024 * 1024);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        String baseUrl = getResources().getString(R.string.base_url);
        sharedDatabase=new SharedDatabase(getApplicationContext());
        sharedDatabase.setPosition(0);
        sharedDatabase.setType("");

        if (PACKAGE_NAME.equals("com.root.unilever.ae")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#0157ac"));
            }
        }
        Constants.BASEURL = baseUrl;
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    pushnotificationisfalse = true;
                    Intent intent = new Intent(getApplicationContext(), FragmentReplaceAnimationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
