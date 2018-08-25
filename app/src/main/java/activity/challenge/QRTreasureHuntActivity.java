package activity.challenge;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import adaptor.QRCodeListAdaptor;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class QRTreasureHuntActivity  extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageButton btnBack;
    private TextView tvTitle;
    private RelativeLayout rlTop;
    private RecyclerView rvScanQRCode;
    private GridLayoutManager gridLayoutManager;
    private QRCodeListAdaptor mScanQRCodeAdaptor = null;
    private LinearLayout llColleagues;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_treasure_hunt);
        initView();
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rvScanQRCode = (RecyclerView) findViewById(R.id.rvScanQRCode);

        llColleagues = (LinearLayout) findViewById(R.id.llColleagues);

        btnBack.setOnClickListener(this);

        mScanQRCodeAdaptor = new QRCodeListAdaptor(mContext);
        gridLayoutManager = new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false);
        rvScanQRCode.setHasFixedSize(true);
        rvScanQRCode.setItemAnimator(new DefaultItemAnimator());
        rvScanQRCode.setLayoutManager(gridLayoutManager);
        rvScanQRCode.setAdapter(mScanQRCodeAdaptor);

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
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
