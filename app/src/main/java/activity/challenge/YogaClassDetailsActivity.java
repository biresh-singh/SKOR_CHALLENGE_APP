package activity.challenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.root.skor.R;

import java.text.NumberFormat;
import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;
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

public class YogaClassDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnBack;
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private RobotoRegularTextView tvPoints;
    private RobotoRegularTextView tvParticipant;
    private ProgressBar progressBar;
    private RobotoBoldTextView tvCheckIn;
    private LinearLayout llTakeChallenge;
    private LinearLayout llMain;
    private ImageView imgCenter;
    private String challengetype;
    Challenge clng;
    private TextView tvChalengeTypeTitle,tvChallengeDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_class_details);
        if(getIntent()!=null)
        {
            Bundle bundle=getIntent().getExtras();
            if(bundle!=null)
            {

                if(bundle.containsKey(Constants.CHALLENGETYPE))
                {
                    challengetype=bundle.getString(Constants.CHALLENGETYPE);
                }
                if(bundle.containsKey(Constants.GETCHALLENGEDATA))
                {
                    clng=bundle.getParcelable(Constants.GETCHALLENGEDATA);

                }

            }
        }
        initView();
    }

    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        tvPoints = (RobotoRegularTextView) findViewById(R.id.tvPoints);
        tvParticipant = (RobotoRegularTextView) findViewById(R.id.tvParticipant);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvCheckIn = (RobotoBoldTextView) findViewById(R.id.tvCheckIn);
        llTakeChallenge = (LinearLayout) findViewById(R.id.llTakeChallenge);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        imgCenter = (ImageView) findViewById(R.id.imgCenter);
        tvChalengeTypeTitle= (TextView) findViewById(R.id.tvChalengeTypeTitle);
        tvChallengeDesc= (TextView) findViewById(R.id.tvChallengeDesc);
        tvCheckIn.setOnClickListener(this);
        tvPoints.setText("+"+ NumberFormat.getInstance().format(clng.getValue())+" "+clng.getRewardName());
        tvChalengeTypeTitle.setText(clng.getChallengetitle());
        tvChallengeDesc.setText(clng.getDescription());

        TextView tvChallengeIcon= (TextView) findViewById(R.id.challengeIcon);
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvChallengeIcon, iconFont);

        tvChallengeIcon.setText(this.getResources().getString(R.string.fa_icon_areachart));
        tvChallengeIcon.bringToFront();

        GetParticipant(clng.getid());
        btnBack.setOnClickListener(this);
    }


    private void GetParticipant(int ChallengeID)
    {
        Loader.showProgressDialog(YogaClassDetailsActivity.this);
        final SharedDatabase sharedDatabase = new SharedDatabase(YogaClassDetailsActivity.this);
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
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }
                        else
                        {
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }
                        Loader.dialogDissmiss(YogaClassDetailsActivity.this);
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
                Loader.dialogDissmiss(YogaClassDetailsActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tvCheckIn:
                showOtpPopup(this);
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

        EditText mPinField1 = (EditText) dialog.findViewById(R.id.otp_tv_otp_1);
        final EditText mPinField2 = (EditText) dialog.findViewById(R.id.otp_tv_otp_2);
        final EditText mPinField3 = (EditText) dialog.findViewById(R.id.otp_tv_otp_3);
        final EditText mPinField4 = (EditText) dialog.findViewById(R.id.otp_tv_otp_4);

        TextView tvChallengeIcon= (TextView) dialog.findViewById(R.id.challengeIcon);
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvChallengeIcon, iconFont);

        tvChallengeIcon.setText(this.getResources().getString(R.string.fa_icon_areachart));
        tvChallengeIcon.bringToFront();

        mPinField1.setOnClickListener(this);
        mPinField2.setOnClickListener(this);
        mPinField3.setOnClickListener(this);
        mPinField4.setOnClickListener(this);

        mPinField1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPinField2.requestFocus();
            }
        });
        mPinField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPinField3.requestFocus();
            }
        });
        mPinField3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPinField4.requestFocus();
            }
        });



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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                Intent intent = new Intent(YogaClassDetailsActivity.this, MainActivity.class);
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
                Intent intent = new Intent(YogaClassDetailsActivity.this, MainActivity.class);
                intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

}