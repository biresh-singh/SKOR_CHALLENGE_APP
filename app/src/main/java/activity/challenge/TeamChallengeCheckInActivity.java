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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import adaptor.InvitedFriendsTeamCheckAdaptor;
import constants.Constants;

/**
 * Created by biresh.singh on 18-06-2018.
 */

public class TeamChallengeCheckInActivity extends AppCompatActivity implements View.OnClickListener {
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
    private RecyclerView rvInvitedFriends;
    private InvitedFriendsTeamCheckAdaptor mInvitedFriendsAdaptor = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_challenge_checkin);
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
        rvInvitedFriends = (RecyclerView) findViewById(R.id.rvInvitedFriends);

        btnBack.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        mInvitedFriendsAdaptor = new InvitedFriendsTeamCheckAdaptor(mContext);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(TeamChallengeCheckInActivity.this, LinearLayoutManager.HORIZONTAL, false);

        //rvCompetitors.setItemAnimator(new DefaultItemAnimator());
        rvInvitedFriends.setLayoutManager(horizontalLayoutManagaer);
        rvInvitedFriends.setAdapter(mInvitedFriendsAdaptor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckIn:
                showCongratulationPopup(this);
                break;
            case R.id.btnBack:
                    finish();
                break;
        }
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
                /*Intent intent = new Intent(TeamChallengeCheckInActivity.this, MainActivity.class);
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
        tvMyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*Intent intent = new Intent(TeamChallengeCheckInActivity.this, MainActivity.class);
                intent.putExtra("screen", "myChallenge");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}