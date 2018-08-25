package activity.challenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.root.skor.R;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.userprofile.MainActivity;
import constants.Constants;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by biresh.singh on 18-06-2018.
 */

public class WalkingtakeChallengeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnBack;
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private RobotoRegularTextView tvTotalPoint;
    private RobotoRegularTextView tvTotalParticipants;
    private ProgressBar progressBar;
    private RobotoBoldTextView tvCheckIn;
    private LinearLayout llTakeChallenge;
    private LinearLayout llMain;
    private ImageView imgCenter;
    private Context mContext;
    private LinearLayout llColleagues;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_challenge);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        tvTotalPoint = (RobotoRegularTextView) findViewById(R.id.tvTotalPoint);
        tvTotalParticipants = (RobotoRegularTextView) findViewById(R.id.tvTotalParticipants);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvCheckIn = (RobotoBoldTextView) findViewById(R.id.tvCheckIn);
        llTakeChallenge = (LinearLayout) findViewById(R.id.llTakeChallenge);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        imgCenter = (ImageView) findViewById(R.id.imgCenter);
        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);
        tvCheckIn.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        for (int i = 0; i < 10; i++) {
            CircleImageView circleImageView = new CircleImageView(mContext);
            circleImageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            circleImageView.setId(i);
            circleImageView.setImageResource(R.drawable.default_user);
            circleImageView.setPadding(10,10,10,10);
            llColleagues.addView(circleImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckIn:
                showOtpPopup(this);
                break;
            case R.id.btnBack:
                    finish();
                break;
        }
    }

    private void showOtpPopup(Activity context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_yoga_otp_verify);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(wlp);
        dialog.show();


        final LinearLayout llOtp = (LinearLayout) dialog.findViewById(R.id.llOtp);
        final LinearLayout llOtpCongrats = (LinearLayout) dialog.findViewById(R.id.llOtpCongrats);
        TextView tvSubmitPin = (TextView) dialog.findViewById(R.id.tvSubmitPin);
        TextView tvDone = (TextView) dialog.findViewById(R.id.tvDone);
        TextView tvCongMessage = (TextView) dialog.findViewById(R.id.tvCongMessage);

 /*       CircleImageView ivChallengeIcon = (CircleImageView) dialog.findViewById(R.id.ivChallengeIcon);
        ivChallengeIcon.setVisibility(View.GONE);*/
        llOtp.setVisibility(View.GONE);
        llOtpCongrats.setVisibility(View.VISIBLE);
        String text_view_str = "You have completed <b>2000 steps</b>.\n Today's task has been completed";
        tvCongMessage.setText(Html.fromHtml(text_view_str));

        tvSubmitPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llOtp.setVisibility(View.GONE);
                llOtpCongrats.setVisibility(View.VISIBLE);
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //showCongratulationPopup(YogaClassDetailsActivity.this);
                /*Intent intent = new Intent(YogaClassDetailsActivity.this, MainActivity.class);
                intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();*/

                /*Intent intent = new Intent(YogaClassDetailsActivity.this, MainActivity.class);
                intent.putExtra("screen", "individual");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();*/

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showCongratulationPopup(Activity context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_congratulation);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(wlp);
        dialog.show();


        TextView tvBack = (TextView) dialog.findViewById(R.id.tvBack);
        TextView tvMyChallenge = (TextView) dialog.findViewById(R.id.tvMyChallenge);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(WalkingtakeChallengeActivity.this, MainActivity.class);
                intent.putExtra("screen", "individual");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        tvMyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(WalkingtakeChallengeActivity.this, MainActivity.class);
                intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}