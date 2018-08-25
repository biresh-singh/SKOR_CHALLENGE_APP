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
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.root.skor.R;

import CustomClass.RobotoBoldTextView;
import activity.userprofile.MainActivity;
import adaptor.FunAwardAdaptor;
import constants.Constants;
import de.hdodenhof.circleimageview.CircleImageView;
import utils.GPSTracker;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class GymCheckinChallengeActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {
    private Context mContext;
    private ImageButton btnBack;
    String position = "", categorySlug = "", type = "",url="";
    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    GPSTracker gpsTracker;
    private LinearLayout llColleagues;
    private RobotoBoldTextView tvCheckIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_checkin_challenge);
        gpsTracker = new GPSTracker(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);
        tvCheckIn = (RobotoBoldTextView) findViewById(R.id.tvCheckIn);

        btnBack.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        if (gpsTracker.canGetLocation()) {
            position = "?lat=" + gpsTracker.getLatitude() + "&lng=" + gpsTracker.getLongitude();
            url = Constants.BASEURL + Constants.REWARDS_BY_LOCATION + position + "";

        }

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
    public void onMapReady(GoogleMap mMap) {

        googleMap = mMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng currentPos = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 10));

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

        /*CircleImageView ivChallengeIcon = (CircleImageView) dialog.findViewById(R.id.ivChallengeIcon);
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

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                startActivity(intent);
                finish();
            }
        });

    }
}
