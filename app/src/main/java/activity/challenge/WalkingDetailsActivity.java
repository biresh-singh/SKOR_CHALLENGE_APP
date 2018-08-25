package activity.challenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.root.skor.R;

import java.text.NumberFormat;
import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.GetParticipantsResponse;
import model.Participants;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.FontManager;
import utils.Loader;

/**
 * Created by biresh.singh on 18-06-2018.
 */

public class WalkingDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnBack;
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private RobotoRegularTextView tvTotalPoint,tvTotalParticipants,tvChallengeDesc;

    private ProgressBar progressBar;
    private RobotoBoldTextView tvCheckIn,tvChallengeTitle,tvAcceptedCount;
    private LinearLayout llTakeChallenge;
    private LinearLayout llMain;
    private ImageView imgCenter;
    private Context mContext;
    private LinearLayout llColleagues,llParticipantsList;

    Challenge clng;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_details);
        if(getIntent()!=null)
        {
            Bundle bundle=getIntent().getExtras();
            if(bundle!=null)
            {


                if(bundle.containsKey(Constants.GETCHALLENGEDATA))
                {
                    clng=bundle.getParcelable(Constants.GETCHALLENGEDATA);

                }

            }
        }
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        tvTotalPoint = (RobotoRegularTextView) findViewById(R.id.tvTotalPoint);
        tvTotalParticipants = (RobotoRegularTextView) findViewById(R.id.tvTotalParticipants);
        tvChallengeDesc= (RobotoRegularTextView) findViewById(R.id.tvChallengeDesc);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvCheckIn = (RobotoBoldTextView) findViewById(R.id.tvCheckIn);
        tvChallengeTitle= (RobotoBoldTextView) findViewById(R.id.tvChallengeTitle);
        tvAcceptedCount= (RobotoBoldTextView) findViewById(R.id.tvAcceptedCount);
        llTakeChallenge = (LinearLayout) findViewById(R.id.llTakeChallenge);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        imgCenter = (ImageView) findViewById(R.id.imgCenter);
        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);
        llParticipantsList= (LinearLayout) findViewById(R.id.llParticipantsList);
        tvCheckIn.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        TextView tvChallengeIcon= (TextView) findViewById(R.id.challengeIcon);
        Typeface iconFont = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvChallengeIcon, iconFont);

        tvChallengeIcon.setText(mContext.getResources().getString(R.string.fa_icon_areachart));
        tvChallengeIcon.bringToFront();


        tvChallengeTitle.setHint(clng.getChallengetitle());
        tvTotalPoint.setHint("+"+ NumberFormat.getInstance().format(clng.getValue())+" "+clng.getRewardName());
        tvChallengeDesc.setHint(clng.getDescription());
        GetParticipant(clng.getid());



    }

    private void GetParticipant(int ChallengeID)
    {
        Loader.showProgressDialog(mContext);
        final SharedDatabase sharedDatabase = new SharedDatabase(mContext);
        String token = sharedDatabase.getToken();
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetParticipantsResponse> call = apiService.GetParticipants(ChallengeID, AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetParticipantsResponse>() {
            @Override
            public void onResponse(Call<GetParticipantsResponse> call, retrofit2.Response<GetParticipantsResponse> response) {
                try{
                    if(response.body()!=null) {
                        List<Participants> lstParticipants = response.body().getResult();

                        if (lstParticipants != null && lstParticipants.size() > 0) {
                            tvTotalParticipants.setHint(lstParticipants.size()+" Participants");
                            tvAcceptedCount.setText(lstParticipants.size()+" Colleagues have accept this challenge");
                            llParticipantsList.setVisibility(View.VISIBLE);

                            for (int i = 0; i < lstParticipants.size(); i++) {
                                CircleImageView circleImageView = new CircleImageView(mContext);
                                circleImageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                                circleImageView.setId(i);
                                circleImageView.setImageResource(R.drawable.default_user);
                                circleImageView.setPadding(10,10,10,10);

                                Glide.with(mContext).load(Constants.BASESTAGINFURL+lstParticipants.get(i).getPartcipantImage())
                                        .apply(RequestOptions.errorOf(R.mipmap.ic_launcher))
                                        .into(circleImageView);

                                llColleagues.addView(circleImageView);
                            }

                        }
                        else
                        {
                            tvTotalParticipants.setHint(lstParticipants.size()+" Participants");
                            llParticipantsList.setVisibility(View.GONE);
                        }
                        Loader.dialogDissmiss(mContext);
                    }
                    else
                    {

                    }

                }

                catch (NullPointerException ex)
                {

                }
                catch (IndexOutOfBoundsException ex)
                {

                }
                catch(IllegalArgumentException ex)
                {

                }
                catch (Exception ex) {

                }
            }



            @Override
            public void onFailure(Call<GetParticipantsResponse>call, Throwable t) {
                // Log error here since request failed
                Loader.dialogDissmiss(mContext);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckIn:
                startActivity(new Intent(this,WalkingtakeChallengeActivity.class));
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

        llOtp.setVisibility(View.VISIBLE);
        llOtpCongrats.setVisibility(View.GONE);

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
                Intent intent = new Intent(WalkingDetailsActivity.this, MainActivity.class);
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
                Intent intent = new Intent(WalkingDetailsActivity.this, MainActivity.class);
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