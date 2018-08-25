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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import adaptor.QRCodeListAdaptor;
import constants.Constants;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by biresh.singh on 18-06-2018.
 */

public class WriteFunArticleChallengeActivity extends AppCompatActivity implements View.OnClickListener {
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
    private RobotoRegularTextView tvSubmit;
    private GridLayoutManager gridLayoutManager;
    private QRCodeListAdaptor mScanQRCodeAdaptor = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvSubmit = (RobotoRegularTextView) findViewById(R.id.tvSubmit);
        btnBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);


        for (int i = 0; i < 10; i++) {
            ImageView circleImageView = new ImageView(mContext);
            circleImageView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            circleImageView.setId(i);
            circleImageView.setImageResource(R.drawable.icon_silhouette);
            circleImageView.setPadding(10,10,10,10);
            llColleagues.addView(circleImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSubmit:
                ConfirmationPopup(this);
                break;
            case R.id.btnBack:
                    finish();
                break;
        }
    }


    private void ConfirmationPopup(Activity context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_submit_article);
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

        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);

        String text_view_str = "Are you sure you want to submit this article for approval?<br>You can not edit this article once submitted.";
        tvMessage.setText(Html.fromHtml(text_view_str));



        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(WriteFunArticleChallengeActivity.this, MainActivity.class);
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
                Intent intent = new Intent(WriteFunArticleChallengeActivity.this, MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                /*intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
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