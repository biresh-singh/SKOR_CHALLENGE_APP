package activity.challenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.Result;
import com.root.skor.R;

import java.util.ArrayList;

import activity.userprofile.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class ScanQRCodeActivity  extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private Context mContext;
    private ImageButton btnBack;
    private TextView tvTitle;
    private ZXingScannerView mScannerView;
    private ProgressBar progressBar;
    private LinearLayout llColleagues;
    private HorizontalScrollView scrollHorizontal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        mScannerView = (ZXingScannerView) findViewById(R.id.scannerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);
        scrollHorizontal = (HorizontalScrollView) findViewById(R.id.scrollHorizontal);

        btnBack.setOnClickListener(this);



        for (int i = 0; i < 10; i++) {
            CircleImageView circleImageView = new CircleImageView(mContext);
            circleImageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            circleImageView.setId(i);
            circleImageView.setImageResource(R.drawable.default_user);
            circleImageView.setPadding(10, 10, 10, 10);
            llColleagues.addView(circleImageView);
        }
        checkPermissions();
        //showCongratulationPopup(ScanQRCodeActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String qrCodeValue = "";
        qrCodeValue = rawResult.getText().toString().trim();
        Log.d("QR Code", qrCodeValue);
        mScannerView.stopCamera();
        showCongratulationPopup(ScanQRCodeActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int flag = 0;
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    flag++;
                }
            }
            if (flag == permissions.length) {
                initiateRequest();
            }
        }
    }

    private void checkPermissions() {
        ArrayList<String> permissionArrayList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionArrayList.add(android.Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionArrayList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionArrayList.size() > 0) {
            String[] permissions = permissionArrayList.toArray(new String[permissionArrayList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            initiateRequest();
        }
    }

    private void initiateRequest() {
        setCamera();
    }

    void setCamera() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
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
//        wlp.y = getResources().getDimensionPixelSize(R.dimen._20sdp);
        dialog.getWindow().setAttributes(wlp);
        dialog.show();


        TextView tvBack = (TextView) dialog.findViewById(R.id.tvBack);
        TextView tvMyChallenge = (TextView) dialog.findViewById(R.id.tvMyChallenge);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ScanQRCodeActivity.this, MainActivity.class);
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
                Intent intent = new Intent(ScanQRCodeActivity.this, MainActivity.class);
                intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

}
