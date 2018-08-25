package activity.challenge;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.root.skor.R;

import java.util.List;

import CustomClass.RobotoBoldTextView;
import CustomClass.RobotoRegularTextView;
import activity.userprofile.MainActivity;
import adaptor.IndividualAdaptor;
import adaptor.InviteFriendsAdaptor;
import adaptor.InvitedFriendsAdaptor;
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
import utils.GPSTracker;
import utils.Loader;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class TeamInviteFriendsChallengeActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageButton btnBack;


    private RobotoBoldTextView tvCheckIn;
    private RobotoBoldTextView tvTitle;
    private RecyclerView rvFriends;
    private RecyclerView rvInvitedFriends;
    private GridLayoutManager gridLayoutManager;
    private GridLayoutManager gridInvitedLayoutManager;
    private InviteFriendsAdaptor mFriendsAdaptor = null;
    private InvitedFriendsAdaptor mInvitedFriendsAdaptor = null;
    private RobotoRegularTextView tvNext,txtNoDate;
    private String challengetype;
    Challenge clng;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_invite_friends);
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
        GetCollegueLists(clng.getid());
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvTitle = (RobotoBoldTextView) findViewById(R.id.tvTitle);
        tvNext = (RobotoRegularTextView) findViewById(R.id.tvNext);
        txtNoDate= (RobotoRegularTextView) findViewById(R.id.txtNoDate);
        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);
        rvInvitedFriends = (RecyclerView) findViewById(R.id.rvInvitedFriends);
        //tvTitle.setText("INVITE FRIENDS \n 5/5 Persons");
        mInvitedFriendsAdaptor = new InvitedFriendsAdaptor(mContext);
        gridInvitedLayoutManager = new GridLayoutManager(this,1);
        //rvInvitedFriends.setHasFixedSize(true);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(TeamInviteFriendsChallengeActivity.this, LinearLayoutManager.HORIZONTAL, false);

        //rvCompetitors.setItemAnimator(new DefaultItemAnimator());
        rvInvitedFriends.setLayoutManager(horizontalLayoutManagaer);
        rvInvitedFriends.setAdapter(mInvitedFriendsAdaptor);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvFriends.setLayoutManager(gridLayoutManager);
        rvFriends.setHasFixedSize(true);
        rvFriends.setNestedScrollingEnabled(false);
        mFriendsAdaptor = new InviteFriendsAdaptor(mContext);
        rvFriends.setAdapter(mFriendsAdaptor);

       /* mFriendsAdaptor = new InviteFriendsAdaptor(mContext);
        gridLayoutManager = new GridLayoutManager(this,1);
        rvFriends.setHasFixedSize(true);
        //rvCompetitors.setItemAnimator(new DefaultItemAnimator());
        rvFriends.setLayoutManager(gridLayoutManager);
        rvFriends.setAdapter(mFriendsAdaptor);*/

        btnBack.setOnClickListener(this);
        tvNext.setOnClickListener(this);

       /* for (int i = 0; i < 10; i++) {
            CircleImageView circleImageView = new CircleImageView(mContext);
            circleImageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            circleImageView.setId(i);
            circleImageView.setImageResource(R.drawable.default_user);
            circleImageView.setPadding(10,10,10,10);
            llColleagues.addView(circleImageView);
        }*/

    }

    private void GetCollegueLists(int ChallengeID)
    {
        //Loader.showProgressDialog(mContext);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetParticipantsResponse> call = apiService.Getinvitations(ChallengeID, AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetParticipantsResponse>() {
            @Override
            public void onResponse(Call<GetParticipantsResponse> call, retrofit2.Response<GetParticipantsResponse> response) {
                try{
                    if(response.body()!=null) {
                        List<Participants> lstParticipants = response.body().getResult();

                        if (lstParticipants != null && lstParticipants.size() > 0) {

                            rvFriends.setVisibility(View.VISIBLE);

                            mFriendsAdaptor.setData(lstParticipants);
                            txtNoDate.setVisibility(View.GONE);

                        }
                        else
                        {

                            rvFriends.setVisibility(View.GONE);
                            txtNoDate.setVisibility(View.VISIBLE);
                        }
                       // Loader.dialogDissmiss(mContext);
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
            case R.id.tvNext:
                Intent intent = new Intent(getApplicationContext(), TeamChallengeDetailActivity.class);
                startActivity(intent);
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

       /* CircleImageView ivChallengeIcon = (CircleImageView) dialog.findViewById(R.id.ivChallengeIcon);
        ivChallengeIcon.setVisibility(View.GONE);*/
        llOtp.setVisibility(View.GONE);
        llOtpCongrats.setVisibility(View.VISIBLE);
        String text_view_str = "You have successfully checked in for Day 1 in 20 days <b>Check in Gym</b> challenge.";
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

                Intent intent = new Intent(getApplicationContext(), TeamChallengeDetailActivity.class);
                startActivity(intent);

            }
        });

    }
}
