package activity.challenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import CustomClass.RobotoBoldTextView;
import activity.userprofile.MainActivity;
import adaptor.FunAwardAdaptor;
import constants.Constants;
import fragment.DashboardFragment;
import fragment.NavigationDrawerFragment;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class FunAwardChallengeActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageButton btnBack;
    private TextView tvTitle;
    private RelativeLayout rlTop;
    private RecyclerView rvCompetitors;
    private GridLayoutManager gridLayoutManager;
    private FunAwardAdaptor mFunAwardAdaptor = null;
    private EditText etSearch;
    private RobotoBoldTextView tvVotenow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_award_challenge);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvVotenow = (RobotoBoldTextView) findViewById(R.id.tvVotenow);

        tvVotenow.setOnClickListener(this);
        btnBack.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVotenow:
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
        TextView tvComplete = (TextView) dialog.findViewById(R.id.tvComplete);
        TextView tvUnLockTxt = (TextView) dialog.findViewById(R.id.tvUnLockTxt);

        String text_view_str = "Your vote for <b>Annabele</b> has been captured successfully.";
        tvComplete.setText(Html.fromHtml(text_view_str));

        String text_view_unlock = "You unlock a new challenge of <b>Appereciate challenge</b>.";
        tvUnLockTxt.setText(Html.fromHtml(text_view_unlock));

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(FunAwardChallengeActivity.this, MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                /*intent.putExtra("screen", "individual");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(intent);
                finish();
            }
        });
        tvMyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
             /*   Intent intent = new Intent(FunAwardChallengeActivity.this, MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                *//*intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*//*
                startActivity(intent);
                finish();*/

                /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                MainActivity.isInternetConnection = false;
                intent.putExtra("target", "challengeFragment");
                startActivity(intent);
                finish();*/


                Bundle bundle = new Bundle();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                MainActivity.isInternetConnection = false;
                intent.putExtra("action", Constants.ACTION_NONE);
                bundle.putString("target", "challengeFragment");
                //intent.putExtra("target", "challengeFragment");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();




                /*Fragment fragment = new DashboardFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("target", "skorChatFragment");
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();*/
            }
        });
    }
}
