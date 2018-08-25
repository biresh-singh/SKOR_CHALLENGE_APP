package activity.skorchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.root.skor.R;

import utils.GPSTracker;

/**
 * Created by dss-17 on 6/8/17.
 */

public class ShareLocationActivity extends AppCompatActivity implements OnMapReadyCallback{
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    GPSTracker gpsTracker;

    TextView shareButton;
    LinearLayout backLinearLayout;
    public LatLng currentLatLng;

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
        currentLatLng = currentPos;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 10));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        shareButton = (TextView) findViewById(R.id.activity_share_location_shareButton);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_share_location_map);
        backLinearLayout = (LinearLayout) findViewById(R.id.activity_share_location_backLinearLayout);
        supportMapFragment.getMapAsync(this);
        gpsTracker = new GPSTracker(this);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = String.valueOf(gpsTracker.getLatitude());
                String longitude = String.valueOf(gpsTracker.getLongitude());

                Intent intent = new Intent();
                intent.putExtra("lat_lng", latitude + "_"+ longitude);
                setResult(5, intent);
                finish();
            }
        });
        backLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}
